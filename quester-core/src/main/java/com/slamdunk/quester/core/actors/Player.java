package com.slamdunk.quester.core.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;

public class Player extends Character {

	public Player(String name, GameWorld gameWorld, int col, int row) {
		super(name, Assets.hero, gameWorld, col, row);
		setHP(15);
	}

	@Override
	public void think() {
		// Ne rien faire ici revient à continuer à appeler think()
		// jusqu'à ce qu'une action ait été initiée par le joueur.
		// nextAction vaudra alors ACTION_MOVE ou ACTION_ATTACK
		// par exemple, et on ne passera plus ici.
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// Ne rien faire ici pour ne pas dessiner le nombre de points de vie
		// et d'attaque
	}
}
