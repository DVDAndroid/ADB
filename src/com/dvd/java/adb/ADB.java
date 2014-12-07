package com.dvd.java.adb;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ADB extends JFrame {

	JPanel contentPane;
	JTextField textField_custom_ip;
	JButton btnAdbKillserver;
	JButton btnAdbConnect;
	JRadioButton zero;
	JRadioButton one;
	JRadioButton two;
	JRadioButton three;
	JRadioButton four;
	JRadioButton custom;
	static JLabel status;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					ADB frame = new ADB();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 */
	public ADB() {
		setResizable(false);
		setTitle("ADB Utility");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 298, 236);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		status = new JLabel("", SwingConstants.CENTER);
		status.setVisible(false);
		status.setBounds(17, 178, 262, 23);
		contentPane.add(status);

		zero = new JRadioButton("192.168.1.100");
		zero.setBounds(17, 17, 109, 23);
		contentPane.add(zero);

		one = new JRadioButton("192.168.1.101");
		one.setBounds(17, 43, 109, 23);
		contentPane.add(one);

		two = new JRadioButton("192.168.1.102");
		two.setSelected(true);
		two.setBounds(17, 70, 109, 23);
		contentPane.add(two);

		three = new JRadioButton("192.168.1.103");
		three.setBounds(17, 96, 109, 23);
		contentPane.add(three);

		custom = new JRadioButton("192.168.");
		custom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textField_custom_ip.setEnabled(true);
			}
		});

		four = new JRadioButton("192.168.107.132");
		four.setBounds(17, 122, 109, 23);
		contentPane.add(four);
		custom.setBounds(17, 148, 69, 23);
		contentPane.add(custom);

		textField_custom_ip = new JTextField();
		textField_custom_ip.setEnabled(false);
		textField_custom_ip.setBounds(88, 149, 38, 20);
		textField_custom_ip.setColumns(10);
		contentPane.add(textField_custom_ip);

		final ButtonGroup group = new ButtonGroup();
		group.add(zero);
		group.add(one);
		group.add(two);
		group.add(three);
		group.add(four);
		group.add(custom);

		btnAdbKillserver = new JButton("ADB kill-server");
		btnAdbKillserver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					adbComm("kill-server");
				} catch (IOException e) {
					e.printStackTrace();
				}
				status.setText("Server killed");
			}
		});
		btnAdbKillserver.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAdbKillserver.setForeground(Color.red);
		btnAdbKillserver.setBounds(161, 96, 118, 23);
		contentPane.add(btnAdbKillserver);

		btnAdbConnect = new JButton("ADB connect");
		btnAdbConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getSelectedButtonText(group).equals("192.168.")) {
					try {
						adbComm("connect 192.168."
								+ textField_custom_ip.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						adbComm("connect " + getSelectedButtonText(group));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		});

		btnAdbConnect.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAdbConnect.setForeground(new Color(0, 128, 0));
		btnAdbConnect.setBounds(161, 43, 118, 23);
		contentPane.add(btnAdbConnect);

	}

	public String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons
				.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}

	public static String adbComm(String arg) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
					.exec("adb " + arg).getInputStream()));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String strLine;
		String text = "";

		try {

			while ((strLine = br.readLine()) != null) {
				text = text + strLine + "\n";
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!text.equals(""))
			status.setText(text);

		if (arg.contains("connect")) {
			if (text.contains("unable") || text.contains("batch")) {
				status.setForeground(Color.red);
			} else {
				if (text.contains("already")) {
					status.setForeground(Color.decode("#FFCC00"));
				} else {

					if (text.contains("daemon")) {
						status.setText("Retry.");
						status.setForeground(Color.red);
						if (text.contains("connected")) {
							status.setText("connected to "
									+ arg.replace("connect", "") + ":5555");
							status.setForeground(Color.green);
						}
					} else {
						if (text.contains("not")) {
							status.setForeground(Color.red);
						}
					}
				}
			}
		} else {
			if (arg.equals("kill-server") || text.equals("")) {
				status.setForeground(Color.black);
				status.setText("Server killed\n");

			}
		}

		status.setVisible(true);

		return text;
	}
}
