const express = require('express');
const oracledb = require('oracledb');
const db = require('../db/database');
const { ApiError, asyncHandler } = require('../lib/errors');
const {
    hashPassword,
    verifyPassword,
    signAccessToken,
    safeEqualText,
} = require('../lib/security');
const { validateCredentials } = require('../lib/validation');
const { requireAuth } = require('../middleware/auth');

const router = express.Router();

function publicUser(user) {
    return { id: user.id, username: user.username, role: user.role };
}

function authenticationResponse(user, message) {
    const { token, expiresIn } = signAccessToken(user);
    return { message, token, expiresIn, user: publicUser(user) };
}

function nextMockId(users) {
    return users.reduce((maxId, user) => Math.max(maxId, Number(user.id) || 0), 0) + 1;
}

function isUniqueConstraintError(err) {
    return err && (err.errorNum === 1 || String(err.message).includes('ORA-00001'));
}

async function findOracleUser(username) {
    const result = await db.executeQuery(
        `SELECT id AS "id", username AS "username", password_hash AS "passwordHash", role AS "role"
         FROM app_users
         WHERE LOWER(username) = :username`,
        { username },
    );
    return result.rows[0];
}

router.post('/register', asyncHandler(async (req, res) => {
    const { username, password } = validateCredentials(req.body, { registration: true });
    const passwordHash = await hashPassword(password);
    let user;

    if (db.USE_MOCK_DB) {
        user = await db.mutateLocalDB(localDB => {
            if (localDB.users.some(item => item.username.toLowerCase() === username)) {
                throw new ApiError(409, 'USERNAME_EXISTS', 'Tên đăng nhập đã tồn tại.');
            }
            const created = {
                id: nextMockId(localDB.users),
                username,
                passwordHash,
                role: 'manager',
                createdAt: new Date().toISOString(),
            };
            localDB.users.push(created);
            return created;
        });
    } else {
        try {
            const result = await db.executeQuery(
                `INSERT INTO app_users (username, password_hash, role)
                 VALUES (:username, :passwordHash, 'manager')
                 RETURNING id INTO :id`,
                {
                    username,
                    passwordHash,
                    id: { type: oracledb.NUMBER, dir: oracledb.BIND_OUT },
                },
                { autoCommit: true },
            );
            user = { id: result.outBinds.id[0], username, role: 'manager' };
        } catch (err) {
            if (isUniqueConstraintError(err)) {
                throw new ApiError(409, 'USERNAME_EXISTS', 'Tên đăng nhập đã tồn tại.');
            }
            throw err;
        }
    }

    return res.status(201).json(authenticationResponse(user, 'Đăng ký thành công.'));
}));

router.post('/login', asyncHandler(async (req, res) => {
    const { username, password } = validateCredentials(req.body);
    let user;

    if (db.USE_MOCK_DB) {
        const localDB = db.readLocalDB();
        user = localDB.users.find(item => item.username.toLowerCase() === username);
    } else {
        user = await findOracleUser(username);
    }

    let passwordMatches = false;
    if (user && user.passwordHash) {
        passwordMatches = await verifyPassword(password, user.passwordHash);
    } else if (user && db.USE_MOCK_DB && typeof user.password === 'string') {
        passwordMatches = safeEqualText(password, user.password);
        if (passwordMatches) {
            const upgradedHash = await hashPassword(password);
            await db.mutateLocalDB(localDB => {
                const storedUser = localDB.users.find(item => item.id === user.id);
                if (storedUser && storedUser.password === user.password) {
                    storedUser.passwordHash = upgradedHash;
                    delete storedUser.password;
                    storedUser.passwordMigratedAt = new Date().toISOString();
                }
            });
            user.passwordHash = upgradedHash;
            delete user.password;
        }
    }

    if (!user || !passwordMatches) {
        throw new ApiError(401, 'INVALID_CREDENTIALS', 'Tên đăng nhập hoặc mật khẩu không chính xác.');
    }

    return res.json(authenticationResponse(user, 'Đăng nhập thành công.'));
}));

router.get('/me', requireAuth, (req, res) => {
    res.json({ user: { id: Number(req.auth.sub), username: req.auth.username, role: req.auth.role } });
});

module.exports = router;
