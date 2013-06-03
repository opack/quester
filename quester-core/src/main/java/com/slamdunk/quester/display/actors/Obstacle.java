package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Obstacle extends WorldActor {
	public Obstacle(TextureRegion texture, int col, int row) {
		super(texture, col, row);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
}
