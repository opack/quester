package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.dungeon.RoomElements;
import com.slamdunk.quester.map.dungeon.RoomWalls;

public class EntranceDoor extends Door {

	public EntranceDoor(
			int col, int row,
			GameWorld gameWorldListener,
			RoomWalls wall) {
		super(Assets.entranceDoor, col, row, gameWorldListener, -1, -1, wall, RoomElements.DUNGEON_ENTRANCE_DOOR);
		setOpenable(false);
	}
}
