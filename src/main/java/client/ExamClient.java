package client;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

public class ExamClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8888;
    private PrintWriter out;
    private Socket socket;

    public ExamClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAnswer(int questionNumber, String answer) {
        LocalTime time = LocalTime.now();
        out.println("Answered question " + questionNumber + " at " + time + ": " + answer);
        System.out.println("Logged answer for question " + questionNumber + " at " + time);
    }

    public void submitExam() {
        out.println("End of exam");
        closeConnection();
    }

    private void closeConnection() {
        try {
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
