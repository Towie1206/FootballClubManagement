-- ==============================================================================
-- TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á - VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
-- ĐỀ THI KẾT THÚC HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
-- FILE: 05_USER_MANAGEMENT_AND_BACKUP.sql
-- MÔ TẢ: QUẢN TRỊ NGƯỜI DÙNG, PHÂN QUYỀN (ROLES/GRANTS) VÀ SAO LƯU/PHỤC HỒI (DATA PUMP)
-- ==============================================================================

-- ==============================================================================
-- 1. QUẢN TRỊ NGƯỜI DÙNG VÀ PHÂN QUYỀN (USER MANAGEMENT & SECURITY)
-- (Chạy bằng tài khoản SYSTEM hoặc SYSDBA)
-- ==============================================================================

-- 1.1 Tạo người dùng Quản trị cơ sở dữ liệu CLB
CREATE USER sports_admin IDENTIFIED BY AdminSecurePass2026#
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA UNLIMITED ON USERS;

-- Cấp quyền kết nối và khởi tạo tài nguyên cho admin
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO sports_admin;

-- 1.2 Thiết lập các Vai trò (Roles) theo mô hình kiểm soát truy cập dựa trên vai trò (RBAC)
-- Role 1: HLV (Coach Role) - Xem danh sách, cập nhật điểm tập và đội hình
CREATE ROLE coach_role;
GRANT CONNECT TO coach_role;
GRANT SELECT ON sports_admin.players TO coach_role;
GRANT SELECT, INSERT, UPDATE ON sports_admin.player_training TO coach_role;
GRANT SELECT, INSERT ON sports_admin.training_sessions TO coach_role;
GRANT SELECT ON sports_admin.v_player_performance_summary TO coach_role;
GRANT EXECUTE ON sports_admin.func_check_player_eligibility TO coach_role;

-- Role 2: Nhân viên thống kê trận đấu (Match Recorder Role)
CREATE ROLE match_recorder_role;
GRANT CONNECT TO match_recorder_role;
GRANT SELECT ON sports_admin.matches TO match_recorder_role;
GRANT SELECT, INSERT ON sports_admin.match_events TO match_recorder_role;
GRANT EXECUTE ON sports_admin.proc_record_match_result TO match_recorder_role;

-- Role 3: Nhân viên kế toán (Financial Staff Role)
CREATE ROLE finance_role;
GRANT CONNECT TO finance_role;
GRANT SELECT, INSERT, UPDATE ON sports_admin.finances TO finance_role;

-- 1.3 Tạo các tài khoản người dùng thực tế và gán Role tương ứng
-- Tài khoản HLV Park Hang Seo
CREATE USER user_coach_park IDENTIFIED BY ParkCoachPass2026#
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA 50M ON USERS;
GRANT coach_role TO user_coach_park;

-- Tài khoản Nhân viên thư ký trận đấu
CREATE USER user_recorder_linh IDENTIFIED BY LinhRecorder2026#
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
QUOTA 20M ON USERS;
GRANT match_recorder_role TO user_recorder_linh;

-- 1.4 Kiểm tra quyền và thu hồi quyền (REVOKE) khi cần thiết
-- Thu hồi quyền cập nhật điểm tập từ coach nếu phát hiện vi phạm
REVOKE UPDATE ON sports_admin.player_training FROM coach_role;


-- ==============================================================================
-- 2. KỊCH BẢN SAO LƯU VÀ PHỤC HỒI DỮ LIỆU (BACKUP & RECOVERY VỚI ORACLE DATA PUMP)
-- ==============================================================================

/*
BƯỚC 1: CẤU HÌNH THƯ MỤC DIRECTORY TRONG ORACLE SQL*PLUS / SQL DEVELOPER (Chạy quyền SYSDBA)
-----------------------------------------------------------------------------------------
1. Mở SQL*Plus hoặc SQL Developer đăng nhập bằng SYSDBA.
2. Tạo Directory vật lý trỏ vào thư mục trên ổ đĩa máy chủ:

   CREATE OR REPLACE DIRECTORY sports_backup_dir AS 'C:\oracle_backup';
   GRANT READ, WRITE ON DIRECTORY sports_backup_dir TO sports_admin;

BƯỚC 2: SAO LƯU TOÀN BỘ SCHEMA (LOGICAL EXPORT VỚI EXPDP)
---------------------------------------------------------
Mở Command Prompt (cmd) trên Windows và thực thi lệnh sau:

   expdp sports_admin/AdminSecurePass2026#@localhost:1521/XEPDB1 \
   schemas=sports_admin \
   directory=sports_backup_dir \
   dumpfile=sports_club_full_backup_%U.dmp \
   logfile=sports_club_export.log \
   filesize=2G

Giải thích tham số:
- schemas=sports_admin: Xuất toàn bộ cấu trúc bảng, view, trigger, dữ liệu thuộc schema.
- directory=sports_backup_dir: Đường dẫn lưu file dump.
- dumpfile: Tên file sao lưu có định dạng phân mảnh %U.
- logfile: Ghi lại toàn bộ nhật ký tiến trình sao lưu để kiểm tra lỗi.

BƯỚC 3: SAO LƯU CÁC BẢNG TRỌNG YẾU (TABLE-LEVEL EXPORT)
-------------------------------------------------------
Trong trường hợp chỉ cần sao lưu nhanh bảng cầu thủ và lịch thi đấu:

   expdp sports_admin/AdminSecurePass2026#@localhost:1521/XEPDB1 \
   tables=sports_admin.players,sports_admin.matches,sports_admin.match_events \
   directory=sports_backup_dir \
   dumpfile=sports_tables_backup.dmp \
   logfile=sports_tables_export.log

BƯỚC 4: KỊCH BẢN PHỤC HỒI TOÀN BỘ CƠ SỞ DỮ LIỆU KHI XẢY RA SỰ CỐ (DISASTER RECOVERY)
------------------------------------------------------------------------------------
Khi cơ sở dữ liệu gặp sự cố mất dữ liệu, quản trị viên sử dụng lệnh impdp:

   impdp sports_admin/AdminSecurePass2026#@localhost:1521/XEPDB1 \
   schemas=sports_admin \
   directory=sports_backup_dir \
   dumpfile=sports_club_full_backup_%U.dmp \
   logfile=sports_club_restore.log \
   table_exists_action=replace

Giải thích tham số phục hồi:
- table_exists_action=replace: Nếu bảng đã tồn tại nhưng dữ liệu bị hỏng, Data Pump sẽ tự động drop bảng cũ và tạo lại mới tinh từ bản backup.
- logfile: Kiểm tra lại các đối tượng đã được import thành công 100%.
*/

PROMPT Hoàn thành cấu hình phân quyền người dùng và kịch bản sao lưu phục hồi!
