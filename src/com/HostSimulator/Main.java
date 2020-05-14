package com.HostSimulator;

import java.awt.EventQueue;

public class Main {
	public static String fepName = "HPS";
	public static GUI window = new GUI();
	public static Server server = null;

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}