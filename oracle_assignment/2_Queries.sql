-- A. BASIC QUERIES (5 câu truy vấn cơ bản)
-- 1. Lấy danh sách tất cả cầu thủ thuộc câu lạc bộ 'Tự do'
SELECT full_name, position, ovr 
FROM players 
WHERE club = 'Tự do' 
ORDER BY ovr DESC;

-- 2. Tìm các trận đấu diễn ra trong tháng 9 năm 2026
SELECT match_id, match_date, opponent_team 
FROM matches 
WHERE match_date >= TO_DATE('2026-09-01', 'YYYY-MM-DD') 
  AND match_date < TO_DATE('2026-10-01', 'YYYY-MM-DD');

-- 3. Hiển thị thông tin người quản lý hệ thống
SELECT id, username, role, created_at 
FROM app_users 
WHERE role = 'manager';

-- 4. Danh sách các buổi tập chú trọng vào 'Thể lực'
SELECT session_id, session_date, duration_minutes 
FROM training_sessions 
WHERE focus_area LIKE '%Thể lực%';

-- 5. Lấy danh sách cầu thủ có chỉ số PAC (Tốc độ) và SHO (Sút bóng) trên 80
SELECT full_name, pac, sho 
FROM players 
WHERE pac > 80 AND sho > 80;


-- B. NESTED QUERIES / SUBQUERIES (5 câu truy vấn lồng nhau)
-- 6. Tìm cầu thủ có chỉ số OVR cao nhất đội
SELECT full_name, position, ovr 
FROM players 
WHERE ovr = (SELECT MAX(ovr) FROM players);

-- 7. Lấy danh sách cầu thủ đã từng ghi bàn trong bất kỳ trận nào (dùng IN)
SELECT full_name 
FROM players 
WHERE id IN (SELECT player_id FROM match_events WHERE event_type = 'Goal');

-- 8. Tìm các trận đấu mà đội nhà ghi nhiều bàn hơn mức trung bình bàn thắng đội nhà
SELECT match_id, opponent_team, home_score 
FROM matches 
WHERE home_score > (SELECT AVG(home_score) FROM matches);

-- 9. Lấy tên các cầu thủ chưa từng tham gia buổi tập nào (dùng NOT EXISTS)
SELECT p.full_name 
FROM players p 
WHERE NOT EXISTS (
    SELECT 1 FROM player_training pt WHERE pt.player_id = p.id
);

-- 10. Tìm người quản lý được tạo tài khoản sớm nhất
SELECT username, created_at 
FROM app_users 
WHERE created_at = (SELECT MIN(created_at) FROM app_users);


-- C. GROUP BY / HAVING (5 câu truy vấn gom nhóm)
-- 11. Đếm số lượng cầu thủ theo từng vị trí thi đấu (FW, MF, DF, GK)
SELECT position, COUNT(*) AS total_players 
FROM players 
GROUP BY position;

-- 12. Tính tổng số bàn thắng của từng cầu thủ, chỉ lấy những người ghi trên 2 bàn
SELECT player_id, COUNT(*) AS total_goals 
FROM match_events 
WHERE event_type = 'Goal' 
GROUP BY player_id 
HAVING COUNT(*) > 2;

-- 13. Hiển thị điểm đánh giá buổi tập trung bình của từng cầu thủ
SELECT player_id, AVG(performance_rating) AS avg_rating 
FROM player_training 
GROUP BY player_id;

-- 14. Tổng số trận đấu theo từng loại (Giao hữu, Giải đấu)
SELECT match_type, COUNT(*) AS count_matches 
FROM matches 
GROUP BY match_type;

-- 15. Lấy danh sách các buổi tập có trung bình điểm đánh giá của toàn đội lớn hơn 7
SELECT session_id, AVG(performance_rating) AS avg_team_rating 
FROM player_training 
GROUP BY session_id 
HAVING AVG(performance_rating) > 7;


-- D. AGGREGATE / STATISTICAL QUERIES (5 câu truy vấn thống kê tổng hợp)
-- 16. Tính trung bình các chỉ số FIFA của toàn bộ cầu thủ trong đội
SELECT AVG(pac) AS avg_pac, AVG(sho) AS avg_sho, AVG(pas) AS avg_pas, 
       AVG(dri) AS avg_dri, AVG(def) AS avg_def, AVG(phy) AS avg_phy 
FROM players;

-- 17. Tìm chỉ số OVR cao nhất, thấp nhất và trung bình trong câu lạc bộ
SELECT MAX(ovr) AS max_ovr, MIN(ovr) AS min_ovr, ROUND(AVG(ovr), 2) AS avg_ovr 
FROM players;

-- 18. Tính tổng số bàn thắng đội nhà và đội khách trong tất cả các trận
SELECT SUM(home_score) AS total_home_goals, SUM(away_score) AS total_away_goals 
FROM matches;

-- 19. Đếm tổng số sự kiện thẻ phạt (Thẻ vàng, Thẻ đỏ) đã diễn ra
SELECT COUNT(*) AS total_cards 
FROM match_events 
WHERE event_type IN ('Yellow Card', 'Red Card');

-- 20. Tính tổng thời gian tập luyện (theo phút) trong tháng hiện tại
SELECT SUM(duration_minutes) AS total_training_time 
FROM training_sessions 
WHERE EXTRACT(MONTH FROM session_date) = EXTRACT(MONTH FROM SYSDATE);
