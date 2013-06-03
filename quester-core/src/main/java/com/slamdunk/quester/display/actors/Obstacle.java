package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.model.map.ElementData;

public class Obstacle extends WorldActor {
	public Obstacle(ElementData data, TextureRegion texture, int col, int row) {
		super(data, texture, col, row);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
}
