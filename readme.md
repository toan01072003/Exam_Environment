
### Chủ đề
Công cụ thiết lập, tạo môi trường thi online xác minh danh tính thí sinh, theo dõi hành vi của thí sinh thông qua camera.

### Mô tả
Tập trung vào việc phát triển hệ thống đảm bảo tính minh bạch và trung thực trong các kỳ thi trực tuyến. Hệ thống này sẽ tạo ra môi trường thi online với khả năng giám sát và phát hiện gian lận.

### Tính năng
1. **Camera Stream**: Server sẽ thấy được học sinh trong quá trình làm bài.
2. **Phát hiện hành động bất thường**: Hệ thống giúp theo dõi *các cử chỉ, hành động trên khuôn mặt của thí sinh: ánh mắt, quay trái, quay phải,...* thông qua camera giám sát trong suốt quá trình thi.
3. **Phát hiện tiếng nói**: Hệ thống giúp *theo dõi và phát hiện tiếng nói* của thí sinh trong suốt quá trình thi.
4. **Chặn ứng dụng**: Hệ thống sẽ chặn một số ứng dụng để không cho phép thí sinh bật lên khi đang trong suốt quá trình thi.
5. **Xác thực**: Hệ thống sẽ xác thực xem xem có phải học sinh đó tham gia kỳ thi không thông qua ảnh từ camera.

### Công cụ 
1. **OpenCV** sử dụng các mô hình phân biện và phát hiện khuôn mặt, cơ thể của OpenCV như Haarcascade, FaceNet, LBPH,..
2. **Python và Visual Studio Code*** sử dụng để tạo môi trường nhằm xác thực một cách chính xác 
