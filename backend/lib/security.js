const crypto = require('crypto');
const { promisify } = require('util');

const scryptAsync = promisify(crypto.scrypt);
const PASSWORD_KEY_LENGTH = 64;
const SCRYPT_PARAMS = Object.freeze({ N: 16384, r: 8, p: 1 });
const TOKEN_ISSUER = 'football-club-management-api';
const TOKEN_AUDIENCE = 'football-club-management-app';

let developmentSecret;
let developmentWarningShown = false;

function tokenSecret() {
    const configured = process.env.TOKEN_SECRET;
    if (configured) {
        if (Buffer.byteLength(configured, 'utf8') < 32) {
            throw new Error('TOKEN_SECRET must contain at least 32 bytes.');
        }
        return configured;
    }

    if (process.env.NODE_ENV === 'production') {
        throw new Error('TOKEN_SECRET is required in production.');
    }

    if (!developmentSecret) developmentSecret = crypto.randomBytes(48).toString('base64url');
    if (!developmentWarningShown && process.env.NODE_ENV !== 'test') {
        console.warn('TOKEN_SECRET is not configured; using an ephemeral development secret. Tokens will expire when the server restarts.');
        developmentWarningShown = true;
    }
    return developmentSecret;
}

async function hashPassword(password) {
    const salt = crypto.randomBytes(16);
    const derived = await scryptAsync(password, salt, PASSWORD_KEY_LENGTH, {
        ...SCRYPT_PARAMS,
        maxmem: 64 * 1024 * 1024,
    });
    return [
        'scrypt',
        SCRYPT_PARAMS.N,
        SCRYPT_PARAMS.r,
        SCRYPT_PARAMS.p,
        salt.toString('base64url'),
        Buffer.from(derived).toString('base64url'),
    ].join('$');
}

async function verifyPassword(password, encodedHash) {
    if (typeof encodedHash !== 'string') return false;
    const parts = encodedHash.split('$');
    if (parts.length !== 6 || parts[0] !== 'scrypt') return false;

    const N = Number(parts[1]);
    const r = Number(parts[2]);
    const p = Number(parts[3]);
    if (N !== SCRYPT_PARAMS.N || r !== SCRYPT_PARAMS.r || p !== SCRYPT_PARAMS.p) return false;

    let salt;
    let expected;
    try {
        salt = Buffer.from(parts[4], 'base64url');
        expected = Buffer.from(parts[5], 'base64url');
    } catch (err) {
        return false;
    }
    if (salt.length < 16 || expected.length !== PASSWORD_KEY_LENGTH) return false;

    const actual = Buffer.from(await scryptAsync(password, salt, expected.length, {
        N,
        r,
        p,
        maxmem: 64 * 1024 * 1024,
    }));
    return crypto.timingSafeEqual(actual, expected);
}

function encodeJson(value) {
    return Buffer.from(JSON.stringify(value), 'utf8').toString('base64url');
}

function signAccessToken(user, options = {}) {
    const now = options.now ?? Math.floor(Date.now() / 1000);
    const configuredTtl = Number(process.env.TOKEN_TTL_SECONDS);
    const defaultTtl = Number.isInteger(configuredTtl) && configuredTtl >= 300 && configuredTtl <= 604800
        ? configuredTtl
        : 86400;
    const expiresIn = options.expiresInSeconds ?? defaultTtl;
    if (!Number.isInteger(expiresIn) || expiresIn < 1 || expiresIn > 604800) {
        throw new Error('Invalid token expiry.');
    }

    const header = encodeJson({ alg: 'HS256', typ: 'JWT' });
    const payload = encodeJson({
        sub: String(user.id),
        username: user.username,
        role: user.role,
        iat: now,
        exp: now + expiresIn,
        iss: TOKEN_ISSUER,
        aud: TOKEN_AUDIENCE,
        jti: crypto.randomUUID(),
    });
    const unsigned = `${header}.${payload}`;
    const signature = crypto.createHmac('sha256', tokenSecret()).update(unsigned).digest('base64url');
    return { token: `${unsigned}.${signature}`, expiresIn };
}

function verifyAccessToken(token, options = {}) {
    if (typeof token !== 'string' || token.length > 4096) throw new Error('Malformed token.');
    const parts = token.split('.');
    if (parts.length !== 3 || parts.some(part => !part)) throw new Error('Malformed token.');

    const unsigned = `${parts[0]}.${parts[1]}`;
    const expected = crypto.createHmac('sha256', tokenSecret()).update(unsigned).digest();
    let supplied;
    try {
        supplied = Buffer.from(parts[2], 'base64url');
    } catch (err) {
        throw new Error('Malformed token.');
    }
    if (supplied.length !== expected.length || !crypto.timingSafeEqual(supplied, expected)) {
        throw new Error('Invalid token signature.');
    }

    let header;
    let payload;
    try {
        header = JSON.parse(Buffer.from(parts[0], 'base64url').toString('utf8'));
        payload = JSON.parse(Buffer.from(parts[1], 'base64url').toString('utf8'));
    } catch (err) {
        throw new Error('Malformed token payload.');
    }

    const now = options.now ?? Math.floor(Date.now() / 1000);
    if (header.alg !== 'HS256' || header.typ !== 'JWT') throw new Error('Unsupported token.');
    if (payload.iss !== TOKEN_ISSUER || payload.aud !== TOKEN_AUDIENCE) throw new Error('Invalid token audience.');
    if (!payload.sub || !payload.username || !payload.role) throw new Error('Incomplete token.');
    if (!Number.isInteger(payload.iat) || !Number.isInteger(payload.exp)) throw new Error('Invalid token dates.');
    if (payload.iat > now + 60 || payload.exp <= now) throw new Error('Expired token.');
    return payload;
}

function safeEqualText(left, right) {
    if (typeof left !== 'string' || typeof right !== 'string') return false;
    const leftDigest = crypto.createHash('sha256').update(left, 'utf8').digest();
    const rightDigest = crypto.createHash('sha256').update(right, 'utf8').digest();
    return crypto.timingSafeEqual(leftDigest, rightDigest);
}

module.exports = {
    hashPassword,
    verifyPassword,
    signAccessToken,
    verifyAccessToken,
    safeEqualText,
};
