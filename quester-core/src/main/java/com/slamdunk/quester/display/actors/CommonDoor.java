package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.model.map.PathData;

public class CommonDoor extends Door {

	public CommonDoor(
			PathData data,
			TextureRegion texture,
			int col, int row) {
		super(
			data,
			texture, 
			col, row);
	}
}
