package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.model.map.MapElements;

public class CommonDoor extends Door {

	public CommonDoor(
			TextureRegion texture,
			int col, int row,
			int destinationRoomX, int destinationRoomY) {
		super(
			texture, 
			col, row, 
			destinationRoomX, destinationRoomY,
			MapElements.COMMON_DOOR);
	}
}
