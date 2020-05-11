package com.HostSimulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServerConnection extends Thread {
	final static Logger logger = Logger.getLogger(ServerConnection.class);
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
		PropertyConfigurator.configure("log4j.properties");
	}

	public void sendStringtoClient(String text) {
		try {
			String tempString = converter.toHexString(text);
			byte[] messageToClient = tempString.getBytes("ISO-8859-1");
			int messageSize = messageToClient.length;
			dout.writeShort(messageSize + 2);
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

	public void closeServer() throws IOException {
		try {
			if (dout != null)
				dout.close();
			if (din != null)
				din.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (!socket.isClosed())
			socket.close();
		System.out.println("Server connection closed");
		
	}

	public void run() {
		String msgin = "", msgout = "";
		System.out.println(socket.getRemoteSocketAddress().toString() + " is connected");
		logger.info("Client " + socket.getRemoteSocketAddress().toString() + " is connected");
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
							
				// HPS Sends the Data length in the first 2 bytes but other FEPs don't send.
				int msgSize = 0;
				if (Main.fepName.equals("HPS")) {
					msgSize = din.readShort() - 2;
				} else {
					msgSize = din.available();
				}
				
				byte[] message = new byte[msgSize];
				din.read(message, 0, msgSize);

				StringBuffer requestPacket = new StringBuffer();
				for (byte currByte : message) {
					requestPacket.append(String.format("%02x", currByte));
				}

				responses = new Responses(requestPacket.toString());
				logger.info(
						"*************************************************************************************************");
				logger.info("                                  Start of Transaction");
				logger.info(
						"*************************************************************************************************");
				logger.debug(requestPacket.toString());
				String responsePacket = "";
				if (msgSize < 33) {
					responsePacket = responses.echoMessageResponse();
				} else {
					responsePacket = responses.getResponsePacket();
				}
				sendStringtoClient(responsePacket);
				logger.info(
						"*************************************************************************************************");
				logger.info("                                   End of Transaction");
				logger.info(
						"*************************************************************************************************");
			}
			din.close();
			dout.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
