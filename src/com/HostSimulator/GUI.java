package com.HostSimulator;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.BadLocationException;

import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JInternalFrame;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class GUI {

	public JFrame frame;
	public JTextArea txtLogs;
	public JMenuItem mntmExit;
	private JTextField txtPort;
	private JTextField txtIP;
	private JTextField textField;
	private JTextField textField_1;

	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 466, 609);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE).addContainerGap()));

		JPanel pnConfiguration = new JPanel();
		tabbedPane.addTab("Configuration", null, pnConfiguration, null);

		JLabel lblServer = new JLabel("Server:");
		lblServer.setFont(new Font("Tahoma", Font.BOLD, 13));

		JLabel lblIp = new JLabel("IP:");

		JLabel lblPort = new JLabel("Port:");

		txtPort = new JTextField();
		txtPort.setColumns(10);

		txtIP = new JTextField();
		txtIP.setColumns(10);

		JLabel lblserverstatusvalue = new JLabel("Offline");

		JButton btnStartServer = new JButton("Start Server");

		JButton btnStopServer = new JButton("Stop Server");

		JLabel lblResponseConfiguration = new JLabel("Response Configuration:");
		lblResponseConfiguration.setFont(new Font("Tahoma", Font.BOLD, 13));

		JLabel lblTransactionResult = new JLabel("Transaction Result:");
		String[] result = { "Approve", "Decline", "Partially Approve" };
		JComboBox cbxTransactionResult = new JComboBox(result);

		JLabel lblResponseCode = new JLabel("Response Code:");

		JLabel lblApprovalAmount = new JLabel("Approval Amount:");

		textField = new JTextField();
		textField.setColumns(10);

		JCheckBox chckbxApproveForHalf = new JCheckBox("Approve for half of transaction amout");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		GroupLayout gl_pnConfiguration = new GroupLayout(pnConfiguration);
		gl_pnConfiguration.setHorizontalGroup(
			gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnConfiguration.createSequentialGroup()
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addGap(12)
							.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnConfiguration.createSequentialGroup()
									.addComponent(lblServer)
									.addGap(12)
									.addComponent(lblserverstatusvalue))
								.addGroup(gl_pnConfiguration.createSequentialGroup()
									.addComponent(lblIp)
									.addGap(30)
									.addComponent(txtIP, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(btnStartServer))
								.addComponent(lblResponseConfiguration)
								.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING, false)
									.addGroup(gl_pnConfiguration.createSequentialGroup()
										.addComponent(lblPort)
										.addGap(18)
										.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(btnStopServer))
									.addGroup(gl_pnConfiguration.createSequentialGroup()
										.addComponent(lblTransactionResult)
										.addGap(18)
										.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
											.addComponent(cbxTransactionResult, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(chckbxApproveForHalf)
											.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
											.addComponent(textField, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))))))
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblResponseCode))
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblApprovalAmount)))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		gl_pnConfiguration.setVerticalGroup(
			gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnConfiguration.createSequentialGroup()
					.addGap(13)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
						.addComponent(lblServer)
						.addComponent(lblserverstatusvalue))
					.addGap(13)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addGap(4)
							.addComponent(lblIp))
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addGap(1)
							.addComponent(txtIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnStartServer))
					.addGap(24)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addGap(4)
							.addComponent(lblPort))
						.addGroup(gl_pnConfiguration.createSequentialGroup()
							.addGap(1)
							.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnStopServer))
					.addGap(52)
					.addComponent(lblResponseConfiguration)
					.addGap(21)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTransactionResult)
						.addComponent(cbxTransactionResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblResponseCode)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(24)
					.addGroup(gl_pnConfiguration.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblApprovalAmount)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxApproveForHalf)
					.addGap(157))
		);
		pnConfiguration.setLayout(gl_pnConfiguration);

		JPanel pnLogs = new JPanel();
		tabbedPane.addTab("Logs", null, pnLogs, null);

		JButton btnClearLogs = new JButton("Clear Logs");

		JScrollPane spLogs = new JScrollPane();
		GroupLayout gl_pnLogs = new GroupLayout(pnLogs);
		gl_pnLogs.setHorizontalGroup(gl_pnLogs.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnLogs.createSequentialGroup().addContainerGap()
						.addGroup(gl_pnLogs.createParallelGroup(Alignment.LEADING)
								.addComponent(btnClearLogs, Alignment.TRAILING)
								.addComponent(spLogs, GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE))
						.addContainerGap()));
		gl_pnLogs.setVerticalGroup(gl_pnLogs.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnLogs.createSequentialGroup().addContainerGap().addComponent(btnClearLogs)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(spLogs, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE).addContainerGap()));

		txtLogs = new JTextArea();
		spLogs.setViewportView(txtLogs);
		pnLogs.setLayout(gl_pnLogs);
		PrintStream printStream = new PrintStream(new CustomOutputStream(txtLogs));
		System.setOut(printStream);
		System.setErr(printStream);

		btnClearLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					txtLogs.getDocument().remove(0, txtLogs.getDocument().getLength());
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().setLayout(groupLayout);

		/*
		 * Given below are the actions of all the GUI items
		 */

		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
		});

		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.server = new Server();
				Main.server.start();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (Main.server.serverStarted) {
					lblserverstatusvalue.setText("Online");
					btnStartServer.setEnabled(false);
				} else {
					lblserverstatusvalue.setText("Offline");
					btnStartServer.setEnabled(true);
				}
			}
		});

		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
						Main.server.serverSocket.close();
						btnStartServer.setEnabled(true);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					lblserverstatusvalue.setText("Offline");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Unable to close socket");
				}

			}
		});

	}
}
