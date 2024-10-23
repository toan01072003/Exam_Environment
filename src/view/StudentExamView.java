package view;

import javax.swing.*;
import java.awt.event.ActionListener;

public class StudentExamView extends JFrame {
    private JTextField[] answerFields = new JTextField[10];
    private JButton submitButton;

    public StudentExamView() {
        super("Exam Interface");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        for (int i = 0; i < 10; i++) {
            JLabel label = new JLabel("Question " + (i + 1));
            JTextField answerField = new JTextField(20);
            answerFields[i] = answerField;
            add(label);
            add(answerField);
        }

        submitButton = new JButton("Submit");
        add(submitButton);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    public String[] getAnswers() {
        String[] answers = new String[10];
        for (int i = 0; i < 10; i++) {
            answers[i] = answerFields[i].getText();
        }
        return answers;
    }
}
