package controller;

import client.ExamClient;
import view.StudentExamView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class ExamController {
    private ExamClient examClient;
    private StudentExamView examView;
    private boolean isWindowActive = true;

    public ExamController() {
        examClient = new ExamClient();
        examView = new StudentExamView();
        startWindowMonitoring();

        // Thêm action listener cho nút "Submit"
        examView.addSubmitListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] answers = examView.getAnswers();
                for (int i = 0; i < answers.length; i++) {
                    examClient.sendAnswer(i + 1, answers[i]);
                }
                examClient.submitExam();
                JOptionPane.showMessageDialog(null, "You have submitted your exam!");
                System.exit(0);
            }
        });
    }
    
    private void startWindowMonitoring() {
        Timer windowMonitor = new Timer(2000, new ActionListener() { // Check window state every 2 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = examView.isActive(); // Check if the exam window is active
                if (isWindowActive && !isActive) {
                    // If the window was active and suddenly is not -> possible student switched windows
                    System.out.println("Warning: Student switched to another window!");
                    System.out.println("Warning: Student switched to another window at " + LocalTime.now());
                }
                isWindowActive = isActive; // Update the window state
            }
        });
        windowMonitor.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamController());
    }
}
