# Quản lý dự án và Git

## 1. Vai trò có bằng chứng hiện tại

Lịch sử Git trong bản sao hiện tại ghi nhận **Nguyen Cong Bang** là tác giả commit khởi tạo. Nếu dự án có thêm thành viên, nhóm phải bổ sung đúng họ tên, tài khoản Git và liên kết commit/PR thật trước khi nộp.

| Vai trò | Phạm vi | Sản phẩm/bằng chứng |
|---|---|---|
| Product Owner / Developer / Backend / AI / QA | Nguyen Cong Bang (theo lịch sử Git hiện có) | Android app, backend, tài liệu kỹ thuật, test và bản release |

## 2. Backlog theo mức ưu tiên

| ID | User story | Ưu tiên | Trạng thái nghiệm thu |
|---|---|---:|---|
| US-01 | Là quản lý, tôi đăng ký/đăng nhập/đăng xuất để bảo vệ dữ liệu | P0 | Có API và màn hình; phải qua automated test |
| US-02 | Tôi xem dashboard để nắm quân số, vua phá lưới và quỹ | P0 | Demo trực tiếp với dữ liệu thật/mẫu có cấu trúc |
| US-03 | Tôi thêm, xem, sửa, xóa cầu thủ với validation rõ | P0 | CRUD API + UI + test lỗi đầu vào |
| US-04 | Tôi tìm, lọc và sắp xếp cầu thủ để chọn người nhanh | P0 | Demo thao tác ngoài kịch bản |
| US-05 | Tôi vẫn xem được dữ liệu gần nhất khi mất mạng | P0 | Room cache + trạng thái lỗi/empty |
| US-06 | Tôi ghi thu/chi và xem số dư | P1 | Room DAO + PIN + test tính số dư |
| US-07 | Tôi nhận nhắc việc phù hợp | P1 | WorkManager + quyền notification |
| US-08 | Tôi hỏi AI về đội hình/phong độ dựa trên đội hiện tại | P0 | Backend AI gateway + 20 ca đánh giá |
| US-09 | Tôi biết khi AI không chắc hoặc dịch vụ lỗi | P0 | Thông báo giới hạn/fallback minh bạch |

## 3. Luồng công việc

`To Do -> Doing -> Review -> Done`

- Mỗi task có ID user story, người phụ trách và tiêu chí nghiệm thu.
- Một task chỉ sang `Review` khi build/test cục bộ thành công.
- Một task chỉ sang `Done` khi có ít nhất một người khác kiểm tra, hoặc với nhóm một người phải có checklist tự review và log test kèm theo.

## 4. Definition of Done

1. Chức năng đáp ứng acceptance criteria và không chứa secret.
2. Có xử lý Loading, Empty, Error, Success nếu liên quan dữ liệu.
3. Có automated test hoặc test case thủ công tái lập được.
4. Android build thành công; backend test thành công.
5. README/tài liệu API được cập nhật.
6. Commit message mô tả ý nghĩa thay đổi, ví dụ `fix(android): preserve player stats when editing`.

## 5. Quy tắc Git đề xuất

- Nhánh: `feature/<id>-<ten-ngan>`, `fix/<id>-<ten-ngan>`, `docs/<pham-vi>`.
- Commit nhỏ, có nghĩa; tránh gộp toàn bộ dự án vào một commit.
- Pull request ghi: mục tiêu, ảnh/video nếu có UI, cách test và rủi ro.
- Không commit `.env`, database mock chứa tài khoản thật, khóa ký APK hoặc file cấu hình máy cá nhân.

## 6. Checklist code review

- Không hard-code secret, IP máy cá nhân hoặc mật khẩu.
- Request/response/error code khớp tài liệu API.
- Không chặn main thread bằng I/O.
- Validation bao phủ null, rỗng, số âm, vượt phạm vi và ID không tồn tại.
- AI không bịa cầu thủ ngoài context và công bố giới hạn khi thiếu dữ liệu.
- Test mới thất bại trước sửa và thành công sau sửa.

> Lưu ý trung thực: tài liệu này định nghĩa quy trình và backlog. Nó không thay thế bằng chứng Issue/PR/code review thật trên GitHub; nhóm cần tạo và liên kết các bằng chứng đó khi làm việc thực tế.

