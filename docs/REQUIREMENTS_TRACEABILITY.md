# Ma trận truy vết yêu cầu

| Rubric/yêu cầu | Điểm | Implementation/bằng chứng | Cách xác minh |
|---|---:|---|---|
| Ý tưởng, người dùng, vấn đề thực tế | 1.0 | `docs/USER_RESEARCH.md` | Đính kèm phản hồi khảo sát thật và tổng hợp |
| Splash + login/register | - | `SplashActivity`, `LoginActivity`, `RegisterActivity` | Demo lỗi/success và session |
| Dashboard điều hướng | - | `DashboardActivity`, bottom navigation | Mở đủ module |
| >=5 màn hình, list/detail | - | Activities + layouts | Đếm và demo |
| CRUD thực sự | - | Player form/detail/repository + Players API | Create/read/update/delete và đọc lại |
| Tìm/lọc/sắp xếp | - | `MainActivity` | Thay query/filter/sort ngoài kịch bản |
| Room/REST lưu trữ | - | `AppDatabase`, DAO, repository, backend | Tắt server và xem cache |
| Loading/Empty/Error/Success | - | UI state + layout | Test server chậm/rỗng/lỗi/thành công |
| Validation thân thiện | - | Android validator + backend validator | Test null, range, malformed ID |
| Notification/nhắc việc | - | `SettingsActivity`, `MatchReminderWorker` | Cấp/từ chối quyền và notification demo |
| Settings/account/logout | - | `SettingsActivity` | Logout xóa session và back stack |
| UI/UX và trải nghiệm | 1.0 | XML theme, strings, screenshots/report | Lint accessibility + demo một tay |
| Android và kiến trúc | 2.0 | `docs/ARCHITECTURE.md`, ViewModel/repository/Room | Build, lifecycle test, giải thích luồng |
| Database/API | 1.5 | `docs/ERD_AND_API.md`, `schema.sql`, routes | Backend tests và smoke test |
| AI use case có giá trị | 2.0 | AI Coach, AI gateway, `docs/AI_EVALUATION.md` | Gọi thật + 20 test cases |
| AI key không nằm trong Android | - | Backend `.env`, `.env.example` | Secret scan tracked files |
| Không hard-code AI giả | - | AI route trả 503/fallback công khai | Test thiếu key/provider lỗi |
| Prompt/template và giới hạn | - | AI route + AI evaluation doc | Code review và test injection |
| Kiểm thử | 0.75 | Android/backend tests + `TEST_PLAN.md` | Lệnh test exit 0 và report |
| Git/quản lý nhóm | 0.5 | `PROJECT_MANAGEMENT.md`, Git/Issue/PR thật | Git log, board và review link |
| Báo cáo/trình bày | 0.75 | DOCX/PDF, PPTX, demo script | Render QA và demo đúng sản phẩm |
| Sáng tạo/hoàn thiện | 0.5 | AI Coach, offline cache, sổ quỹ, notification | Demo tích hợp end-to-end |
| Repository source | - | Repository hiện tại | Clone sạch và build |
| APK/AAB | - | `output/apk/` | Cài đặt/smoke test |
| Figma/prototype | - | Nhóm bổ sung link/ảnh nếu có | Click prototype hoặc đối chiếu UI |
| Video 5-10 phút | - | Nhóm quay theo `DEMO_SCRIPT.md` | Kiểm tra thời lượng và luồng |

## Trạng thái trung thực

Các cột implementation cho biết nơi dự kiến chứa bằng chứng. Một hàng chỉ được coi là hoàn tất sau audit cuối; những bằng chứng con người hoặc nền tảng ngoài repository (khảo sát, Figma, video, Issue/PR) phải do nhóm cung cấp và không được giả lập.

