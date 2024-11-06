package client;
import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ExamClient {
    private Socket socketCamera;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectOutputStream outCamera ;
    private Socket gradingSocket;
    private ObjectOutputStream gradingOut;
    private ObjectInputStream gradingIn;
    public ExamClient() {
        try { 
            // Kết nối tới server
            socket = new Socket("localhost", 8888); 
            socketCamera = new Socket("localhost", 8889);
            out = new ObjectOutputStream(socket.getOutputStream());
            outCamera = new ObjectOutputStream(socketCamera.getOutputStream());
            
            // ket noi cham diem 
            gradingSocket = new Socket("localhost", 8890);
            gradingOut = new ObjectOutputStream(gradingSocket.getOutputStream());
            gradingIn = new ObjectInputStream(gradingSocket.getInputStream());
            // Khởi động stream camera
            startCameraStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Bắt đầu stream camera
    public void startCameraStream() {
        new Thread(() -> {
            try {
                Webcam webcam = Webcam.getDefault();
                if (webcam == null) {
                    System.err.println("No webcam detected.");
                    return; // Thoát nếu không có webcam
                }
                webcam.open();

                // Gửi ảnh webcam liên tục
                while (true) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        byte[] imageBytes = convertImageToByteArray(image, 0.9f); // Set quality to 90%
                        
                        // Gửi dữ liệu ảnh tới server
                        outCamera.writeObject(imageBytes);
                        outCamera.flush();

                        // Tạm dừng để giảm băng thông
                        Thread.sleep(200); // Gửi 10 khung hình mỗi giây
                    } else {
                        System.err.println("Failed to capture image from webcam.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private byte[] convertImageToByteArray(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();
        
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // Set quality (0.0 - 1.0)
        }
        
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        ios.flush();
        writer.dispose();
        return baos.toByteArray();
    }
    
    public void sendAnswer(int questionNumber, String answer) {
        try {
            out.writeObject("ANSWER:" + questionNumber + ":" + answer);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // cham diem 
    public void submitExam() {
        try {
            out.writeObject("SUBMIT_EXAM");
            out.flush();

            
           
            // Gửi yêu cầu chấm điểm
            gradingOut.writeObject("REQUEST_GRADING");
            gradingOut.flush();

            // Nhận kết quả từ server
            String result = (String) gradingIn.readObject();
            JOptionPane.showMessageDialog(null, result); // Hiển thị kết quả cho học sinh

            gradingSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

 // Send the window status to the server
    public void sendWindowStatus(boolean isActive) {
        try {
            out.writeObject("Open new tab:" + isActive);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    
    public void sendBlockedKeyCombination(String keyCombination) {
        try {
            out.writeObject("BLOCKED_KEY_COMBINATION:" + keyCombination);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Submit the exam
    

    // Other methods remain unchanged...
    public void sendAlert(String message) {
        // Gửi thông báo âm thanh đến server
    	try {
        out.writeObject("Alert: " + message);
        out.flush();
        // Thêm mã gửi thông báo đến server nếu cần
        // Ví dụ: socket.send(message);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
