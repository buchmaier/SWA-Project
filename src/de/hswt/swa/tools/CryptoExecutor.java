package de.hswt.swa.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.rmi.Naming;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import de.hswt.swa.rmi.RemoteServerInterface;
import de.hswt.swa.socket.SocketClient;

public class CryptoExecutor {
	
	public static final int LOCAL_MODE = 0;
	public static final int EXTERN_MODE = 1;
	public static final int RMI_MODE = 2;
	public static final int SOCKET_MODE = 3;
	public static final int REST_MODE = 4;
	
	
	static String cryptTransformation = "AES";
	private Properties p;
	
	
	int mode;
	Crypto crypto;
	
	public CryptoExecutor(int mode,Crypto crypto) {
		this.mode = mode;
		this.crypto = crypto;
		this.p = new Properties();
		
		String propertyFileName = "crypto.properties";
		try {
			FileInputStream fis = new FileInputStream(propertyFileName);
			BufferedInputStream stream = new BufferedInputStream(fis);
			p.load(stream);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public boolean decrypt(String passphrase) {
		if (mode == LOCAL_MODE)
			return localDecode(passphrase);
		if (mode == EXTERN_MODE)
			return externDecode(passphrase);
		if (mode == SOCKET_MODE)
			return socketDecode(passphrase);
		if (mode == REST_MODE)
			return restDecode(passphrase);
		if (mode == RMI_MODE) {
			return rmiDecode(passphrase);
		}
		
		return false;
	}
	

	public boolean encrypt(String passphrase) {
		if (mode == LOCAL_MODE)
			return localEncode(passphrase);
		if (mode == EXTERN_MODE)
			return externEncode(passphrase);
		if (mode == SOCKET_MODE)
			return socketEncode(passphrase);
		if (mode == RMI_MODE)
			return rmiEncode(passphrase);
		if (mode == REST_MODE)
			return restEncode(passphrase);
		
		return false;
	}
	
	private boolean restDecode(String passphrase) {
		System.out.println("restDecode");
		Gson gson = new Gson();
	    try {
	        
	          ArrayList<String> encodedText = (ArrayList<String>) crypto.getEncrypted();
	          //add password at end of List
	          encodedText.add(passphrase);
	        
	          JsonObject jsonFile = new JsonObject();
	          String contentJson = gson.toJson(encodedText);
	          jsonFile.addProperty("content", contentJson);
	          String mesg = gson.toJson(jsonFile);

	          String webhost = p.getProperty("webHost");
	          String webPort = p.getProperty("webPort");
	          String webService = p.getProperty("webserviceRestDecode");
	          String urlSpec = "http://"+ webhost +":" + webPort + "/" + webService;
	          System.out.println(urlSpec);
	          
	          URL url = new URL(urlSpec);
	          HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	          httpConnection.setDoOutput(true);
	          httpConnection.setRequestMethod("POST");
	
	          PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
	          out.println(mesg);
	          out.close();
	
	          Integer responseCode = httpConnection.getResponseCode();
	
	          BufferedReader bufferedReader;
	
	          // 
	          if (responseCode > 199 && responseCode < 300) {
	              bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
	          } else {
	               bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
	          }
	
	          String resultString = bufferedReader.readLine();
	          System.out.println(resultString);
	
	          JsonObject ergObj = gson.fromJson(resultString, com.google.gson.JsonObject.class);
	          String result = ergObj.get("result").getAsString();
	          List<String> output = gson.fromJson(result, java.util.ArrayList.class);
	          bufferedReader.close();
	          
	          //remove password again
	          encodedText.remove(encodedText.size()-1);
	          
	          //set Crypto properties
	          if(!output.isEmpty()) {
	        	  crypto.setPlain(output);
	          	  crypto.setPassphrase(passphrase);
	          	  return true;
	          }else {
	        	  return false;
	          }
	        

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	}

	
	private boolean restEncode(String passphrase) {
		System.out.println("restEncode");
		Gson gson = new Gson();
	    try {
	         System.out.println("RestEncode");
	          ArrayList<String> plainText = (ArrayList<String>) crypto.getPlain();
	          
	          //add password at end of List
	          plainText.add(passphrase);
	          
	          //establish Connection to Servlet
	          JsonObject jsonFile = new JsonObject();
	          String contentJson = gson.toJson(plainText);
	          jsonFile.addProperty("content", contentJson);
	          String mesg = gson.toJson(jsonFile);

	          String webhost = p.getProperty("webHost");
	          String webPort = p.getProperty("webPort");
	          String webService = p.getProperty("webserviceRestEncode");
	          String urlSpec = "http://"+ webhost +":" + webPort + "/" + webService;
	          System.out.println(urlSpec);
	          
	          URL url = new URL(urlSpec);
	          HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	          httpConnection.setDoOutput(true);
	          httpConnection.setRequestMethod("POST");
	
	          PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
	          out.println(mesg);
	          out.close();
	
	          Integer responseCode = httpConnection.getResponseCode();
	
	          BufferedReader bufferedReader;
	
	          // Einlesestrom erzeugen
	          if (responseCode > 199 && responseCode < 300) {
	              bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
	           } else {
	               bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
	            }
	
	          String resultString = bufferedReader.readLine();
	          System.out.println(resultString);
	
	          JsonObject ergObj = gson.fromJson(resultString, com.google.gson.JsonObject.class);
	          String result = ergObj.get("result").getAsString();
	          String dateString = ergObj.get("encryption date").getAsString();
	          LocalDateTime date = LocalDateTime.parse(dateString);
	          List<String> output = gson.fromJson(result, java.util.ArrayList.class);
	          bufferedReader.close();
	          
	          //remove password
	          plainText.remove(plainText.size()-1);
	          crypto.setEncrypted(output);
	          crypto.setDate(date);
	          
	          System.out.println("rest-encrtyption date:");
	          System.out.println(date);
	          
	          crypto.setPassphrase(passphrase);
	          return true;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	}
	
	private boolean socketEncode(String passphrase) {
		// TODO Auto-generated method stub
		System.out.println("socketEncode");
		try {
			String host = p.getProperty("socketHost");
			int port = Integer.parseInt(p.getProperty("socketPort"));
			Socket client = new Socket(host, port);
			
			crypto.setModus(Crypto.ENCODE_MODUS);
			crypto.setPassphrase(passphrase);
			ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
			System.out.println("Streams created");
			oout.writeObject(crypto);
			oout.flush();
			System.out.println("object sent");
			
			Crypto newcrypto = (Crypto) oin.readObject();
			
			oin.close();
			oout.close();
			client.close();
			System.out.println("done");
			
			
			crypto.copy(newcrypto);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
			
	}

	private boolean socketDecode(String passphrase) {
		// TODO Auto-generated method stub
		System.out.println("socketDecode");
		try {
			
			
			String host = p.getProperty("socketHost");
			int port = Integer.parseInt(p.getProperty("socketPort"));
			
			// create copy of crypto to send to server
			// Password of original crypto Object only reset if Decryption was successful
			Crypto socketCrypto = new Crypto();
			socketCrypto.copy(crypto);
			socketCrypto.setPassphrase(passphrase);
			
			Socket client = new Socket(host, port);
			socketCrypto.setModus(Crypto.DECODE_MODUS);
			
			ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
			System.out.println("Streams created");
			
			oout.writeObject(socketCrypto);
			oout.flush();
			System.out.println("object sent");
			
			System.out.println("");
			Crypto newcrypto = (Crypto) oin.readObject();
			System.out.println("decryptedText: ");
			System.out.println(newcrypto.getPlain());
			
			oin.close();
			oout.close();
			client.close();
			
			//set new passphrase if encryption is successful
			if(!newcrypto.getPlain().isEmpty()) {
				crypto.setPlain(newcrypto.getPlain());
				crypto.setPassphrase(passphrase);
				System.out.println("done");
				return true;
			} else {
				System.out.println("error: plain text is empty after decryption, wrong password");
				return false;
			}
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	


	private boolean externEncode(String passphrase) {
		// TODO Auto-generated method stub
		System.out.println("externEncode");
		
			try {
				//save plain text in temp folder
				String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
				String fileName = "./temp/"+timeStamp+".list";
				File textFile = new File(fileName);
				textFile.createNewFile();
				saveList(textFile, crypto.getPlain());
				String mode = "encode";
				
				
				ProcessBuilder builder = new ProcessBuilder("java","-cp",System.getProperty("java.class.path"),
															"de.hswt.swa.ext.externalJava",
															fileName, passphrase, mode);
				Process process = builder.start();
				process.waitFor();
				
				ObjectInputStream oin = new ObjectInputStream(process.getInputStream());
				List<String> encoded = (List<String>) oin.readObject();
				
				
				crypto.setEncrypted(encoded);
				crypto.setPassphrase(passphrase);
				crypto.setDate(LocalDateTime.now());
				return true;
			    
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		
		
	}
	
	private boolean externDecode(String passphrase) {
		// TODO Auto-generated method stub
		System.out.println("externDecode");
		try {
			//save plain text in temp folder
			String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
			String fileName = "./temp/"+timeStamp+".list";
			File textFile = new File(fileName);
			textFile.createNewFile();
			List<String> encr = crypto.getEncrypted();
			saveList(textFile, encr);
			String mode = "decode";
			
			ProcessBuilder builder = new ProcessBuilder("java","-cp",System.getProperty("java.class.path"),
														"de.hswt.swa.ext.externalJava",
														fileName, passphrase, mode);
			Process process = builder.start();
			process.waitFor();
			
			ObjectInputStream oin = new ObjectInputStream(process.getInputStream());
			List<String> decoded = (List<String>) oin.readObject();
			
			if (!decoded.isEmpty()) {
				crypto.setPlain(decoded);
				crypto.setPassphrase(passphrase);
			    return true;
			} else {
				return false;
			}
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	public boolean rmiEncode(String passphrase) {
		System.out.println("rmiEncode");
		try {
			System.out.println("try connecting Server");
			RemoteServerInterface server = (RemoteServerInterface) Naming.lookup("rmi://bb-x-xterm.hswt.de:1903/cryptoService");
			
			System.out.println("got Server");
				
			Crypto result = server.encrypt(crypto, passphrase);
			System.out.println("after encryption");
			System.out.println(result.getEncrypted());
			
			crypto.copy(result);
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	public boolean rmiDecode(String passphrase) {
		System.out.println("rmiDecode");
		try {
			System.out.println("try connecting Server");
			RemoteServerInterface server = (RemoteServerInterface) Naming.lookup("rmi://bb-x-xterm.hswt.de:1903/cryptoService");
			
			System.out.println("got Server");
				
			Crypto result = server.decrypt(crypto, passphrase);
			System.out.println("after decryption");
			System.out.println(result.getPlain());
			
			if(!result.getPlain().isEmpty()) {
				crypto.copy(result);
				return true;
			}else {
				System.out.println("empty result: password error");
				return false;
			}
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
	}

	public boolean localDecode(String passphrase) {
		System.out.println("localDecode");
		try {
			List<String >encoded = crypto.getEncrypted();
			List<String> plainText = CryptoUtil.create_decoded(passphrase, encoded);
			
			//in case of error (wrong password) create_decoded returns empty list
			//in this case do not reset PlainText
			
			if(!plainText.isEmpty()) {
				crypto.setPlain(plainText);
				crypto.setPassphrase(passphrase);
				return true;
			}else {
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean localEncode(String passphrase) {
		System.out.println("localEncode");
		try {
			List<String >plain = crypto.getPlain();
			List<String> encryptedText = CryptoUtil.create_encoded(passphrase, plain);
			
			//set properties of crypto object
			
			crypto.setEncrypted(encryptedText);
			crypto.setPassphrase(passphrase);
			crypto.setDate(LocalDateTime.now());
			
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	//helper function for saving List Objects temporarily
	public void saveList(File file, List<String> list) {
		try {
			FileOutputStream fout = new FileOutputStream(file);
			// using the decorator-pattern again to augment the stream with new functionality
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			
			oout.writeObject(list);
			
			oout.flush();
			oout.close();
			
		} catch (Exception e) {
			System.out.println("Exception when saving crypto object");
			e.printStackTrace();
		}
	}
	
	
		
	

	
}
