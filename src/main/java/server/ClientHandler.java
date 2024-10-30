package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream()) {
            // Nhận và lưu ảnh chụp màn hình từ máy khách
            byte[] screenshotData = new byte[1024 * 1024]; // Buffer ảnh (1MB)
            int bytesRead;
            int count = 0;

            while ((bytesRead = in.read(screenshotData)) != -1) {
                // Lưu ảnh chụp màn hình
                File screenshotFile = new File("screenshot_" + count++ + ".jpg");
                
                // Use FileOutputStream to write the data to the file
                try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                    fos.write(screenshotData, 0, bytesRead);
                }
                
                System.out.println("Screenshot saved: " + screenshotFile.getName());
            }

        } catch (IOException e) {
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

