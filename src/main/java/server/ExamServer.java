package server;

import java.io.*;
import java.net.*;

public class ExamServer {
    private static final int PORT_TEXT = 8888; // For text messages
    private static final int PORT_CAMERA = 8889; // For camera data
    private static final int PORT_GRADING = 8890; // For grading results

    public static void main(String[] args) {
        try {
            ServerSocket textServerSocket = new ServerSocket(PORT_TEXT);
            ServerSocket cameraServerSocket = new ServerSocket(PORT_CAMERA);
            ServerSocket gradingServerSocket = new ServerSocket(PORT_GRADING);

            System.out.println("Exam Server is running..."); 
            new Thread(() -> handleTextData(textServerSocket)).start();
            new Thread(() -> handleCameraData(cameraServerSocket)).start();
            new Thread(() -> handleGradingData(gradingServerSocket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleTextData(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("hello world");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new ClientHandler(clientSocket).start(); // Start the thread for text messages
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleCameraData(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Kết nối camera từ Client...");
                // Xử lý dữ liệu camera ở đây
                new CameraHandler(clientSocket).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
    
    private static void handleGradingData(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Kết nối chấm điểm từ Client...");
                GradingHandler gradingHandler = new GradingHandler(clientSocket);
                new GradingHandler(clientSocket).start(); // Khởi động thread
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
