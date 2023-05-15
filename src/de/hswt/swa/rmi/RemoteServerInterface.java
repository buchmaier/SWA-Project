package de.hswt.swa.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.hswt.swa.tools.Crypto;

public interface RemoteServerInterface extends Remote {
	
	public Crypto encrypt(Crypto c, String passphrase) throws RemoteException;
	
	public Crypto decrypt(Crypto c, String passphrase) throws RemoteException;

	
	
}
