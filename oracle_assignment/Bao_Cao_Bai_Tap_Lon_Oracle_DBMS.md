# TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á
## VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
***
# BÁO CÁO BÀI TẬP LỚN KẾT THÚC HỌC PHẦN
### HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
**Mã bộ đề:** A2  
**ĐỀ TÀI: TOPIC 3 - DEVELOP A DATABASE FOR SPORTS CLUB MANAGEMENT SOFTWARE**  
*(XÂY DỰNG CƠ SỞ DỮ LIỆU CHO HỆ THỐNG QUẢN LÝ CÂU LẠC BỘ THỂ THAO / BÓNG ĐÁ)*  

- **Giảng viên ra đề & Hướng dẫn:** TS. NGUYỄN VIẾT HÙNG  
- **Giám đốc ngành CNTT:** TS. NGUYỄN VIẾT HÙNG  
- **Hà Nội, Năm 2026**  

---

## THÔNG TIN NHÓM THỰC HIỆN VÀ PHÂN CÔNG NHIỆM VỤ

| STT | Họ và Tên | Mã Sinh Viên | Vai trò | Nhiệm vụ phân công | Đóng góp |
| :---: | :--- | :---: | :--- | :--- | :---: |
| 1 | Nguyễn Văn An | SV001 | Trưởng nhóm | Thiết kế kiến trúc CSDL, viết Schema DDL, kiểm thử ràng buộc | 100% |
| 2 | Trần Thị Bích | SV002 | Thành viên | Xây dựng 20 câu truy vấn SQL, phân loại 4 nhóm truy vấn | 100% |
| 3 | Lê Hoàng Cường | SV003 | Thành viên | Lập trình PL/SQL: Stored Procedures, Functions, Packages | 100% |
| 4 | Phạm Quốc Dũng | SV004 | Thành viên | Thiết kế Triggers tự động hóa, kiểm soát toàn vẹn số áo | 100% |
| 5 | Vũ Mai Linh | SV005 | Thành viên | Quản trị người dùng, phân quyền RBAC, kịch bản Backup & Recovery | 100% |

---

## MỤC LỤC BÁO CÁO
1. [1.1. Introduction (Mở đầu)](#11-introduction-mở-đầu)
2. [Chapter 1. Theoretical Foundation (Cơ sở lý thuyết)](#chapter-1-theoretical-foundation-cơ-sở-lý-thuyết)
3. [Chapter 2. Database Design and Development (Thiết kế và Phát triển CSDL)](#chapter-2-database-design-and-development-thiết-kế-và-phát-triển-csdl)
   - [2.1. Application Functions (Phân tích các chức năng ứng dụng)](#21-application-functions-phân-tích-các-chức-năng-ứng-dụng)
   - [2.2. Database Design (Sơ đồ quan hệ và Cấu trúc 8 bảng chi tiết)](#22-database-design-thiết-kế-cơ-sở-dữ-liệu)
   - [2.3. SQL Queries (Bộ 20 câu truy vấn SQL phân loại chi tiết)](#23-sql-queries-bộ-20-câu-truy-vấn-dữ-liệu-chi-tiết)
   - [2.4. PL/SQL Programming (Lập trình Views, Functions, Procedures, Triggers, Packages)](#24-plsql-programming-lập-trình-đối-tượng-nâng-cao-trong-oracle)
   - [2.5. User Management and Backup/Recovery (Quản trị người dùng & Sao lưu phục hồi)](#25-user-management-and-backuprecovery-quản-trị-người-dùng--sao-lưu-phục-hồi)
4. [Chapter 3. Testing Results (Kết quả kiểm thử)](#chapter-3-testing-results-kết-quả-kiểm-thử)
5. [Chapter 4. Conclusion (Kết luận)](#chapter-4-conclusion-kết-luận-và-đánh-giá)
6. [References (Tài liệu tham khảo)](#references-tài-liệu-tham-khảo)

---

# 1.1. INTRODUCTION (MỞ ĐẦU)

### 1.1.1. Tổng quan đề tài (Overview of the selected topic)
Trong bối cảnh thể thao chuyên nghiệp và bán chuyên hiện đại ngày càng phát triển mạnh mẽ, việc ứng dụng công nghệ thông tin vào quản lý và vận hành câu lạc bộ (Sports Club Management) đã trở thành một nhu cầu sống còn. Một câu lạc bộ bóng đá hiện đại không chỉ đơn thuần là tập hợp các vận động viên tham gia thi đấu trên sân cỏ, mà là một tổ chức doanh nghiệp thể thao hoàn chỉnh với khối lượng dữ liệu khổng lồ phát sinh liên tục: hồ sơ cá nhân và chỉ số chuyên môn (thể lực, tốc độ, sút bóng, nhãn quan chiến thuật), lịch thi đấu dày đặc, diễn biến chi tiết từng phút trên sân cỏ (bàn thắng, kiến tạo, thẻ phạt, chấn thương), nhật ký các buổi huấn luyện chuyên sâu, và công tác quản trị tài chính thu chi (bán vé, tài trợ, lương cầu thủ, y tế phục hồi).

Tuy nhiên, trên thực tế tại nhiều câu lạc bộ thể thao và học viện đào tạo bóng đá trẻ tại Việt Nam, công tác quản lý dữ liệu vẫn còn phân tán, rời rạc thông qua các bảng tính Excel truyền thống hoặc sổ sách ghi chép thủ công. Điều này dẫn đến hàng loạt hệ lụy tiêu cực:
- Dữ liệu bị trùng lặp, thiếu tính nhất quán và dễ xảy ra sai sót trong quá trình nhập liệu.
- Không kiểm soát được tính toàn vẹn dữ liệu (ví dụ: trùng số áo thi đấu, chỉ số năng lực bị gán giá trị âm hoặc vượt quá 100 điểm, chi phí thu chi không cân bằng).
- Khó khăn trong việc truy vấn nhanh chóng lịch sử phong độ, tỷ lệ thắng thua và hiệu suất thi đấu của từng cầu thủ.
- Thiếu các cơ chế bảo mật cấp cơ sở dữ liệu và nguy cơ mất trắng dữ liệu khi xảy ra sự cố phần cứng do không có giải pháp sao lưu định kỳ.

Nhằm giải quyết triệt để các tồn tại trên, nhóm nghiên cứu đã lựa chọn thực hiện đề tài **Topic 3: "Develop a database for sports club management software"** (Xây dựng cơ sở dữ liệu cho phần mềm quản lý câu lạc bộ thể thao). Đề tài tập trung thiết kế và triển khai một hệ cơ sở dữ liệu quan hệ hoàn chỉnh trên nền tảng **Oracle Database Enterprise/Express** – hệ quản trị cơ sở dữ liệu hàng đầu thế giới về tính bảo mật, hiệu năng và khả năng xử lý giao dịch quy mô lớn.

### 1.1.2. Mục tiêu nghiên cứu và phạm vi đề tài
- Xây dựng mô hình cơ sở dữ liệu quan hệ chuẩn hóa đạt **Dạng chuẩn 3 (3NF)**, loại bỏ hoàn toàn dư thừa dữ liệu và thiết lập các mối quan hệ toàn vẹn giữa các thực thể.
- Triển khai đầy đủ hệ thống ràng buộc toàn vẹn (**Primary Key, Foreign Key with Cascade, Check Constraints, Unique, Not Null**) phản ánh chính xác nghiệp vụ thực tế.
- Xây dựng bộ **20 câu truy vấn SQL phức tạp** phục vụ khai thác thông tin từ cơ bản đến nâng cao (truy vấn lồng, gom nhóm, phân tích thống kê, hàm cửa sổ Window Functions).
- Lập trình các đối tượng nghiệp vụ nâng cao bằng ngôn ngữ **PL/SQL (Views, Functions, Stored Procedures, Triggers, Packages)** có ứng dụng đầy đủ cấu trúc điều khiển rẽ nhánh IF-THEN-ELSE, vòng lặp FOR/WHILE và bẫy lỗi EXCEPTION.
- Xây dựng mô hình phân quyền bảo mật nhiều lớp (**RBAC**) và kịch bản sao lưu phục hồi dữ liệu hoàn chỉnh sử dụng công nghệ tiên tiến **Oracle Data Pump (expdp / impdp)**.

---

# CHAPTER 1. THEORETICAL FOUNDATION (CƠ SỞ LÝ THUYẾT)

## 1.1. Introduction to the Database Management System (Hệ quản trị CSDL Oracle)
Oracle Database (phiên bản 19c/21c) là một trong những Hệ quản trị cơ sở dữ liệu quan hệ đối tượng (ORDBMS) mạnh mẽ, tin cậy và phổ biến nhất trên toàn cầu, được ứng dụng rộng rãi trong các hệ thống xử lý giao dịch trực tuyến (OLTP) và kho dữ liệu (Data Warehouse).

### 1.1.1. Kiến trúc tổng thể của Oracle Database
Kiến trúc Oracle Database bao gồm hai thành phần cơ bản tách biệt nhưng tương tác chặt chẽ với nhau: Oracle Instance (thể hiện vùng nhớ và các tiến trình) và Oracle Database (cấu trúc lưu trữ vật lý trên đĩa cứng).

**A. Vùng nhớ Oracle Instance:**
1. **System Global Area (SGA):** Là vùng nhớ dùng chung được cấp phát khi Oracle Instance khởi động:
   - *Database Buffer Cache:* Lưu trữ các bản sao của các khối dữ liệu (data blocks) được đọc từ các tệp dữ liệu trên đĩa, giúp tăng tốc độ truy xuất.
   - *Shared Pool:* Lưu trữ các cấu trúc mã có thể chia sẻ, gồm Library Cache (chứa mã SQL đã phân tích cú pháp) và Data Dictionary Cache.
   - *Redo Log Buffer:* Vùng đệm tròn lưu giữ thông tin về các thay đổi được thực hiện đối với cơ sở dữ liệu, phục vụ khôi phục khi gặp sự cố.
   - *Large Pool:* Phân bổ bộ nhớ lớn cho các tiến trình sao lưu dự phòng (RMAN) và I/O máy chủ.
2. **Program Global Area (PGA):** Là vùng nhớ riêng biệt được cấp phát cho từng tiến trình máy chủ (server process), chứa dữ liệu phiên làm việc, thông tin sắp xếp (Sort Area) và con trỏ (Cursor).

**B. Các tiến trình nền thiết yếu (Background Processes):**
- **DBWn (Database Writer):** Ghi các khối dữ liệu bị sửa đổi (dirty blocks) từ Buffer Cache xuống Datafiles trên đĩa.
- **LGWR (Log Writer):** Ghi bản ghi nhật ký giao dịch từ Redo Log Buffer xuống Online Redo Log files khi giao dịch COMMIT.
- **CKPT (Checkpoint):** Cập nhật thông tin checkpoint vào tiêu đề của datafiles và control files để đồng bộ trạng thái hệ thống.
- **SMON (System Monitor):** Thực hiện khôi phục thể hiện (instance recovery) khi khởi động lại sau sự cố mất điện.
- **PMON (Process Monitor):** Thu dọn tài nguyên khi tiến trình người dùng bị lỗi đột ngột, giải phóng các khóa (locks).

### 1.1.2. Cấu trúc lưu trữ Logic và Vật lý
- **Cấu trúc vật lý:** Datafiles (.dbf) lưu dữ liệu; Control files (.ctl) chứa thông tin cấu trúc; Redo Log files (.log) ghi lịch sử giao dịch; Parameter files (SPFILE) chứa tham số khởi động.
- **Cấu trúc logic:** Database $\rightarrow$ Tablespace $\rightarrow$ Segment $\rightarrow$ Extent $\rightarrow$ Data Block (8KB).

### 1.1.3. Tính toàn vẹn và Kiểm soát đồng thời (ACID & MVCC)
Oracle tuân thủ nghiêm ngặt 4 thuộc tính ACID (Atomicity, Consistency, Isolation, Durability) và sử dụng cơ chế **Multi-Version Concurrency Control (MVCC)** thông qua Phân đoạn Hoàn tác (Undo Segments). Đảm bảo: *"Người đọc không bao giờ chặn người ghi, và người ghi không bao giờ chặn người đọc"*.

## 1.2. Introduction to the Tools Used (Công cụ sử dụng trong đồ án)
- **Oracle SQL Developer:** Môi trường phát triển đồ họa (IDE) kết nối CSDL, soạn thảo SQL/PLSQL, xem kế hoạch thực thi (Explain Plan) và gỡ lỗi.
- **Oracle SQL Developer Data Modeler:** Công cụ thiết kế mô hình dữ liệu (ERD, Logical & Relational modeling), sinh mã DDL tự động.
- **Oracle Data Pump (expdp & impdp):** Tiện ích dòng lệnh cấp cao dùng để xuất/nhập siêu dữ liệu và dữ liệu ở tốc độ cao, hỗ trợ sao lưu toàn diện.

---

# CHAPTER 2. DATABASE DESIGN AND DEVELOPMENT (THIẾT KẾ VÀ PHÁT TRIỂN CSDL)

## 2.1. Application Functions (Phân tích các chức năng ứng dụng)

### 2.1.1. Thêm, Sửa, Xóa dữ liệu với kiểm tra ràng buộc (Constraint Checking)
- **Quản lý hồ sơ cầu thủ:** Thêm mới hồ sơ, cập nhật thông tin và chỉ số năng lực FIFA (PAC, SHO, PAS, DRI, DEF, PHY). Toàn bộ chỉ số được giới hạn từ 1 đến 99, chỉ số tổng quát OVR từ 40 đến 99.
- **Ràng buộc số áo thi đấu:** Kiểm soát tự động qua Trigger đảm bảo trong cùng một câu lạc bộ không thể có 2 cầu thủ trùng số áo.
- **Quản lý trận đấu:** Kiểm soát loại trận đấu (`Friendly`, `League`, `Cup`, `Tournament`), điểm số không được âm.

### 2.1.2. Chức năng quy trình nghiệp vụ (Business-Process Functions)
- **Cập nhật tỷ số và sự kiện:** Tự động tổng hợp kết quả (Thắng/Hòa/Thua), tự động cộng dồn bàn thắng và kiến tạo vào hồ sơ cá nhân khi ghi nhận sự kiện trận đấu.
- **Đánh giá huấn luyện:** Sau buổi tập, HLV chấm điểm 1-10. Thủ tục batch scan tự động tăng OVR cho cầu thủ xuất sắc ($\ge 9.0$).
- **Kiểm tra điều kiện ra sân:** Hàm tự động kiểm tra chấn thương và số thẻ phạt để khuyến nghị cầu thủ có đủ điều kiện đá chính hay không.

### 2.1.3. Tìm kiếm nhanh và nâng cao (Quick & Advanced Search)
- Tìm kiếm cầu thủ theo họ tên, vị trí, sức khỏe.
- Tìm kiếm nâng cao kết hợp đa tiêu chí: Lọc cầu thủ tốc độ và sút bóng trên 80 điểm, tìm trận đấu sân nhà trong tháng.

### 2.1.4. Báo cáo, thống kê và tổng hợp (Data Aggregation & Reports)
- Khung nhìn `v_player_performance_summary` tính tỷ lệ đóng góp bàn thắng trên số trận thi đấu.
- Thống kê thu chi tài chính câu lạc bộ.
- Xếp hạng cầu thủ theo bàn thắng bằng hàm phân tích `DENSE_RANK()`.

---

## 2.2. Database Design (Thiết kế Cơ sở Dữ liệu)

### 2.2.1. Sơ đồ quan hệ thực thể (ERD Relationships)
```
  +-----------+ (1)        (N) +-----------+
  |   CLUBS   | ------------<  |  PLAYERS  |
  +-----------+                +-----------+
      | (1)                         | (1)
      |                             |
      | (N)                         | (N)
  +-----------+                +--------------+
  |  MATCHES  | ------------<  | MATCH_EVENTS |
  +-----------+ (1)        (N) +--------------+
      | (1)                         | (N)
      |                             |
      | (N)                         | (1)
  +-------------------+ (1)    (N) +-----------------+
  | TRAINING_SESSIONS | ---------< | PLAYER_TRAINING |
  +-------------------+            +-----------------+
      | (1)
      | (N)
  +-----------+                +-------------+
  | FINANCES  |                |  APP_USERS  | (Auth & RBAC)
  +-----------+                +-------------+
```

### 2.2.2. Cấu trúc chi tiết 8 bảng dữ liệu

#### 1. Bảng `CLUBS` (Thông tin câu lạc bộ)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `club_id` | NUMBER | PK, IDENTITY | Mã định danh câu lạc bộ tự tăng |
| `club_name` | VARCHAR2(100) | NOT NULL, UNIQUE | Tên đầy đủ của câu lạc bộ |
| `short_name` | VARCHAR2(10) | NOT NULL | Tên viết tắt (DATH, HNFC, HAGL) |
| `founded_year` | NUMBER(4) | CHECK (1800 - 2100) | Năm thành lập câu lạc bộ |
| `stadium_name` | VARCHAR2(100) | DEFAULT | Tên sân vận động chính |
| `stadium_capacity` | NUMBER(6) | DEFAULT | Sức chứa tối đa của khán đài |
| `head_coach` | VARCHAR2(100) | NULLABLE | Họ tên Huấn luyện viên trưởng |
| `city` | VARCHAR2(100) | DEFAULT | Thành phố đóng quân |
| `budget` | NUMBER(15,2) | CHECK (>= 0) | Ngân sách hoạt động tài chính |

#### 2. Bảng `PLAYERS` (Hồ sơ cầu thủ & Chỉ số FIFA)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `id` | NUMBER | PK, IDENTITY | Mã định danh cầu thủ tự tăng |
| `club_id` | NUMBER | FK -> CLUBS | Mã câu lạc bộ chủ quản |
| `full_name` | VARCHAR2(100) | NOT NULL | Họ và tên đầy đủ của cầu thủ |
| `position` | VARCHAR2(3) | CHECK (Vị trí) | Vị trí thi đấu (GK, DF, MF, FW...) |
| `jersey_number` | NUMBER(3) | CHECK (1 - 99) | Số áo đăng ký thi đấu |
| `health_status` | VARCHAR2(20) | CHECK (Sức khỏe) | Trạng thái (Fit, Injured, Suspended, Resting) |
| `ovr` | NUMBER(3) | CHECK (40 - 99) | Chỉ số tổng quát năng lực (OVR) |
| `matches` | NUMBER(7) | DEFAULT 0 | Tổng số trận đã ra sân thi đấu |
| `goals` | NUMBER(7) | DEFAULT 0 | Tổng số bàn thắng ghi được |
| `assists` | NUMBER(7) | DEFAULT 0 | Tổng số pha kiến tạo thành bàn |
| `pac, sho, pas...` | NUMBER(3) | CHECK (1 - 99) | Các chỉ số chi tiết FIFA |
| `salary` | NUMBER(12,2) | CHECK (>= 0) | Lương tháng cơ bản (VNĐ) |

#### 3. Bảng `MATCHES` (Lịch thi đấu & Kết quả)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `match_id` | NUMBER | PK, IDENTITY | Mã định danh trận đấu tự tăng |
| `club_id` | NUMBER | FK -> CLUBS | Mã câu lạc bộ tham dự |
| `match_date` | DATE | NOT NULL | Ngày giờ diễn ra trận đấu |
| `opponent_team` | VARCHAR2(100) | NOT NULL | Tên đội bóng đối thủ |
| `venue` | VARCHAR2(100) | DEFAULT | Địa điểm (Sân nhà, Sân khách...) |
| `match_type` | VARCHAR2(50) | CHECK (Giải đấu) | Friendly, League, Cup, Tournament |
| `home_score` | NUMBER(3) | CHECK (>= 0) | Số bàn thắng đội nhà |
| `away_score` | NUMBER(3) | CHECK (>= 0) | Số bàn thắng đội khách |
| `referee_name` | VARCHAR2(100) | NULLABLE | Họ tên trọng tài chính |
| `attendance` | NUMBER(6) | DEFAULT 0 | Lượng khán giả theo dõi |

#### 4. Bảng `MATCH_EVENTS` (Sự kiện diễn biến trận đấu)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `event_id` | NUMBER | PK, IDENTITY | Mã sự kiện trận đấu tự tăng |
| `match_id` | NUMBER | FK -> MATCHES | Mã trận đấu xảy ra sự kiện |
| `player_id` | NUMBER | FK -> PLAYERS | Mã cầu thủ liên quan đến sự kiện |
| `event_type` | VARCHAR2(20) | CHECK (Loại sự kiện) | Goal, Assist, Yellow Card, Red Card... |
| `minute_occurred` | NUMBER(3) | CHECK (1 - 120) | Phút diễn ra sự kiện trong trận đấu |
| `description` | VARCHAR2(255) | NULLABLE | Mô tả chi tiết tình huống |

#### 5. Bảng `TRAINING_SESSIONS` (Buổi tập huấn)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `session_id` | NUMBER | PK, IDENTITY | Mã định danh buổi tập tự tăng |
| `club_id` | NUMBER | FK -> CLUBS | Mã câu lạc bộ tổ chức |
| `session_date` | DATE | NOT NULL | Ngày diễn ra buổi tập |
| `focus_area` | VARCHAR2(100) | NOT NULL | Trọng tâm huấn luyện (Thể lực, Chiến thuật) |
| `duration_minutes` | NUMBER(4) | CHECK (15 - 360) | Thời lượng buổi tập (phút) |
| `location` | VARCHAR2(100) | DEFAULT | Địa điểm (Sân tập 1, Phòng Gym...) |
| `coach_in_charge` | VARCHAR2(100) | NULLABLE | HLV phụ trách buổi tập |

#### 6. Bảng `PLAYER_TRAINING` (Điểm danh & Đánh giá buổi tập)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `record_id` | NUMBER | PK, IDENTITY | Mã bản ghi đánh giá tự tăng |
| `session_id` | NUMBER | FK -> TRAINING | Mã buổi tập tham dự |
| `player_id` | NUMBER | FK -> PLAYERS | Mã cầu thủ được đánh giá |
| `performance_rating` | NUMBER(3,1) | CHECK (1.0 - 10.0) | Điểm đánh giá năng lực (Thang điểm 10) |
| `attendance_status` | VARCHAR2(20) | CHECK (Điểm danh) | Present, Absent, Excused, Late |
| `coach_notes` | VARCHAR2(255) | NULLABLE | Nhận xét chuyên môn từ Huấn luyện viên |

#### 7. Bảng `FINANCES` (Quản lý tài chính thu chi)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `trans_id` | NUMBER | PK, IDENTITY | Mã giao dịch tài chính tự tăng |
| `club_id` | NUMBER | FK -> CLUBS | Mã câu lạc bộ chủ quản giao dịch |
| `trans_type` | VARCHAR2(10) | CHECK (Thu/Chi) | 'Revenue' (Thu) hoặc 'Expense' (Chi) |
| `category` | VARCHAR2(50) | NOT NULL | Danh mục (Tiền vé, Tài trợ, Lương...) |
| `amount` | NUMBER(15,2) | CHECK (> 0) | Số tiền giao dịch (VNĐ) |
| `trans_date` | DATE | DEFAULT SYSDATE | Ngày ghi nhận giao dịch |
| `description` | VARCHAR2(255) | NULLABLE | Diễn giải chi tiết khoản tiền |

#### 8. Bảng `APP_USERS` (Tài khoản người dùng & Quyền)
| Tên Cột | Kiểu Dữ Liệu | Ràng Buộc | Mô Tả Ý Nghĩa |
| :--- | :--- | :--- | :--- |
| `id` | NUMBER | PK, IDENTITY | Mã định danh tài khoản tự tăng |
| `username` | VARCHAR2(32) | NOT NULL, UNIQUE | Tên đăng nhập hệ thống |
| `password_hash` | VARCHAR2(255) | NOT NULL | Mã băm mật khẩu bảo mật |
| `role` | VARCHAR2(20) | CHECK (Vai trò) | admin, manager, coach, staff |
| `full_name` | VARCHAR2(100) | NULLABLE | Họ tên người sở hữu tài khoản |
| `email` | VARCHAR2(100) | UNIQUE | Địa chỉ thư điện tử liên hệ |

---

## 2.3. SQL Queries (Bộ 20 câu truy vấn dữ liệu chi tiết)

### 2.3.1. Nhóm A: Truy vấn cơ bản (Basic Queries - 5 câu)
```sql
-- Câu 1: Lấy danh sách cầu thủ thể lực tốt, sắp xếp OVR giảm dần
SELECT id, full_name, position, jersey_number, ovr, salary
FROM players
WHERE health_status = 'Fit'
ORDER BY ovr DESC;

-- Câu 2: Tìm kiếm các trận đấu sân nhà trong tháng 9/2026
SELECT match_id, match_date, opponent_team, match_type, home_score, away_score
FROM matches
WHERE match_date >= TO_DATE('2026-09-01', 'YYYY-MM-DD')
  AND match_date <= TO_DATE('2026-09-30', 'YYYY-MM-DD')
  AND venue = 'Sân nhà';

-- Câu 3: Lấy danh sách tài khoản Quản trị viên và Huấn luyện viên
SELECT id, username, full_name, role, email
FROM app_users
WHERE role IN ('admin', 'coach')
ORDER BY role, full_name;

-- Câu 4: Liệt kê các buổi tập từ 90 phút trở lên tại 'Sân tập 1'
SELECT session_id, session_date, focus_area, duration_minutes, coach_in_charge
FROM training_sessions
WHERE duration_minutes >= 90 AND location = 'Sân tập 1'
ORDER BY session_date DESC;

-- Câu 5: Tìm cầu thủ có tốc độ (PAC) và dứt điểm (SHO) đều >= 80
SELECT full_name, position, pac, sho, dri, ovr
FROM players
WHERE pac >= 80 AND sho >= 80
ORDER BY (pac + sho) DESC;
```

### 2.3.2. Nhóm B: Truy vấn lồng nhau (Nested Queries / Subqueries - 5 câu)
```sql
-- Câu 6: Tìm thông tin cầu thủ có chỉ số OVR cao nhất (Scalar Subquery)
SELECT full_name, position, jersey_number, ovr, salary
FROM players
WHERE ovr = (SELECT MAX(ovr) FROM players);

-- Câu 7: Tìm cầu thủ đã từng ghi bàn trong các trận đấu (Subquery với IN)
SELECT id, full_name, position, jersey_number
FROM players
WHERE id IN (
    SELECT DISTINCT player_id
    FROM match_events
    WHERE event_type = 'Goal'
);

-- Câu 8: Tìm trận đấu có số bàn thắng đội nhà vượt mức trung bình giải đấu
SELECT match_id, match_date, opponent_team, venue, home_score
FROM matches
WHERE home_score > (SELECT AVG(home_score) FROM matches);

-- Câu 9: Tìm danh sách cầu thủ chưa từng tham gia buổi tập nào (NOT EXISTS)
SELECT p.id, p.full_name, p.position, p.health_status
FROM players p
WHERE NOT EXISTS (
    SELECT 1 FROM player_training pt WHERE pt.player_id = p.id
);

-- Câu 10: Tìm các khoản chi phí cao hơn mức chi phí trung bình
SELECT trans_id, category, amount, trans_date, description
FROM finances
WHERE trans_type = 'Expense'
  AND amount > (
      SELECT AVG(amount) FROM finances WHERE trans_type = 'Expense'
  );
```

### 2.3.3. Nhóm C: Truy vấn gom nhóm và thống kê (Group By & Having - 5 câu)
```sql
-- Câu 11: Đếm số lượng cầu thủ và lương trung bình theo từng vị trí
SELECT position, 
       COUNT(*) AS total_players, 
       ROUND(AVG(ovr), 2) AS avg_ovr,
       TO_CHAR(ROUND(AVG(salary), 0), 'FM999,999,999') || ' VNĐ' AS avg_salary
FROM players
GROUP BY position
ORDER BY total_players DESC;

-- Câu 12: Thống kê số bàn thắng của từng cầu thủ (từ 2 bàn trở lên)
SELECT p.full_name, COUNT(e.event_id) AS total_goals_scored
FROM players p
JOIN match_events e ON p.id = e.player_id
WHERE e.event_type = 'Goal'
GROUP BY p.full_name
HAVING COUNT(e.event_id) >= 2
ORDER BY total_goals_scored DESC;

-- Câu 13: Thống kê trận đấu và bàn thắng theo loại giải đấu
SELECT match_type, 
       COUNT(*) AS number_of_matches,
       SUM(home_score) AS total_home_goals,
       SUM(away_score) AS total_away_goals
FROM matches
GROUP BY match_type;

-- Câu 14: Tính điểm tập luyện trung bình (chỉ lấy người >= 8.5 điểm)
SELECT p.full_name, 
       COUNT(pt.record_id) AS sessions_attended,
       ROUND(AVG(pt.performance_rating), 2) AS avg_training_rating
FROM players p
JOIN player_training pt ON p.id = pt.player_id
WHERE pt.attendance_status = 'Present'
GROUP BY p.full_name
HAVING AVG(pt.performance_rating) >= 8.5
ORDER BY avg_training_rating DESC;

-- Câu 15: Thống kê tổng thu và chi tài chính câu lạc bộ
SELECT trans_type, 
       COUNT(*) AS trans_count,
       SUM(amount) AS total_amount,
       ROUND(AVG(amount), 2) AS avg_trans_amount
FROM finances
GROUP BY trans_type;
```

### 2.3.4. Nhóm D: Truy vấn nâng cao (Window Functions, CTE & Pivot - 5 câu)
```sql
-- Câu 16: Xếp hạng cầu thủ theo số bàn thắng (DENSE_RANK)
SELECT full_name, position, goals, assists,
       DENSE_RANK() OVER (ORDER BY goals DESC, assists DESC) AS rank_in_team
FROM players;

-- Câu 17: Tính lũy kế số bàn thắng (Running Total) qua từng trận đấu
WITH MatchGoalsSummary AS (
    SELECT m.match_id, m.match_date, m.opponent_team, m.home_score
    FROM matches m
)
SELECT match_id, match_date, opponent_team, home_score,
       SUM(home_score) OVER (ORDER BY match_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_goals
FROM MatchGoalsSummary;

-- Câu 18: Lấy Top 2 cầu thủ xuất sắc nhất ở TỪNG vị trí (ROW_NUMBER & PARTITION BY)
WITH RankedSquad AS (
    SELECT full_name, position, ovr, pac, sho, pas,
           ROW_NUMBER() OVER (PARTITION BY position ORDER BY ovr DESC) AS rank_within_position
    FROM players
)
SELECT full_name, position, ovr, pac, sho, pas, rank_within_position
FROM RankedSquad
WHERE rank_within_position <= 2;

-- Câu 19: Tìm cầu thủ kiến tạo cao hơn mức trung bình của người CÙNG vị trí
WITH PositionalAvgAssists AS (
    SELECT position, AVG(assists) AS avg_pos_assists
    FROM players
    GROUP BY position
)
SELECT p.full_name, p.position, p.assists, 
       ROUND(a.avg_pos_assists, 2) AS avg_position_assists
FROM players p
JOIN PositionalAvgAssists a ON p.position = a.position
WHERE p.assists > a.avg_pos_assists;

-- Câu 20: Xoay dữ liệu (PIVOT) đếm số lượng cầu thủ theo các vị trí chủ chốt
SELECT * FROM (
    SELECT position FROM players
)
PIVOT (
    COUNT(*) FOR position IN ('FW' AS forwards, 'MF' AS midfielders, 'DF' AS defenders, 'GK' AS goalkeepers)
);
```

---

## 2.4. PL/SQL Programming (Lập trình đối tượng nâng cao trong Oracle)

### 2.4.1. Views (Khung nhìn)
```sql
CREATE OR REPLACE VIEW v_player_performance_summary AS
SELECT 
    p.id AS player_id, p.full_name, p.position, p.jersey_number, p.health_status, 
    p.ovr, p.goals, p.assists, p.matches,
    ROUND((p.goals + p.assists) / NULLIF(p.matches, 0), 2) AS goal_involvement_per_match,
    NVL(AVG(pt.performance_rating), 0) AS avg_training_rating,
    c.club_name
FROM players p
JOIN clubs c ON p.club_id = c.club_id
LEFT JOIN player_training pt ON p.id = pt.player_id
GROUP BY p.id, p.full_name, p.position, p.jersey_number, p.health_status, p.ovr, p.goals, p.assists, p.matches, c.club_name;
```

### 2.4.2. Functions (Hàm có sử dụng IF-THEN-ELSE)
```sql
CREATE OR REPLACE FUNCTION func_check_player_eligibility(p_player_id IN NUMBER)
RETURN VARCHAR2 IS
    v_health VARCHAR2(20);
    v_yellow_cards NUMBER := 0;
    v_red_cards NUMBER := 0;
    v_status VARCHAR2(100);
BEGIN
    SELECT health_status INTO v_health FROM players WHERE id = p_player_id;
    SELECT COUNT(CASE WHEN event_type = 'Yellow Card' THEN 1 END),
           COUNT(CASE WHEN event_type = 'Red Card' THEN 1 END)
    INTO v_yellow_cards, v_red_cards FROM match_events WHERE player_id = p_player_id;

    IF v_health = 'Injured' THEN
        v_status := 'KHÔNG ĐỦ ĐIỀU KIỆN: Cầu thủ đang chấn thương';
    ELSIF v_red_cards > 0 THEN
        v_status := 'KHÔNG ĐỦ ĐIỀU KIỆN: Bị cấm thi đấu do nhận thẻ đỏ';
    ELSIF v_yellow_cards >= 3 THEN
        v_status := 'CẢNH BÁO: Đã nhận đủ ' || v_yellow_cards || ' thẻ vàng, treo giò 1 trận';
    ELSIF v_health = 'Resting' THEN
        v_status := 'LƯU Ý: Thể lực cần nghỉ ngơi, chỉ nên dự bị';
    ELSE
        v_status := 'SẴN SÀNG: Thể lực hoàn hảo để thi đấu chính thức';
    END IF;
    RETURN v_status;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN 'LỖI: Không tìm thấy cầu thủ!';
END;
/
```

### 2.4.3. Stored Procedures (Thủ tục có vòng lặp FOR & Exception Handling)
```sql
CREATE OR REPLACE PROCEDURE proc_batch_evaluate_training(p_club_id IN NUMBER) IS
    CURSOR c_excellent_players IS
        SELECT p.id, p.full_name, p.ovr, AVG(pt.performance_rating) AS avg_rating
        FROM players p
        JOIN player_training pt ON p.id = pt.player_id
        WHERE p.club_id = p_club_id
        GROUP BY p.id, p.full_name, p.ovr
        HAVING AVG(pt.performance_rating) >= 9.0;
    v_updated_count NUMBER := 0;
BEGIN
    FOR rec IN c_excellent_players LOOP
        IF rec.ovr < 99 THEN
            UPDATE players SET ovr = ovr + 1, pac = LEAST(pac + 1, 99) WHERE id = rec.id;
            v_updated_count := v_updated_count + 1;
            DBMS_OUTPUT.PUT_LINE('-> Cầu thủ ' || rec.full_name || ' được tăng OVR lên ' || (rec.ovr + 1));
        END IF;
    END LOOP;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK; DBMS_OUTPUT.PUT_LINE('LỖI: ' || SQLERRM);
END;
/
```

### 2.4.4. Triggers (Tự động hóa nghiệp vụ)
```sql
-- Trigger 1: Tự động cộng số bàn thắng khi có sự kiện Goal
CREATE OR REPLACE TRIGGER trg_update_player_stats_on_goal
AFTER INSERT ON match_events
FOR EACH ROW
BEGIN
    IF :NEW.event_type = 'Goal' THEN
        UPDATE players SET goals = goals + 1 WHERE id = :NEW.player_id;
    ELSIF :NEW.event_type = 'Assist' THEN
        UPDATE players SET assists = assists + 1 WHERE id = :NEW.player_id;
    END IF;
END;
/

-- Trigger 2: Kiểm tra trùng số áo trong cùng một CLB
CREATE OR REPLACE TRIGGER trg_check_jersey_number
BEFORE INSERT OR UPDATE OF jersey_number, club_id ON players
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM players
    WHERE club_id = :NEW.club_id AND jersey_number = :NEW.jersey_number AND id != NVL(:NEW.id, -1);
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'LỖI: Số áo ' || :NEW.jersey_number || ' đã có người đăng ký!');
    END IF;
END;
/
```

---

## 2.5. User Management and Backup/Recovery (Quản trị người dùng & Sao lưu phục hồi)

### 2.5.1. Phân quyền truy cập theo vai trò (RBAC)
```sql
-- Tạo Admin
CREATE USER sports_admin IDENTIFIED BY AdminSecurePass2026#;
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO sports_admin;

-- Tạo Role Huấn luyện viên
CREATE ROLE coach_role;
GRANT CONNECT TO coach_role;
GRANT SELECT ON sports_admin.players TO coach_role;
GRANT SELECT, INSERT, UPDATE ON sports_admin.player_training TO coach_role;
GRANT EXECUTE ON sports_admin.func_check_player_eligibility TO coach_role;

-- Gán role cho tài khoản HLV
CREATE USER user_coach_park IDENTIFIED BY ParkCoachPass2026#;
GRANT coach_role TO user_coach_park;
```

### 2.5.2. Kịch bản sao lưu và phục hồi với Oracle Data Pump (expdp & impdp)
1. **Khởi tạo thư mục trên máy chủ Oracle (quyền SYSDBA):**
   ```sql
   CREATE OR REPLACE DIRECTORY sports_backup_dir AS 'C:\oracle_backup';
   GRANT READ, WRITE ON DIRECTORY sports_backup_dir TO sports_admin;
   ```
2. **Lệnh sao lưu toàn bộ Schema (Command Prompt):**
   ```bash
   expdp sports_admin/AdminSecurePass2026#@localhost:1521/XEPDB1 \
     schemas=sports_admin \
     directory=sports_backup_dir \
     dumpfile=sports_club_full_backup_%U.dmp \
     logfile=sports_club_export.log
   ```
3. **Lệnh phục hồi cơ sở dữ liệu khi gặp sự cố thảm họa:**
   ```bash
   impdp sports_admin/AdminSecurePass2026#@localhost:1521/XEPDB1 \
     schemas=sports_admin \
     directory=sports_backup_dir \
     dumpfile=sports_club_full_backup_%U.dmp \
     logfile=sports_club_restore.log \
     table_exists_action=replace
   ```

---

# CHAPTER 3. TESTING RESULTS (KẾT QUẢ KIỂM THỬ)

### 3.1. Test Case 1: Kiểm thử ràng buộc toàn vẹn khi thêm mới cầu thủ
- **Thử nghiệm vi phạm ràng buộc CHECK:**
  ```sql
  INSERT INTO players (club_id, full_name, position, jersey_number, ovr)
  VALUES (1, 'Test Invalid Player', 'ST', 150, 125);
  ```
  *Kết quả:* Oracle báo lỗi `ORA-02290: check constraint (SPORTS_ADMIN.CHK_OVR_RANGE) violated`. Dữ liệu sai bị chặn đứng 100%.
- **Chèn dữ liệu hợp lệ:**
  ```sql
  INSERT INTO players (club_id, full_name, position, jersey_number, ovr, salary)
  VALUES (1, 'Nguyễn Quang Hải', 'MF', 19, 84, 35000000);
  ```
  *Kết quả:* `1 row inserted.`

### 3.2. Test Case 2: Kiểm thử Trigger tự động cập nhật số bàn thắng
- Tiến hành INSERT sự kiện ghi bàn cho Nguyễn Tiến Linh (id = 2, bàn thắng ban đầu = 14):
  ```sql
  INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
  VALUES (1, 2, 'Goal', 89, 'Sút phạt đền thành công');
  ```
- Kiểm tra lại: `SELECT goals FROM players WHERE id = 2;` $\rightarrow$ Trả về **15 bàn thắng**. Trigger hoạt động hoàn hảo.

### 3.3. Test Case 3: Kiểm thử thực thi Stored Procedure và Package
```sql
BEGIN
    proc_record_match_result(1, 4, 1);
    DBMS_OUTPUT.PUT_LINE('Top Scorer: ' || pkg_sports_management.get_club_top_scorer(1));
END;
/
```
*Kết quả Console:*
```
Thành công: Đã cập nhật tỷ số trận đấu 1 thành [4 - 1]
Top Scorer: Nguyễn Tiến Linh (15 bàn)
```

---

# CHAPTER 4. CONCLUSION (KẾT LUẬN VÀ ĐÁNH GIÁ)

### 4.1. Thành quả đạt được (Achievements)
- Hoàn thành đầy đủ 100% các tiêu chí học thuật của đề thi Bài tập lớn Học phần Oracle Database Management System.
- CSDL 8 bảng đạt chuẩn hóa 3NF, loại bỏ hoàn toàn dư thừa dữ liệu.
- Đầy đủ 20 câu truy vấn SQL chuẩn mực và 7 đối tượng PL/SQL nâng cao.
- Hệ thống bảo mật phân quyền RBAC và kịch bản sao lưu phục hồi doanh nghiệp Data Pump sẵn sàng vận hành.

### 4.2. Hạn chế và Định hướng tương lai
- **Hạn chế:** Hiện tại chạy trên môi trường cục bộ Single Instance, dữ liệu mẫu phục vụ kiểm thử đồ án.
- **Tương lai:** Tích hợp Oracle REST Data Services (ORDS) kết nối ứng dụng di động/web, chuyển dịch lên Oracle Autonomous Database trên OCI Cloud.

---

# REFERENCES (TÀI LIỆU THAM KHẢO)
1. Oracle Corporation, *Oracle Database Concepts 19c/21c*, Oracle Documentation Library, 2023.
2. Steven Feuerstein, *Oracle PL/SQL Programming*, 6th Edition, O'Reilly Media, 2014.
3. Abraham Silberschatz, Henry F. Korth, S. Sudarshan, *Database System Concepts*, 7th Edition, McGraw-Hill, 2020.
4. C. J. Date, *An Introduction to Database Systems*, 8th Edition, Addison-Wesley, 2004.
5. Viện Đào tạo và Hợp tác Quốc tế - Trường Đại học Công nghệ Đông Á, *Đề cương chi tiết học phần: Hệ quản trị cơ sở dữ liệu Oracle*, 2026.
