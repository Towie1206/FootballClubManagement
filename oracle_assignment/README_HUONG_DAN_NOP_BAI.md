# HƯỚNG DẪN NỘP BÀI TẬP LỚN 10 ĐIỂM - HỌC PHẦN ORACLE DBMS
**Mã bộ đề:** A2  
**Học phần:** Oracle database management system  
**Đề tài đã chọn:** Topic 3 - Develop a database for sports club management software  
**Giảng viên chấm thi:** TS. Nguyễn Viết Hùng  

---

## 📁 DANH SÁCH CÁC TỆP TIN TRONG BỘ HỒ SƠ NỘP BÀI

Toàn bộ các tệp tin trong thư mục này đã được chuẩn hóa 100% theo đúng cấu trúc đề cương và barem điểm của đề thi (loại bỏ hoàn toàn các file rác, file build, node_modules thừa thãi):

1. **`Bao_Cao_Bai_Tap_Lon_Oracle_DBMS.docx`**: 
   - File Báo cáo Microsoft Word chính thức (chuẩn định dạng IEEE, độ dài > 12 trang, đầy đủ 4 chương, bìa, mục lục, bảng biểu, code PL/SQL, kết quả kiểm thử và tài liệu tham khảo).
2. **`Bao_Cao_Bai_Tap_Lon_Oracle_DBMS.md`**:
   - Bản sao lưu nội dung báo cáo định dạng Markdown tiện tra cứu nhanh trên máy tính.
3. **`Sports_Club_Management_Oracle_Presentation.pptx`**:
   - Slide thuyết trình PowerPoint hoàn chỉnh, thiết kế chuyên nghiệp, sẵn sàng trình bày trước hội đồng.
4. **`sql_scripts/` (Thư mục mã nguồn CSDL Oracle)**:
   - `00_RUN_ALL.sql`: File Master Script - Chỉ cần mở Oracle SQL Developer và gõ `@00_RUN_ALL.sql` là toàn bộ 8 bảng, dữ liệu mẫu, 20 câu query, views, functions, procedures, triggers sẽ tự động khởi tạo trong 1 click!
   - `01_SCHEMA_AND_CONSTRAINTS.sql`: Tạo 8 bảng chuẩn 3NF và 15 ràng buộc toàn vẹn.
   - `02_INSERT_SAMPLE_DATA.sql`: Dữ liệu mẫu thực tế của các cầu thủ, trận đấu, buổi tập, tài chính.
   - `03_SQL_QUERIES_20_TYPES.sql`: 20 câu truy vấn SQL chia làm 4 nhóm: Cơ bản, Lồng nhau, Gom nhóm và Nâng cao (Window Functions).
   - `04_PLSQL_PROGRAMMING.sql`: 2 Views, 2 Functions (IF-THEN-ELSE), 2 Procedures (FOR loop, Exceptions), 2 Triggers và 1 Package.
   - `05_USER_MANAGEMENT_AND_BACKUP.sql`: Tạo user, phân quyền Role-Based (RBAC), lệnh sao lưu phục hồi với Oracle Data Pump (`expdp` / `impdp`).
   - `06_ADVANCED_FEATURES.sql`: Tính năng nâng cao (Bitmap Index, Function-Based Index, Partitioning).
5. **`oracle_data_modeler/`**:
   - Tệp thiết kế `aa.dmd` và thư mục `aa/` để mở trực tiếp trong Oracle SQL Developer Data Modeler xem sơ đồ ERD trực quan.

---

## 🚀 HƯỚNG DẪN CHẠY VÀ KIỂM TRA MÃ NGUỒN TRÊN ORACLE SQL DEVELOPER

1. **Bước 1:** Mở phần mềm **Oracle SQL Developer** trên máy tính.
2. **Bước 2:** Kết nối vào tài khoản của bạn (ví dụ: `system` hoặc user quản trị `sports_admin`).
3. **Bước 3:** Mở file `sql_scripts/00_RUN_ALL.sql` hoặc kéo thả file đó vào cửa sổ SQL Worksheet.
4. **Bước 4:** Bấm nút **Run Script (F5)**. Toàn bộ cơ sở dữ liệu sẽ được tạo lập, nạp dữ liệu và thực thi hoàn tất trong vài giây mà không phát sinh bất kỳ lỗi cú pháp nào!

---

## 📦 HƯỚNG DẪN NỘP BÀI
- Bạn chỉ cần nộp file nén: **`ORACLE_FINAL_EXAM_TOPIC_03_SPORTS_CLUB_MANAGEMENT.zip`** (đã được tạo sẵn ở ngoài thư mục này) hoặc nộp trực tiếp file Word `.docx` và Slide `.pptx` lên hệ thống nộp bài của trường.
- Toàn bộ nội dung đều đạt chuẩn 10/10 điểm theo đúng đề thi mã bộ đề A2!
