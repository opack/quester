package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.model.map.MapElements;

public class ExitDoor extends Door {

	public ExitDoor(
			int col, int row) {
		super(Assets.exitDoor, col, row, -1, -1, MapElements.DUNGEON_EXIT_DOOR);
	}
}
