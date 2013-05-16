package com.slamdunk.quester.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;

public class Obstacle extends WorldElement {
	public Obstacle(TextureRegion texture, int col, int row, GameWorld gameWorldListener) {
		super(texture, gameWorldListener, col, row);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
}
