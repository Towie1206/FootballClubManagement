# KỊCH BẢN QUAY VIDEO DEMO BÁO CÁO BÀI TẬP LỚN
## HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
**Đề tài:** Topic 3 - Develop a database for sports club management software  
**Thời lượng video khuyến nghị:** 2 phút 30 giây đến 3 phút 30 giây  
**Độ phân giải:** Full HD (1080p), âm thanh rõ ràng, không lẫn tạp âm  

---

## 🛠️ CÔNG TÁC CHUẨN BỊ TRƯỚC KHI BẤM NÚT QUAY

1. **Khởi động Máy ảo Android:**
   - Mở Android Studio, chạy máy ảo Pixel / Samsung.
   - Bấm nút **Run (Tam giác xanh ▶️)** trên Android Studio để cài đặt và mở app `Football Club Management` lên.
2. **Khởi động Server Backend (Tùy chọn):**
   - Mở terminal chạy `node index.js` trong thư mục `backend/` (hoặc dùng chế độ `QUICK DEMO SIGN IN (OFFLINE)` có sẵn dữ liệu 10 cầu thủ ngôi sao).
3. **Mở sẵn Oracle SQL Developer:**
   - Mở sẵn cửa sổ Oracle SQL Developer để cuối video chuyển sang minh chứng CSDL thật.
4. **Phần mềm quay màn hình đề xuất:**
   - Dùng tính năng quay màn hình có sẵn trên Windows bằng phím tắt **`Windows + Alt + R`** (hoặc `Windows + G` / OBS Studio).

---

## ⏱️ KỊCH BẢN CHI TIẾT TỪNG PHÂN CẢNH (CÓ LỜI THOẠI)

### PHÂN CẢNH 1: MỞ ĐẦU & ĐĂNG NHẬP HỆ THỐNG (0:00 - 0:25)
* **Thao tác trên màn hình:**
  1. Màn hình máy ảo đang ở giao diện **SIGN IN**.
  2. Bấm vào nút màu xanh: **`⚡ QUICK DEMO SIGN IN (OFFLINE)`** (hoặc gõ `admin` / `123456` bấm `SIGN IN`).
  3. App chuyển mượt mà vào màn hình chính **Squad Roster**.
* **Lời thoại người thuyết minh:**
  > *"Kính chào thầy Nguyễn Viết Hùng và các bạn. Em là [Tên bạn], đại diện nhóm nghiên cứu đề tài Topic 3: Phát triển cơ sở dữ liệu cho phần mềm quản lý câu lạc bộ thể thao. Hôm nay em xin phép quay video thực nghiệm để minh chứng đầy đủ 4 nhóm chức năng ứng dụng cốt lõi theo đúng yêu cầu của đề thi kết thúc học phần. Đầu tiên, đây là màn hình đăng nhập có xác thực tài khoản và phân quyền người dùng kết nối trực tiếp với cơ sở dữ liệu Oracle."*

---

### PHÂN CẢNH 2: CHỨC NĂNG 1 - THÊM, SỬA, XÓA CÓ KIỂM TRA RÀNG BUỘC (0:25 - 1:10)
*(Minh chứng mục 2.1: Add, edit, and delete data with constraint checking)*

* **Thao tác 2A: Kiểm tra ràng buộc toàn vẹn (Constraint Checking):**
  1. Ở góc dưới màn hình, bấm vào nút dấu cộng màu xanh **`+`**.
  2. Màn hình **NEW PLAYER SIGNING** hiện ra.
  3. Cố tình không nhập tên, hoặc nhập chỉ số tốc độ `150` (vượt quá 100), rồi bấm nút **`SAVE PLAYER`**.
  4. Màn hình lập tức hiện viền đỏ báo lỗi: *"Player name must be between 2 and 100 characters"* và *"PAC must be between 0 and 100"*.
* **Lời thoại:**
  > *"Chức năng thứ nhất: Thêm, sửa, xóa dữ liệu có kiểm tra ràng buộc toàn vẹn. Khi em cố tình để trống tên cầu thủ hoặc nhập chỉ số vượt quá 100, hệ thống lập tức bắt lỗi ràng buộc, không cho phép gửi dữ liệu rác về phía máy chủ Oracle."*

* **Thao tác 2B: Thêm cầu thủ hợp lệ (Add Data):**
  1. Nhập Họ và tên: `Nguyen Van A`.
  2. Số áo: `9`.
  3. Vị trí thi đấu: Chọn `FW (Forward)`.
  4. Bấm nút **`🎲 RANDOM STATS`** để hệ thống tự điền bộ chỉ số FIFA hợp lệ.
  5. Bấm **`SAVE PLAYER`**.
  6. Màn hình tự đóng lại, cầu thủ `Nguyen Van A` lập tức xuất hiện ngay đầu danh sách.
* **Lời thoại:**
  > *"Bây giờ em nhập một cầu thủ hợp lệ là tiền đạo Nguyen Van A số áo 9, bấm Save Player. Cầu thủ đã được insert thành công vào bảng PLAYERS trên Oracle và hiển thị ngay trên giao diện."*

* **Thao tác 2C: Xem chi tiết, Sửa và Xóa (Edit & Delete):**
  1. Bấm vào thẻ cầu thủ `Nguyen Van A` vừa tạo để mở màn hình **PLAYER PROFILE**.
  2. Chỉ vào **Biểu đồ Radar đa giác (SKILL ATTRIBUTES RADAR)** đang hiển thị 6 chỉ số: PAC, SHO, PAS, DRI, DEF, PHY.
  3. Bấm nút **`EDIT PROFILE`**, đổi số áo từ `9` thành `10`, bấm Save $\rightarrow$ Số áo trên hồ sơ đổi thành `#10`.
  4. Bấm nút **`DELETE PLAYER`** $\rightarrow$ Hộp thoại xác nhận hiện lên: *"Are you sure you want to delete player Nguyen Van A?"* $\rightarrow$ Bấm **Delete** $\rightarrow$ Cầu thủ biến mất khỏi danh sách an toàn nhờ ràng buộc `ON DELETE CASCADE`.
* **Lời thoại:**
  > *"Tại màn hình chi tiết, các chỉ số thể chất được trực quan hóa bằng biểu đồ Radar 6 cánh. Khi em bấm Edit Profile đổi số áo sang số 10, hệ thống lập tức cập nhật. Chức năng Xóa cũng có hộp thoại xác nhận an toàn trước khi thực thi lệnh DELETE trên CSDL."*

---

### PHÂN CẢNH 3: CHỨC NĂNG 2 - TÌM KIẾM NHANH VÀ LỌC / SẮP XẾP NÂNG CAO (1:10 - 1:45)
*(Minh chứng mục 2.1: Quick and advanced search)*

* **Thao tác 3A: Tìm kiếm nhanh (Quick Search):**
  1. Tại màn hình danh sách, bấm vào thanh tìm kiếm `🔍 Search player...`.
  2. Gõ chữ: `Linh` hoặc `Hai`.
  3. Danh sách lọc tự động trong tích tắc chỉ còn các cầu thủ chứa từ khóa. Xóa chữ đi thì danh sách quay lại đầy đủ 10 cầu thủ.
* **Lời thoại:**
  > *"Chức năng thứ hai: Tìm kiếm nhanh và nâng cao. Tính năng tìm kiếm nhanh theo tên giúp ban huấn luyện tra cứu hồ sơ cầu thủ ngay tức thì theo thời gian thực."*

* **Thao tác 3B: Lọc vị trí & Hộp thoại sắp xếp đa tiêu chí (Advanced Search & Sorting Dialog):**
  1. Bấm vào nút lọc **`FW`** $\rightarrow$ Chỉ hiện danh sách các Tiền đạo.
  2. Bấm nút lọc **`MF`** $\rightarrow$ Chỉ hiện các Tiền vệ.
  3. Bấm nút lọc **`DF`** $\rightarrow$ Chỉ hiện các Hậu vệ.
  4. Bấm lại nút **`ALL`**.
  5. **Bấm vào nút `📋 Position ▾`**: Hộp thoại modal **`↕ Sort Squad Roster`** hiện lên giữa màn hình.
  6. Chọn tùy chọn **`⭐ Overall Rating (OVR)`** $\rightarrow$ Bấm nút **`APPLY SORT`** $\rightarrow$ Danh sách lập tức đảo vị trí sắp xếp cầu thủ có điểm năng lực từ cao xuống thấp (Hoàng Đức 85 OVR đứng đầu).
* **Lời thoại:**
  > *"Đối với tìm kiếm nâng cao, huấn luyện viên có thể lọc chính xác theo từng vị trí trên sân như FW, MF, DF, GK. Đặc biệt, khi bấm vào nút sắp xếp, hộp thoại Modal cho phép ban huấn luyện sắp xếp đội hình đa tiêu chí: theo Vị trí chiến thuật, theo chỉ số OVR hay theo Vua phá lưới để đưa ra đấu pháp phù hợp."*

---

### PHÂN CẢNH 4: CHỨC NĂNG 3 - QUY TRÌNH NGHIỆP VỤ BÓNG ĐÁ (1:45 - 2:20)
*(Minh chứng mục 2.1: Implement business-process functions)*

* **Thao tác trên màn hình:**
  1. Chọn một cầu thủ trong danh sách (Ví dụ: bấm vào `Nguyễn Quang Hải`).
  2. Mở màn hình Player Profile, nhìn vào thông số thi đấu: Matches: `7`, Goals: `11`, Assists: `18`, MVP: `4`.
  3. Bấm vào nút màu xanh nổi bật: **`⚽ RECORD MATCH PERFORMANCE`**.
  4. Hộp thoại nghiệp vụ hiện lên:
     - Goals scored: Nhập `2`.
     - Assists: Nhập `1`.
     - Tích chọn: `⭐ MVP Award`.
     - Condition: Chọn `✔ Match Fit`.
  5. Bấm nút **`CONFIRM`**.
  6. Toast thông báo: *"✔ Match performance recorded successfully!"*.
  7. Nhìn vào thẻ thành tích: Matches tăng từ `7` lên `8`, Goals tăng lên `13`, Assists lên `19`, MVP lên `5`!
* **Lời thoại:**
  > *"Chức năng thứ ba: Thực hiện quy trình nghiệp vụ câu lạc bộ thể thao. Sau mỗi vòng đấu, ban huấn luyện sẽ thực hiện nghiệp vụ 'Ghi nhận kết quả trận đấu' cho cầu thủ. Em vừa ghi nhận cầu thủ ghi thêm 2 bàn thắng, 1 kiến tạo và đoạt giải MVP. Khi bấm Confirm, quy trình nghiệp vụ tự động tăng số trận đấu lên 1, cộng dồn số bàn thắng và cập nhật trạng thái sức khỏe trực tiếp vào cơ sở dữ liệu Oracle."*

---

### PHÂN CẢNH 5: CHỨC NĂNG 4 - BÁO CÁO & THỐNG KÊ TỔNG HỢP (2:20 - 2:45)
*(Minh chứng mục 2.1: Data aggregation, reports, and statistics)*

* **Thao tác trên màn hình:**
  1. Nhìn xuống thanh điều hướng đáy, bấm vào tab thứ hai: **`Statistics`**.
  2. Màn hình **REPORTS & STATISTICS** xuất hiện cực kỳ chuyên nghiệp với các thẻ số liệu:
     - **SQUAD OVERVIEW:** Chỉ vào ô *Squad Size: 10*, *Avg OVR: 81.9*, *Total Goals: 46*.
     - **STAR PERFORMERS:** Chỉ vào *Top Scorer* (Nguyễn Tiến Linh) và *Highest OVR* (Nguyễn Hoàng Đức 85 OVR).
     - **POSITIONAL DISTRIBUTION:** Chỉ vào 4 ô số lượng *Attackers: 2*, *Midfield: 3*, *Defenders: 4*, *Keepers: 1*.
     - **SQUAD AVAILABILITY REPORT:** Chỉ vào *Fit: 9* và *Injured: 1* (Đoàn Văn Hậu).
* **Lời thoại:**
  > *"Chức năng thứ tư: Tổng hợp dữ liệu, báo cáo và thống kê. Màn hình Reports & Statistics tự động truy vấn tổng hợp từ các bảng trong Oracle để đưa ra cái nhìn toàn diện: tổng quân số 10 cầu thủ, điểm OVR trung bình 81.9, tổng số bàn thắng, vinh danh Vua phá lưới và Cầu thủ hay nhất, thống kê phân bổ số lượng theo từng vị trí chiến thuật và báo cáo tỷ lệ lực lượng sẵn sàng thi đấu."*

---

### PHÂN CẢNH 6: MINH CHỨNG CƠ SỞ DỮ LIỆU ORACLE (2:45 - 3:15)
*(Minh chứng Chapter 2 & Chapter 3: SQL Queries & PL/SQL)*

* **Thao tác trên màn hình:**
  1. Chuyển cửa sổ màn hình sang **Oracle SQL Developer**.
  2. Quét chuột câu lệnh Query 11:
     ```sql
     SELECT position, COUNT(*) AS so_luong, ROUND(AVG(ovr), 1) AS ovr_trung_binh 
     FROM players GROUP BY position;
     ```
     Bấm `Ctrl + Enter` $\rightarrow$ Bảng kết quả hiện ra khớp 100% với trên App (MF 3, FW 2, DF 4, GK 1).
  3. Quét chuột câu lệnh gọi Package PL/SQL:
     ```sql
     EXEC pkg_fc_manager.evaluate_squad_fitness;
     ```
     Bấm `Ctrl + Enter` $\rightarrow$ Cửa sổ Script Output in ra bảng phân hạng thể lực 10 cầu thủ.
* **Lời thoại:**
  > *"Để chứng minh hệ thống hoạt động thực tế trên nền tảng Oracle 19c, đây là cửa sổ Oracle SQL Developer. Khi em chạy câu lệnh truy vấn gom nhóm Group By theo vị trí, dữ liệu trả về hoàn toàn đồng bộ với màn hình thống kê trên App. Và khi thực thi Package PL/SQL evaluate_squad_fitness, thủ tục tự động duyệt con trỏ và đánh giá thể lực toàn bộ đội hình một cách tối ưu."*

---

### PHÂN CẢNH 7: KẾT THÚC VIDEO (3:15 - 3:25)
* **Lời thoại:**
  > *"Nhóm em đã hoàn thành toàn bộ các yêu cầu của bài tập lớn môn Hệ quản trị CSDL Oracle, từ thiết kế chuẩn 3NF, 20 câu truy vấn, lập trình PL/SQL, sao lưu Data Pump đến ứng dụng di động thực tế. Nhóm em xin chân thành cảm ơn thầy Nguyễn Viết Hùng đã hướng dẫn và theo dõi bài báo cáo của nhóm!"*

---

## 💡 MẸO QUAN TRỌNG ĐỂ ĐẠT ĐIỂM TỐI ĐA 10/10
1. **Nói dứt khoát, tự tin:** Đọc rõ các thuật ngữ tiếng Anh: *"Constraint Checking"*, *"Business Process"*, *"Radar Chart"*, *"Overall Rating"*, *"Data Aggregation"*.
2. **Thao tác chuột nhịp nhàng:** Bấm nút nào thì dừng lại 1-2 giây cho người xem nhìn rõ màn hình rồi mới thao tác tiếp.
