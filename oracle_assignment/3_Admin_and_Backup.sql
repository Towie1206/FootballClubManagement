-- 4.1 Tạo người dùng và cấp quyền (Chạy bằng tài khoản SYSTEM / SYSDBA)
-- Tạo user quản trị Database cho dự án FC Manager
CREATE USER fcm_admin IDENTIFIED BY fcm_password_123;

-- Cấp quyền kết nối và tạo bảng
GRANT CONNECT, RESOURCE TO fcm_admin;

-- Cấp không gian lưu trữ (Quota) trên tablespace
ALTER USER fcm_admin QUOTA UNLIMITED ON USERS;

-- Tạo một Role cụ thể cho Nhân viên Nhập liệu (Chỉ được phép xem và thêm)
CREATE ROLE data_entry_role;
GRANT SELECT, INSERT, UPDATE ON fcm_admin.players TO data_entry_role;
GRANT SELECT, INSERT ON fcm_admin.matches TO data_entry_role;
GRANT SELECT, INSERT ON fcm_admin.match_events TO data_entry_role;

-- Tạo một user nhân viên và gán Role
CREATE USER staff_user IDENTIFIED BY staff_123;
GRANT CONNECT TO staff_user;
GRANT data_entry_role TO staff_user;


-- 4.2 Lệnh Sao lưu và Phục hồi (Backup & Recovery)
-- Các lệnh này không chạy trong SQL*Plus mà chạy trên Terminal (Command Prompt) của hệ điều hành.

/*
=====================================================
 HƯỚNG DẪN BACKUP (XUẤT DỮ LIỆU) DÙNG DATA PUMP
=====================================================
1. Mở Command Prompt (cmd) với quyền Administrator.
2. Tạo thư mục chứa file backup, ví dụ: C:\oracle_backup
3. Trong SQL*Plus, khai báo directory:
   CREATE DIRECTORY backup_dir AS 'C:\oracle_backup';
   GRANT READ, WRITE ON DIRECTORY backup_dir TO fcm_admin;
4. Chạy lệnh sao lưu (Export) toàn bộ schema:

   expdp fcm_admin/fcm_password_123@orcl schemas=fcm_admin directory=backup_dir dumpfile=fcm_backup_2026.dmp logfile=fcm_backup.log

=====================================================
 HƯỚNG DẪN RECOVERY (PHỤC HỒI DỮ LIỆU) DÙNG DATA PUMP
=====================================================
1. Khi database bị hỏng hoặc cần chuyển sang máy khác, dùng lệnh Import:

   impdp fcm_admin/fcm_password_123@orcl schemas=fcm_admin directory=backup_dir dumpfile=fcm_backup_2026.dmp logfile=fcm_restore.log

2. Nếu bảng đã tồn tại và muốn ghi đè dữ liệu, thêm tham số: table_exists_action=replace
*/
