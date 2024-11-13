package server;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private static final long serialVersionUID = 1L;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            while (true) { 
                String notification = (String) in.readObject();
                // Xử lý các thông báo từ client ở đây
                System.out.println("Received notification: " + notification);
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
}
