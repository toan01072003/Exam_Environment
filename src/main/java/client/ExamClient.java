package client;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;

public class ExamClient {
    private Socket socketCamera;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectOutputStream outCamera ;
    
    public ExamClient() {
        try {
            // Kết nối tới server
            socket = new Socket("localhost", 8888); 
            socketCamera = new Socket("localhost", 8889);
            out = new ObjectOutputStream(socket.getOutputStream());
            outCamera = new ObjectOutputStream(socketCamera.getOutputStream());
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
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    byte[] imageBytes = baos.toByteArray();
                    
                   
                    // Gửi dữ liệu ảnh tới server
                    outCamera .writeObject(imageBytes);
                    outCamera.flush();

                    // Tạm dừng để giảm băng thông
                    Thread.sleep(100); // gửi 10 khung hình mỗi giây
                } else {
                    System.err.println("Failed to capture image from webcam.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
    }
    
    public void sendAnswer(int questionNumber, String answer) {
        try {
            out.writeObject("ANSWER:" + questionNumber + ":" + answer);
            out.flush();
        } catch (IOException e) {
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
    public void submitExam() {
        try {
            out.writeObject("SUBMIT_EXAM");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Other methods remain unchanged...
}
