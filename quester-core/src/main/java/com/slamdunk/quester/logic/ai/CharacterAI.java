package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.Character;

public class CharacterAI extends AI {
	public Character getCharacter() {
		return (Character)body;
	}
	
	@Override
	public void init() {
		super.init();
		// Par défaut, on veut que le personnage pense au lieu de ne rien faire
		addAction(ACTION_THINK);
	}
}
