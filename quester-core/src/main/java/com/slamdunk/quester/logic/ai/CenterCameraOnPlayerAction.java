package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Arr�te le tour du personnage en cours et le pr�pare � penser
 * de nouveau.
 */
public class CenterCameraOnPlayerAction implements AIAction {
	public void act() {
		GameControler.instance.getScreen().centerCameraOn(GameControler.instance.getPlayer().getActor());
		GameControler.instance.getCurrentCharacter().getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.CENTER_CAMERA;
	}
}
