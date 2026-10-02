# HƯỚNG DẪN THỰC HIỆN TRÊN ORACLE SQL DEVELOPER
## BÀI TẬP LỚN MÔN: ORACLE DATABASE MANAGEMENT SYSTEM
**Đề tài:** Topic 3 - Develop a database for sports club management software  
**File script tổng hợp dùng để chạy:** `oracle_assignment/ORACLE_SQL_DEVELOPER_FULL.sql`  

---

## 🎯 BẠN CẦN LÀM NHỮNG GÌ TRÊN SQL DEVELOPER?

Trong đề thi của thầy TS. Nguyễn Viết Hùng, phần cơ sở dữ liệu trên Oracle SQL Developer yêu cầu:
1. **Khởi tạo 6 bảng CSDL có ràng buộc toàn vẹn** (PK, FK, Check constraints).
2. **Nạp dữ liệu mẫu phong phú** vào cả 6 bảng để các câu lệnh SELECT có dữ liệu hiển thị.
3. **Biên dịch các đối tượng PL/SQL:** View, 2 Triggers, 1 Package chứa Function (có dùng `IF`) và 2 Procedures (có dùng vòng lặp `FOR LOOP` và `WHILE LOOP`).
4. **Thực thi bộ 20 câu truy vấn SQL** thuộc 4 nhóm (Cơ bản, Lồng nhau, Gom nhóm, Tính toán tổng hợp).
5. **Thực thi phân quyền người dùng (User Management)**: Tạo Role, cấp quyền trên bảng, tạo User.

> **TẤT CẢ 5 YÊU CẦU TRÊN ĐÃ ĐƯỢC TỔNG HỢP TRONG 1 FILE DUY NHẤT:**  
> 👉 `oracle_assignment/ORACLE_SQL_DEVELOPER_FULL.sql`

---

## 🚀 CÁC BƯỚC THỰC HIỆN TRÊN SQL DEVELOPER (MẤT ĐÚNG 1 PHÚT)

### Bước 1: Mở Oracle SQL Developer và Kết nối Database
1. Mở phần mềm **Oracle SQL Developer** trên máy tính của bạn.
2. Ở cột bên trái (thẻ **Connections**), bấm đúp chuột vào kết nối Oracle của bạn (ví dụ: `system` hoặc `fcm_admin` hoặc `xe`).
3. Nhập mật khẩu để mở cửa sổ soạn thảo SQL Worksheet.

### Bước 2: Mở và chạy file script tổng hợp
1. Trên thanh menu của SQL Developer, chọn **File** $\rightarrow$ **Open...**
2. Tìm đến thư mục dự án và chọn file:  
   `c:\Users\Admin\AndroidStudioProjects\FootballClubManagement\oracle_assignment\ORACLE_SQL_DEVELOPER_FULL.sql`
3. Khi nội dung file hiện ra trên cửa sổ SQL Worksheet, bạn chỉ cần bấm phím tắt:  
   👉 **`F5`** (hoặc bấm nút **Run Script** - icon tờ giấy có hình tam giác xanh nhỏ cạnh nút Run lớn).
4. Nhìn xuống cửa sổ **Script Output** bên dưới:  
   - Tất cả 6 bảng sẽ được tạo thành công.
   - Dữ liệu mẫu sẽ được nạp và `COMMIT`.
   - Package PL/SQL và Triggers sẽ được biên dịch (`Compiled`).
   - Cửa sổ sẽ in ra ngay kết quả phân hạng cầu thủ từ thủ tục `pkg_fc_manager.evaluate_squad_fitness`!
   - 20 câu truy vấn SQL sẽ lần lượt in ra kết quả.
   - Các Role và User sẽ được tạo thành công!

---

## 📸 5 BỨC ẢNH "HÁI RA ĐIỂM 10" CẦN CHỤP TRÊN SQL DEVELOPER

Để bài báo cáo hoặc slide thuyết trình của bạn đạt điểm tối đa, hãy chụp lại 5 màn hình này trên SQL Developer:

### 1. Ảnh Sơ đồ quan hệ ERD (Relational Schema)
- **Cách lấy:** Bạn đã tạo được từ Data Modeler trong SQL Developer (chính là hình Figure 1 ở trang 4-5 trong file PDF báo cáo của bạn).

### 2. Ảnh Cây thư mục đối tượng (Object Tree)
- **Cách chụp:** Ở cột **Connections** bên trái, mở rộng cây thư mục:
  - Bấm vào mục **Tables**: Sẽ thấy đủ 6 bảng: `APP_USERS`, `MATCHES`, `MATCH_EVENTS`, `PLAYERS`, `PLAYER_TRAINING`, `TRAINING_SESSIONS`.
  - Bấm vào mục **Packages**: Thấy `PKG_FC_MANAGER`.
  - Bấm vào mục **Triggers**: Thấy `TRG_AFTER_MATCH_EVENT`.
- *Chụp ảnh cột này để chứng minh đã tạo đầy đủ đối tượng CSDL.*

### 3. Ảnh Thực thi thủ tục PL/SQL (Package Output)
- **Cách chụp:** Quét chuột chọn 2 dòng lệnh này rồi bấm **Ctrl + Enter**:
  ```sql
  SET SERVEROUTPUT ON;
  EXEC pkg_fc_manager.evaluate_squad_fitness;
  ```
- Nhìn vào ô **Script Output** bên dưới, hệ thống sẽ in ra:
  ```text
  === BÁO CÁO PHÂN HẠNG CẦU THỦ TOÀN ĐỘI ===
  [*] Nguyễn Quang Hải: Ngôi sao đẳng cấp (Star Player)
  [*] Nguyễn Tiến Linh: Ngôi sao đẳng cấp (Star Player)
  [-] Đoàn Văn Hậu: Đang chấn thương (Cần chăm sóc y tế)
  ...
  ```
- *Chụp ảnh kết quả này làm minh chứng cho mục 2.4 PL/SQL Programming.*

### 4. Ảnh Kết quả chạy câu truy vấn SQL (Query Result)
- **Cách chụp:** Quét chuột chọn câu số 11 (Gom nhóm theo vị trí) rồi bấm **Ctrl + Enter**:
  ```sql
  SELECT position, COUNT(*) AS so_luong, ROUND(AVG(ovr), 1) AS ovr_trung_binh 
  FROM players 
  GROUP BY position 
  ORDER BY so_luong DESC;
  ```
- Cửa sổ **Query Result** bên dưới sẽ hiện ra bảng dữ liệu dạng lưới (Grid View) cực kỳ đẹp với các cột: `POSITION`, `SO_LUONG`, `OVR_TRUNG_BINH`.
- *Chụp ảnh bảng kết quả này làm minh chứng cho mục 2.3 SQL Queries.*

### 5. Ảnh Phân quyền và Tạo User (User Management)
- **Cách chụp:** Quét chọn khối lệnh Phần 5 (tạo `coach_data_entry`, gán quyền và tạo user `coach_bang`) rồi bấm `F5`.
- Cửa sổ Script Output báo:
  - `Role COACH_DATA_ENTRY created.`
  - `Grant succeeded.`
  - `User COACH_BANG created.`
- *Chụp lại để làm minh chứng mục 2.5 User Management.*

---

## 🎥 CÁCH LỒNG SQL DEVELOPER VÀO VIDEO DEMO (NẾU MUỐN QUAY THÊM)

Nếu trong video bạn muốn dành 30 giây để chứng minh toàn bộ dữ liệu trên app Android ăn thẳng từ Oracle Database:
1. Sau khi demo trên máy ảo xong, chuyển cửa sổ màn hình sang **Oracle SQL Developer**.
2. **Lời thoại gợi ý:**
   > *"Và đây là cơ sở dữ liệu Oracle Database 19c được quản lý trên Oracle SQL Developer. Toàn bộ hệ thống gồm 6 bảng chuẩn hóa 3NF. Em xin chạy thử thủ tục PL/SQL trong Package `pkg_fc_manager` để đánh giá thể lực toàn đội (bấm Ctrl+Enter chạy)... kết quả in ra tức thì. Tiếp theo, em chạy câu truy vấn thống kê số lượng cầu thủ theo vị trí (bấm Ctrl+Enter)... Dữ liệu trên ứng dụng di động Android chính là dữ liệu được đồng bộ trực tiếp từ các bảng trong Oracle này."*
3. Giảng viên xem đoạn này sẽ đánh giá bạn hiểu sâu từ Backend CSDL lên tới Frontend Ứng dụng, điểm 10 là chắc chắn trong tay!
