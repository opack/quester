package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.dungeon.RoomElements;

public class ExitDoor extends Door {

	public ExitDoor(
			int col, int row,
			GameWorld gameWorldListener) {
		super(Assets.exitDoor, col, row, gameWorldListener, -1, -1, RoomElements.DUNGEON_EXIT_DOOR);
	}
}
