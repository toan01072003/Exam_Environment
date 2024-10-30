package server;

import java.io.*;
import java.net.*;

public class ExamServer {
    private static final int PORT_TEXT = 8888; // For text messages
    private static final int PORT_CAMERA = 8889; // For camera data

    public static void main(String[] args) {
        try {
            ServerSocket textServerSocket = new ServerSocket(PORT_TEXT);
            ServerSocket cameraServerSocket = new ServerSocket(PORT_CAMERA);

            System.out.println("Exam Server is running..."); 
            new Thread(() -> handleTextData(textServerSocket)).start();
            new Thread(() -> handleCameraData(cameraServerSocket)).start();
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
}
