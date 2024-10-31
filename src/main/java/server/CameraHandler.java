package server;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

public class CameraHandler extends Thread {
    private Socket clientSocket;

    public CameraHandler(Socket socket) {
        this.clientSocket = socket;
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
                // Read image bytes from the input stream
                byte[] imageBytes = (byte[]) in.readObject();

                // Convert byte array to BufferedImage
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bais);

                
                
                // Update the JLabel with the new image
                label.setIcon(new ImageIcon(image));
                label.repaint(); // Repaint the label to show the new image
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Ensure the socket is closed when done
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}  