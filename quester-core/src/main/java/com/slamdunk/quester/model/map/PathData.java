package com.slamdunk.quester.model.map;

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
