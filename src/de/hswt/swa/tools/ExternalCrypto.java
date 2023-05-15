package de.hswt.swa.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
/*
 * 
 * not needed in present implementation
 * 
 * 
 */
public class ExternalCrypto {
	final static int ENCODE_MODUS = 0;
	final static int DECODE_MODUS = 1;
	static String cryptTransformation = "AES";
	
	
	public static void main(String[] args) {
			// arg[0] = passphrase
		    // arg[1] = modus
		
		try {
			String pass = args[0];
			String mode = args[1];
			ObjectInputStream oin = new ObjectInputStream(System.in);
			ObjectOutputStream oout = new ObjectOutputStream(System.out);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			oin.wait();
			List<String> originalText = (List<String>) oin.readObject();
			
			if(mode.equalsIgnoreCase("encode")) {
				
					LinkedList<String> text = new LinkedList<String>();
					
					for(String line : originalText) {
						encode( out, line.getBytes(), pass);
						String s = Base64.getEncoder().encodeToString(out.toByteArray() );
						//System.out.println( s );  
						text.add(s);
						out.reset();
						text.add(s);
						
					}
					oout.writeObject(text);	
				
			}	 else if(mode.equalsIgnoreCase("decode")) {
				LinkedList<String> text = new LinkedList<String>();
				for (String s : originalText) {
					byte[] bytes  = Base64.getDecoder().decode( s );
					InputStream is = new ByteArrayInputStream( bytes);
					byte[] plain = decode(is, pass);
	
					//String line = new BASE64Encoder().encode(plain);
					text.add(new String(plain) );  
					out.reset();
				}
				oout.writeObject(text);
			}	
				
				
		}catch(Exception e) {
			e.printStackTrace();
		}
				
			
			
	}
	
	
	public static boolean encode(OutputStream writer, byte[] input, String passPhrase) {
			
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
	
	
	
	public static byte[] decode(InputStream input, String passPhrase) {
		
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
	
	public List<String> create_encoded(String passphrase, List<String> text) {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LinkedList<String> encoded_text = new LinkedList<String>();
		
		
		for (String line : text) {
			encode( out, line.getBytes(), passphrase);
			String s = Base64.getEncoder().encodeToString(out.toByteArray() );
			//System.out.println( s );  
			encoded_text.add(s);
			out.reset();
		}
		
		return encoded_text;
		
	}
	
	public List<String> create_decoded(String passphrase, List<String> text) {
			LinkedList<String> plain_text = new LinkedList<String>();
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				
				
				for (String s : text) {
					System.out.println(s + "[]");
					byte[] bytes  = Base64.getDecoder().decode( s );
					InputStream is = new ByteArrayInputStream( bytes);
					byte[] plain = decode(is, passphrase);
						
						
						plain_text.add(new String(plain));
						  
						out.reset();
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return plain_text;
	}
				
}
