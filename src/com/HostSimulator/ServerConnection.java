package com.HostSimulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection extends Thread {
	Socket socket;
	Server server;
	DataInputStream din;
	DataOutputStream dout;
	boolean shouldRun = true;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Converter converter = new Converter();
	Responses responses;
	public ServerConnection(Socket socket, Server server) {
		super("ServerConnectionThread");
		this.socket = socket;
		this.server = server;
	}

	public void sendStringtoClient(String text) {
		try {
			String tempString = converter.toHexString(text);
			byte[] messageToClient = tempString.getBytes("ISO-8859-1");
			int messageSize = messageToClient.length;
			dout.writeShort(messageSize+2);
			dout.write(messageToClient);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//
	// public void sendStringToAllClients(String text) {
	// for (int index = 0; index < server.connections.size(); index++) {
	// ServerConnection sc = server.connections.get(index);
	// sc.sendStringtoClient(text);
	// }
	//
	// }

	public void run() {
		String msgin = "", msgout = "";
		System.out.println("Client is connected");
		try {
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());

			while (shouldRun) {
				while (din.available() == 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				int msgSize = din.readShort();
				byte[] message = new byte[msgSize - 2];
				din.read(message, 0, msgSize - 2);

				StringBuffer requestPacket = new StringBuffer();
				for (byte currByte : message) {
					requestPacket.append(String.format("%02x", currByte));
				}
				
				responses = new Responses(requestPacket.toString());
				String responsePacket = "";
				if (msgSize < 33) {
					responsePacket = responses.echoMessageResponse();
				} else {
					responsePacket = responses.getResponsePacket();
				}				
				sendStringtoClient(responsePacket);

			}
			din.close();
			dout.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
