package com.slamdunk.quester.model.ai;

import com.slamdunk.quester.display.actors.Character;

public class CharacterAI extends AI {
	public Character getCharacter() {
		return (Character)body;
	}
}
