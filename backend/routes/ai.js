const express = require('express');
const router = express.Router();
const { GoogleGenAI } = require('@google/genai');
const db = require('../db/database');
require('dotenv').config();

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

router.post('/coach', async (req, res) => {
    const { prompt } = req.body;
    if (!prompt) return res.status(400).json({ error: "Prompt is required" });

    try {
        let squadContext = "";
        
        if (db.USE_MOCK_DB) {
            const localDB = db.readLocalDB();
            squadContext = JSON.stringify(localDB.players);
        } else {
            const result = await db.executeQuery(`SELECT * FROM Players ORDER BY jersey_number ASC`);
            squadContext = JSON.stringify(result.rows);
        }
        
        const systemInstruction = `
            Bạn là "Trợ lý Bầu sô" - một chuyên gia chiến thuật cho đội bóng đá phủi (sân 5, sân 7, sân 11) tại Việt Nam.
            Dữ liệu đội bóng hiện tại của bạn: ${squadContext}. (Các chỉ số: OVR là điểm trung bình, goals là bàn thắng, mvp là số lần hay nhất trận, healthStatus là thể lực).
            
            QUY TẮC TƯ VẤN (TUYỆT ĐỐI TUÂN THỦ):
            1. Dựa CHÍNH XÁC vào danh sách cầu thủ ở trên. Nếu bầu sô bảo "xếp đội hình sân 7", hãy chọn ra đúng 7 cái tên tốt nhất dựa vào OVR và Vị trí (VD: 1 GK, 2 DF, 3 MF, 1 FW) và sơ đồ (ví dụ 2-3-1 hoặc 3-2-1).
            2. Xưng hô "mình" và "anh em" hoặc "bầu sô", giọng điệu cực kỳ dân dã, bóng đá phủi (như trên web phui.id.vn).
            3. Nhắc nhở thu quỹ, điểm danh trước trận nếu được hỏi.
            4. Phân tích điểm mạnh yếu của đội dựa trên OVR.
            5. Đưa ra format dễ đọc, có dùng bullet points và icon bóng đá.
        `;

        const fullPrompt = `${systemInstruction}\n\nĐội trưởng hỏi: ${prompt}`;

        const response = await ai.models.generateContent({
            model: 'gemini-2.5-flash',
            contents: fullPrompt,
        });

        res.json({ reply: response.text });
    } catch (err) {
        res.status(500).json({ error: "Lỗi kết nối AI: " + err.message });
    }
});

module.exports = router;
