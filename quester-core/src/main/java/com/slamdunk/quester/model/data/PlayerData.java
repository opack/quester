package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import com.slamdunk.quester.logic.ai.PlayerAI;

public class PlayerData extends CharacterData {
	public PlayerData(int hp, int attack) {
		super(PLAYER, hp, attack, new PlayerAI());
	}
}
