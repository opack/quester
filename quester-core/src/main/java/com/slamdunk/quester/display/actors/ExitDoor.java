package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.model.data.PathData;

public class ExitDoor extends Door {

	public ExitDoor(
			PathData data,
			int col, int row) {
		super(data, Assets.exitDoor, col, row);
	}
}
