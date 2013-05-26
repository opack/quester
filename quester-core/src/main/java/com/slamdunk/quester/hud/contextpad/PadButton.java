package com.slamdunk.quester.hud.contextpad;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class PadButton extends Button {
	private List<OnClickManager> managers;
	private OnClickManager currentManager;

	public PadButton(OnClickManager... actionManagers) {
		super(actionManagers[0].getStyle());
		managers = Arrays.asList(actionManagers);
		
		// Ajout des listeners
		for (OnClickManager manager : managers) {
			manager.listener.setActive(false);
			addListener(manager.listener);
		}
		actionManagers[0].listener.setActive(true);
	}
	
	public OnClickManager getCurrentManager() {
		return currentManager;
	}

	public void setCurrentManager(int managerId) {
		currentManager = null;
		for (OnClickManager manager : managers) {
			if (manager.getId() == managerId) {
				setStyle(manager.getStyle());
				manager.listener.setActive(true);
				currentManager = manager;
			} else {
				manager.listener.setActive(false);
			}
		}
	}
}
