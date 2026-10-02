-- ==============================================================================
-- TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á - VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
-- ĐỀ THI KẾT THÚC HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
-- FILE: 02_INSERT_SAMPLE_DATA.sql
-- MÔ TẢ: CHÈN DỮ LIỆU MẪU ĐẦY ĐỦ VÀO 8 BẢNG TRONG HỆ THỐNG
-- ==============================================================================

-- 1. Chèn dữ liệu bảng APP_USERS
INSERT INTO app_users (username, password_hash, role, full_name, email)
VALUES ('admin', 'hash_admin_secret_2026', 'admin', 'Nguyễn Viết Hùng (Admin)', 'admin.hung@eaut.edu.vn');

INSERT INTO app_users (username, password_hash, role, full_name, email)
VALUES ('manager_an', 'hash_an_pass_123', 'manager', 'Nguyễn Văn An (Giám đốc CLB)', 'an.nguyen@fcmanager.vn');

INSERT INTO app_users (username, password_hash, role, full_name, email)
VALUES ('coach_park', 'hash_park_pass_456', 'coach', 'Park Hang Seo (HLV Trưởng)', 'park@fcmanager.vn');

INSERT INTO app_users (username, password_hash, role, full_name, email)
VALUES ('coach_tro', 'hash_tro_pass_789', 'coach', 'Philippe Troussier (HLV Thể lực)', 'troussier@fcmanager.vn');

INSERT INTO app_users (username, password_hash, role, full_name, email)
VALUES ('staff_linh', 'hash_linh_pass_321', 'staff', 'Trần Thùy Linh (Y tế/Hậu cần)', 'linh.tran@fcmanager.vn');

-- 2. Chèn dữ liệu bảng CLUBS
INSERT INTO clubs (club_name, short_name, founded_year, stadium_name, stadium_capacity, head_coach, city, budget)
VALUES ('Đông Á Thanh Hóa FC', 'DATH', 2009, 'Sân vận động Thanh Hóa', 14000, 'Velizar Popov', 'Thanh Hóa', 45000000000.00);

INSERT INTO clubs (club_name, short_name, founded_year, stadium_name, stadium_capacity, head_coach, city, budget)
VALUES ('Hà Nội FC', 'HNFC', 2006, 'Sân vận động Hàng Đẫy', 22500, 'Daiki Iwamasa', 'Hà Nội', 85000000000.00);

INSERT INTO clubs (club_name, short_name, founded_year, stadium_name, stadium_capacity, head_coach, city, budget)
VALUES ('Hoàng Anh Gia Lai', 'HAGL', 2001, 'Sân vận động Pleiku', 12000, 'Vũ Tiến Thành', 'Gia Lai', 38000000000.00);

-- 3. Chèn dữ liệu bảng PLAYERS (CLB Đông Á Thanh Hóa - club_id = 1)
INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Nguyễn Quang Hải', 'MF', 19, 'Fit', 84, 18, 7, 11, 4, 82, 85, 88, 86, 60, 72, 35000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Nguyễn Tiến Linh', 'FW', 22, 'Fit', 83, 20, 14, 4, 5, 81, 86, 75, 78, 45, 84, 32000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Đoàn Văn Hậu', 'DF', 5, 'Injured', 81, 12, 2, 5, 1, 84, 70, 78, 79, 82, 86, 30000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Đặng Văn Lâm', 'GK', 1, 'Fit', 82, 19, 0, 0, 3, 50, 40, 72, 45, 85, 84, 28000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Nguyễn Hoàng Đức', 'MF', 28, 'Fit', 85, 21, 6, 12, 6, 80, 81, 89, 87, 74, 82, 40000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Quế Ngọc Hải', 'DF', 3, 'Fit', 82, 17, 1, 2, 2, 72, 60, 76, 70, 85, 85, 27000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Phạm Tuấn Hải', 'FW', 10, 'Fit', 83, 20, 11, 6, 3, 86, 84, 79, 83, 55, 80, 29000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Vũ Văn Thanh', 'DF', 17, 'Fit', 80, 16, 3, 4, 1, 85, 73, 76, 78, 77, 81, 25000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Nguyễn Tuấn Anh', 'MF', 11, 'Resting', 79, 14, 1, 5, 1, 74, 72, 85, 84, 70, 68, 24000000.00);

INSERT INTO players (club_id, full_name, position, jersey_number, health_status, ovr, matches, goals, assists, mvp, pac, sho, pas, dri, def, phy, salary)
VALUES (1, 'Bùi Tiến Dũng', 'DF', 4, 'Fit', 80, 15, 1, 1, 1, 74, 55, 71, 68, 83, 81, 23000000.00);

-- 4. Chèn dữ liệu bảng MATCHES
INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score, referee_name, attendance)
VALUES (1, TO_DATE('2026-09-05', 'YYYY-MM-DD'), 'Hải Phòng FC', 'Sân nhà', 'League', 3, 1, 'Ngô Duy Lân', 12500);

INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score, referee_name, attendance)
VALUES (1, TO_DATE('2026-09-12', 'YYYY-MM-DD'), 'Nam Định FC', 'Sân khách', 'League', 2, 2, 'Hoàng Ngọc Hà', 18000);

INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score, referee_name, attendance)
VALUES (1, TO_DATE('2026-09-19', 'YYYY-MM-DD'), 'Bình Định FC', 'Sân nhà', 'Cup', 2, 0, 'Nguyễn Mạnh Hải', 9500);

INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score, referee_name, attendance)
VALUES (1, TO_DATE('2026-09-26', 'YYYY-MM-DD'), 'Viettel FC', 'Sân khách', 'League', 1, 0, 'Trần Đình Thịnh', 15200);

INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score, referee_name, attendance)
VALUES (1, TO_DATE('2026-10-03', 'YYYY-MM-DD'), 'Công An Hà Nội', 'Sân nhà', 'League', 0, 0, 'Ngô Duy Lân', 14000);

-- 5. Chèn dữ liệu bảng MATCH_EVENTS
INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (1, 1, 'Goal', 18, 'Sút phạt hiểm hóc từ cự ly 25m');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (1, 2, 'Goal', 42, 'Đánh đầu cận thành sau quả tạt góc');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (1, 5, 'Assist', 42, 'Tạt bóng chuẩn xác từ cánh phải');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (1, 7, 'Goal', 76, 'Đột phá vòng cấm dứt điểm chéo góc');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (1, 6, 'Yellow Card', 60, 'Phạm lỗi chiến thuật ngăn chặn phản công');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (2, 2, 'Goal', 35, 'Đệm bóng cận thành');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (2, 7, 'Goal', 88, 'Sút xa gỡ hòa ngoạn mục');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (3, 1, 'Goal', 12, 'Dứt điểm má trong chân trái');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (3, 2, 'Goal', 65, 'Đánh đầu từ pha bóng cố định');

INSERT INTO match_events (match_id, player_id, event_type, minute_occurred, description)
VALUES (4, 5, 'Goal', 82, 'Solo qua 2 hậu vệ ghi bàn duy nhất trận đấu');

-- 6. Chèn dữ liệu bảng TRAINING_SESSIONS
INSERT INTO training_sessions (club_id, session_date, focus_area, duration_minutes, location, coach_in_charge)
VALUES (1, TO_DATE('2026-09-01', 'YYYY-MM-DD'), 'Tăng cường Thể lực & Sức bền', 120, 'Phòng Gym CLB', 'Philippe Troussier');

INSERT INTO training_sessions (club_id, session_date, focus_area, duration_minutes, location, coach_in_charge)
VALUES (1, TO_DATE('2026-09-03', 'YYYY-MM-DD'), 'Chiến thuật Phản công Nhanh', 90, 'Sân tập 1', 'Park Hang Seo');

INSERT INTO training_sessions (club_id, session_date, focus_area, duration_minutes, location, coach_in_charge)
VALUES (1, TO_DATE('2026-09-10', 'YYYY-MM-DD'), 'Chống Tình huống Cố định', 90, 'Sân tập 2', 'Velizar Popov');

INSERT INTO training_sessions (club_id, session_date, focus_area, duration_minutes, location, coach_in_charge)
VALUES (1, TO_DATE('2026-09-17', 'YYYY-MM-DD'), 'Phối hợp Nhỏ & Kiểm soát Bóng', 105, 'Sân tập 1', 'Park Hang Seo');

INSERT INTO training_sessions (club_id, session_date, focus_area, duration_minutes, location, coach_in_charge)
VALUES (1, TO_DATE('2026-09-24', 'YYYY-MM-DD'), 'Rèn luyện Dứt điểm & Penalty', 80, 'Sân tập 1', 'Velizar Popov');

-- 7. Chèn dữ liệu bảng PLAYER_TRAINING
INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (1, 1, 9.2, 'Present', 'Thể lực sung mãn, hoàn thành 100% giáo án');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (1, 2, 8.8, 'Present', 'Sức rướn tốt, khả năng tăng tốc ấn tượng');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (1, 3, 6.0, 'Excused', 'Chấn thương dây chằng, chỉ tập phục hồi nhẹ');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (1, 5, 9.5, 'Present', 'Cầu thủ xuất sắc nhất buổi tập thể lực');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (2, 1, 9.0, 'Present', 'Chuyền bóng sắc bén, nhãn quan chiến thuật tuyệt hảo');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (2, 2, 8.5, 'Present', 'Chạy chỗ thông minh');

INSERT INTO player_training (session_id, player_id, performance_rating, attendance_status, coach_notes)
VALUES (2, 7, 9.1, 'Present', 'Khả năng độc lập tác chiến rất cao');

-- 8. Chèn dữ liệu bảng FINANCES
INSERT INTO finances (club_id, trans_type, category, amount, trans_date, description)
VALUES (1, 'Revenue', 'Tiền bán vé', 450000000.00, TO_DATE('2026-09-05', 'YYYY-MM-DD'), 'Doanh thu bán vé trận gặp Hải Phòng');

INSERT INTO finances (club_id, trans_type, category, amount, trans_date, description)
VALUES (1, 'Revenue', 'Tài trợ thương mại', 5000000000.00, TO_DATE('2026-09-01', 'YYYY-MM-DD'), 'Nhà tài trợ Tập đoàn Đông Á giải ngân Quý 3');

INSERT INTO finances (club_id, trans_type, category, amount, trans_date, description)
VALUES (1, 'Expense', 'Lương cầu thủ & HLV', 650000000.00, TO_DATE('2026-09-10', 'YYYY-MM-DD'), 'Chi trả lương đợt 1 tháng 9/2026');

INSERT INTO finances (club_id, trans_type, category, amount, trans_date, description)
VALUES (1, 'Expense', 'Trang thiết bị & Y tế', 180000000.00, TO_DATE('2026-09-15', 'YYYY-MM-DD'), 'Nhập khẩu thiết bị vật lý trị liệu từ Đức');

INSERT INTO finances (club_id, trans_type, category, amount, trans_date, description)
VALUES (1, 'Expense', 'Di chuyển & Khách sạn', 95000000.00, TO_DATE('2026-09-25', 'YYYY-MM-DD'), 'Chi phí thi đấu sân khách trận gặp Viettel FC');

COMMIT;
PROMPT Đã chèn dữ liệu mẫu thực tế thành công vào toàn bộ 8 bảng!
