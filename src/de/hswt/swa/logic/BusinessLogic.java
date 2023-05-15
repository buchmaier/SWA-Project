package de.hswt.swa.logic;
import java.io.File;

import de.hswt.swa.gui.TextModelObserver;
import  de.hswt.swa.tools.Crypto;
public class BusinessLogic {
	TextModel textmodel = new TextModel();
	
	
	
	public void readEncrypted(File file) {
		textmodel.readEncrypted(file);
	}
	
	public void readPlain(File file) {
		textmodel.readPlain(file);
	}
	
	public void readCrypto(File file) {
		textmodel.openCrypto(file);
	}
	
	public void saveCrypto(File file) {
		textmodel.saveCrypto(file);
	}
	
	
	public boolean saveEncrypted(File file) {
		return textmodel.saveEncrypted(file);
	}
	
	
	public boolean decrypt(String passphrase, int mode) {
		
		boolean error = textmodel.decrypt(passphrase, mode);
		return error;
	}
	
	public boolean encrypt(String passphrase, int mode) {
		boolean success = textmodel.encrypt(passphrase, mode);
		return success;
	}
	
	

	public void registerTextModelObserver(TextModelObserver tv) {
		// TODO Auto-generated method stub
		textmodel.registerObserver(tv);
	}
	
	
	// check if current Crypto Object already contains Plain text
	public boolean decryptedExists(){
		return (textmodel.getCrypto().getEncrypted().isEmpty());
	}
	
	// check if current Crypto Object already contains Encrypted text
	public boolean encryptedExists() {
		return (textmodel.getCrypto().getPlain().isEmpty());
	}
	
	//Checks if current Crypto Object already containspassword
	public boolean passwordExists() {
		return !(textmodel.getCrypto().getPassword() == null);
	}

	public TextModel getTextmodel() {
		// TODO Auto-generated method stub
		return this.textmodel;
	}
	
	
}
