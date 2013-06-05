package com.slamdunk.quester.display.actors;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.model.data.PathData;

public class EntranceDoor extends Door {

	public EntranceDoor(
			PathData data,
			int col, int row) {
		super(data, Assets.entranceDoor, col, row);
		data.isCrossable = false;
	}
}
