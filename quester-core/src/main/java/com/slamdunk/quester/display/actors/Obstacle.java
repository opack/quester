package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;

public class Obstacle extends WorldActor {
	public Obstacle(TextureRegion texture, int col, int row, GameWorld gameWorld) {
		super(texture, gameWorld, col, row);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
}
