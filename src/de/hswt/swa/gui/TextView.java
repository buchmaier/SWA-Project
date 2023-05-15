package de.hswt.swa.gui;

import java.util.List;


import de.hswt.swa.logic.TextModelObservable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/*
*Text view for the Textmodel
*
*shows two Tabs for plain and encrypted Text
*can be updates by logic when changes in Textmodel occur
*/
public class TextView implements TextModelObserver{
	Tab plainTab;
	Tab encrTab;
	private ListView<String> listViewEncr;
	private ListView<String> listViewPlain;
	private List<TextModelObservable> obsList;

	
	
	public TextView() {
		
		encrTab = new Tab();
		plainTab = new Tab();
		
		plainTab = new Tab();
		plainTab.setText("plain Text");
		
		encrTab = new Tab();
		encrTab.setText("encrypted Txt");
		
		listViewEncr = new ListView(FXCollections.observableArrayList());
		listViewPlain = new ListView(FXCollections.observableArrayList());
		encrTab.setContent(listViewEncr);
		plainTab.setContent(listViewPlain);
	}
	
	public void addtoTabPane(TabPane tp) {
		//add tabs to Tab pane (used inside of MainFrame)
		tp.getTabs().add(plainTab);
		tp.getTabs().add(encrTab);
	}
	
	


	@Override
	public void update(List<String> encrypted, List<String> plain) {
		// Update when changes in text model occur
		ObservableList<String> obsEncr = FXCollections.observableArrayList();
		ObservableList<String> obsPlain = FXCollections.observableArrayList();
		
		obsEncr.addAll(encrypted);
		obsPlain.addAll(plain);
		
		listViewEncr.setItems(obsEncr);
		listViewPlain.setItems(obsPlain);
		
		
		
		
	}
	
}
