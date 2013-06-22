package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;

/**
 * Attend le nombre de tours indiqués avant de passer à l'action
 * suivante. Cela permet de ne faire jouer le personnage que tous
 * les X tours.
 */
public class PassTimeAction implements AIAction {
	private int timeLeft;
	private CharacterControler character;
	
	public PassTimeAction(CharacterControler character, int timeToWait) {
		this.character = character;
		this.timeLeft = timeToWait;
	}
	
	@Override
	public void act() {
		if (timeLeft <= 0) {
			character.getAI().nextAction();
		} else {
			character.getAI().setNextAction(new EndTurnAction(character));
		}
		timeLeft --;
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.PASS_TIME;
	}

}
