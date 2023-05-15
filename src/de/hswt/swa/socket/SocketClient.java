package de.hswt.swa.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.hswt.swa.tools.Crypto;

public class SocketClient {
	/*
	 * 
	 * Client Class not needed in present implementation
	 * 
	 * 
	 */
	public Socket socket;
	
	public SocketClient(String host, int port) throws UnknownHostException, IOException{
		this.socket = new Socket(host, port);
	}


	
	
	public Socket getSocket() {
		return socket;
	}
	
	public Crypto decode(Crypto crypto) {
		
		try {
			DataOutputStream dout=new DataOutputStream(socket.getOutputStream());
			dout.writeUTF("decode");
			dout.close();
			
			ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
			
			oout.writeObject(crypto);
			oout.flush();
			
			
			
			crypto = (Crypto) oin.readObject();
			
			oin.close();
			oout.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return crypto;
	}
	
	

	
}
