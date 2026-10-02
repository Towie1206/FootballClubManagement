const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');

const temporaryDirectory = fs.mkdtempSync(path.join(os.tmpdir(), 'fcm-backend-test-'));
const mockDatabasePath = path.join(temporaryDirectory, 'db.json');

process.env.NODE_ENV = 'test';
process.env.USE_MOCK_DB = 'true';
process.env.MOCK_DB_PATH = mockDatabasePath;
process.env.TOKEN_SECRET = 'test-only-token-secret-with-at-least-32-bytes';
process.env.GEMINI_API_KEY = '';
process.env.GEMINI_MODEL = '';
delete process.env.SERVER_API_KEY;

fs.writeFileSync(mockDatabasePath, JSON.stringify({
    metadata: { keepMe: 'migration-proof' },
    users: [
        { id: 1, username: 'legacy1', password: 'OldPass1', role: 'manager', note: 'preserve-user-field' },
        { id: 2, username: 'viewer1', password: 'ViewPass1', role: 'viewer' },
    ],
    players: [
        {
            id: 1,
            fullName: 'Legacy Player',
            position: 'MF',
            jerseyNumber: 7,
            healthStatus: 'Fit',
            ovr: 75,
            goals: 2,
            mvp: 1,
            customNote: 'preserve-player-field',
        },
    ],
}, null, 2));

const { createApp } = require('../index');
const { signAccessToken, verifyAccessToken, verifyPassword } = require('../lib/security');

async function listen(app) {
    const server = await new Promise((resolve, reject) => {
        const candidate = app.listen(0, '127.0.0.1', () => resolve(candidate));
        candidate.on('error', reject);
    });
    const address = server.address();
    return {
        server,
        baseUrl: `http://127.0.0.1:${address.port}`,
    };
}

function close(server) {
    return new Promise((resolve, reject) => server.close(error => (error ? reject(error) : resolve())));
}

async function request(baseUrl, pathname, options = {}) {
    const headers = { ...(options.headers || {}) };
    if (options.token) headers.authorization = `Bearer ${options.token}`;
    let body;
    if (Object.prototype.hasOwnProperty.call(options, 'body')) {
        headers['content-type'] = 'application/json';
        body = JSON.stringify(options.body);
    } else if (Object.prototype.hasOwnProperty.call(options, 'rawBody')) {
        headers['content-type'] = 'application/json';
        body = options.rawBody;
    }

    const response = await fetch(`${baseUrl}${pathname}`, {
        method: options.method || 'GET',
        headers,
        body,
    });
    const text = await response.text();
    return {
        status: response.status,
        body: text ? JSON.parse(text) : undefined,
    };
}

test('FC Manager backend security, CRUD, migration and AI contract', async t => {
    let capturedAiRequest;
    const app = createApp({
        aiModel: 'test-grounded-model',
        aiGenerateContent: async params => {
            capturedAiRequest = params;
            return { text: 'Đội hình đề xuất dựa trên dữ liệu hiện có.' };
        },
    });
    const running = await listen(app);
    t.after(async () => {
        await close(running.server);
        fs.rmSync(temporaryDirectory, { recursive: true, force: true });
    });

    let managerToken;
    let legacyToken;

    await t.test('health check is public and API errors use the standard shape', async () => {
        const health = await request(running.baseUrl, '/');
        assert.equal(health.status, 200);
        assert.equal(health.body.status, 'ok');
        assert.equal(health.body.database, 'mock');

        const missing = await request(running.baseUrl, '/api/does-not-exist');
        assert.equal(missing.status, 404);
        assert.deepEqual(Object.keys(missing.body.error).sort(), ['code', 'message']);

        const malformed = await request(running.baseUrl, '/api/auth/login', {
            method: 'POST',
            rawBody: '{',
        });
        assert.equal(malformed.status, 400);
        assert.equal(malformed.body.error.code, 'INVALID_JSON');
    });

    await t.test('registration validates input, hashes passwords and returns a signed token', async () => {
        const invalid = await request(running.baseUrl, '/api/auth/register', {
            method: 'POST',
            body: { username: 'x', password: 'short' },
        });
        assert.equal(invalid.status, 400);
        assert.equal(invalid.body.error.code, 'VALIDATION_ERROR');
        assert.ok(invalid.body.error.details.length >= 2);

        const registered = await request(running.baseUrl, '/api/auth/register', {
            method: 'POST',
            body: { username: 'New.Manager', password: 'Secure123' },
        });
        assert.equal(registered.status, 201);
        assert.equal(registered.body.user.username, 'new.manager');
        assert.equal(registered.body.user.role, 'manager');
        assert.equal(typeof registered.body.expiresIn, 'number');
        assert.equal(registered.body.token.split('.').length, 3);
        assert.equal(registered.body.user.password, undefined);
        managerToken = registered.body.token;

        const persisted = JSON.parse(fs.readFileSync(mockDatabasePath, 'utf8'));
        const storedUser = persisted.users.find(user => user.username === 'new.manager');
        assert.match(storedUser.passwordHash, /^scrypt\$/);
        assert.equal(storedUser.password, undefined);
        assert.equal(await verifyPassword('Secure123', storedUser.passwordHash), true);
        assert.equal(persisted.metadata.keepMe, 'migration-proof');
        assert.equal(persisted.players[0].customNote, 'preserve-player-field');

        const duplicate = await request(running.baseUrl, '/api/auth/register', {
            method: 'POST',
            body: { username: 'new.manager', password: 'Secure456' },
        });
        assert.equal(duplicate.status, 409);
        assert.equal(duplicate.body.error.code, 'USERNAME_EXISTS');
    });

    await t.test('login rejects bad credentials and migrates a legacy plaintext password safely', async () => {
        const rejected = await request(running.baseUrl, '/api/auth/login', {
            method: 'POST',
            body: { username: 'legacy1', password: 'wrong-password' },
        });
        assert.equal(rejected.status, 401);
        assert.equal(rejected.body.error.code, 'INVALID_CREDENTIALS');

        const loggedIn = await request(running.baseUrl, '/api/auth/login', {
            method: 'POST',
            body: { username: 'legacy1', password: 'OldPass1' },
        });
        assert.equal(loggedIn.status, 200);
        legacyToken = loggedIn.body.token;

        const persisted = JSON.parse(fs.readFileSync(mockDatabasePath, 'utf8'));
        const migrated = persisted.users.find(user => user.username === 'legacy1');
        assert.equal(migrated.password, undefined);
        assert.match(migrated.passwordHash, /^scrypt\$/);
        assert.equal(migrated.note, 'preserve-user-field');
        assert.equal(persisted.metadata.keepMe, 'migration-proof');
    });

    await t.test('Bearer authentication validates signature and expiry', async () => {
        const unauthorized = await request(running.baseUrl, '/api/players');
        assert.equal(unauthorized.status, 401);
        assert.equal(unauthorized.body.error.code, 'AUTH_REQUIRED');

        const profile = await request(running.baseUrl, '/api/auth/me', { token: managerToken });
        assert.equal(profile.status, 200);
        assert.equal(profile.body.user.username, 'new.manager');

        const tampered = `${managerToken.slice(0, -1)}${managerToken.endsWith('a') ? 'b' : 'a'}`;
        const invalidToken = await request(running.baseUrl, '/api/players', { token: tampered });
        assert.equal(invalidToken.status, 401);
        assert.equal(invalidToken.body.error.code, 'TOKEN_INVALID');

        const signed = signAccessToken(
            { id: 10, username: 'expiry-test', role: 'manager' },
            { now: 1000, expiresInSeconds: 10 },
        );
        assert.equal(verifyAccessToken(signed.token, { now: 1009 }).username, 'expiry-test');
        assert.throws(() => verifyAccessToken(signed.token, { now: 1010 }), /Expired token/);
    });

    await t.test('role authorization allows reads but blocks viewer mutations', async () => {
        const viewerLogin = await request(running.baseUrl, '/api/auth/login', {
            method: 'POST',
            body: { username: 'viewer1', password: 'ViewPass1' },
        });
        assert.equal(viewerLogin.status, 200);

        const list = await request(running.baseUrl, '/api/players', { token: viewerLogin.body.token });
        assert.equal(list.status, 200);

        const create = await request(running.baseUrl, '/api/players', {
            method: 'POST',
            token: viewerLogin.body.token,
            body: { fullName: 'Viewer Cannot Create', position: 'DF', jerseyNumber: 55 },
        });
        assert.equal(create.status, 403);
        assert.equal(create.body.error.code, 'INSUFFICIENT_ROLE');
    });

    await t.test('player CRUD validates, preserves partial updates and handles conflicts/not-found', async () => {
        const invalid = await request(running.baseUrl, '/api/players', {
            method: 'POST',
            token: managerToken,
            body: { fullName: '', position: 'XX', jerseyNumber: -1 },
        });
        assert.equal(invalid.status, 400);
        assert.equal(invalid.body.error.code, 'VALIDATION_ERROR');

        const created = await request(running.baseUrl, '/api/players', {
            method: 'POST',
            token: managerToken,
            body: {
                fullName: 'Nguyễn Văn Test',
                position: 'FW',
                jerseyNumber: 9,
                club: 'FC Test',
                pac: 90,
                sho: 80,
                pas: 70,
                dri: 80,
                def: 40,
                phy: 60,
            },
        });
        assert.equal(created.status, 201);
        assert.equal(created.body.ovr, 70);
        const playerId = created.body.id;

        const fetched = await request(running.baseUrl, `/api/players/${playerId}`, { token: managerToken });
        assert.equal(fetched.status, 200);
        assert.equal(fetched.body.club, 'FC Test');

        const updatedStats = await request(running.baseUrl, `/api/players/${playerId}`, {
            method: 'PUT',
            token: managerToken,
            body: { goals: 3 },
        });
        assert.equal(updatedStats.status, 200);
        assert.equal(updatedStats.body.fullName, 'Nguyễn Văn Test');
        assert.equal(updatedStats.body.club, 'FC Test');
        assert.equal(updatedStats.body.goals, 3);

        const updatedAttribute = await request(running.baseUrl, `/api/players/${playerId}`, {
            method: 'PUT',
            token: managerToken,
            body: { pac: 96 },
        });
        assert.equal(updatedAttribute.status, 200);
        assert.equal(updatedAttribute.body.ovr, 71);

        const conflict = await request(running.baseUrl, `/api/players/${playerId}`, {
            method: 'PUT',
            token: managerToken,
            body: { jerseyNumber: 7 },
        });
        assert.equal(conflict.status, 409);
        assert.equal(conflict.body.error.code, 'JERSEY_NUMBER_EXISTS');

        const search = await request(running.baseUrl, '/api/players?q=Nguy%E1%BB%85n&sort=ovr&order=desc', { token: managerToken });
        assert.equal(search.status, 200);
        assert.equal(search.body.length, 1);

        const removed = await request(running.baseUrl, `/api/players/${playerId}`, {
            method: 'DELETE',
            token: managerToken,
        });
        assert.equal(removed.status, 200);

        const missing = await request(running.baseUrl, `/api/players/${playerId}`, { token: managerToken });
        assert.equal(missing.status, 404);
        assert.equal(missing.body.error.code, 'PLAYER_NOT_FOUND');
    });

    await t.test('AI is authenticated, grounded, bounded and transparently reports provider failures', async () => {
        const invalid = await request(running.baseUrl, '/api/ai/coach', {
            method: 'POST',
            token: legacyToken,
            body: { prompt: '' },
        });
        assert.equal(invalid.status, 400);

        const coached = await request(running.baseUrl, '/api/ai/coach', {
            method: 'POST',
            token: legacyToken,
            body: { prompt: 'Phân tích tuyến giữa.' },
        });
        assert.equal(coached.status, 200);
        assert.equal(coached.body.meta.grounded, true);
        assert.equal(coached.body.meta.model, 'test-grounded-model');
        assert.equal(capturedAiRequest.contents[0].parts[0].text, 'Phân tích tuyến giữa.');
        assert.match(capturedAiRequest.config.systemInstruction, /Legacy Player/);
        assert.match(capturedAiRequest.config.systemInstruction, /Không bịa/);
        assert.ok(capturedAiRequest.config.maxOutputTokens <= 2048);

        const providerFailureApp = createApp({
            aiModel: 'test-grounded-model',
            aiGenerateContent: async () => { throw new Error('sensitive provider detail'); },
        });
        const providerRunning = await listen(providerFailureApp);
        try {
            const failed = await request(providerRunning.baseUrl, '/api/ai/coach', {
                method: 'POST',
                token: legacyToken,
                body: { prompt: 'Tư vấn đội hình.' },
            });
            assert.equal(failed.status, 502);
            assert.equal(failed.body.error.code, 'AI_PROVIDER_ERROR');
            assert.doesNotMatch(JSON.stringify(failed.body), /sensitive provider detail/);
        } finally {
            await close(providerRunning.server);
        }

        const unconfiguredApp = createApp();
        const unconfiguredRunning = await listen(unconfiguredApp);
        try {
            const unavailable = await request(unconfiguredRunning.baseUrl, '/api/ai/coach', {
                method: 'POST',
                token: legacyToken,
                body: { prompt: 'Tư vấn đội hình.' },
            });
            assert.equal(unavailable.status, 503);
            assert.equal(unavailable.body.error.code, 'AI_NOT_CONFIGURED');
            assert.equal(unavailable.body.reply, undefined);
        } finally {
            await close(unconfiguredRunning.server);
        }
    });

    await t.test('optional API key is read only from environment', async () => {
        process.env.SERVER_API_KEY = 'server-test-key';
        try {
            const denied = await request(running.baseUrl, '/api/auth/login', {
                method: 'POST',
                body: { username: 'legacy1', password: 'OldPass1' },
            });
            assert.equal(denied.status, 403);
            assert.equal(denied.body.error.code, 'API_KEY_INVALID');

            const allowed = await request(running.baseUrl, '/api/auth/login', {
                method: 'POST',
                headers: { 'x-api-key': 'server-test-key' },
                body: { username: 'legacy1', password: 'OldPass1' },
            });
            assert.equal(allowed.status, 200);
        } finally {
            delete process.env.SERVER_API_KEY;
        }
    });
});
