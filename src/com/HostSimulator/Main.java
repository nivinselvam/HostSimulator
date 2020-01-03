package com.HostSimulator;

import java.awt.EventQueue;
import javax.swing.UIManager;

public class Main{
	public static String fepName = "FCB";
	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					SimulatorGUI frame = new SimulatorGUI();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		//Server server = new Server();
		
		String requestPacket = "000000030510203800000A80000092000000000303355401033030303330303030303330303030383939303935303030303833";
		HexDecoder decoder = new HexDecoder(requestPacket);
		decoder.printEncodedData();
		//HexEncoder encoder = new HexEncoder(Constants.authorisationResponseMTI, decoder.geteHeader());
		

		
	}
	
}