-- ==============================================================================
-- TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á - VIỆN ĐÀO TẠO VÀ HỢP TÁC QUỐC TẾ
-- ĐỀ THI KẾT THÚC HỌC PHẦN: ORACLE DATABASE MANAGEMENT SYSTEM
-- FILE: 04_PLSQL_PROGRAMMING.sql
-- MÔ TẢ: LẬP TRÌNH PL/SQL: VIEWS, FUNCTIONS, PROCEDURES, TRIGGERS, PACKAGES
-- SỬ DỤNG CẤU TRÚC ĐIỀU KHIỂN: IF-THEN-ELSE, FOR LOOP, EXCEPTION HANDLING
-- ==============================================================================

SET SERVEROUTPUT ON;

-- ==============================================================================
-- 1. VIEWS (KHUNG NHÌN DỮ LIỆU)
-- ==============================================================================

-- View 1: v_player_performance_summary - Tổng hợp năng lực toàn diện của cầu thủ
CREATE OR REPLACE VIEW v_player_performance_summary AS
SELECT 
    p.id AS player_id,
    p.full_name,
    p.position,
    p.jersey_number,
    p.health_status,
    p.ovr,
    p.goals,
    p.assists,
    p.matches,
    ROUND((p.goals + p.assists) / NULLIF(p.matches, 0), 2) AS goal_involvement_per_match,
    NVL(AVG(pt.performance_rating), 0) AS avg_training_rating,
    c.club_name
FROM players p
JOIN clubs c ON p.club_id = c.club_id
LEFT JOIN player_training pt ON p.id = pt.player_id
GROUP BY p.id, p.full_name, p.position, p.jersey_number, p.health_status, p.ovr, p.goals, p.assists, p.matches, c.club_name;

-- View 2: v_match_scoreboard - Bảng tổng hợp diễn biến và kết quả các trận đấu
CREATE OR REPLACE VIEW v_match_scoreboard AS
SELECT 
    m.match_id,
    m.match_date,
    c.club_name AS home_team,
    m.opponent_team,
    m.home_score || ' - ' || m.away_score AS final_score,
    CASE 
        WHEN m.home_score > m.away_score THEN 'Thắng (Win)'
        WHEN m.home_score < m.away_score THEN 'Thua (Loss)'
        ELSE 'Hòa (Draw)'
    END AS match_result,
    m.match_type,
    m.venue,
    m.attendance
FROM matches m
JOIN clubs c ON m.club_id = c.club_id;


-- ==============================================================================
-- 2. FUNCTIONS (HÀM CÓ DÙNG CẤU TRÚC ĐIỀU KHIỂN IF-THEN-ELSE)
-- ==============================================================================

-- Hàm 1: func_calculate_win_rate - Tính tỷ lệ thắng trận của câu lạc bộ
CREATE OR REPLACE FUNCTION func_calculate_win_rate(p_club_id IN NUMBER)
RETURN NUMBER IS
    v_total_matches NUMBER := 0;
    v_wins NUMBER := 0;
    v_win_rate NUMBER := 0;
BEGIN
    SELECT COUNT(*) INTO v_total_matches
    FROM matches
    WHERE club_id = p_club_id;

    IF v_total_matches = 0 THEN
        RETURN 0;
    ELSE
        SELECT COUNT(*) INTO v_wins
        FROM matches
        WHERE club_id = p_club_id AND home_score > away_score;

        v_win_rate := ROUND((v_wins / v_total_matches) * 100, 2);
        RETURN v_win_rate;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END func_calculate_win_rate;
/

-- Hàm 2: func_check_player_eligibility - Kiểm tra điều kiện ra sân của cầu thủ (Dùng IF-ELSIF-ELSE)
CREATE OR REPLACE FUNCTION func_check_player_eligibility(p_player_id IN NUMBER)
RETURN VARCHAR2 IS
    v_health VARCHAR2(20);
    v_yellow_cards NUMBER := 0;
    v_red_cards NUMBER := 0;
    v_status VARCHAR2(100);
BEGIN
    -- Lấy tình trạng thể lực
    SELECT health_status INTO v_health
    FROM players
    WHERE id = p_player_id;

    -- Kiểm tra số thẻ phạt
    SELECT COUNT(CASE WHEN event_type = 'Yellow Card' THEN 1 END),
           COUNT(CASE WHEN event_type = 'Red Card' THEN 1 END)
    INTO v_yellow_cards, v_red_cards
    FROM match_events
    WHERE player_id = p_player_id;

    -- Đánh giá điều kiện ra sân
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
    WHEN NO_DATA_FOUND THEN
        RETURN 'LỖI: Không tìm thấy cầu thủ có mã ' || p_player_id;
    WHEN OTHERS THEN
        RETURN 'LỖI HỆ THỐNG: ' || SQLERRM;
END func_check_player_eligibility;
/


-- ==============================================================================
-- 3. STORED PROCEDURES (THỦ TỤC CÓ DÙNG VÒNG LẶP FOR VÀ BẪY LỖI EXCEPTION)
-- ==============================================================================

-- Thủ tục 1: proc_record_match_result - Cập nhật tỷ số trận đấu với Exception Handling
CREATE OR REPLACE PROCEDURE proc_record_match_result(
    p_match_id IN NUMBER,
    p_home_score IN NUMBER,
    p_away_score IN NUMBER
) IS
    v_count NUMBER;
    e_invalid_score EXCEPTION;
BEGIN
    -- Kiểm tra tỷ số hợp lệ
    IF p_home_score < 0 OR p_away_score < 0 THEN
        RAISE e_invalid_score;
    END IF;

    -- Kiểm tra trận đấu có tồn tại
    SELECT COUNT(*) INTO v_count
    FROM matches
    WHERE match_id = p_match_id;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Lỗi: Không tìm thấy trận đấu có ID = ' || p_match_id);
    END IF;

    -- Cập nhật kết quả
    UPDATE matches
    SET home_score = p_home_score,
        away_score = p_away_score
    WHERE match_id = p_match_id;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Thành công: Đã cập nhật tỷ số trận đấu ' || p_match_id || ' thành [' || p_home_score || ' - ' || p_away_score || ']');
EXCEPTION
    WHEN e_invalid_score THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('LỖI DỮ LIỆU: Điểm số trận đấu không được âm!');
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('LỖI HỆ THỐNG: ' || SQLERRM);
END proc_record_match_result;
/

-- Thủ tục 2: proc_batch_evaluate_training - Duyệt danh sách bằng FOR LOOP cập nhật chỉ số OVR
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
    DBMS_OUTPUT.PUT_LINE('=== BẮT ĐẦU XÉT THƯỞNG TĂNG CHỈ SỐ TẬP LUYỆN ===');
    
    -- Dùng vòng lặp FOR quét qua Cursor
    FOR rec IN c_excellent_players LOOP
        IF rec.ovr < 99 THEN
            UPDATE players
            SET ovr = ovr + 1,
                pac = LEAST(pac + 1, 99),
                sho = LEAST(sho + 1, 99)
            WHERE id = rec.id;

            v_updated_count := v_updated_count + 1;
            DBMS_OUTPUT.PUT_LINE('-> Cầu thủ ' || rec.full_name || ' (Điểm tập: ' || ROUND(rec.avg_rating, 2) || ') được tăng OVR từ ' || rec.ovr || ' lên ' || (rec.ovr + 1));
        END IF;
    END LOOP;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('=== HOÀN TẤT: Đã cập nhật chỉ số cho ' || v_updated_count || ' cầu thủ xuất sắc! ===');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('LỖI TRONG QUÁ TRÌNH XỬ LÝ: ' || SQLERRM);
END proc_batch_evaluate_training;
/


-- ==============================================================================
-- 4. DATABASE TRIGGERS (TRIGGER TỰ ĐỘNG HÓA NGHIỆP VỤ)
-- ==============================================================================

-- Trigger 1: trg_update_player_stats_on_goal - Tự động cộng số bàn thắng khi có sự kiện Goal
CREATE OR REPLACE TRIGGER trg_update_player_stats_on_goal
AFTER INSERT ON match_events
FOR EACH ROW
BEGIN
    IF :NEW.event_type = 'Goal' THEN
        UPDATE players
        SET goals = goals + 1
        WHERE id = :NEW.player_id;
    ELSIF :NEW.event_type = 'Assist' THEN
        UPDATE players
        SET assists = assists + 1
        WHERE id = :NEW.player_id;
    END IF;
END;
/

-- Trigger 2: trg_check_jersey_number - Kiểm tra trùng số áo thi đấu trong cùng CLB (BEFORE INSERT OR UPDATE)
CREATE OR REPLACE TRIGGER trg_check_jersey_number
BEFORE INSERT OR UPDATE OF jersey_number, club_id ON players
FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM players
    WHERE club_id = :NEW.club_id
      AND jersey_number = :NEW.jersey_number
      AND id != NVL(:NEW.id, -1);

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'LỖI RÀNG BUỘC: Số áo ' || :NEW.jersey_number || ' đã được đăng ký cho cầu thủ khác trong câu lạc bộ!');
    END IF;
END;
/


-- ==============================================================================
-- 5. ORACLE PACKAGES (GÓI NGHIỆP VỤ DOANH NGHIỆP)
-- ==============================================================================

-- Package Specification
CREATE OR REPLACE PACKAGE pkg_sports_management IS
    c_package_version CONSTANT VARCHAR2(10) := '2.0.0';
    
    FUNCTION get_club_top_scorer(p_club_id IN NUMBER) RETURN VARCHAR2;
    PROCEDURE register_new_match(
        p_club_id IN NUMBER,
        p_match_date IN DATE,
        p_opponent IN VARCHAR2,
        p_venue IN VARCHAR2,
        p_type IN VARCHAR2
    );
END pkg_sports_management;
/

-- Package Body
CREATE OR REPLACE PACKAGE BODY pkg_sports_management IS

    FUNCTION get_club_top_scorer(p_club_id IN NUMBER) RETURN VARCHAR2 IS
        v_name VARCHAR2(100);
        v_goals NUMBER;
    BEGIN
        SELECT full_name, goals
        INTO v_name, v_goals
        FROM (
            SELECT full_name, goals
            FROM players
            WHERE club_id = p_club_id
            ORDER BY goals DESC
        )
        WHERE ROWNUM = 1;

        RETURN v_name || ' (' || v_goals || ' bàn)';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 'Chưa có dữ liệu bàn thắng';
        WHEN OTHERS THEN
            RETURN 'Lỗi: ' || SQLERRM;
    END get_club_top_scorer;

    PROCEDURE register_new_match(
        p_club_id IN NUMBER,
        p_match_date IN DATE,
        p_opponent IN VARCHAR2,
        p_venue IN VARCHAR2,
        p_type IN VARCHAR2
    ) IS
    BEGIN
        INSERT INTO matches (club_id, match_date, opponent_team, venue, match_type, home_score, away_score)
        VALUES (p_club_id, p_match_date, p_opponent, p_venue, p_type, 0, 0);
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Đã lên lịch thành công trận đấu gặp ' || p_opponent || ' vào ngày ' || TO_CHAR(p_match_date, 'DD/MM/YYYY'));
    END register_new_match;

END pkg_sports_management;
/

PROMPT Đã biên dịch toàn bộ Views, Functions, Procedures, Triggers và Packages thành công!
