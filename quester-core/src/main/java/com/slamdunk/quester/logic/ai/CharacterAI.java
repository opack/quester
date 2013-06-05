package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.CharacterActor;

public class CharacterAI extends AI {
	public CharacterActor getCharacter() {
		return (CharacterActor)body;
	}
	
	@Override
	public void init() {
		super.init();
		// Par défaut, on veut que le personnage pense au lieu de ne rien faire
		addAction(ACTION_THINK);
	}
}
