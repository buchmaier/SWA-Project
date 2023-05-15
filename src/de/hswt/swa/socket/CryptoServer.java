package de.hswt.swa.socket;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;



public class CryptoServer {
	private static final int PORT = 1604;
	private ServerSocket server;
	
	public CryptoServer() throws IOException {
		
		// Port @portNummer belegen: hole Port aus Properties-Datei
		Properties properties = new Properties();
		String propertyFileName = "crypto.properties";
		try {
			FileInputStream fis = new FileInputStream(propertyFileName);
			BufferedInputStream stream = new BufferedInputStream(fis);
			properties.load(stream);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int port = Integer.parseInt(properties.getProperty("socketPort"));
		
		
		server = new ServerSocket(port);
		
		

		
		while (true) {
			//warte auf CLient
			System.out.println("waiting for client");
			Socket clientAnfrage =  server.accept();
			System.out.println("client accepted");
			//Starte Bearbeitung in neuem Thread
			SocketWorker worker = new SocketWorker(clientAnfrage);
			worker.start();
		}
	}	
		
	
	public static void main(String args[]) {
			
			try {
				new CryptoServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}

