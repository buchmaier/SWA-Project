package de.hswt.swa.gui;

import java.util.List;

;

public interface TextModelObserver {
	public void update(List<String> encrypted, List<String> plain);
}
