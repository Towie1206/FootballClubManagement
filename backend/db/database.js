const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const oracledb = require('oracledb');

const USE_MOCK_DB = process.env.USE_MOCK_DB === 'true';
const dbPath = process.env.MOCK_DB_PATH
    ? path.resolve(process.env.MOCK_DB_PATH)
    : path.join(__dirname, 'db.json');

function finiteInteger(value, fallback) {
    return Number.isInteger(value) ? value : fallback;
}

function normalizePlayer(player, index) {
    const normalized = { ...player };
    normalized.id = finiteInteger(player.id, index + 1);
    normalized.fullName = typeof player.fullName === 'string' ? player.fullName : `Cầu thủ ${normalized.id}`;
    normalized.position = typeof player.position === 'string' ? player.position : 'MF';
    normalized.jerseyNumber = finiteInteger(player.jerseyNumber, 0);
    normalized.healthStatus = typeof player.healthStatus === 'string' ? player.healthStatus : 'Fit';
    normalized.matches = finiteInteger(player.matches, 0);
    normalized.goals = finiteInteger(player.goals, 0);
    normalized.assists = finiteInteger(player.assists, 0);
    normalized.mvp = finiteInteger(player.mvp, 0);
    normalized.club = typeof player.club === 'string' ? player.club : 'Tự do';
    for (const field of ['pac', 'sho', 'pas', 'dri', 'def', 'phy']) {
        normalized[field] = finiteInteger(player[field], 70);
    }
    normalized.ovr = finiteInteger(
        player.ovr,
        Math.floor((normalized.pac + normalized.sho + normalized.pas + normalized.dri + normalized.def + normalized.phy) / 6),
    );
    return normalized;
}

function normalizeUser(user, index) {
    const normalized = { ...user };
    normalized.id = finiteInteger(user.id, index + 1);
    normalized.username = typeof user.username === 'string' ? user.username.trim().toLowerCase() : `user${normalized.id}`;
    normalized.role = typeof user.role === 'string' ? user.role : 'manager';
    return normalized;
}

function normalizeLocalDB(input) {
    const source = input && typeof input === 'object' && !Array.isArray(input) ? input : {};
    return {
        ...source,
        users: Array.isArray(source.users) ? source.users.map(normalizeUser) : [],
        players: Array.isArray(source.players) ? source.players.map(normalizePlayer) : [],
    };
}

function atomicWrite(data) {
    fs.mkdirSync(path.dirname(dbPath), { recursive: true });
    const temporaryPath = `${dbPath}.${process.pid}.${crypto.randomUUID()}.tmp`;
    fs.writeFileSync(temporaryPath, `${JSON.stringify(data, null, 2)}\n`, { encoding: 'utf8', mode: 0o600 });
    try {
        fs.renameSync(temporaryPath, dbPath);
    } finally {
        if (fs.existsSync(temporaryPath)) fs.unlinkSync(temporaryPath);
    }
}

function readLocalDB() {
    let source = {};
    if (fs.existsSync(dbPath)) {
        const raw = fs.readFileSync(dbPath, 'utf8');
        source = raw.trim() ? JSON.parse(raw) : {};
    }
    const normalized = normalizeLocalDB(source);
    if (JSON.stringify(source) !== JSON.stringify(normalized)) atomicWrite(normalized);
    return normalized;
}

function writeLocalDB(data) {
    atomicWrite(normalizeLocalDB(data));
}

let mutationQueue = Promise.resolve();
function mutateLocalDB(mutator) {
    const operation = mutationQueue.then(async () => {
        const data = readLocalDB();
        const result = await mutator(data);
        writeLocalDB(data);
        return result;
    });
    mutationQueue = operation.catch(() => undefined);
    return operation;
}

async function executeQuery(query, binds = [], opts = {}) {
    if (USE_MOCK_DB) throw new Error('executeQuery is unavailable in mock database mode.');

    let connection;
    try {
        connection = await oracledb.getConnection({
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            connectString: process.env.DB_CONNECTION_STRING,
        });
        return await connection.execute(query, binds, {
            outFormat: oracledb.OUT_FORMAT_OBJECT,
            ...opts,
        });
    } finally {
        if (connection) await connection.close();
    }
}

module.exports = {
    USE_MOCK_DB,
    executeQuery,
    readLocalDB,
    writeLocalDB,
    mutateLocalDB,
    normalizeLocalDB,
};
