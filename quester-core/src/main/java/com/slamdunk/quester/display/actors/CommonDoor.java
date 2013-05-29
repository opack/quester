package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.logical.MapElements;

public class CommonDoor extends Door {

	public CommonDoor(
			TextureRegion texture,
			int col, int row,
			GameWorld gameWorldListener,
			int destinationRoomX, int destinationRoomY) {
		super(
			texture, 
			col, row, 
			gameWorldListener, 
			destinationRoomX, destinationRoomY,
			MapElements.COMMON_DOOR);
	}
}
