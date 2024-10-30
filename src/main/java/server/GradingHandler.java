package server;
import java.io.*;
import java.net.*;

public class GradingHandler extends Thread {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public GradingHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            // Listen for grading requests
            while (true) {
                String request = (String) in.readObject();
                if (request.equals("REQUEST_GRADING")) {
                    String result = gradeExam(); // Your logic to grade the exam
                    out.writeObject(result);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String gradeExam() {
        // Implement your grading logic here
        return "Your score is: 90"; // Example result
    }
}
