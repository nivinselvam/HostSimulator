package com.HostSimulator;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import java.awt.Font;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpringLayout;

public class SimulatorGUI extends JFrame {

	private JPanel contentPane;
	private JLabel lblServerPortValue;
	private JLabel lblServerIPValue;
	private JButton btnStart;
	private JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	public SimulatorGUI() {		
		initComponents();
		createEvents();
	}

	// ---------------------------------------------------------------------------------------------------------------------------
	/*
	 * This method contains code for creating and initializing the GUI
	 * components
	 */
	// --------------------------------------------------------------------------------------------------------------------------
	public void initComponents() {
		
		//JFrame
		setTitle("HPS NWS Host Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 704, 628);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File     ");
		mnFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
		menuBar.add(mnFile);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setFont(new Font("Segoe UI", Font.BOLD, 16));
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
		);
		
		JPanel panel_Main = new JPanel();
		tabbedPane.addTab("Main", null, panel_Main, null);
		
		JLabel lblSavedConfiguratoin = new JLabel("Server Details");
		lblSavedConfiguratoin.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JLabel lblHostLogs = new JLabel("Logs");
		lblHostLogs.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JLabel lblServerIp = new JLabel("Server IP:");
		lblServerIp.setFont(new Font("Tahoma", Font.BOLD, 13));
		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 13));
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			lblServerIPValue = new JLabel(localHost.getHostAddress());
			lblServerIPValue.setFont(new Font("Tahoma", Font.PLAIN, 14));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}		
		lblServerPortValue = new JLabel("15031");
		lblServerPortValue.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblConnectionStatus = new JLabel("Status:");
		lblConnectionStatus.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		lblStatus = new JLabel("Not Running");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnStop = new JButton("Stop");

		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 14));
		SpringLayout sl_panel_Main = new SpringLayout();
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblHostLogs, 82, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblHostLogs, 12, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, btnStop, 39, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, btnStop, 590, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, btnStop, 659, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, btnStart, 39, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, btnStart, 504, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, btnStart, 583, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblStatus, 43, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblStatus, 395, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, lblStatus, 499, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblConnectionStatus, 44, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblConnectionStatus, 316, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, lblConnectionStatus, 388, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblServerPortValue, 43, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblServerPortValue, 247, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblPort, 44, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblPort, 195, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, lblPort, 240, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblServerIPValue, 43, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblServerIPValue, 85, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblServerIp, 44, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblServerIp, 12, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.NORTH, lblSavedConfiguratoin, 13, SpringLayout.NORTH, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.WEST, lblSavedConfiguratoin, 12, SpringLayout.WEST, panel_Main);
		sl_panel_Main.putConstraint(SpringLayout.EAST, lblSavedConfiguratoin, 151, SpringLayout.WEST, panel_Main);
		panel_Main.setLayout(sl_panel_Main);
		panel_Main.add(lblSavedConfiguratoin);
		panel_Main.add(lblServerIp);
		panel_Main.add(lblServerIPValue);
		panel_Main.add(lblPort);
		panel_Main.add(lblServerPortValue);
		panel_Main.add(lblConnectionStatus);
		panel_Main.add(lblStatus);
		panel_Main.add(btnStart);
		panel_Main.add(btnStop);
		panel_Main.add(lblHostLogs);
		
		JScrollPane scrollPane = new JScrollPane();
		sl_panel_Main.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, lblHostLogs);
		sl_panel_Main.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, lblSavedConfiguratoin);
		sl_panel_Main.putConstraint(SpringLayout.SOUTH, scrollPane, 404, SpringLayout.SOUTH, lblHostLogs);
		sl_panel_Main.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, btnStop);
		panel_Main.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		JPanel panel_Configuration = new JPanel();
		tabbedPane.addTab("Configuration", null, panel_Configuration, null);
		
		JLabel lblTransactionDetails = new JLabel("Transaction Details:");
		lblTransactionDetails.setFont(new Font("Tahoma", Font.BOLD, 15));
		GroupLayout gl_panel_Configuration = new GroupLayout(panel_Configuration);
		gl_panel_Configuration.setHorizontalGroup(
			gl_panel_Configuration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Configuration.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTransactionDetails)
					.addContainerGap(603, Short.MAX_VALUE))
		);
		gl_panel_Configuration.setVerticalGroup(
			gl_panel_Configuration.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_Configuration.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTransactionDetails)
					.addContainerGap(486, Short.MAX_VALUE))
		);
		panel_Configuration.setLayout(gl_panel_Configuration);
		contentPane.setLayout(gl_contentPane);
	}

	// ---------------------------------------------------------------------------------------------------------------------------
	/*
	 * This method contains code for creating and initializing the GUI
	 * components
	 */
	// --------------------------------------------------------------------------------------------------------------------------
	public void createEvents() {
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Server server = new Server();
			}
		});
	}
	
	public void setServerStatus(String status){
		lblStatus.setText(status);
	}
}
