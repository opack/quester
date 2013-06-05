package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.MapElements;

public class PathData extends ObstacleData {
	public int toX;
	public int toY;
	public boolean isCrossable;
	
	public PathData(MapElements pathType, int toX, int toY) {
		super(pathType);
		this.toX = toX;
		this.toY = toY;
		isCrossable = true;
	}
}
