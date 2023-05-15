package de.hswt.swa.socket;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

import de.hswt.swa.tools.CryptoUtil;

import de.hswt.swa.tools.Crypto;

public class SocketWorker extends Thread{
	/*
	 * 
	 */
	Socket client;
	
	public SocketWorker(Socket clientAnfrage) {
		// TODO Auto-generated constructor stub
		
		client = clientAnfrage;
		
	}

	public void run() {
		System.out.println("Running");
		if (client != null) {
			bearbeiteAnfrage();
		}
	}

	private boolean bearbeiteAnfrage() {
		// TODO Auto-generated method stub
		System.out.println("bearbeite Anfrage");
        try {
        	System.out.println(client);
        	ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
        	ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
        	
        	System.out.println("Streams created");
        	System.out.println("waiting for crypto");
			Crypto c = (Crypto) oin.readObject();
			
			if(c.getModus() == Crypto.ENCODE_MODUS) {
				
				List<String> encrypted = CryptoUtil.create_encoded(c.getPassphrase(),
                        c.getPlain());
				
				//passphrase allready set in Crypto executor! (encryption needs no check if wrong password is used)
				c.setEncrypted(encrypted);
				c.setDate(LocalDateTime.now());
				
				
				oout.writeObject(c);
				oout.flush();
				
				oin.close();
				oout.close();
				
				System.out.println("done");
				return true;
			} else if (c.getModus() == Crypto.DECODE_MODUS) {
				
				List<String> plain = CryptoUtil.create_decoded(c.getPassphrase(),
                                                               c.getEncrypted());
				//Password already set in Crypto executor!
				//there the password will only be changed in TextModel's crypto object
				//if the decoding was successful! (right password)
				c.setPlain(plain);
				
				
				oout.writeObject(c);
				oout.flush();
				
				oin.close();
				oout.close();
				return true;
			}
			return false;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
		
}
