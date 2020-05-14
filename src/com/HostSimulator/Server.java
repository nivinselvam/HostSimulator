package com.HostSimulator;

import java.io.IOException;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Server extends Thread{
	
	static {
		TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stubroot
				return null;
			}
		}

		};

		HostnameVerifier trustAllHostnames = new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		};

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCertificates, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
		} catch (GeneralSecurityException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	final static Logger logger = Logger.getLogger(Server.class);
	
	public ServerSocket serverSocket;
	public Socket socket;
	ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	boolean shouldRun = true , serverStarted = false;
	
	public String serverStatus = "";
	public ServerConnection serverConnection;
	int portNumber = Integer.parseInt(Main.window.getPortNumber());
	public Server() {
				
	}
	

	public void run() {
		try {
			PropertyConfigurator.configure("log4j.properties");
			serverSocket = new ServerSocket(portNumber);	
			serverStarted = true;
			System.out.println("Server started");
			serverStatus = "Server Started";
			logger.info(Main.fepName+" Server started successfully");
			while (shouldRun) {
				socket = serverSocket.accept();
				serverConnection = new ServerConnection(socket, this);
				serverConnection.start();
				connections.add(serverConnection);
			}			
		} catch (Exception e) {
			serverStarted = false;
			logger.fatal("Unable to start the server");
			serverStatus = ("Unable to start the server");
			e.printStackTrace();
		}
	}
	
}
