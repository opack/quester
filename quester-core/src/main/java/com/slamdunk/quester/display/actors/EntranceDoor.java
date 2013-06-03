package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.model.map.MapElements;

public class EntranceDoor extends Door {

	public EntranceDoor(
			int col, int row) {
		super(Assets.entranceDoor, col, row, -1, -1, MapElements.DUNGEON_ENTRANCE_DOOR);
		setCrossable(false);
	}
}
