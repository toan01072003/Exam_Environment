package server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class FaceHandler extends Thread {
    private Socket clientSocket;  // Cổng giao tiếp với client
    private static final String SERVER_ADDRESS = "localhost"; // Địa chỉ server Python
    private static final int SERVER_PORT = 5000; // Cổng của server Python
    private Socket pythonSocket; // Cổng kết nối đến Python server

    public FaceHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        
        // Khởi tạo cổng kết nối với Python server
        try {
            pythonSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            while (true) {
                try {
                    // Đọc dữ liệu ảnh từ client
                    byte[] imageData = (byte[]) in.readObject();

                    // Chuyển đổi byte[] thành BufferedImage
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bais);

                    // Gửi hình ảnh cho Python server
                    sendImageToImageSender(image);

                    // Sau khi gửi xong, kết thúc cổng này
                    break;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break; // Nếu có lỗi, thoát khỏi vòng lặp và đóng kết nối
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo cổng FaceHandler được đóng sau khi gửi xong
            closeConnection();
        }
    }
    
    private void sendImageToImageSender(BufferedImage image) throws IOException {
        // Kiểm tra xem pythonSocket có còn mở không trước khi gửi
        if (pythonSocket != null && !pythonSocket.isClosed()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(pythonSocket.getOutputStream())) {
                // Chuyển BufferedImage thành byte array với chất lượng cao hơn
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // Obtain an ImageWriter for JPEG format
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                if (!writers.hasNext()) {
                    throw new IOException("No writers available for JPEG format.");
                }
                ImageWriter writer = writers.next();

                // Set up ImageOutputStream to write to the ByteArrayOutputStream
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                    writer.setOutput(ios);

                    // Set the quality parameter for compression (between 0.0 and 1.0)
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    if (param.canWriteCompressed()) {
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(1.0f); // Set quality to 100% for highest quality
                    }

                    // Write the image with the specified quality
                    writer.write(null, new IIOImage(image, null, null), param);
                }

                // Gửi độ dài của ảnh và dữ liệu ảnh đến Python server
                byte[] imageBytes = baos.toByteArray();
                dataOutputStream.writeInt(imageBytes.length);
                dataOutputStream.write(imageBytes);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Python socket is closed or null. Cannot send image.");
        }
    }


    // Hàm đóng kết nối sau khi hoàn thành công việc
    private void closeConnection() {
        try {
            if (pythonSocket != null && !pythonSocket.isClosed()) {
                pythonSocket.close(); // Đóng kết nối với Python server
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close(); // Đóng kết nối với client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
