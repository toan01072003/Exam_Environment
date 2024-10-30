package view;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

public class StudentExamView extends JPanel {
    private JTextArea textArea;
    private JButton submitButton;

    public StudentExamView() {
        textArea = new JTextArea(20, 50);
        submitButton = new JButton("Submit");

        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    public String[] getAnswers() {
        return textArea.getText().split("\n");
    }

    public boolean isActive() {
    	return  this.isShowing();
    }
}
