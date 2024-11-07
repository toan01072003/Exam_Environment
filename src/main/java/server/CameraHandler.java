package server;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;


public class CameraHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out; // To send messages to the server
    private List<CascadeClassifier> detectors; // For face detection
    private List<CascadeClassifier> eyedetectors; // For eye detection
    private int missedFaceCount = 0;
    private static final int MAX_MISSED_FACE_COUNT = 5;

    public CameraHandler(Socket socket) {
        this.clientSocket = socket;

        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Initialize detector lists
        detectors = new ArrayList<>();
        eyedetectors = new ArrayList<>();

        // Load Haar Cascade models
        String basePath = "C:\\Users\\Toan\\Downloads\\";
      
        detectors.add(new CascadeClassifier(basePath + "haarcascade_frontalface_default.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_frontalface_alt.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_frontalface_alt2.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_frontalface_alt_tree.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_fullbody.xml"));
        
        detectors.add(new CascadeClassifier(basePath + "haarcascade_lowerbody.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_profileface.xml"));
        
        detectors.add(new CascadeClassifier(basePath + "haarcascade_smile.xml"));
        detectors.add(new CascadeClassifier(basePath + "haarcascade_upperbody.xml"));

        eyedetectors.add(new CascadeClassifier(basePath + "haarcascade_righteye_2splits.xml"));
        eyedetectors.add(new CascadeClassifier(basePath + "haarcascade_lefteye_2splits.xml"));
        eyedetectors.add(new CascadeClassifier(basePath + "haarcascade_eye.xml"));
        eyedetectors.add(new CascadeClassifier(basePath + "haarcascade_eye_tree_eyeglasses.xml"));
        
        try {
            // Initialize PrintWriter to send messages to the server
            OutputStream os = clientSocket.getOutputStream();
            out = new PrintWriter(os, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Setup GUI
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

                // Convert BufferedImage to Mat for OpenCV processing
                Mat matImage = bufferedImageToMat(image);

                // Detect faces using multiple models
                MatOfRect faces = new MatOfRect();
                detectObjects(matImage, faces);

                // Check results of face detection
                if (faces.toArray().length == 0) {
                    missedFaceCount++;
                    if (missedFaceCount >= MAX_MISSED_FACE_COUNT) {
                        System.out.println("Warning: Student is not in camera view!");
                        missedFaceCount = 0; // Reset counter after warning
                    }
                } else {
                    // Reset missed face count if a face is detected
                    missedFaceCount = 0;

                    // Check face direction by detecting eyes
                 // Kiểm tra hướng mặt của học sinh bằng cách phát hiện mắt
                    for (Rect face : faces.toArray()) {
                        Mat faceROI = matImage.submat(face);
                        MatOfRect eyes = new MatOfRect();
                        detectEyes(faceROI, eyes); // Gọi hàm phát hiện mắt

                        int eyeCount = eyes.toArray().length;

                        // Kiểm tra số lượng mắt được phát hiện
                        if (eyeCount == 2) {
                            System.out.println("Học sinh đang nhìn thẳng vào camera.");
                        } else if (eyeCount == 1) {
                            System.out.println("Học sinh có thể đang quay mặt sang một bên.");
                        } 
                    }

              }

                // Update image in JLabel
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

    // Function to detect faces and other objects using multiple models
    private void detectObjects(Mat matImage, MatOfRect faces) {
        for (CascadeClassifier detector : detectors) {
            detector.detectMultiScale(matImage, faces, 1.1, 4, 0, new Size(40, 40), new Size());

            if (faces.toArray().length > 0) {
                break; // Stop searching if faces are detected
            }
        }
    } 

    // Function to detect eyes using the list of eye detectors
    private void detectEyes(Mat faceROI, MatOfRect eyes) {
        for (CascadeClassifier eyeDetector : eyedetectors) {
            eyeDetector.detectMultiScale(faceROI, eyes, 1.1, 4, 0, new Size(40, 40), new Size());

            if (eyes.toArray().length > 0) {
                break; // Stop searching if eyes are detected
            }
        }
    }
    
    

    // Convert BufferedImage to Mat
    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), org.opencv.core.CvType.CV_8UC3);
        byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
}  