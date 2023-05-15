package de.hswt.swa.ext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import de.hswt.swa.tools.Crypto;

public class externalJava {
	final static int ENCODE_MODUS = 0;
	final static int DECODE_MODUS = 1;
	
	public static void main(String[] args) {
		/*
		File file = new File("./temp/2023_01_08_08_41_27.list"); 
		List<String> list = openList(file);
		List<String> decoded_list = create_decoded("123456", list);
		System.out.println(list);
		System.out.println(decoded_list);
		*/
		
		
		//create parameters from the input parameters passed during external call
		String filename = args[0];
		String pass = args[1];
		String mode = args[2];
		
		File file = new File(filename); 
		
		// perform task depending on mode
		if (mode.equalsIgnoreCase("encode")){
			try {
				ObjectOutputStream oout = new ObjectOutputStream(System.out);
				List<String> list = openList(file);
				List<String> encoded_list = create_encoded(pass, list);
				oout.writeObject(encoded_list);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mode.equalsIgnoreCase("decode")) {
			try {
				ObjectOutputStream oout = new ObjectOutputStream(System.out);
				List<String> list = openList(file);
				List<String> decoded_list = create_decoded(pass, list);
				oout.writeObject(decoded_list);
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static List<String> openList(File file) {
		List<String> list = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			// using the decorator-pattern again to augment the stream with new functionality
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			list = (List<String>) ois.readObject(); // class cast
			
			ois.close();
		
		} catch (Exception e) {
			System.out.println("Exception when saving file");
			e.printStackTrace();
			
		}
		return list;
		
		
	}
	
	/*
	 * 
	 * Helper Methods for encryption and decryption
	 * 
	 */
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
					
					byte[] bytes  = Base64.getDecoder().decode( s );
					InputStream is = new ByteArrayInputStream( bytes);
					byte[] plain = decode(is, passphrase);
						
						
						plain_text.add(new String(plain));
						  
						out.reset();
						
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return new LinkedList<String>();
			}
			
			return plain_text;
			
	}
	
	
	
	
}
