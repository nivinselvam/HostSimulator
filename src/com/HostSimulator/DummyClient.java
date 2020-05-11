package com.HostSimulator;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class DummyClient {

	public static void main(String[] args) {
		try {
			boolean shouldRun = true;
			Socket socket = new Socket("127.0.0.1", 15032);
			System.out.println("Client is running");
			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String msgin = "", msgout = "";
			Converter converter = new Converter();
			String temp = "";
			while (!msgin.equals("end")) {

				msgout = br.readLine();
				temp = converter.toHexString(msgout);
				byte[] messageToClient = temp.getBytes("ISO-8859-1");
				int messageSize = messageToClient.length;
				dout.writeShort(messageSize + 2);
				dout.write(messageToClient);
				dout.flush();

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
					int msgSize = din.readShort() - 2;
					byte[] message = new byte[msgSize];
					din.read(message, 0, msgSize);
					StringBuffer responsePacket = new StringBuffer();
					for (byte currByte : message) {
						responsePacket.append(String.format("%02x", currByte));

					}
					System.out.println(responsePacket.toString());
				}
				din.close();
				dout.close();
			}
			socket.close();

		} catch (ConnectException e) {
			System.out.println("Server is not available, unable to establish connection");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String toHexString(String hex) {
		if (hex == null || hex.trim().length() == 0) {
			System.out.println("Cannot convert null HexString to ByteString! ");
			return ("");
		}
		int l = hex.length();
		if (l % 2 != 0) {
			hex = "0" + hex;
		}
		l = hex.length();
		byte[] data = new byte[l / 2];
		for (int i = 0; i < l; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}
		return new String(data, Charset.forName("ISO-8859-1"));
	}

}
