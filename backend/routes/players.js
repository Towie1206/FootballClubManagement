const express = require('express');
const router = express.Router();
const db = require('../db/database');

// GET: Lấy danh sách cầu thủ (Thẻ cầu thủ OVR)
router.get('/', async (req, res) => {
    if (db.USE_MOCK_DB) {
        const localDB = db.readLocalDB();
        return res.json(localDB.players);
    }
    
    try {
        const result = await db.executeQuery(`SELECT * FROM Players ORDER BY jersey_number ASC`);
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// POST: Thêm cầu thủ mới
router.post('/', async (req, res) => {
    const { fullName, position, jerseyNumber } = req.body;
    
    if (db.USE_MOCK_DB) {
        const localDB = db.readLocalDB();
        const nextId = localDB.players.length > 0 ? Math.max(...localDB.players.map(p => p.id)) + 1 : 1;
        
        const newPlayer = {
            id: nextId,
            fullName,
            position,
            jerseyNumber,
            healthStatus: 'Fit',
            ovr: 75,
            goals: 0,
            mvp: 0
        };
        localDB.players.push(newPlayer);
        db.writeLocalDB(localDB);
        return res.json(newPlayer);
    }
    
    try {
        const query = `
            INSERT INTO Players (full_name, position, jersey_number, health_status, ovr, goals, mvp)
            VALUES (:fullName, :position, :jerseyNumber, 'Fit', 75, 0, 0)
            RETURNING id, full_name, position, jersey_number, health_status, ovr, goals, mvp INTO :id, :fn, :pos, :jn, :hs, :o, :g, :m
        `;
        const binds = {
            fullName: fullName,
            position: position,
            jerseyNumber: jerseyNumber,
            id: { type: require('oracledb').NUMBER, dir: require('oracledb').BIND_OUT },
            fn: { type: require('oracledb').STRING, dir: require('oracledb').BIND_OUT },
            pos: { type: require('oracledb').STRING, dir: require('oracledb').BIND_OUT },
            jn: { type: require('oracledb').NUMBER, dir: require('oracledb').BIND_OUT },
            hs: { type: require('oracledb').STRING, dir: require('oracledb').BIND_OUT },
            o: { type: require('oracledb').NUMBER, dir: require('oracledb').BIND_OUT },
            g: { type: require('oracledb').NUMBER, dir: require('oracledb').BIND_OUT },
            m: { type: require('oracledb').NUMBER, dir: require('oracledb').BIND_OUT }
        };
        const result = await db.executeQuery(query, binds, { autoCommit: true });
        
        const outBinds = result.outBinds;
        res.json({
            id: outBinds.id[0],
            fullName: outBinds.fn[0],
            position: outBinds.pos[0],
            jerseyNumber: outBinds.jn[0],
            healthStatus: outBinds.hs[0],
            ovr: outBinds.o[0],
            goals: outBinds.g[0],
            mvp: outBinds.m[0]
        });
    } catch (err) {
        res.status(500).json({ error: "Lỗi thêm vào Oracle DB: " + err.message });
    }
});

// PUT: Sửa cầu thủ
router.put('/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    const { fullName, position, jerseyNumber, healthStatus, ovr, goals, mvp } = req.body;
    
    if (db.USE_MOCK_DB) {
        const localDB = db.readLocalDB();
        const playerIndex = localDB.players.findIndex(p => p.id == id);
        if (playerIndex > -1) {
            localDB.players[playerIndex] = { ...localDB.players[playerIndex], fullName, position, jerseyNumber, healthStatus, ovr, goals, mvp };
            db.writeLocalDB(localDB);
            return res.json(localDB.players[playerIndex]);
        }
        return res.status(404).json({ error: "Không tìm thấy cầu thủ" });
    }
    
    try {
        const query = `
            UPDATE Players 
            SET full_name = :fullName, position = :position, jersey_number = :jerseyNumber, 
                health_status = :healthStatus, ovr = :ovr, goals = :goals, mvp = :mvp
            WHERE id = :id
        `;
        const binds = { id, fullName, position, jerseyNumber, healthStatus, ovr, goals, mvp };
        const result = await db.executeQuery(query, binds, { autoCommit: true });
        
        if (result.rowsAffected === 0) {
            return res.status(404).json({ error: "Không tìm thấy cầu thủ" });
        }
        res.json({ id, fullName, position, jerseyNumber, healthStatus, ovr, goals, mvp });
    } catch (err) {
        res.status(500).json({ error: "Lỗi cập nhật Oracle DB: " + err.message });
    }
});

// DELETE: Xóa cầu thủ
router.delete('/:id', async (req, res) => {
    const id = req.params.id;
    if (db.USE_MOCK_DB) {
        const localDB = db.readLocalDB();
        localDB.players = localDB.players.filter(p => p.id != id);
        db.writeLocalDB(localDB);
        return res.json({ message: "Xóa thành công" });
    }
    
    try {
        const query = `DELETE FROM Players WHERE id = :id`;
        await db.executeQuery(query, [id], { autoCommit: true });
        res.json({ message: "Xóa thành công khỏi Oracle DB" });
    } catch (err) {
        res.status(500).json({ error: "Lỗi xóa Oracle DB: " + err.message });
    }
});

module.exports = router;
