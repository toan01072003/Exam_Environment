package server;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

public class CameraHandler extends Thread {
    private Socket clientSocket;
    private CascadeClassifier faceDetector;
    private CascadeClassifier eyeDetector;
    private int missedFaceCount = 0;
    private static final int MAX_MISSED_FACE_COUNT = 5; // Ngưỡng để phát hiện quay mặt đi
    
    public CameraHandler(Socket socket) {
        this.clientSocket = socket;
        
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Tải các mô hình phát hiện mặt và mắt
        faceDetector = new CascadeClassifier("path/to/haarcascade_frontalface_alt.xml");
        eyeDetector = new CascadeClassifier("path/to/haarcascade_eye.xml");
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Camera Stream");
        JLabel label = new JLabel();
        frame.add(label);
        frame.setSize(640, 480); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            while (true) {
                byte[] imageBytes = (byte[]) in.readObject();
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bais);

                // Chuyển đổi BufferedImage thành Mat để xử lý bằng OpenCV
                Mat matImage = bufferedImageToMat(image);

                // Phát hiện khuôn mặt
                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(matImage, faces, 1.1, 3, 0, new Size(30, 30), new Size());
                
                if (faces.toArray().length == 0) {
                    missedFaceCount++;
                    if (missedFaceCount >= MAX_MISSED_FACE_COUNT) {
                        System.out.println("Cảnh báo: Sinh viên không ở trong tầm nhìn camera!");
                        JOptionPane.showMessageDialog(null, "Sinh viên không ở trong tầm nhìn camera!");
                        missedFaceCount = 0; // Đặt lại bộ đếm sau cảnh báo
                    }
                } else {
                    // Reset missed face count nếu phát hiện khuôn mặt
                    missedFaceCount = 0;

                    // Kiểm tra hướng mặt qua phát hiện mắt
                    for (Rect face : faces.toArray()) {
                        Mat faceROI = matImage.submat(face); // Vùng khuôn mặt phát hiện được
                        MatOfRect eyes = new MatOfRect();
                        eyeDetector.detectMultiScale(faceROI, eyes);

                        if (eyes.toArray().length < 2) {
                            System.out.println("Cảnh báo: Sinh viên có thể đang quay mặt!");
                            JOptionPane.showMessageDialog(null, "Sinh viên có thể đang quay mặt!");
                        }
                    }
                }

                // Cập nhật hình ảnh vào JLabel
                label.setIcon(new ImageIcon(image));
                label.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm chuyển đổi BufferedImage thành Mat
    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), org.opencv.core.CvType.CV_8UC3);
        byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
}
