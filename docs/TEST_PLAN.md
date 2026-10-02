# Kế hoạch kiểm thử và báo cáo nghiệm thu

## 1. Phạm vi

- Android unit test: validation, ViewModel/state và logic lọc/sắp xếp có thể tách.
- Android instrumentation: Room DAO, navigation và luồng CRUD trọng yếu.
- Backend unit/integration: validation, auth, token, CRUD mock và AI gateway với provider mock.
- Manual UI: Loading/Empty/Error/Success, accessibility, rotation và mất mạng.
- AI evaluation: 20 tình huống trong `AI_EVALUATION.md`.

## 2. Test matrix nghiệp vụ

| ID | Chức năng | Ca kiểm tra | Kết quả mong đợi |
|---|---|---|---|
| AUTH-01 | Register | Username/password hợp lệ | 201, không trả hash/salt |
| AUTH-02 | Register | Username trùng | 409 |
| AUTH-03 | Login | Sai mật khẩu | 401, message không tiết lộ tài khoản tồn tại |
| AUTH-04 | Token | Token thiếu/sai/hết hạn | 401 |
| PL-01 | Create | Dữ liệu hợp lệ | 201 và đọc lại giống dữ liệu đã nhập |
| PL-02 | Create | Vị trí sai/chỉ số >99 | 400 với details |
| PL-03 | Update | Chỉ đổi một trường qua form đầy đủ | Các trường khác được giữ nguyên |
| PL-04 | Delete | ID tồn tại/không tồn tại | 204/404 đúng |
| PL-05 | Search/filter | Tên không phân biệt hoa thường + vị trí | Danh sách đúng, empty state đúng |
| OFF-01 | Offline | Cache có dữ liệu, server tắt | Hiện cache + cảnh báo |
| OFF-02 | Offline | Cache rỗng, server tắt | Error state, không loading vô hạn |
| FUND-01 | Quỹ | Thu rồi chi | Số dư = tổng thu - tổng chi |
| FUND-02 | Quỹ | Số tiền rỗng/âm/quá lớn | Không crash, báo lỗi |
| NOTIF-01 | Notification | Từ chối quyền | Switch/UI trở lại trạng thái đúng |
| UI-01 | Lifecycle | Xoay màn hình trong lúc loading | Không crash, state hợp lý |

## 3. Lệnh kiểm tra chuẩn

```text
Android: gradlew.bat testDebugUnitTest lintDebug assembleDebug
Backend: npm test
Backend syntax: node --check index.js và các route
```

## 4. Điều kiện phát hành

- Build/test exit code 0.
- Không có Android lint Error; warning nghiêm trọng về security/accessibility được xử lý hoặc có giải trình.
- Backend test bao phủ auth, validation, CRUD và AI error path.
- Không có secret trong file tracked.
- Smoke test backend health/auth/players/AI validation thành công.
- APK cài được trên emulator hoặc thiết bị API >= 24.

## 5. Mẫu báo cáo chạy test

| Ngày | Commit | Môi trường | Suite | Passed/Total | Bằng chứng |
|---|---|---|---|---:|---|
| Cập nhật tự động ở lần nghiệm thu cuối | HEAD | Windows + JDK Android Studio | Android unit/lint/build | Chưa chốt | `app/build/reports/` |
| Cập nhật tự động ở lần nghiệm thu cuối | HEAD | Node.js + mock DB | Backend tests | Chưa chốt | console/test report |
| Nhóm điền sau lần model thật | HEAD | Gemini model cấu hình | AI manual evaluation | Chưa chốt | Bảng AI-01..AI-20 |

Không đổi “Chưa chốt” thành “Passed” nếu chưa chạy đúng suite tương ứng.

