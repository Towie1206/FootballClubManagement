const express = require('express');
const cors = require('cors');
require('dotenv').config();

const playerRoutes = require('./routes/players');
const aiRoutes = require('./routes/ai');
const authRoutes = require('./routes/auth');

const app = express();
app.use(cors());
app.use(express.json());

// Basic health check route
app.get('/', (req, res) => {
    res.json({ message: "FC Manager API is running", mockMode: process.env.USE_MOCK_DB === 'true' });
});

// THÊM CƠ CHẾ BẢO MẬT: Bức tường lửa (API Key Guard)
app.use((req, res, next) => {
    const apiKey = req.header('x-api-key');
    if (!apiKey || apiKey !== 'phui-secret-2026') {
        return res.status(403).json({ error: "FORBIDDEN: Thiếu hoặc sai API Key. Truy cập bị từ chối!" });
    }
    next();
});

app.use('/api/players', playerRoutes);
app.use('/api/ai', aiRoutes);
app.use('/api/auth', authRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
