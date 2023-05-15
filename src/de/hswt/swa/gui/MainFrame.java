package de.hswt.swa.gui;

import java.io.File;


import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.hswt.swa.logic.MainController;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;

public class MainFrame extends Application{
	private MainController controller;
	private Stage mainStage;
	private Tab plainTab;
	private Tab encryptedTab;
	private TabPane tabPane;
	private ListView statusLines;
	private TextView tv;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		controller = new MainController(this);
		
		// generate the main window
		primaryStage.setTitle("Crypto-Tool");
		mainStage = primaryStage;
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800, 600);
		
		// generate a menu bar
		MenuBar menu = new MenuBar();
		
		// a menu for opening/saving files and objects
		Menu openMenu = new Menu("Open");
		
		// Open files and objects
		MenuItem openEncrypted = new MenuItem("open encrypted text");
		openMenu.getItems().add(openEncrypted);
		// register the controller as listener
		openEncrypted.setOnAction(controller.getEventHandler(MainController.OPEN_ENCRYPTED));
		
		MenuItem openPlain = new MenuItem("open plain text");
		openMenu.getItems().add(openPlain);
		// register the controller as listener
		openPlain.setOnAction(controller.getEventHandler(MainController.OPEN_PLAIN));
		
		MenuItem openCrypto = new MenuItem("open Crypto Object");
		openMenu.getItems().add(openCrypto);
		// register the controller as listener
		openCrypto.setOnAction(controller.getEventHandler(MainController.OPEN_CRYPTO));
		
		MenuItem saveFile = new MenuItem("save encrypted File");
		openMenu.getItems().add(saveFile);
		// register the controller as listener
		saveFile.setOnAction(controller.getEventHandler(MainController.SAVE_FILE));
		
		MenuItem saveObject = new MenuItem("save Crypto Object");
		openMenu.getItems().add(saveObject);
		// register the controller as listener
		saveObject.setOnAction(controller.getEventHandler(MainController.SAVE_OBJECT));
		
		Menu actionsMenu = new Menu("Actions");
		
		// first item: decrypt encrypted text
		MenuItem decrypt = new MenuItem("local decrypt");
		actionsMenu.getItems().add(decrypt);
		// register the controller as listener
		decrypt.setOnAction(controller.getEventHandler(MainController.LOCAL_DECRYPT));
		
		MenuItem encrypt = new MenuItem("local encrypt");
		actionsMenu.getItems().add(encrypt);
		// register the controller as listener
		encrypt.setOnAction(controller.getEventHandler(MainController.LOCAL_ENCRYPT));
		
		MenuItem xtern_decrypt = new MenuItem("external decrypt");
		actionsMenu.getItems().add(xtern_decrypt);
		// register the controller as listener
		xtern_decrypt.setOnAction(controller.getEventHandler(MainController.EXTERN_DECRYPT));
		
		MenuItem xtern_encrypt = new MenuItem("external encrypt");
		actionsMenu.getItems().add(xtern_encrypt);
		// register the controller as listener
		xtern_encrypt.setOnAction(controller.getEventHandler(MainController.EXTERN_ENCRYPT));
		//splitPane
		
		MenuItem socket_decrypt = new MenuItem("socket decrypt");
		actionsMenu.getItems().add(socket_decrypt);
		// register the controller as listener
		socket_decrypt.setOnAction(controller.getEventHandler(MainController.SOCKET_DECRYPT));
		
		MenuItem socket_encrypt = new MenuItem("socket encrypt");
		actionsMenu.getItems().add(socket_encrypt);
		// register the controller as listener
		socket_encrypt.setOnAction(controller.getEventHandler(MainController.SOCKET_ENCRYPT));
		
		MenuItem rmi_decrypt = new MenuItem("rmi decrypt");
		actionsMenu.getItems().add(rmi_decrypt);
		// register the controller as listener
		rmi_decrypt.setOnAction(controller.getEventHandler(MainController.RMI_DECRYPT));
		
		MenuItem rmi_encrypt = new MenuItem("rmi encrypt");
		actionsMenu.getItems().add(rmi_encrypt);
		// register the controller as listener
		rmi_encrypt.setOnAction(controller.getEventHandler(MainController.RMI_ENCRYPT));
		
		
		
		MenuItem rest_decrypt = new MenuItem("rest decrypt");
		actionsMenu.getItems().add(rest_decrypt);
		// register the controller as listener
		rest_decrypt.setOnAction(controller.getEventHandler(MainController.REST_DECRYPT));
		
		MenuItem rest_encrypt = new MenuItem("rest encrypt");
		actionsMenu.getItems().add(rest_encrypt);
		// register the controller as listener
		rest_encrypt.setOnAction(controller.getEventHandler(MainController.REST_ENCRYPT));
		
		
		
		
		// add execMenu to menu bar
		menu.getMenus().add(openMenu);
		menu.getMenus().add(actionsMenu);
		
		ScrollPane statusPane = new ScrollPane();
		statusPane.setFitToHeight(true);
		statusPane.setFitToWidth(true);	
		statusPane.setPrefHeight(20);
		statusLines = new ListView<>();
		statusLines.getItems().addListener((ListChangeListener<String>)c->{
			statusLines.scrollTo(statusLines.getItems().size()-1);
		});
		statusPane.setContent(statusLines);
		root.setBottom(statusPane);
		
		//tab Pane for the display of decrypted/encrypted Text (Textview = Observer of Textmodel)
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		tv = new TextView();
		tv.addtoTabPane(tabPane);
		
		controller.registerTextModelObserver(tv);
		
		root.setCenter(tabPane);
				
		// add the menu bar to the window
		root.setTop(menu);
		// finalize and display the window
		primaryStage.getIcons().add(new Image("file:hswt_shadow.png"));
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	
	
	public File openFileChooser(String title, ExtensionFilter filter, File dir, boolean open) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(dir);
		chooser.setTitle(title);
		chooser.getExtensionFilters().add(filter);
		if (open)
			return chooser.showOpenDialog(mainStage);
		
		return chooser.showSaveDialog(mainStage);
	}
	
	public void addStatus(String msg) {
		statusLines.getItems().clear();
		statusLines.getItems().add(msg);
	}
}
