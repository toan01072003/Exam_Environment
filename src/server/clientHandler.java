package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class clientHandler implements Runnable {
    private Socket clientSocket;

    public clientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream()) {
        
            byte[] screenshotData = new byte[1024 * 1024]; 
            int bytesRead;
            int count = 0;

            while ((bytesRead = in.read(screenshotData)) != -1) {
                
                File screenshotFile = new File("screenshot_" + count++ + ".jpg");
                
               
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

