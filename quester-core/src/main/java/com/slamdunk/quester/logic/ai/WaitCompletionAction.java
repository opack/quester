package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.NONE;

import com.slamdunk.quester.logic.controlers.CharacterControler;

/**
 * Attend la fin des actions en cours sur l'Actor puis passe à 
 * l'action suivante.
 */
public class WaitCompletionAction implements AIAction {
	private CharacterControler character;
	
	public WaitCompletionAction(CharacterControler character) {
		this.character = character;
	}
	
	public void act() {
		if (character.getActor().getCurrentAction() == NONE) {
			// L'attente est finie, on exécute l'action suivante
			character.getAI().nextAction();
		}
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.WAIT_COMPLETION;
	}
}
