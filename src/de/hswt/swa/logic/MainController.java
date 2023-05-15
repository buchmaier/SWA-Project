package de.hswt.swa.logic;

import java.io.File;


import de.hswt.swa.gui.MainFrame;
import de.hswt.swa.gui.TextModelObserver;
import de.hswt.swa.gui.TextView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser.ExtensionFilter;
import de.hswt.swa.tools.CryptoExecutor;

public class MainController {
	public static final int OPEN_ENCRYPTED = 0;
	public static final int OPEN_PLAIN = 1;
	public static final int OPEN_CRYPTO = 2;
	public static final int LOCAL_DECRYPT = 3;
	public static final int LOCAL_ENCRYPT = 4;
	public static final int EXTERN_DECRYPT = 5;
	public static final int EXTERN_ENCRYPT = 6;
	public static final int RMI_DECRYPT = 7;
	public static final int RMI_ENCRYPT = 8;
	public static final int SOCKET_DECRYPT = 9;
	public static final int SOCKET_ENCRYPT = 10;
	public static final int SAVE_FILE = 11;
	public static final int SAVE_OBJECT = 12;
	public static final int REST_DECRYPT = 13;
	public static final int REST_ENCRYPT = 14;

	
	private MainFrame view;
	private BusinessLogic logic = new BusinessLogic();

	public MainController(MainFrame mainFrame) {
		view = mainFrame;
	}

	public EventHandler<ActionEvent> getEventHandler(int eventType) {
		
		if (eventType == OPEN_ENCRYPTED)
			return new OpenEncryptedHandler();
		if (eventType == OPEN_PLAIN)
			return new OpenPlainHandler();
		if (eventType == OPEN_CRYPTO)
			return new OpenCryptoHandler();
		if (eventType == LOCAL_DECRYPT)
			return new DecryptHandler(CryptoExecutor.LOCAL_MODE);
		if (eventType == LOCAL_ENCRYPT)
			return new EncryptHandler(CryptoExecutor.LOCAL_MODE);
		if (eventType == EXTERN_DECRYPT)
			return new DecryptHandler(CryptoExecutor.EXTERN_MODE);
		if (eventType == EXTERN_ENCRYPT)
			return new EncryptHandler(CryptoExecutor.EXTERN_MODE);
		if (eventType == RMI_DECRYPT)
			return new DecryptHandler(CryptoExecutor.RMI_MODE);
		if (eventType == RMI_ENCRYPT)
			return new EncryptHandler(CryptoExecutor.RMI_MODE);
		if (eventType == SOCKET_DECRYPT)
			return new DecryptHandler(CryptoExecutor.SOCKET_MODE);
		if (eventType == SOCKET_ENCRYPT)
			return new EncryptHandler(CryptoExecutor.SOCKET_MODE);
		if (eventType == REST_DECRYPT)
			return new DecryptHandler(CryptoExecutor.REST_MODE);
		if (eventType == REST_ENCRYPT)
			return new EncryptHandler(CryptoExecutor.REST_MODE);
		if (eventType == SAVE_FILE)
			return new SaveEncryptedFileHandler();
		if (eventType == SAVE_OBJECT)
			return new SaveObjectHandler();
		return null;
	}
	
	
	public class OpenEncryptedHandler implements EventHandler {

		@Override
		public void handle(Event arg0) {
			// TODO Auto-generated method stub
			// open a file chooser
			File file = view.openFileChooser("choose .encr file",
					new ExtensionFilter(".encr", "*.encr"),
					new File("."),
					true);

			// call application logic to open the respective file
			if (file != null) {
				logic.readEncrypted(file);
				view.addStatus("encr text: " + file.getAbsolutePath() + " loaded sucessfully");
			} else {
				view.addStatus("Error encrypted File import");
			}	
		}
		
	}
	
	public class OpenPlainHandler implements EventHandler {

		@Override
		public void handle(Event arg0) {
			// TODO Auto-generated method stub
			File file = view.openFileChooser("choose plain file",
					new ExtensionFilter(".txt", "*.txt"),
					new File("."),
					true);

			// call application logic to open the respective file
			if (file != null) {
				logic.readPlain(file);
				view.addStatus("plain text: " + file.getAbsolutePath() + " loaded sucessfully");
			} else {
				view.addStatus("Error plain File import");
			}	
		}
		
	}
	
	
	public class OpenCryptoHandler implements EventHandler {

		@Override
		public void handle(Event arg0) {
			//open file chooser
			File file = view.openFileChooser("choose plain file",
					new ExtensionFilter(".crypto", "*.crypto"),
					new File("."),
					true);

			// call application logic to open the respective file
			// and to adjust the crypto object
			if (file != null) {
				
				//when load is successfull show message and date 
				//(if date is not known print that date is unknown)
				logic.readCrypto(file);
				String encrDate;
				if(logic.getTextmodel().getCrypto().getDate()!= null)
					encrDate = logic.getTextmodel().getCrypto().getDate().toString();
				else
					encrDate = "unknown";
				view.addStatus("Crypto-Object: " + file.getAbsolutePath() + " loaded sucessfully"+
						       ", encr. Date = " + encrDate);
			} else {
				view.addStatus("Error Crypto import");
				
			}	
		}
		
	}
	
	public class DecryptHandler implements EventHandler {
		
		private int mode;
		
		public DecryptHandler(int mode) {
			this.mode = mode;
		}
		@Override
		public void handle(Event arg0) {
			
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				TextInputDialog td = new TextInputDialog("Please enter Passphrase");
				td.showAndWait();
			
				String passphrase = td.getEditor().getText();
			
				//boolean success true if decryption was successful
				boolean success = logic.decrypt(passphrase, mode);
				
				//return feedback if action was successful
				if(success) {
					view.addStatus("Decryption successful");
				} else {
					view.addStatus("Error during decryption: Try other passphrase");
				}
			
		}
		
	}
	
	
	public class EncryptHandler implements EventHandler {
		
		private int mode;
		
		public EncryptHandler(int mode) {
			this.mode = mode;
			
		}
		
		@Override
		public void handle(Event arg0) {
			// TODO Auto-generated method stub
			TextInputDialog td = new TextInputDialog();
			td.setHeaderText("enter passphrase");
			td.showAndWait();
			String passphrase = td.getEditor().getText();
			boolean success = logic.encrypt(passphrase, mode);
			
			//return feedback if action was successful
			if(success) {
				String encrDate = logic.getTextmodel().getCrypto().getDate().toString();
				view.addStatus("Encryption successful, date: " + encrDate);
			} else {
				view.addStatus("Error during encryption: Unkknown characters in Text or no plain Text loaded yet");
			}
		}
		
	}
	
	public void registerTextModelObserver(TextModelObserver tv) {
		logic.registerTextModelObserver(tv);
	}
	
	
	
	public class SaveEncryptedFileHandler implements EventHandler {

		@Override
		public void handle(Event arg0) {
			System.out.println("Save file ....");
			// First create file then save encrypted text to that file
			File file = view.openFileChooser("save encrypted text locally",
					new ExtensionFilter(".encr", "*.encr"),
					new File("."),
					false);
			if(file != null) {
				boolean success = logic.saveEncrypted(file);
				if(success) {
					view.addStatus(".encr File saved successfully");
				} else {
					view.addStatus("error: create or load encrypted file first");
				}
			}else {
				view.addStatus("error: file could not be created");
			}
			
		}
		
	}
	
	public class SaveObjectHandler implements EventHandler {

		@Override
		public void handle(Event arg0) {
			// TODO Auto-generated method stub
			File file = view.openFileChooser("save Crypto Object locally",
					new ExtensionFilter(".crypto", "*.crypto"),
					new File("."),
					false);
			if(file != null) {
				logic.saveCrypto(file);
			}
		}
		
	}

	
}
