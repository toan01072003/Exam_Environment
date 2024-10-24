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
        Timer windowMonitor = new Timer(2000, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isActive = examView.isActive(); 
                if (isWindowActive && !isActive) {
                    
                    System.out.println("Warning: Student switched to another window!");
                    System.out.println("Warning: Student switched to another window at " + LocalTime.now());
                }
                isWindowActive = isActive;
            }
        });
        windowMonitor.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamController());
    }
}
