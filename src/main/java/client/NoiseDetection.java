package client;

import javax.sound.sampled.*;

public class NoiseDetection {

    private static final float THRESHOLD = 0.2f; // Ngưỡng để phát hiện âm thanh mạnh
    private static final int BUFFER_SIZE = 1024; // Kích thước bộ đệm
    private ExamClient examClient; // Tham chiếu đến ExamClient để gửi thông báo

    public NoiseDetection(ExamClient examClient) {
        this.examClient = examClient;
    }

    public void startListening() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    float amplitude = calculateAmplitude(buffer, bytesRead);
                    if (amplitude > THRESHOLD) {
                        sendAlert("Âm thanh bất thường phát hiện!");
                    }
                }
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private float calculateAmplitude(byte[] buffer, int bytesRead) {
        float sum = 0;
        for (int i = 0; i < bytesRead; i += 2) {
            int sample = (buffer[i] & 0xFF) | (buffer[i + 1] << 8);
            sum += Math.abs(sample / 32768.0); // Chia cho 32768 để chuẩn hóa giá trị
        }
        return sum / (bytesRead / 2);
    }


    private void sendAlert(String message) {
        // Gửi thông báo đến ExamClient
        examClient.sendAlert(message);
    }
}