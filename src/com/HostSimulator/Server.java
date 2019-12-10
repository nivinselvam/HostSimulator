package com.HostSimulator;

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

public class Server {

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

	ServerSocket ss;
	ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
	boolean shouldRun = true;

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		try {
			ss = new ServerSocket(15031);
			SimulatorGUI gui = new SimulatorGUI();
			System.out.println("Server started");
			gui.setServerStatus("Running");
			while (shouldRun) {
				Socket s = ss.accept();
				ServerConnection sc = new ServerConnection(s, this);
				sc.start();
				connections.add(sc);
			}

		} catch (Exception e) {

		}

	}

}
