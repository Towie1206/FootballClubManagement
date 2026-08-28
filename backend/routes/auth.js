const express = require('express');
const router = express.Router();
const db = require('../db/database');

// POST: API Đăng ký
router.post('/register', (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) return res.status(400).json({ error: "Vui lòng nhập đầy đủ thông tin" });
    
    const localDB = db.readLocalDB();
    const exists = localDB.users.find(u => u.username === username);
    if (exists) return res.status(400).json({ error: "Tên đăng nhập đã tồn tại!" });

    const newUser = { id: Date.now(), username, password, role: 'manager' };
    localDB.users.push(newUser);
    db.writeLocalDB(localDB);
    
    return res.json({ message: "Đăng ký thành công", user: newUser });
});

// POST: API Đăng nhập thật
router.post('/login', (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ error: "Vui lòng nhập tài khoản và mật khẩu" });
    }

    const localDB = db.readLocalDB();
    const user = localDB.users.find(u => u.username === username && u.password === password);

    if (user) {
        return res.json({
            message: "Đăng nhập thành công",
            token: "JWT_FAKE_TOKEN_" + user.id + "_" + Date.now(),
            user: {
                id: user.id,
                username: user.username,
                role: user.role
            }
        });
    } else {
        return res.status(401).json({ error: "Tên đăng nhập hoặc mật khẩu không chính xác!" });
    }
});

module.exports = router;
