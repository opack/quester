package com.slamdunk.quester.map.logical;

public class PathData extends ElementData {
	public int toX;
	public int toY;
	
	public PathData(MapElements pathType, int toX, int toY) {
		super(pathType);
		this.toX = toX;
		this.toY = toY;
	}
}
