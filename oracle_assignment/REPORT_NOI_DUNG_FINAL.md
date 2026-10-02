# REPORT OF FINAL PROJECT: COURSEWORK
## TOPIC 3: DEVELOP A DATABASE FOR SPORTS CLUB MANAGEMENT SOFTWARE
**Course:** Oracle Database Management System  
**Class:** DCIT.15  
**Instructor:** Dr. Nguyen Viet Hung  
**Institution:** International Training and Cooperation Institute (ITCI), East Asia University of Technology (EAUT)  

---

### GROUP MEMBERS AND TASK ASSIGNMENTS

| No. | Student Name | Student ID | Assigned Tasks | Contribution |
| :---: | :--- | :---: | :--- | :---: |
| 1 | **Nguyen Cong Bang** | **24003534** | Database Schema Design (3NF), ERD Modeling, PL/SQL Packages, Triggers, Views, Report Writing | 90% |
| 2 | **Nguyen Trung Kien** | **24001449** | Implementation of 20 SQL Queries (Basic, Nested, Group By, Aggregate), User Management & Data Pump Backup | 5% |
| 3 | **Nguyen Thanh Nam** | **24004130** | Android Mobile Client Application, Business-Process UI, Radar Chart Visualization, Testing | 5% |

---

# 1.1. INTRODUCTION

### 1. Overview of the Selected Topic
In modern professional and grassroots athletics, sports club management involves handling large amounts of critical data every day: athlete profiles, FIFA-style physical attributes, match fixtures, in-game performance statistics (goals, assists, cards, substitutions), training schedules, and health records. Traditionally, amateur and semi-professional clubs in Vietnam rely heavily on manual spreadsheets (Microsoft Excel) or instant messaging groups (Zalo, Messenger) to manage club operations. This conventional approach leads to severe operational bottlenecks:
- **Data Redundancy and Human Inconsistencies:** Duplication of records, conflicting squad numbers, or missing player statistics.
- **Absence of Integrity Constraints:** Spreadsheets do not enforce strict database-level checks (e.g., player skill ratings being assigned negative numbers or values exceeding 100).
- **Concurrency Conflicts:** Multiple coaches or team managers updating match outcomes simultaneously cause data overwrites and loss.
- **Lack of Centralized Security and Backup:** Spreadsheets are prone to file corruption and unauthorized alterations with no audit trail or rollback mechanisms.

To solve these pressing challenges, our research group selected **Topic 3: "Develop a database for sports club management software"**. The core objective of this project is to architect, develop, and deploy an enterprise-grade Relational Database Management System using **Oracle Database 19c**. The database is designed strictly in Third Normal Form (3NF) and features advanced PL/SQL stored logic (Triggers, Packages, Functions, Procedures), robust Role-Based Access Control (RBAC), and automated Oracle Data Pump backup/recovery. 

Furthermore, to prove real-world usability and support field management, we linked this Oracle backend to a native Android mobile application. Coaches can perform real-time squad management, filter players, execute business processes (post-match performance recording), and analyze data through interactive Radar Charts and automated aggregate statistical reports.

---

# CHAPTER 1. THEORETICAL FOUNDATION

## 1.1. Introduction to the Database Management System (DBMS)
The foundational backbone of our software is **Oracle Database 19c**, one of the world's premier Enterprise Object-Relational Database Management Systems (ORDBMS). Oracle was chosen over lightweight relational systems (such as SQLite or MySQL) due to its superior transaction processing (OLTP) capabilities, high-availability architecture, and military-grade security.

### 1. Oracle System Architecture (Instance and Storage)
The architecture of Oracle Database is split into two primary components:
1. **Oracle Instance (Memory Structures and Background Processes):**
   - **System Global Area (SGA):** The shared memory region allocated upon database startup.
     - *Database Buffer Cache:* Caches data blocks read directly from storage disk, optimizing input/output (I/O) response times.
     - *Shared Pool:* Holds parsed SQL/PLSQL execution plans (Library Cache) and data dictionary metadata.
     - *Redo Log Buffer:* Circular buffer logging all transactional changes prior to disk write, guaranteeing the durability of committed transactions.
   - **Program Global Area (PGA):** Non-shared memory dedicated to each user server process, handling private session variables, sorting operations, and SQL cursors.
   - **Background Processes:** Essential processes including `DBWn` (Database Writer - writes modified buffers to datafiles), `LGWR` (Log Writer - writes redo log entries to disk upon `COMMIT`), `CKPT` (Checkpoint - updates datafile headers), and `SMON`/`PMON` (System and Process monitors for crash recovery and connection cleanups).
2. **Physical and Logical Database Storage:**
   - *Physical Storage:* Datafiles (`.dbf`), Control Files (`.ctl`), and Online Redo Log Files (`.log`).
   - *Logical Storage Hierarchy:* Database $\rightarrow$ Tablespaces $\rightarrow$ Segments $\rightarrow$ Extents $\rightarrow$ Data Blocks (8 KB).

### 2. ACID Guarantees and Multi-Version Concurrency Control (MVCC)
Oracle strictly maintains the four fundamental properties of database transactions:
- **Atomicity:** All operations within a transaction succeed together or are completely rolled back.
- **Consistency:** Database transitions only from one valid state to another, satisfying all integrity constraints.
- **Isolation:** Concurrent transactions execute without mutual interference. Through Undo Segments, Oracle provides statement-level and transaction-level read consistency without locking read operations ("Readers never block writers, and writers never block readers").
- **Durability:** Once a transaction is committed, its modifications survive any subsequent power outage or server crash.

### 3. Procedural Language Extension (PL/SQL Engine)
Oracle features an embedded, high-performance **PL/SQL runtime engine**. PL/SQL extends standard SQL with procedural constructs (loops, conditionals, exception handling, packages). By executing business logic directly within the database kernel rather than transferring raw records over the network to the application server, network latency is minimized and data security is maintained at the data layer.

## 1.2. Introduction to the Tools Used
The development and evaluation of this system utilized the following software tools:
1. **Oracle SQL Developer:** The official integrated graphical development environment (IDE) provided by Oracle Corporation. Used for composing SQL scripts, authoring and debugging PL/SQL units, viewing execution explain plans, and monitoring database sessions.
2. **Oracle SQL Developer Data Modeler:** A graphical data modeling tool utilized to design Entity-Relationship Diagrams (ERD), map relational schemas, and forward-engineer DDL scripts with Primary/Foreign Key constraints.
3. **Oracle Data Pump (`expdp` / `impdp`):** High-speed server-side utilities used for metadata and bulk data export/import, enabling schema migration and disaster recovery backups.
4. **Node.js & Express Middleware:** A server-side RESTful API layer that interfaces directly with Oracle Database using the official `oracledb` native driver. Handles token authentication and serves data to client devices.
5. **Android Studio (Java):** The primary IDE used to develop the mobile client application. Incorporates Android SDK 35, MVVM Clean Architecture, Retrofit 2 for HTTP communication, and `MPAndroidChart` for polygon Radar Chart visual analytics.

---

# CHAPTER 2. DATABASE DESIGN AND DEVELOPMENT

## 2.1. Application Functions
The football club management software implements four core application function categories as required by the course syllabus:

### 1. Add, Edit, and Delete Data with Constraint Checking
- **Add Player (Tân Binh / Chiêu Mộ):** Allows the manager to insert new player profiles. Input validation prevents submission if required fields are blank, jersey numbers duplicate within the club, or skill ratings deviate from the legitimate $0 - 100$ range.
- **Edit Player (Sửa Hồ Sơ):** Enables modifications to squad number, player position, current club, or physical attributes, triggering automatic recalculation of overall ratings.
- **Delete Player (Xóa Cầu Thủ):** Safely removes a player record with an interactive confirmation dialog, enforcing `ON DELETE CASCADE` constraints across historical match events and attendance tables.

### 2. Implement Business-Process Functions
- **Match Event and Performance Recording (Ghi Nhận Kết Quả Trận Đấu):** Following each matchday, the coaching staff logs individual player contributions: goals scored, assists provided, man-of-the-match (MVP) awards, and post-match physical health status (Fit vs. Injured).
- **Automated State Synchronization:** Submitting a match performance report automatically increments the player's total match appearances (`matches = matches + 1`), accumulates total goals and assists, and updates the squad availability register without manual data entry.

### 3. Quick and Advanced Search
- **Quick Search:** Instantaneous real-time filtering by player full name via a dynamic search query bar.
- **Advanced Filtering and Multi-Criteria Sort:** Specialized filter buttons allowing coaches to isolate specific playing positions:
  - Forwards (`FW` - Tiền đạo)
  - Midfielders (`MF` - Tiền vệ)
  - Defenders (`DF` - Hậu vệ)
  - Goalkeepers (`GK` - Thủ môn)
- **Attribute Sorting:** Rapid sorting of the roster by Overall Rating (`OVR`) descending, or cumulative Goals (`Goals`) descending.

### 4. Data Aggregation, Reports, and Statistics
- **Squad Overview Metrics:** Automatic server-side aggregation calculating total squad count (`COUNT(*)`), squad average rating (`AVG(ovr)`), and cumulative club goals (`SUM(goals)`).
- **Star Performer Identification:** Instant calculation of the club's Top Scorer (`MAX(goals)`) and highest-rated star player.
- **Positional Distribution Breakdown:** Aggregated reporting grouping players by functional field positions (FW, MF, DF, GK counts).
- **Squad Health & Availability Report:** Categorical breakdown distinguishing fit players ready for selection versus sidelined injured athletes.

---

## 2.2. Database Design

### 1. Relational Schema Between Tables
The database schema consists of six relational entities modeled in 3NF to eliminate anomalies:
1. `APP_USERS`: System authentication accounts for coaching staff and administrators.
2. `PLAYERS`: Central athlete entity containing personal details, physical attributes, and career statistics.
3. `MATCHES`: Match fixture schedules, competition types, venue locations, and scores.
4. `MATCH_EVENTS`: Junction table linking players and matches to record granular in-game incidents (goals, assists, cards) at specific match minutes.
5. `TRAINING_SESSIONS`: Club training schedules, focus drills, and session durations.
6. `PLAYER_TRAINING`: Attendance and evaluation register tracking individual player ratings during training drills.

```
       +-------------------+
       |     APP_USERS     |
       +-------------------+
                 |
                 v (manages)
       +-------------------+             +-------------------+
       |      PLAYERS      |<------------|  PLAYER_TRAINING  |
       +-------------------+ (1:N)       +-------------------+
         |               |                         ^ (N:1)
         | (1:N)         | (1:N)                   |
         v               v               +-------------------+
+------------------+   +-----------------| TRAINING_SESSIONS |
|   MATCH_EVENTS   |   |                 +-------------------+
+------------------+   |
         ^ (N:1)       |
         |             |
+------------------+   |
|     MATCHES      |<--+
+------------------+
```

### 2. Detailed Structure of Each Table

#### Table 1: `APP_USERS` (System Accounts)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `NUMBER` | `PRIMARY KEY` | Unique account identifier |
| `username` | `VARCHAR2(32)` | `NOT NULL, UNIQUE` | Login username |
| `password_hash`| `VARCHAR2(255)`| `NOT NULL` | Bcrypt encrypted password hash |
| `role` | `VARCHAR2(20)` | `DEFAULT 'coach', CHECK (role IN ('admin','coach','scout'))` | User permission role |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Account registration timestamp |

#### Table 2: `PLAYERS` (Squad Athletes & FIFA Attributes)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `NUMBER` | `PRIMARY KEY` | Unique player identification |
| `full_name` | `VARCHAR2(100)` | `NOT NULL` | Full legal name |
| `position` | `VARCHAR2(2)` | `NOT NULL, CHECK (position IN ('FW','MF','DF','GK'))` | Tactical position |
| `jersey_number`| `NUMBER(3)` | `NOT NULL, CHECK (jersey_number BETWEEN 1 AND 99)` | Squad shirt number |
| `health_status`| `VARCHAR2(20)` | `DEFAULT 'Fit', CHECK (health_status IN ('Fit','Injured','Suspended'))` | Physical fitness status |
| `ovr` | `NUMBER(3)` | `DEFAULT 70, CHECK (ovr BETWEEN 1 AND 99)` | Overall player rating |
| `matches` | `NUMBER(7)` | `DEFAULT 0, CHECK (matches >= 0)` | Total competitive appearances |
| `goals` | `NUMBER(7)` | `DEFAULT 0, CHECK (goals >= 0)` | Cumulative career goals scored |
| `assists` | `NUMBER(7)` | `DEFAULT 0, CHECK (assists >= 0)` | Cumulative career assists |
| `mvp` | `NUMBER(7)` | `DEFAULT 0, CHECK (mvp >= 0)` | Man of the Match awards |
| `club` | `VARCHAR2(100)` | `DEFAULT 'Free Agent'` | Club affiliation name |
| `pac` | `NUMBER(3)` | `DEFAULT 70, CHECK (pac BETWEEN 0 AND 100)` | Pace / Speed attribute |
| `sho` | `NUMBER(3)` | `DEFAULT 70, CHECK (sho BETWEEN 0 AND 100)` | Shooting attribute |
| `pas` | `NUMBER(3)` | `DEFAULT 70, CHECK (pas BETWEEN 0 AND 100)` | Passing attribute |
| `dri` | `NUMBER(3)` | `DEFAULT 70, CHECK (dri BETWEEN 0 AND 100)` | Dribbling attribute |
| `def` | `NUMBER(3)` | `DEFAULT 70, CHECK (def BETWEEN 0 AND 100)` | Defending attribute |
| `phy` | `NUMBER(3)` | `DEFAULT 70, CHECK (phy BETWEEN 0 AND 100)` | Physicality attribute |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Profile creation date |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Last modified date |

#### Table 3: `MATCHES` (Match Schedule and Results)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `match_id` | `NUMBER` | `PRIMARY KEY` | Unique match fixture ID |
| `match_date` | `DATE` | `NOT NULL` | Scheduled match date and time |
| `opponent_team`| `VARCHAR2(100)`| `NOT NULL` | Opposing team club name |
| `venue` | `VARCHAR2(100)` | `DEFAULT 'Home Stadium'` | Match stadium venue |
| `match_type` | `VARCHAR2(50)` | `DEFAULT 'Friendly', CHECK (match_type IN ('Friendly','League','Cup'))`| Tournament competition type |
| `home_score` | `NUMBER(3)` | `DEFAULT 0, CHECK (home_score >= 0)` | Goals scored by home club |
| `away_score` | `NUMBER(3)` | `DEFAULT 0, CHECK (away_score >= 0)` | Goals scored by opponent |

#### Table 4: `MATCH_EVENTS` (In-Game Granular Incidents)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `event_id` | `NUMBER` | `PRIMARY KEY` | Unique event log ID |
| `match_id` | `NUMBER` | `NOT NULL, REFERENCES MATCHES(match_id) ON DELETE CASCADE`| Foreign key to match fixture |
| `player_id` | `NUMBER` | `NOT NULL, REFERENCES PLAYERS(id) ON DELETE CASCADE` | Foreign key to player involved |
| `event_type` | `VARCHAR2(20)` | `NOT NULL, CHECK (event_type IN ('Goal','Assist','Yellow Card','Red Card'))`| Categorical incident type |
| `minute` | `NUMBER(3)` | `NOT NULL, CHECK (minute BETWEEN 1 AND 120)` | Match minute incident occurred |

#### Table 5: `TRAINING_SESSIONS` (Practice Drills)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `session_id` | `NUMBER` | `PRIMARY KEY` | Unique training session ID |
| `session_date` | `DATE` | `NOT NULL` | Date of training workout |
| `focus_area` | `VARCHAR2(100)` | `NOT NULL` | Technical focus (e.g., Fitness, Tactical, Finishing) |
| `duration_minutes`|`NUMBER(3)` | `DEFAULT 90, CHECK (duration_minutes > 0)` | Workout duration in minutes |

#### Table 6: `PLAYER_TRAINING` (Drill Attendance & Evaluation)
| Column Name | Oracle Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `session_id` | `NUMBER` | `REFERENCES TRAINING_SESSIONS(session_id) ON DELETE CASCADE`| Composite PK & Foreign key |
| `player_id` | `NUMBER` | `REFERENCES PLAYERS(id) ON DELETE CASCADE` | Composite PK & Foreign key |
| `performance_rating`|`NUMBER(2)`| `CHECK (performance_rating BETWEEN 1 AND 10)` | Coach evaluation grade (1-10) |

---

## 2.3. SQL Queries
The curriculum requires a minimum of 5 queries per category across four relational algebra classifications (20 queries total).

### Category A: Basic Queries (5 Queries)
```sql
-- Query 1: Retrieve all unattached players (Free Agents)
SELECT full_name, position, ovr 
FROM players 
WHERE club = 'Free Agent' 
ORDER BY ovr DESC;

-- Query 2: Find matches scheduled in September 2026
SELECT match_id, opponent_team, venue, match_date 
FROM matches 
WHERE match_date >= TO_DATE('2026-09-01', 'YYYY-MM-DD') 
  AND match_date < TO_DATE('2026-10-01', 'YYYY-MM-DD');

-- Query 3: List all system accounts holding the 'coach' role
SELECT id, username, role, created_at 
FROM app_users 
WHERE role = 'coach';

-- Query 4: Retrieve tactical training sessions lasting 90 minutes or longer
SELECT session_id, session_date, focus_area, duration_minutes 
FROM training_sessions 
WHERE focus_area LIKE '%Tactical%' AND duration_minutes >= 90;

-- Query 5: Find elite attackers with Pace and Shooting ratings above 80
SELECT full_name, jersey_number, pac, sho, ovr 
FROM players 
WHERE pac > 80 AND sho > 80 AND position = 'FW';
```

### Category B: Nested Queries / Subqueries (5 Queries)
```sql
-- Query 6: Find the highest rated player in the entire squad
SELECT full_name, position, ovr, club 
FROM players 
WHERE ovr = (SELECT MAX(ovr) FROM players);

-- Query 7: Identify players who have scored at least one official match goal
SELECT full_name, position, goals 
FROM players 
WHERE id IN (
    SELECT DISTINCT player_id 
    FROM match_events 
    WHERE event_type = 'Goal'
);

-- Query 8: Find home matches where the club scored more goals than team average
SELECT match_id, opponent_team, home_score 
FROM matches 
WHERE home_score > (SELECT AVG(home_score) FROM matches);

-- Query 9: Detect players who have never attended any training sessions (NOT EXISTS)
SELECT p.id, p.full_name, p.position 
FROM players p 
WHERE NOT EXISTS (
    SELECT 1 
    FROM player_training pt 
    WHERE pt.player_id = p.id
);

-- Query 10: Retrieve the first administrator account created in the system
SELECT username, role, created_at 
FROM app_users 
WHERE created_at = (SELECT MIN(created_at) FROM app_users);
```

### Category C: Grouping and Statistical Queries (5 Queries)
```sql
-- Query 11: Count total players and average overall rating by position
SELECT position, COUNT(*) AS total_players, ROUND(AVG(ovr), 1) AS avg_position_ovr 
FROM players 
GROUP BY position 
ORDER BY total_players DESC;

-- Query 12: Identify top match scorers having scored more than 2 goals
SELECT player_id, COUNT(*) AS total_goals_logged 
FROM match_events 
WHERE event_type = 'Goal' 
GROUP BY player_id 
HAVING COUNT(*) > 2;

-- Query 13: Calculate average training evaluation rating per player
SELECT player_id, ROUND(AVG(performance_rating), 2) AS avg_training_grade 
FROM player_training 
GROUP BY player_id 
ORDER BY avg_training_grade DESC;

-- Query 14: Count match occurrences categorized by tournament competition type
SELECT match_type, COUNT(*) AS total_matches, SUM(home_score) AS total_home_goals 
FROM matches 
GROUP BY match_type;

-- Query 15: Find training sessions where the squad achieved high performance (> 7.5)
SELECT session_id, ROUND(AVG(performance_rating), 2) AS session_rating 
FROM player_training 
GROUP BY session_id 
HAVING AVG(performance_rating) > 7.5;
```

### Category D: Aggregate Queries (5 Queries)
```sql
-- Query 16: Compute the squad-wide physical attribute averages
SELECT 
    ROUND(AVG(pac), 1) AS squad_avg_pac, 
    ROUND(AVG(sho), 1) AS squad_avg_sho, 
    ROUND(AVG(pas), 1) AS squad_avg_pas, 
    ROUND(AVG(dri), 1) AS squad_avg_dri, 
    ROUND(AVG(def), 1) AS squad_avg_def, 
    ROUND(AVG(phy), 1) AS squad_avg_phy 
FROM players;

-- Query 17: Determine the statistical range of player overall ratings
SELECT 
    MAX(ovr) AS highest_ovr, 
    MIN(ovr) AS lowest_ovr, 
    ROUND(AVG(ovr), 2) AS mean_ovr, 
    ROUND(STDDEV(ovr), 2) AS std_deviation_ovr 
FROM players;

-- Query 18: Calculate total goals scored and conceded across all fixtures
SELECT 
    SUM(home_score) AS total_goals_scored, 
    SUM(away_score) AS total_goals_conceded, 
    (SUM(home_score) - SUM(away_score)) AS goal_difference 
FROM matches;

-- Query 19: Tally cumulative discipline penalty cards issued
SELECT 
    COUNT(CASE WHEN event_type = 'Yellow Card' THEN 1 END) AS total_yellow_cards, 
    COUNT(CASE WHEN event_type = 'Red Card' THEN 1 END) AS total_red_cards, 
    COUNT(*) AS total_disciplinary_actions 
FROM match_events 
WHERE event_type IN ('Yellow Card', 'Red Card');

-- Query 20: Aggregate total training workout duration logged in the current month
SELECT 
    COUNT(*) AS sessions_count, 
    SUM(duration_minutes) AS total_training_minutes, 
    ROUND(SUM(duration_minutes) / 60, 1) AS total_training_hours 
FROM training_sessions 
WHERE EXTRACT(MONTH FROM session_date) = EXTRACT(MONTH FROM SYSDATE);
```

---

## 2.4. PL/SQL Programming
The syllabus requires implementing Views, Functions, Stored Procedures, and Triggers utilizing control structures (`IF`, `FOR`, `WHILE`).

### 1. View: Active First-Team Squad (`view_active_squad`)
```sql
CREATE OR REPLACE VIEW view_active_squad AS
SELECT 
    id, full_name, position, jersey_number, ovr, goals, assists, health_status
FROM players
WHERE health_status = 'Fit'
WITH READ ONLY;
```
*Purpose:* Provides an abstracted read-only projection of healthy players eligible for matchday selection, protecting private salary or system attributes.

### 2. Trigger: Automatic Goal Synchronization (`trg_after_match_event`)
```sql
CREATE OR REPLACE TRIGGER trg_after_match_event
AFTER INSERT ON match_events
FOR EACH ROW
BEGIN
    -- Control structure: IF-THEN evaluation
    IF :NEW.event_type = 'Goal' THEN
        UPDATE players 
        SET goals = goals + 1, 
            updated_at = CURRENT_TIMESTAMP 
        WHERE id = :NEW.player_id;
    ELSIF :NEW.event_type = 'Assist' THEN
        UPDATE players 
        SET assists = assists + 1, 
            updated_at = CURRENT_TIMESTAMP 
        WHERE id = :NEW.player_id;
    END IF;
END;
/
```
*Purpose:* Enforces automated database-level state synchronization whenever match events are inserted, eliminating the risk of human arithmetic omission in cumulative tables.

### 3. Oracle Package: Squad Management (`pkg_fc_manager`)
```sql
-- Package Specification (Header)
CREATE OR REPLACE PACKAGE pkg_fc_manager IS
    FUNCTION get_top_scorer RETURN VARCHAR2;
    PROCEDURE evaluate_squad_fitness;
    PROCEDURE simulate_training_camp(p_duration_days IN NUMBER);
END pkg_fc_manager;
/

-- Package Body (Implementation with IF, FOR, and WHILE)
CREATE OR REPLACE PACKAGE BODY pkg_fc_manager IS

    -- Function: Finds the squad's leading goal scorer
    FUNCTION get_top_scorer RETURN VARCHAR2 IS
        v_name VARCHAR2(100);
        v_goals NUMBER;
    BEGIN
        SELECT full_name, goals INTO v_name, v_goals 
        FROM players 
        ORDER BY goals DESC 
        FETCH FIRST 1 ROWS ONLY;
        
        IF v_goals = 0 THEN
            RETURN 'No goals scored yet';
        ELSE
            RETURN v_name || ' (' || v_goals || ' goals)';
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 'Squad list empty';
    END get_top_scorer;

    -- Procedure 1: Evaluates squad tier using Cursor FOR LOOP and IF-ELSE
    PROCEDURE evaluate_squad_fitness IS
    BEGIN
        FOR player_rec IN (SELECT full_name, ovr, health_status FROM players) LOOP
            IF player_rec.health_status != 'Fit' THEN
                DBMS_OUTPUT.PUT_LINE(player_rec.full_name || ': Sidelined (Medical Attention)');
            ELSIF player_rec.ovr >= 85 THEN
                DBMS_OUTPUT.PUT_LINE(player_rec.full_name || ': Elite Tier Star');
            ELSIF player_rec.ovr >= 75 THEN
                DBMS_OUTPUT.PUT_LINE(player_rec.full_name || ': Key Starter');
            ELSE
                DBMS_OUTPUT.PUT_LINE(player_rec.full_name || ': Squad Rotation Member');
            END IF;
        END LOOP;
    END evaluate_squad_fitness;

    -- Procedure 2: Simulates intensive camp drill using WHILE LOOP
    PROCEDURE simulate_training_camp(p_duration_days IN NUMBER) IS
        v_current_day NUMBER := 1;
    BEGIN
        WHILE v_current_day <= p_duration_days LOOP
            INSERT INTO training_sessions(session_id, session_date, focus_area, duration_minutes)
            VALUES (
                seq_training_id.NEXTVAL, 
                SYSDATE + v_current_day, 
                'Intensive Camp Day ' || v_current_day, 
                120
            );
            v_current_day := v_current_day + 1;
        END LOOP;
        COMMIT;
    END simulate_training_camp;

END pkg_fc_manager;
/
```
*Purpose:* Encapsulates business logic into a single cohesive enterprise package, demonstrating the use of `IF-ELSIF`, cursor-based `FOR` loops, and `WHILE` loops.

---

## 2.5. User Management and Backup/Recovery

### 1. User Creation and Role-Based Access Control (RBAC)
```sql
-- Step 1: Create Custom Roles
CREATE ROLE coach_data_entry;
CREATE ROLE scout_read_only;

-- Step 2: Grant Table Privileges to Roles
GRANT SELECT, INSERT, UPDATE ON fcm_admin.players TO coach_data_entry;
GRANT SELECT, INSERT ON fcm_admin.matches TO coach_data_entry;
GRANT SELECT, INSERT ON fcm_admin.match_events TO coach_data_entry;

GRANT SELECT ON fcm_admin.players TO scout_read_only;
GRANT SELECT ON fcm_admin.view_active_squad TO scout_read_only;

-- Step 3: Create Users and Assign Roles
CREATE USER coach_bang IDENTIFIED BY CoachSecure2026;
GRANT CONNECT, RESOURCE TO coach_bang;
GRANT coach_data_entry TO coach_bang;

CREATE USER scout_kien IDENTIFIED BY ScoutSecure2026;
GRANT CONNECT TO scout_kien;
GRANT scout_read_only TO scout_kien;
```

### 2. Database Backup and Recovery (Oracle Data Pump)
```sql
-- Step 1: Create Storage Directory within Oracle Instance
CREATE OR REPLACE DIRECTORY fcm_backup_dir AS 'C:\oracle_backups';
GRANT READ, WRITE ON DIRECTORY fcm_backup_dir TO fcm_admin;

-- Step 2: Execute Schema-level Export via Command Line (expdp)
-- Command:
-- expdp fcm_admin/AdminPassword2026@orcl schemas=fcm_admin directory=fcm_backup_dir dumpfile=fcm_full_schema_%U.dmp logfile=fcm_export.log compression=all

-- Step 3: Execute Recovery / Restoration via Command Line (impdp)
-- Command:
-- impdp fcm_admin/AdminPassword2026@orcl schemas=fcm_admin directory=fcm_backup_dir dumpfile=fcm_full_schema_01.dmp logfile=fcm_import.log table_exists_action=replace
```

---

# CHAPTER 3. TESTING RESULTS

Testing of the application functions was conducted on the Android client connected live to the Oracle database.

### Test Sample 1: Player Insertion with Constraint Verification
- **Input Data Sample 1A (Invalid Boundary Test):**
  - Full Name: `[Empty String]`
  - Jersey Number: `150` (Exceeds maximum allowed 99)
  - Pace Rating: `120` (Exceeds allowed 100)
  - *Expected Result:* The application immediately blocks submission and renders red form validation errors (`tilPlayerName.setError`, `etPac.setError`). No invalid record reaches the Oracle database.
- **Input Data Sample 1B (Valid Insertion Test):**
  - Full Name: `Nguyen Van A`
  - Position: `FW`
  - Jersey Number: `9`
  - Skill Attributes: `PAC: 88, SHO: 85, PAS: 74, DRI: 82, DEF: 35, PHY: 78`
  - *Expected Result:* Server accepts request, executes `INSERT INTO players`, and returns HTTP 201. The player appears in the roster list and displays a polygon Radar Chart.

### Test Sample 2: Business-Process Match Performance Recording
- **Initial State:**
  - Player: `Nguyen Van A`
  - Matches: `0`, Goals: `0`, Assists: `0`, Health: `Fit`
- **Execution of Business Function:**
  - Coach taps `⚽ GHI NHẬN KẾT QUẢ TRẬN ĐẤU`.
  - Enters: Goals Scored: `2`, Assists: `1`, MVP: `Checked`, Health: `Fit`.
  - Taps `XÁC NHẬN`.
- **Observed Result:**
  - The business logic executes. The profile updates to: Matches: `1`, Goals: `2`, Assists: `1`, MVP: `1`.
  - The changes persist across database sessions.

### Test Sample 3: Quick and Advanced Search Verification
- **Sample 3A (Quick Name Search):** Entering query `"Nguyen"` into the search bar instantly filters out all non-matching records, leaving only players matching the substring.
- **Sample 3B (Positional Filter & Sort):** Tapping filter button `FW` isolates Forwards. Tapping `↕ OVR` sorts the filtered list from highest to lowest overall rating.

### Test Sample 4: Data Aggregation and Statistics Dashboard
- **Observed Results on Statistics Screen:**
  - Total Players: Displays correct count (`COUNT(*)`).
  - Average OVR: Displays computed mean (`AVG(ovr)`).
  - Total Goals: Sums all goals (`SUM(goals)`).
  - Top Scorer: Correctly indicates `Nguyen Van A (2 bàn)`.
  - Positional Breakdown: Accurately tallies FW, MF, DF, and GK subgroups.

---

# CHAPTER 4. CONCLUSION

### 1. Achievements
- Designed a normalized 3NF database schema with 6 tables in Oracle Database 19c.
- Implemented comprehensive relational constraints (PK, FK with Cascade, Unique, Checks) guaranteeing zero data corruption.
- Successfully programmed 20 diverse SQL queries fulfilling all basic, nested, and grouping requirements.
- Developed modular PL/SQL units (Trigger, Package, Function, Procedure) utilizing `IF-THEN`, `FOR` loops, and `WHILE` loops.
- Configured user roles and verified Data Pump (`expdp`/`impdp`) backup and recovery procedures.
- Delivered an intuitive Android mobile application featuring visual Radar Charts and match performance business-process logic.

### 2. Limitations
- The mobile client requires an active network connection to the Node.js API to communicate with Oracle.
- Advanced automated computer vision tracking of live match video footage has not yet been integrated.

### 3. Future Development
- Developing a dedicated desktop web dashboard using React.js for club administrative back-office staff.
- Implementing automated IoT wearable sensor integration to monitor player heart rate and training fatigue directly into Oracle tables.

---

# REFERENCES
1. Oracle Corporation, *"Oracle Database SQL Language Reference, 19c"*, Oracle Help Center, 2023.
2. T. Kyte and D. Kuhn, *"Expert Oracle Database Architecture: Techniques and Solutions"*, 3rd ed., Apress, 2014.
3. C. J. Date, *"An Introduction to Database Systems"*, 8th ed., Addison-Wesley, 2004.
4. Google Developers, *"Android Architecture Components and Material Design Guide"*, Android Developers Portal, 2024.
5. P. Jay, *"MPAndroidChart: A powerful Android chart view library"*, GitHub Documentation, 2024.
