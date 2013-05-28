package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.dungeon.RoomElements;
import com.slamdunk.quester.map.dungeon.RoomWalls;

public class CommonDoor extends Door {

	public CommonDoor(
			TextureRegion texture,
			int col, int row,
			GameWorld gameWorldListener,
			RoomWalls wall,
			int destinationRoomX, int destinationRoomY) {
		super(
			texture, 
			col, row, 
			gameWorldListener, 
			destinationRoomX, destinationRoomY,
			wall, RoomElements.COMMON_DOOR);
	}
}