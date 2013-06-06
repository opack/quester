package com.slamdunk.quester.logic.ai;


public class CharacterAI extends AI {
	@Override
	public void init() {
		super.init();
		// Par défaut, on veut que le personnage pense au lieu de ne rien faire
		addAction(ACTION_THINK);
	}
}
