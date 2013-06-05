package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.MapElements;

public class ObstacleData extends ElementData {
	public ObstacleData(MapElements element) {
		super(element);
		isSolid = true;
	}
}
