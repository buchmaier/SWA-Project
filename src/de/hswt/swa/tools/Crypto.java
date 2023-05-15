package de.hswt.swa.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.hswt.swa.gui.TextModelObserver;
import de.hswt.swa.logic.TextModelObservable;


import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

public class Crypto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public final static int ENCODE_MODUS = 0;
	public final static int DECODE_MODUS = 1;
	
	static String cryptTransformation = "AES";
	private List<String> plain;
	private List<String> encrypted;
	private String passphrase;
	private LocalDateTime date;
	private int modus;
	

	public int getModus() {
		return modus;
	}



	public void setModus(int modus) {
		this.modus = modus;
	}

	public void copy(Crypto crypto) {
		this.date = crypto.getDate();
		this.plain = crypto.getPlain();
		this.encrypted = crypto.getEncrypted();
		this.passphrase = crypto.getPassphrase();
		this.modus = crypto.getModus();
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public LocalDateTime getDate(LocalDateTime date) {
		return this.date;
	}

	public LocalDateTime getDate() {
		// TODO Auto-generated method stub
		return date;	
	}



	public boolean encode(OutputStream writer, byte[] input, String passPhrase) {
		
		Cipher c;
		try {
			if (passPhrase.length() > 16) return false;
			while (passPhrase.length() < 16) {
				passPhrase = passPhrase + 'x';
			}
			c = Cipher.getInstance(cryptTransformation);
			Key k = new SecretKeySpec( passPhrase.getBytes(),cryptTransformation);
			c.init( Cipher.ENCRYPT_MODE, k );
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		} 
	    
		try {
			CipherOutputStream cypherOut = new CipherOutputStream(writer, c);
			for (byte nextByte : input) {
				cypherOut.write(nextByte);
			}
			cypherOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	public byte[] decode(InputStream input, String passPhrase) {
		
		Cipher c;
		try {
			if (passPhrase.length() > 16) return null;
			while (passPhrase.length() < 16) {
				passPhrase = passPhrase + 'x';
			}
			c = Cipher.getInstance(cryptTransformation);
			Key k = new SecretKeySpec( passPhrase.getBytes(),cryptTransformation);
			c.init( Cipher.DECRYPT_MODE, k );
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} 
	    
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			CipherInputStream cypherIn = new CipherInputStream(input, c);
			int nextByte;
			while ((nextByte = cypherIn.read()) != -1) {
				bos.write(nextByte);
			}
			cypherIn.close();
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void create_encoded(String passphrase) {
		this.passphrase = passphrase;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LinkedList<String> text = new LinkedList<String>();
		
		if (this.plain != null)
		for (String line : this.plain) {
			encode( out, line.getBytes(), passphrase);
			String s = Base64.getEncoder().encodeToString(out.toByteArray() );
			//System.out.println( s );  
			text.add(s);
			out.reset();
		}
		
		this.encrypted = text;	
		this.date = LocalDateTime.now();
		
	}
	
	
	public List<String> getPlain() {
		return plain;
	}

	public void setPlain(List<String> plain) {
		this.plain = plain;
	}

	public List<String> getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(List<String> encrypted) {
		this.encrypted = encrypted;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public boolean create_decoded(String passphrase) {
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LinkedList<String> text = new LinkedList<String>();
			if (this.encrypted!= null) {
				for (String s : this.encrypted) {
					System.out.println(s + "[]");
					byte[] bytes  = Base64.getDecoder().decode( s );
					InputStream is = new ByteArrayInputStream( bytes);
					byte[] plain = decode(is, passphrase);
					
					
					text.add(new String(plain));
					  
					out.reset();
				}
				
			}
			this.plain = text;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	

	
	public static void main(String[] arg) {
		
		Crypto crypto = new Crypto();
		Scanner scanner = new Scanner(System.in);
		
		try {
			System.out.print("Passphrase please: ");
			String pass = scanner.nextLine();
			
			while (pass.length() < 16) {
				pass = pass + 'x';
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			LinkedList<String> text = new LinkedList<String>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.equalsIgnoreCase("quit")) 
					break;
				crypto.encode( out, line.getBytes(), pass);

				String s = Base64.getEncoder().encodeToString(out.toByteArray() );
				//System.out.println( s );  
				text.add(s);
				out.reset();
			}
			
			text.forEach(System.out::println);
			
			for (String s : text) {
				byte[] bytes  = Base64.getDecoder().decode( s );
				InputStream is = new ByteArrayInputStream( bytes);
				byte[] plain = crypto.decode(is, pass);

				//String line = new BASE64Encoder().encode(plain);
				System.out.println(new String(plain) );  
				out.reset();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}



	public Object getPassword() {
		// TODO Auto-generated method stub
		return passphrase;
	}
}
