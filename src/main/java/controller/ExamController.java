package controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import client.ExamClient;
import server.NoiseDetection;
import view.StudentExamView;

public class ExamController {
	private ExamClient examClient;
	private StudentExamView examView;
	private boolean isWindowActive = true;
	private NoiseDetection noiseDetection;
	private static final String[] BLOCKED_APPS = { "firefox.exe", "chrome.exe" };
	private static final long CHECK_INTERVAL = 1000; // 5 giây
	public Boolean check;

	public ExamController() {
		// biến kiểm tra
		check = true;
		examClient = new ExamClient();
		examView = new StudentExamView();

		JFrame frame = new JFrame("Exam Interface");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(examView); // Add the view to the frame
		frame.pack();
		frame.setVisible(true);

		startWindowMonitoring();
		blockKeyboardShortcuts();
		startAppBlocking();
		examView.addSubmitListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] answers = examView.getAnswers();
				for (int i = 0; i < answers.length; i++) {
					examClient.sendAnswer(i + 1, answers[i]);
				}
				check = false;
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

	private void startAppBlocking() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			if (check) {
				checkAndBlockApps();
			}
		}, 0, CHECK_INTERVAL, TimeUnit.MILLISECONDS);
	}
 
	private void checkAndBlockApps() {
		try {
			Process process = Runtime.getRuntime().exec("tasklist");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				for (String app : BLOCKED_APPS) {
					if (line.contains(app)) {
						System.out.println("Chặn ứng dụng: " + app);
						Runtime.getRuntime().exec("taskkill /F /IM " + app);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_V
						|| e.getKeyCode() == KeyEvent.VK_X)) {
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
