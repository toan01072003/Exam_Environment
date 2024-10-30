package server;

import java.io.*;
import java.net.*;

public class examServer {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Exam Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new clientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
