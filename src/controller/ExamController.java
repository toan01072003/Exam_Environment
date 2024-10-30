package controller;

import client.ExamClient;
import view.StudentExamView;

import javax.swing.*;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;

public class ExamController {
	private PrintWriter out;
    private ExamClient examClient;
    private StudentExamView examView;
    private boolean isWindowActive = true;

    public ExamController() {
    	
        examClient = new ExamClient();
        examView = new StudentExamView();
        startWindowMonitoring();
        blockKeyboardShortcuts();

        
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
    
    private void blockKeyboardShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                
                if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_V || e.getKeyCode() == KeyEvent.VK_X)) {
                    System.out.println("Blocked key combination: " + KeyEvent.getKeyText(e.getKeyCode()));
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
