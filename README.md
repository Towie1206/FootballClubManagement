# Football Club Management (FC Manager) ⚽

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![NodeJS](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini_AI-8E75B2?style=for-the-badge&logo=google&logoColor=white)

Dự án Siêu ứng dụng quản lý Đội bóng Phủi (Sân 5, 7, 11) được phát triển chuẩn Enterprise. Hệ thống hỗ trợ Bầu sô quản lý toàn diện từ Nhân sự, Tài chính đến Chiến thuật.

## 🌟 Tính năng nổi bật vượt chỉ tiêu (Vượt qua ứng dụng thông thường)

- **🤖 Trợ lý Ảo AI (Gemini 2.5 Flash + RAG):** Tích hợp AI đọc hiểu trực tiếp Data của đội bóng từ Oracle DB để đưa ra tư vấn đội hình, chiến thuật, và phân tích điểm yếu cá nhân hóa.
- **💰 Sổ Quỹ Minh Bạch (Room DB & Async Thread):** Quản lý thu chi bằng hệ thống Database cục bộ, xử lý đa luồng (Background Thread) chuẩn kỹ sư phần mềm, tránh giật lag UI.
- **🔐 Bảo mật Cấp Doanh nghiệp (Enterprise Security):** 
  - Mã hóa mật khẩu/PIN Thủ quỹ bằng thuật toán băm **SHA-256**.
  - Tích hợp **OkHttp Interceptor** tiêm API Key tự động để chặn các luồng Request trái phép lên Server Node.js.
- **🔔 Background Worker & Notifications:** Lên lịch nhắc nhở điểm danh tự động kể cả khi tắt App (sử dụng Android WorkManager).
- **📊 Dashboard Phân Tích Tổng Quan:** Theo dõi dòng tiền, thống kê vua phá lưới và tình hình quân số theo thời gian thực.

## 🏗 Kiến trúc Hệ thống (Architecture)

Mô hình **BFF (Backend For Frontend) / API Gateway**:
1. **Frontend (Android):** Giao diện Material Design, kiến trúc kết hợp Offline-first (Room DB) và Online Sync (Retrofit).
2. **Backend (Node.js/Express):** Đóng vai trò cầu nối, xác thực API Key, xử lý Logic AI và tương tác với Database.
3. **Database (Oracle / Mock):** Lưu trữ tập trung danh sách cầu thủ, thông tin đội bóng. Có cơ chế fallback về Mock DB khi code offline.

## 🚀 Hướng dẫn Cài đặt & Chạy dự án

### 1. Khởi chạy Backend (Node.js)
```bash
cd backend
npm install
# Tạo file .env và nhập GEMINI_API_KEY
node index.js
```

### 2. Khởi chạy Android App
- Mở thư mục gốc bằng **Android Studio**.
- Chờ Gradle Sync hoàn tất.
- Bấm **Run (Shift + F10)** để cài đặt lên Máy ảo (Emulator) hoặc điện thoại thật.
*(Lưu ý: Nếu test trên máy thật, cần trỏ lại IP của máy chủ Node.js trong file `RetrofitClient.java` thay vì `10.0.2.2`).*

## 🛡 License
Phát triển cho đồ án kết thúc môn. Nghiêm cấm sao chép thương mại.
