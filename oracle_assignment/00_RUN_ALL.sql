-- ==============================================================================
-- TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á - VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
-- ĐỀ THI KẾT THÚC HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
-- MÃ BỘ ĐỀ: A2 - ĐỀ TÀI: TOPIC 3 - SPORTS CLUB MANAGEMENT SOFTWARE
-- FILE: 00_RUN_ALL.sql
-- MÔ TẢ: SCRIPT TỔNG HỢP CHẠY TẤT CẢ CÁC BƯỚC KHỞI TẠO CSDL TRONG 1 CLICK
-- ==============================================================================

SET ECHO ON;
SET SERVEROUTPUT ON;
SPOOL final_exam_execution.log;

PROMPT ====================================================================
PROMPT 1. KHOI TAO SCHEMA VA RANG BUOC TOAN VEN (TABLES & CONSTRAINTS)
PROMPT ====================================================================
@@01_SCHEMA_AND_CONSTRAINTS.sql

PROMPT ====================================================================
PROMPT 2. CHEN DU LIEU MAU THUC TE (INSERT SAMPLE DATA)
PROMPT ====================================================================
@@02_INSERT_SAMPLE_DATA.sql

PROMPT ====================================================================
PROMPT 3. THI HANH 20 CAU TRUY VAN SQL DA DANG (SQL QUERIES)
PROMPT ====================================================================
@@03_SQL_QUERIES_20_TYPES.sql

PROMPT ====================================================================
PROMPT 4. BIEN DICH CAC DOI TUONG PL/SQL (VIEWS, FUNCTIONS, PROCEDURES, TRIGGERS, PACKAGES)
PROMPT ====================================================================
@@04_PLSQL_PROGRAMMING.sql

PROMPT ====================================================================
PROMPT 5. THIET LAP PHAN QUYEN NGUOI DUNG VA BAN SAO LUU (USER & BACKUP)
PROMPT ====================================================================
@@05_USER_MANAGEMENT_AND_BACKUP.sql

PROMPT ====================================================================
PROMPT >>> CHUC MUNG! TOAN BO HE THONG CSDL ORACLE DA DUOC KHOI TAO 100% <<<
PROMPT ====================================================================

SPOOL OFF;
