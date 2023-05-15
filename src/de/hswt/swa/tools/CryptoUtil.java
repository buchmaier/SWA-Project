package de.hswt.swa.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
	final static int ENCODE_MODUS = 0;
	final static int DECODE_MODUS = 1;
	
	static String cryptTransformation = "AES";
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
	
	public static List<String> create_encoded(String passphrase, List<String> text) {
		
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
	
	public static List<String> create_decoded(String passphrase, List<String> text) {
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
				encode( out, line.getBytes(), pass);

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

}
