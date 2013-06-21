package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;

/**
 * Arrête le tour du personnage en cours et le prépare à penser
 * de nouveau.
 */
public class EndTurnAction implements AIAction {
	private CharacterControler character;
	
	public EndTurnAction(CharacterControler character) {
		this.character = character;
	}
	
	public void act() {
		GameControler.instance.nextPlayer();
		character.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.END_TURN;
	}
}
