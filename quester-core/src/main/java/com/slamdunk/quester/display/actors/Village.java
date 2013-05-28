package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;

public class Village extends WorldActor {

	public Village(TextureRegion texture, int col, int row, GameWorld gameWorld) {
		super(texture, gameWorld, row, col);
	}
	
	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le village
		return false;
	}
}
