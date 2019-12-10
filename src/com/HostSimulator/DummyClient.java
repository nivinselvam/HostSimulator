package com.HostSimulator;

import java.io.*;
import java.net.*;
public class DummyClient {

	public static void main(String[] args) {
		try{
			Socket s = new Socket("127.0.0.1",15031);
			System.out.println("Client is running");
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));			
			String msgin = "", msgout = "";
			
			while(!msgin.equals("end")){
				
				msgout = br.readLine();
				dout.writeUTF(msgout);
				
				//msgin = din.readUTF();
				int messageSize = din.readShort();
				byte[] message = new byte[messageSize];
				din.read(message, 0, messageSize);
				String responsePacket = new String(message);
				System.out.println(responsePacket);		
				
				dout.flush();
			}
			s.close();
			
		}catch(ConnectException e){
			System.out.println("Server is not available, unable to establish connection");
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

}
