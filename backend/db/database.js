const oracledb = require('oracledb');
require('dotenv').config();
const fs = require('fs');
const path = require('path');

const USE_MOCK_DB = process.env.USE_MOCK_DB === 'true'; // Lấy từ .env, hỗ trợ chuyển đổi sang Oracle
const dbPath = path.join(__dirname, 'db.json');

// Hàm Đọc/Ghi File DB
function readLocalDB() {
    return JSON.parse(fs.readFileSync(dbPath, 'utf8'));
}

function writeLocalDB(data) {
    fs.writeFileSync(dbPath, JSON.stringify(data, null, 2));
}

// Bỏ mảng lưu tạm (mockPlayers) để chuyển sang dùng readLocalDB/writeLocalDB

async function executeQuery(query, binds = [], opts = {}) {
    if (USE_MOCK_DB) {
        console.log("[Mock DB] Query:", query);
        return null; // Handle logic trong route
    }

    let connection;
    try {
        connection = await oracledb.getConnection({
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            connectString: process.env.DB_CONNECTION_STRING,
        });
        const result = await connection.execute(query, binds, { outFormat: oracledb.OUT_FORMAT_OBJECT, ...opts });
        return result;
    } catch (err) {
        console.error("Database Error: ", err);
        throw err;
    } finally {
        if (connection) {
            try {
                await connection.close();
            } catch (err) {
                console.error("Error closing connection: ", err);
            }
        }
    }
}

module.exports = {
    USE_MOCK_DB,
    executeQuery,
    readLocalDB,
    writeLocalDB
};
