package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.dungeon.RoomElements;

public class EntranceDoor extends Door {

	public EntranceDoor(
			int col, int row,
			GameWorld gameWorldListener) {
		super(Assets.entranceDoor, col, row, gameWorldListener, -1, -1, RoomElements.DUNGEON_ENTRANCE_DOOR);
		setCrossable(false);
	}
}
