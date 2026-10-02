require('dotenv').config();

const express = require('express');
const cors = require('cors');
const db = require('./db/database');
const authRoutes = require('./routes/auth');
const playerRoutes = require('./routes/players');
const createAiRouter = require('./routes/ai');
const { optionalApiKey } = require('./middleware/auth');
const { notFoundHandler, errorHandler } = require('./lib/errors');

function corsConfiguration() {
    const configuredOrigins = String(process.env.CORS_ORIGINS || '')
        .split(',')
        .map(value => value.trim())
        .filter(Boolean);
    if (configuredOrigins.length === 0) return {};

    return {
        origin(origin, callback) {
            if (!origin || configuredOrigins.includes(origin)) return callback(null, true);
            return callback(null, false);
        },
    };
}

function createApp(options = {}) {
    const app = express();
    app.disable('x-powered-by');
    app.use(cors(corsConfiguration()));
    app.use(express.json({ limit: '64kb' }));

    app.get('/', (req, res) => {
        res.json({
            message: 'FC Manager API is running',
            status: 'ok',
            database: db.USE_MOCK_DB ? 'mock' : 'oracle',
        });
    });

    app.use('/api', optionalApiKey);
    app.use('/api/auth', authRoutes);
    app.use('/api/players', playerRoutes);
    app.use('/api/ai', createAiRouter({
        generateContent: options.aiGenerateContent,
        model: options.aiModel,
    }));

    app.use(notFoundHandler);
    app.use(errorHandler);
    return app;
}

function startServer() {
    const app = createApp();
    const port = Number(process.env.PORT) || 3000;
    return app.listen(port, () => {
        console.log(`FC Manager API listening on port ${port} (${db.USE_MOCK_DB ? 'mock' : 'oracle'} database)`);
    });
}

if (require.main === module) startServer();

module.exports = createApp();
module.exports.createApp = createApp;
module.exports.startServer = startServer;
