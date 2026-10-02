-- ==============================================================================
-- TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á - VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
-- ĐỀ THI KẾT THÚC HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
-- FILE: 03_SQL_QUERIES_20_TYPES.sql
-- MÔ TẢ: BỘ 20 CÂU TRUY VẤN SQL PHÂN CHIA THÀNH 4 NHÓM THEO ĐÚNG YÊU CẦU
-- ==============================================================================

-- ==============================================================================
-- NHÓM A: TRUY VẤN CƠ BẢN (BASIC QUERIES - 5 CÂU)
-- ==============================================================================

-- Câu 1: Lấy danh sách cầu thủ thuộc CLB có trạng thái thể lực hoàn hảo ('Fit'), sắp xếp theo chỉ số OVR giảm dần
SELECT id, full_name, position, jersey_number, ovr, salary
FROM players
WHERE health_status = 'Fit'
ORDER BY ovr DESC;

-- Câu 2: Tìm kiếm các trận đấu diễn ra trong tháng 9 năm 2026 với sân đấu tại 'Sân nhà'
SELECT match_id, match_date, opponent_team, match_type, home_score, away_score
FROM matches
WHERE match_date >= TO_DATE('2026-09-01', 'YYYY-MM-DD')
  AND match_date <= TO_DATE('2026-09-30', 'YYYY-MM-DD')
  AND venue = 'Sân nhà';

-- Câu 3: Lấy danh sách người dùng quản trị và huấn luyện viên trong hệ thống (role = 'admin' hoặc 'coach')
SELECT id, username, full_name, role, email, created_at
FROM app_users
WHERE role IN ('admin', 'coach')
ORDER BY role, full_name;

-- Câu 4: Liệt kê các buổi tập huấn có thời lượng từ 90 phút trở lên tại 'Sân tập 1'
SELECT session_id, session_date, focus_area, duration_minutes, coach_in_charge
FROM training_sessions
WHERE duration_minutes >= 90
  AND location = 'Sân tập 1'
ORDER BY session_date DESC;

-- Câu 5: Tìm kiếm cầu thủ có tốc độ (PAC) và chỉ số sút (SHO) đồng thời vượt ngưỡng 80 điểm
SELECT full_name, position, pac, sho, dri, ovr
FROM players
WHERE pac >= 80 AND sho >= 80
ORDER BY (pac + sho) DESC;


-- ==============================================================================
-- NHÓM B: TRUY VẤN LỒNG NHAU (NESTED QUERIES / SUBQUERIES - 5 CÂU)
-- ==============================================================================

-- Câu 6: Tìm thông tin cầu thủ có chỉ số OVR cao nhất trong toàn bộ câu lạc bộ (Dùng Scalar Subquery)
SELECT full_name, position, jersey_number, ovr, salary
FROM players
WHERE ovr = (SELECT MAX(ovr) FROM players);

-- Câu 7: Tìm các cầu thủ đã từng ghi bàn thắng ('Goal') trong các trận đấu (Dùng IN với Subquery)
SELECT id, full_name, position, jersey_number
FROM players
WHERE id IN (
    SELECT DISTINCT player_id
    FROM match_events
    WHERE event_type = 'Goal'
);

-- Câu 8: Tìm các trận đấu có số bàn thắng của đội nhà cao hơn mức bàn thắng trung bình của tất cả các trận
SELECT match_id, match_date, opponent_team, venue, home_score
FROM matches
WHERE home_score > (SELECT AVG(home_score) FROM matches);

-- Câu 9: Tìm danh sách cầu thủ chưa từng tham gia bất kỳ buổi tập huấn nào (Dùng NOT EXISTS)
SELECT p.id, p.full_name, p.position, p.health_status
FROM players p
WHERE NOT EXISTS (
    SELECT 1 
    FROM player_training pt 
    WHERE pt.player_id = p.id
);

-- Câu 10: Tìm các khoản chi phí (Expense) có giá trị lớn hơn mức chi phí trung bình của câu lạc bộ
SELECT trans_id, category, amount, trans_date, description
FROM finances
WHERE trans_type = 'Expense'
  AND amount > (
      SELECT AVG(amount) 
      FROM finances 
      WHERE trans_type = 'Expense'
  );


-- ==============================================================================
-- NHÓM C: TRUY VẤN GOM NHÓM VÀ THỐNG KÊ (GROUP BY & AGGREGATE - 5 CÂU)
-- ==============================================================================

-- Câu 11: Đếm số lượng cầu thủ và mức lương trung bình theo từng vị trí thi đấu (FW, MF, DF, GK)
SELECT position, 
       COUNT(*) AS total_players, 
       ROUND(AVG(ovr), 2) AS avg_ovr,
       TO_CHAR(ROUND(AVG(salary), 0), 'FM999,999,999') || ' VNĐ' AS avg_salary
FROM players
GROUP BY position
ORDER BY total_players DESC;

-- Câu 12: Thống kê tổng số bàn thắng được ghi bởi từng cầu thủ trong các trận đấu (chỉ hiển thị cầu thủ có từ 2 bàn trở lên)
SELECT p.full_name, 
       COUNT(e.event_id) AS total_goals_scored
FROM players p
JOIN match_events e ON p.id = e.player_id
WHERE e.event_type = 'Goal'
GROUP BY p.full_name
HAVING COUNT(e.event_id) >= 2
ORDER BY total_goals_scored DESC;

-- Câu 13: Thống kê số trận đấu, tổng bàn thắng và tổng bàn thua theo từng loại trận đấu (League, Cup)
SELECT match_type, 
       COUNT(*) AS number_of_matches,
       SUM(home_score) AS total_home_goals,
       SUM(away_score) AS total_away_goals
FROM matches
GROUP BY match_type;

-- Câu 14: Tính điểm đánh giá buổi tập trung bình và số buổi tham gia của từng cầu thủ
SELECT p.full_name, 
       COUNT(pt.record_id) AS sessions_attended,
       ROUND(AVG(pt.performance_rating), 2) AS avg_training_rating
FROM players p
JOIN player_training pt ON p.id = pt.player_id
WHERE pt.attendance_status = 'Present'
GROUP BY p.full_name
HAVING AVG(pt.performance_rating) >= 8.5
ORDER BY avg_training_rating DESC;

-- Câu 15: Thống kê tổng thu và tổng chi của câu lạc bộ, tính số dư chênh lệch ròng
SELECT trans_type, 
       COUNT(*) AS trans_count,
       SUM(amount) AS total_amount,
       ROUND(AVG(amount), 2) AS avg_trans_amount
FROM finances
GROUP BY trans_type;


-- ==============================================================================
-- NHÓM D: TRUY VẤN NÂNG CAO (WINDOW FUNCTIONS, CTE & PIVOT - 5 CÂU)
-- ==============================================================================

-- Câu 16: Xếp hạng cầu thủ theo số bàn thắng bằng hàm DENSE_RANK()
SELECT full_name, position, goals, assists,
       DENSE_RANK() OVER (ORDER BY goals DESC, assists DESC) AS rank_in_team
FROM players;

-- Câu 17: Tính lũy kế số bàn thắng của câu lạc bộ theo từng trận đấu qua thời gian (Running Total)
WITH MatchGoalsSummary AS (
    SELECT m.match_id, m.match_date, m.opponent_team, m.home_score
    FROM matches m
)
SELECT match_id, match_date, opponent_team, home_score,
       SUM(home_score) OVER (ORDER BY match_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_goals
FROM MatchGoalsSummary;

-- Câu 18: Lấy Top 2 cầu thủ có chỉ số OVR xuất sắc nhất trong TỪNG vị trí thi đấu (Sử dụng ROW_NUMBER và PARTITION BY)
WITH RankedSquad AS (
    SELECT full_name, position, ovr, pac, sho, pas,
           ROW_NUMBER() OVER (PARTITION BY position ORDER BY ovr DESC) AS rank_within_position
    FROM players
)
SELECT full_name, position, ovr, pac, sho, pas, rank_within_position
FROM RankedSquad
WHERE rank_within_position <= 2;

-- Câu 19: Tìm các cầu thủ có số đường kiến tạo cao hơn mức trung bình của những người CÙNG vị trí
WITH PositionalAvgAssists AS (
    SELECT position, AVG(assists) AS avg_pos_assists
    FROM players
    GROUP BY position
)
SELECT p.full_name, p.position, p.assists, 
       ROUND(a.avg_pos_assists, 2) AS avg_position_assists,
       (p.assists - ROUND(a.avg_pos_assists, 2)) AS assists_above_avg
FROM players p
JOIN PositionalAvgAssists a ON p.position = a.position
WHERE p.assists > a.avg_pos_assists;

-- Câu 20: Xoay dữ liệu (PIVOT) đếm số lượng cầu thủ theo các vị trí chủ chốt biến dòng thành cột
SELECT * FROM (
    SELECT position FROM players
)
PIVOT (
    COUNT(*) FOR position IN ('FW' AS forwards, 'MF' AS midfielders, 'DF' AS defenders, 'GK' AS goalkeepers)
);

PROMPT Hoàn thành chạy 20 câu truy vấn SQL đa dạng thuộc 4 nhóm nghiệp vụ!
