package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;
import static com.slamdunk.quester.logic.ai.QuesterActions.NONE;

import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.utils.Assets;
public class PlayerActor extends CharacterActor {

	public PlayerActor() {
		super(Assets.hero);
	}
	
	@Override
	public void setCurrentAction(QuesterActions action, int targetX) {
		super.setCurrentAction(action, targetX);
		// DBG En attendant les animations, on dit que l'attaque est finie
		if (action == ATTACK) {
			currentAction = NONE;
		}
	}
}
