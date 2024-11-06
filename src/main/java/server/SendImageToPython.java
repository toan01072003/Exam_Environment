package server;

import java.net.Socket;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class SendImageToPython {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             OutputStream os = socket.getOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(os)) {

            // Load image
            BufferedImage img = ImageIO.read(new File("path_to_image.jpg"));
            oos.writeObject(img);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

