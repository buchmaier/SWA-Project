package de.hswt.swa.rmi;


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;

import de.hswt.swa.tools.Crypto;



import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import de.hswt.swa.tools.CryptoUtil;

import de.hswt.swa.tools.Crypto;


public class RMIServer extends UnicastRemoteObject implements RemoteServerInterface{
	/**
	 * RMI-Server for excercise 3
	 * to be started on bb-x-xterm.hswt.de
	 */
	private static final long serialVersionUID = 1L;

	protected RMIServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		try {
			System.setProperty("java.rmi.server.hostname", "bb-x-xterm.hswt.de");
			// erzeuge den Broker
			 try {
				 LocateRegistry.createRegistry(1903);
			 }catch(RemoteException rexp){
				 rexp.printStackTrace();
			 }
			
			//beachte Hinweis zur internen RMI-Einstellung

			// 1. Schritt: Serverobjekt erzeugen:
			RMIServer myServer = new RMIServer();

			// 2. Schritt: Registrierung am Broker als 'cryptoService':
			System.out.println("Anmeldung am Broker");
			Naming.bind("rmi://bb-x-xterm.hswt.de:1903/cryptoService", myServer);
			// Fertig!
			System.out.println("Server erfolgreich gestartet");
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Es ist folgender Fehler aufgetreten:");
			System.out.println(e.toString());
		}
	}
	/*
	@Override
	public RemoteCrypto getRemoteCrypto() throws RemoteException {
		// TODO Auto-generated method stub
		return new RemoteCryptoRMI();
	}
    */
	
	@Override
	public Crypto decrypt(Crypto c, String passphrase) {
		// TODO Auto-generated method stub
		List<String> plainText = CryptoUtil.create_decoded(passphrase, c.getEncrypted());
		c.setPlain(plainText);
		c.setPassphrase(passphrase);
		return c;
	}

	@Override
	public Crypto encrypt(Crypto c, String passphrase) {
		// TODO Auto-generated method stub
		List<String> encodedText = CryptoUtil.create_encoded(passphrase, c.getPlain());
		c.setEncrypted(encodedText);
		c.setDate(LocalDateTime.now());
		c.setPassphrase(passphrase);
		
		System.out.println(encodedText);
		return c;
	}
	
}