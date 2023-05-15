package de.hswt.swa.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import de.hswt.swa.gui.TextModelObserver;
import de.hswt.swa.tools.Crypto;
import de.hswt.swa.tools.CryptoExecutor;
/*
 * 
 * Textmodel with Crypto-Object observed by the gui
 * 
 */
public class TextModel implements TextModelObservable{
	
	private Collection<TextModelObserver> observers = new ArrayList<>();
	private Crypto crypto;
	
	public TextModel() {
		crypto = new Crypto();
	}
	
	public Crypto getCrypto(){
		return this.crypto;
	}
	
	public void readEncrypted(File file){
		//read encrypted (.encr) file from file System
		List<String> encryptedText = readFile(file);
		
		List<String> l = new ArrayList<String>();
		
		
		crypto.setPlain(l);
		crypto.setEncrypted(encryptedText);
		crypto.setDate(null);
		crypto.setPassphrase(null);
		crypto.setModus(0);
		
		fireUpdate();
	}
	
	public void readPlain(File file) {
		
		List<String> l = new ArrayList<String>();
		
		List<String> plainText = readFile(file);
		crypto.setPlain(plainText);
		crypto.setEncrypted(l);
		crypto.setDate(null);
		crypto.setPassphrase(null);
		crypto.setModus(0);
		
		fireUpdate();
	}
	
	
	
	public boolean saveEncrypted(File file) {
		if(crypto.getEncrypted() != null) {
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
				for (String str : crypto.getEncrypted()) {
				    br.write(str + System.lineSeparator());
				}
				br.close();
				return true;
			} catch (Exception e) {
				System.out.println("Exception when saving encrypted file");
				e.printStackTrace();
				return false;
			}
			
		}else {
			return false;
		}
		
	}
	
	
	
	public void saveCrypto(File file) {
		try {
			FileOutputStream fout = new FileOutputStream(file);
			// using the decorator-pattern again to augment the stream with new functionality
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			
			oout.writeObject(crypto);
			
			oout.flush();
			oout.close();
			
		} catch (Exception e) {
			System.out.println("Exception when saving crypto object");
			e.printStackTrace();
		}
	}
	
	public void openCrypto(File file) {
		
		try {
			FileInputStream fis = new FileInputStream(file);
			// using the decorator-pattern again to augment the stream with new functionality
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			crypto = (Crypto) ois.readObject(); // class cast
			
			ois.close();
		
		} catch (Exception e) {
			System.out.println("Exception when saving file");
			e.printStackTrace();
			
		}
		this.fireUpdate();
		
	}
		
	
	public boolean encrypt(String passphrase, int mode) {
		//check if non-empty plain Text exists in current Crypto Object 
		if(crypto.getPlain()!=null || crypto.getPlain().isEmpty()) {
			CryptoExecutor executor = new CryptoExecutor(mode, crypto);
			boolean success = executor.encrypt(passphrase);
			System.out.println("encrypted: "+crypto.getEncrypted());
			fireUpdate();
			return success;
		}else {
			return false;
		}
		
	}
	
	public boolean decrypt(String passphrase, int mode) {
		boolean success = false;
		if(crypto.getEncrypted()!=null || crypto.getEncrypted().isEmpty()) {
			CryptoExecutor executor = new CryptoExecutor(mode, crypto);
			success = executor.decrypt(passphrase);
			
			if(success) {
				fireUpdate();
			}
			
		}
		return success;
		
	}
	
	public List<String> readFile(File file) {
		BufferedReader br;
		ArrayList<String> content  = new ArrayList<String>();;
		try {
			br = new BufferedReader(new FileReader(file));
			// Declaring a string variable
			String st;
			while ((st = br.readLine()) != null) {
				content.add(st);
			}
			br.close();
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
			
	}
	@Override
	public void registerObserver(TextModelObserver observer) {
		// TODO Auto-generated method stub
		this.observers.add(observer);
	}

	@Override
	public void unRegisterObserver(TextModelObserver observer) {
		// TODO Auto-generated method stub
		this.observers.remove(observer);
	}

	
	@Override
	public void fireUpdate() {
		// TODO Auto-generated method stub
		for(TextModelObserver o : observers) {
			o.update(crypto.getEncrypted(), crypto.getPlain());
		}
	}
}
