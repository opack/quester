package com.slamdunk.quester.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.screens.RoomElements;
import com.slamdunk.quester.screens.RoomWalls;

public class ExitDoor extends Door {

	public ExitDoor(
			TextureRegion texture,
			int col, int row,
			GameWorld gameWorldListener,
			RoomWalls wall) {
		super(texture, col, row, gameWorldListener, -1, -1, wall, RoomElements.DUNGEON_EXIT_DOOR);
	}
}
