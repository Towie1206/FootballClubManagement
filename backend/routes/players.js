const express = require('express');
const oracledb = require('oracledb');
const db = require('../db/database');
const { ApiError, asyncHandler } = require('../lib/errors');
const { validatePlayerPayload, validatePositiveId } = require('../lib/validation');
const { requireAuth, requireRole } = require('../middleware/auth');

const router = express.Router();
const ATTRIBUTE_FIELDS = ['pac', 'sho', 'pas', 'dri', 'def', 'phy'];
const PLAYER_SELECT = `
    SELECT id AS "id", full_name AS "fullName", position AS "position",
           jersey_number AS "jerseyNumber", health_status AS "healthStatus",
           ovr AS "ovr", matches AS "matches", goals AS "goals",
           assists AS "assists", mvp AS "mvp", club AS "club",
           pac AS "pac", sho AS "sho", pas AS "pas", dri AS "dri",
           def AS "def", phy AS "phy"
    FROM players`;

router.use(requireAuth);

function calculateOvr(player) {
    return Math.floor(ATTRIBUTE_FIELDS.reduce((total, field) => total + player[field], 0) / ATTRIBUTE_FIELDS.length);
}

function createPlayerRecord(value) {
    const player = {
        fullName: value.fullName,
        position: value.position,
        jerseyNumber: value.jerseyNumber,
        healthStatus: value.healthStatus ?? 'Fit',
        matches: value.matches ?? 0,
        goals: value.goals ?? 0,
        assists: value.assists ?? 0,
        mvp: value.mvp ?? 0,
        club: value.club ?? 'Tự do',
        pac: value.pac ?? 70,
        sho: value.sho ?? 70,
        pas: value.pas ?? 70,
        dri: value.dri ?? 70,
        def: value.def ?? 70,
        phy: value.phy ?? 70,
    };
    player.ovr = value.ovr ?? calculateOvr(player);
    return player;
}

function nextMockId(players) {
    return players.reduce((maxId, player) => Math.max(maxId, Number(player.id) || 0), 0) + 1;
}

function isUniqueConstraintError(err) {
    return err && (err.errorNum === 1 || String(err.message).includes('ORA-00001'));
}

function assertJerseyAvailable(players, jerseyNumber, exceptId) {
    if (players.some(player => player.jerseyNumber === jerseyNumber && player.id !== exceptId)) {
        throw new ApiError(409, 'JERSEY_NUMBER_EXISTS', 'Số áo đã được sử dụng.');
    }
}

async function getOraclePlayer(id) {
    const result = await db.executeQuery(`${PLAYER_SELECT} WHERE id = :id`, { id });
    return result.rows[0];
}

function validateListQuery(query) {
    const q = typeof query.q === 'string' ? query.q.trim() : '';
    if (q.length > 100) throw new ApiError(400, 'INVALID_QUERY', 'Từ khóa tìm kiếm quá dài.');

    const position = typeof query.position === 'string' ? query.position.toUpperCase() : '';
    if (position && !['GK', 'DF', 'MF', 'FW'].includes(position)) {
        throw new ApiError(400, 'INVALID_QUERY', 'Vị trí lọc không hợp lệ.');
    }

    const sort = query.sort || 'jerseyNumber';
    if (!['jerseyNumber', 'fullName', 'ovr', 'goals'].includes(sort)) {
        throw new ApiError(400, 'INVALID_QUERY', 'Trường sắp xếp không hợp lệ.');
    }
    const order = String(query.order || 'asc').toLowerCase();
    if (!['asc', 'desc'].includes(order)) {
        throw new ApiError(400, 'INVALID_QUERY', 'Chiều sắp xếp không hợp lệ.');
    }
    return { q, position, sort, order };
}

router.get('/', asyncHandler(async (req, res) => {
    const query = validateListQuery(req.query);
    if (db.USE_MOCK_DB) {
        let players = [...db.readLocalDB().players];
        if (query.q) {
            const keyword = query.q.toLocaleLowerCase('vi');
            players = players.filter(player => player.fullName.toLocaleLowerCase('vi').includes(keyword));
        }
        if (query.position) players = players.filter(player => player.position === query.position);
        const direction = query.order === 'asc' ? 1 : -1;
        players.sort((left, right) => {
            const leftValue = left[query.sort];
            const rightValue = right[query.sort];
            if (typeof leftValue === 'string') return leftValue.localeCompare(rightValue, 'vi') * direction;
            return (leftValue - rightValue) * direction;
        });
        return res.json(players.slice(0, 500));
    }

    const filters = [];
    const binds = {};
    if (query.q) {
        filters.push('UPPER(full_name) LIKE UPPER(:keyword)');
        binds.keyword = `%${query.q}%`;
    }
    if (query.position) {
        filters.push('position = :position');
        binds.position = query.position;
    }
    const sortColumns = {
        jerseyNumber: 'jersey_number',
        fullName: 'full_name',
        ovr: 'ovr',
        goals: 'goals',
    };
    const where = filters.length ? ` WHERE ${filters.join(' AND ')}` : '';
    const sql = `${PLAYER_SELECT}${where} ORDER BY ${sortColumns[query.sort]} ${query.order.toUpperCase()} FETCH FIRST 500 ROWS ONLY`;
    const result = await db.executeQuery(sql, binds);
    return res.json(result.rows);
}));

router.get('/:id', asyncHandler(async (req, res) => {
    const id = validatePositiveId(req.params.id);
    const player = db.USE_MOCK_DB
        ? db.readLocalDB().players.find(item => item.id === id)
        : await getOraclePlayer(id);
    if (!player) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
    return res.json(player);
}));

router.post('/', requireRole('manager'), asyncHandler(async (req, res) => {
    const value = validatePlayerPayload(req.body);
    const player = createPlayerRecord(value);

    if (db.USE_MOCK_DB) {
        const created = await db.mutateLocalDB(localDB => {
            assertJerseyAvailable(localDB.players, player.jerseyNumber);
            const record = { id: nextMockId(localDB.players), ...player };
            localDB.players.push(record);
            return record;
        });
        return res.status(201).json(created);
    }

    try {
        const result = await db.executeQuery(
            `INSERT INTO players
                (full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp,
                 club, pac, sho, pas, dri, def, phy)
             VALUES
                (:fullName, :position, :jerseyNumber, :healthStatus, :ovr, :matches, :goals, :assists, :mvp,
                 :club, :pac, :sho, :pas, :dri, :def, :phy)
             RETURNING id INTO :id`,
            {
                ...player,
                id: { type: oracledb.NUMBER, dir: oracledb.BIND_OUT },
            },
            { autoCommit: true },
        );
        return res.status(201).json({ id: result.outBinds.id[0], ...player });
    } catch (err) {
        if (isUniqueConstraintError(err)) {
            throw new ApiError(409, 'JERSEY_NUMBER_EXISTS', 'Số áo đã được sử dụng.');
        }
        throw err;
    }
}));

router.put('/:id', requireRole('manager'), asyncHandler(async (req, res) => {
    const id = validatePositiveId(req.params.id);
    const value = validatePlayerPayload(req.body, { partial: true });
    const attributesChanged = ATTRIBUTE_FIELDS.some(field => Object.prototype.hasOwnProperty.call(value, field));

    if (db.USE_MOCK_DB) {
        const updated = await db.mutateLocalDB(localDB => {
            const index = localDB.players.findIndex(player => player.id === id);
            if (index < 0) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
            const record = { ...localDB.players[index], ...value, id };
            if (attributesChanged) record.ovr = calculateOvr(record);
            assertJerseyAvailable(localDB.players, record.jerseyNumber, id);
            localDB.players[index] = record;
            return record;
        });
        return res.json(updated);
    }

    const existing = await getOraclePlayer(id);
    if (!existing) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
    const player = { ...existing, ...value, id };
    if (attributesChanged) player.ovr = calculateOvr(player);

    try {
        const result = await db.executeQuery(
            `UPDATE players
             SET full_name = :fullName, position = :position, jersey_number = :jerseyNumber,
                 health_status = :healthStatus, ovr = :ovr, matches = :matches,
                 goals = :goals, assists = :assists, mvp = :mvp, club = :club,
                 pac = :pac, sho = :sho, pas = :pas, dri = :dri, def = :def, phy = :phy
             WHERE id = :id`,
            player,
            { autoCommit: true },
        );
        if (result.rowsAffected === 0) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
        return res.json(player);
    } catch (err) {
        if (isUniqueConstraintError(err)) {
            throw new ApiError(409, 'JERSEY_NUMBER_EXISTS', 'Số áo đã được sử dụng.');
        }
        throw err;
    }
}));

router.delete('/:id', requireRole('manager'), asyncHandler(async (req, res) => {
    const id = validatePositiveId(req.params.id);
    if (db.USE_MOCK_DB) {
        await db.mutateLocalDB(localDB => {
            const index = localDB.players.findIndex(player => player.id === id);
            if (index < 0) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
            localDB.players.splice(index, 1);
        });
        return res.json({ message: 'Xóa cầu thủ thành công.', id });
    }

    const result = await db.executeQuery(
        'DELETE FROM players WHERE id = :id',
        { id },
        { autoCommit: true },
    );
    if (result.rowsAffected === 0) throw new ApiError(404, 'PLAYER_NOT_FOUND', 'Không tìm thấy cầu thủ.');
    return res.json({ message: 'Xóa cầu thủ thành công.', id });
}));

module.exports = router;
