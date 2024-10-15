
# Chủ đề
Công cụ thiết lập, tạo môi trường thi online thông bào và phát hiện hành động lạ ( mở tap , chuột trái) trong phiên thi

# Thành viên
| MSV | Tên |
|---|---|
| B21DCVT229  | Nguyễn Đức Huy  |
| B21DCVT713  | Hoàng Minh Toàn |
| B21DCVT288  | Hà Gia Minh  |

# Mô tả
Tập trung vào việc phát triển hệ thống đảm bảo tính minh bạch và trung thực trong các kỳ thi trực tuyến. Hệ thống này sẽ tạo ra môi trường thi online với khả năng giám sát và phát hiện gian lận.

## Tính năng
1. **Phát hiện hành động bất thường**: Theo dõi các sự kiện như: *mở tab mới, nhấp chuột trái, hoặc rời khỏi màn hình thi*. Những hành động này sẽ được ghi lại và gửi cảnh báo ngay lập tức cho giám thị.

2. **Thông báo tức thì**: Khi phát hiện vi phạm, hệ thống sẽ ngay lập tức gửi thông báo đến giám thị để xử lý kịp thời.

## Công nghệ
1. **Back-end**: ***Java*** là lựa chọn tốt cho việc xây dựng server xử lý dữ liệu và logic phía sau. Java có thể được sử dụng với:
    ***Spring Boot***: để xây dựng API cho quản lý thi, lưu trữ dữ liệu vi phạm, và xử lý thông báo.
    ***WebSocket***: để cập nhật real-time giữa client và server (như thông báo vi phạm tức thì).

2. **Front-end**: Bạn có thể sử dụng JavaScript hoặc TypeScript để theo dõi hành động lạ trong trình duyệt. Một số công nghệ cụ thể:
    ***WebRTC API***: để truy cập webcam, microphone và chia sẻ màn hình.
    ***JavaScript***: để theo dõi sự kiện chuột, phím bấm, và mở tab mới.

3. **Cơ sở dữ liệu**: ***MySQL*** hoặc ***PostgreSQL*** có thể dùng để lưu trữ thông tin kỳ thi, sự kiện vi phạm và hành vi bất thường.