# Khảo sát người dùng và phân tích vấn đề

## 1. Bài toán

Người quản lý một đội bóng phong trào thường phải ghép nhiều công cụ rời rạc: bảng tính để lưu cầu thủ, tin nhắn để điểm danh, ghi chú để theo dõi thu chi và kinh nghiệm cá nhân để xếp đội hình. Dữ liệu phân tán làm chậm việc ra quyết định, dễ sai số và khó bàn giao khi người quản lý thay đổi.

**Problem statement:** Người quản lý đội bóng phong trào cần một nơi duy nhất để theo dõi nhân sự, phong độ, tài chính và nhận gợi ý đội hình dựa trên dữ liệu, bởi cách quản lý qua tin nhắn và bảng tính khiến thông tin thiếu nhất quán, khó tra cứu và phụ thuộc vào trí nhớ cá nhân.

## 2. Người dùng mục tiêu

### Persona chính - Bầu sô/đội trưởng

- Quản lý đội sân 5, 7 hoặc 11 người.
- Dùng điện thoại là chính, thường thao tác nhanh trước trận.
- Cần biết ai sẵn sàng, ai có phong độ tốt, số quỹ còn lại và đội hình phù hợp.
- Ưu tiên giao diện tiếng Việt, chữ dễ đọc, thao tác một tay và dữ liệu vẫn xem được khi mạng yếu.

### Persona phụ - Thủ quỹ

- Ghi thu/chi ngay sau giao dịch.
- Cần số dư minh bạch, lịch sử theo thời gian và mã PIN để hạn chế thao tác nhầm.

## 3. Giả thuyết cần xác nhận

1. Danh sách cầu thủ và trạng thái thể lực là thông tin được tra cứu thường xuyên nhất.
2. Tìm kiếm/lọc theo vị trí giúp xếp đội nhanh hơn so với cuộn danh sách.
3. Người dùng cần xem số dư và lịch sử thu/chi nhưng không cần nghiệp vụ kế toán phức tạp.
4. Gợi ý AI chỉ có giá trị khi nêu đúng tên, vị trí và chỉ số của cầu thủ hiện có.
5. Khi AI hoặc mạng lỗi, người dùng vẫn muốn xem dữ liệu đã đồng bộ gần nhất.

## 4. Kịch bản khảo sát thực tế

Nhóm sử dụng bảng hỏi dưới đây với tối thiểu 10 người thuộc một trong hai persona. Không nhập dữ liệu giả. Mỗi dòng phản hồi cần có ngày khảo sát, vai trò người trả lời và sự đồng ý sử dụng câu trả lời ẩn danh.

1. Hiện bạn quản lý danh sách cầu thủ bằng công cụ nào?
2. Việc nào tốn thời gian nhất trước một trận đấu?
3. Bạn thường cần tìm cầu thủ theo tiêu chí nào?
4. Bạn có ghi lại bàn thắng, kiến tạo, MVP hoặc thể lực không?
5. Bạn đang quản lý thu/chi của đội như thế nào?
6. Trường hợp sai sót nào xảy ra thường xuyên nhất?
7. Bạn có dùng gợi ý tự động để xếp đội hình không? Vì sao?
8. Một câu trả lời AI thế nào được xem là hữu ích và đáng tin?
9. Bạn có cần dùng ứng dụng khi mất mạng không?
10. Hãy xếp hạng 5 chức năng quan trọng nhất từ danh sách: cầu thủ, tìm/lọc, thống kê, quỹ, nhắc việc, AI coach, tài khoản.

## 5. Mẫu ghi nhận phản hồi

| Mã | Ngày | Persona | Vấn đề nổi bật | Top 3 chức năng | Trích ý chính đã ẩn danh |
|---|---|---|---|---|---|
| R01-R10 | Nhóm điền sau khảo sát | Bầu sô/Thủ quỹ | Dữ liệu thật | Dữ liệu thật | Dữ liệu thật |

## 6. Cách tổng hợp

- Đếm tần suất từng vấn đề và chức năng được chọn.
- Nhóm câu trả lời mở thành các chủ đề: nhân sự, thao tác nhanh, tài chính, offline, độ tin cậy AI.
- Chỉ kết luận một nhu cầu là ưu tiên nếu có bằng chứng lặp lại từ nhiều người.
- Đưa bảng tổng hợp và 2-3 trích ý ẩn danh vào Chương 2 của báo cáo.

## 7. MVP được chọn

MVP gồm: tài khoản; dashboard; danh sách/chi tiết/CRUD cầu thủ; tìm kiếm, lọc và sắp xếp; Room cache; sổ quỹ; nhắc việc; AI Coach dựa trên dữ liệu đội. Các chức năng lịch thi đấu đầy đủ, thanh toán và mạng xã hội nội bộ nằm ngoài phạm vi bản 1.0.

