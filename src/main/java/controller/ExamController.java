package controller;

import client.ExamClient;
import client.NoiseDetection;
import view.StudentExamView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ExamController {
    private ExamClient examClient;
    private StudentExamView examView;
    private boolean isWindowActive = true;
    private NoiseDetection noiseDetection;
    public ExamController() {
        examClient = new ExamClient();
        examView = new StudentExamView();

        
        JFrame frame = new JFrame("Exam Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(examView); // Add the view to the frame
        frame.pack();
        frame.setVisible(true);

        startWindowMonitoring();
        blockKeyboardShortcuts();

        examView.addSubmitListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] answers = examView.getAnswers();
                for (int i = 0; i < answers.length; i++) {
                    examClient.sendAnswer(i + 1, answers[i]);
                }
                JOptionPane.showMessageDialog(null, "You have submitted your exam!");
                examClient.submitExam();
                
                System.exit(0);
            }
        });

        // chay noise 
        noiseDetection = new NoiseDetection(examClient);
        new Thread(() -> noiseDetection.startListening()).start();
        // Chạy camera stream trên luồng riêng
        new Thread(() -> examClient.startCameraStream()).start();
    }

    private void startWindowMonitoring() {
        Timer windowMonitor = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = examView.isActive();
                System.out.println("Window active status: " + isActive);
                System.out.println("Previous window active status: " + isWindowActive);

                if (isWindowActive && !isActive) {
                    examClient.sendWindowStatus(isActive); // Gửi thông báo đến server khi chuyển tab
                    System.out.println("Window inactive, sent notification to server."); // Ghi log để kiểm tra
                } else if (!isWindowActive && isActive) {
                    examClient.sendWindowStatus(isActive); // Gửi thông báo khi quay lại cửa sổ
                    System.out.println("Window active again, sent notification to server."); // Ghi log để kiểm tra
                }
                isWindowActive = isActive;
            }
        });
        windowMonitor.start();
    }


    private void blockKeyboardShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_V || e.getKeyCode() == KeyEvent.VK_X)) {
                    examClient.sendBlockedKeyCombination(KeyEvent.getKeyText(e.getKeyCode()));
                    return true;
                }
                return false;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamController());
    }
}
