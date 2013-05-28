package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;

public class Player extends Character {

	public Player(String name, GameWorld gameWorld, int col, int row) {
		super(name, Assets.hero, gameWorld, col, row);
	}

	@Override
	public void think() {
		// Ne rien faire ici revient � continuer � appeler think()
		// jusqu'� ce qu'une action ait �t� initi�e par le joueur.
		// nextAction vaudra alors ACTION_MOVE ou ACTION_ATTACK
		// par exemple, et on ne passera plus ici.
	}
}
