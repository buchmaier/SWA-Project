package de.hswt.swa.logic;

import de.hswt.swa.gui.TextModelObserver;

public interface TextModelObservable {
	
	public void registerObserver(TextModelObserver observer);
	
	public void unRegisterObserver(TextModelObserver observer);

	public void fireUpdate();
}
