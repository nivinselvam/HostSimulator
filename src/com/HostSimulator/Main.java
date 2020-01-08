package com.HostSimulator;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.TreeSet;

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
		
//		Server server = new Server();
		
		String requestPacket = "0003000002003020078020C0820000100000000000050000625100520009011102345413330089010483D251220108406035203030303038333730313734383633303030303030303030084001679F1A0208409A031806129F2701809C01009F03060000000000009F360200015F3401099F260863CEDE7110B5F33F9F370487E16FCE9F090200029F4104000000029F530235329F34034103029F02060000000005009F3501259F10120212A0000F240000DAC000000000000000FF9F33120212A0000F240000DAC000000000000000FF5F2A020840820258009F1E0838333734333636338407A000000004306095050280008000";
		Responses responses = new Responses(requestPacket);
		System.out.println(responses.getResponsePacket());
		
		
		

		
	}
	
}