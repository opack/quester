package com.slamdunk.quester.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.screens.RoomElements;
import com.slamdunk.quester.screens.RoomWalls;

public class EntranceDoor extends Door {

	public EntranceDoor(
			TextureRegion texture,
			int col, int row,
			GameWorld gameWorldListener,
			RoomWalls wall) {
		super(texture, col, row, gameWorldListener, -1, -1, wall, RoomElements.DUNGEON_ENTRANCE_DOOR);
	}
}
