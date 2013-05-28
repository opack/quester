package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;

public class Castle extends WorldActor {

	public Castle(TextureRegion texture, int col, int row, GameWorld gameWorld) {
		super(texture, gameWorld, row, col);
	}
	
	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le château
		return false;
	}

	public int getDungeonWidth() {
		return 3;
	}
	
	public int getDungeonHeight() {
		return 3;
	}
	
	public int getRoomWidth() {
		return 9;
	}
	
	public int getRoomHeight() {
		return 11;
	}
}
