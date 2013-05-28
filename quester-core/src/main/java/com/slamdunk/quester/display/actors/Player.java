package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.ia.IA;

public class Player extends Character {

	public Player(String name, IA ia, GameWorld gameWorld, int col, int row) {
		super(name, ia, Assets.hero, gameWorld, col, row);
	}
}
