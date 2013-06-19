package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.Borders;
import com.slamdunk.quester.model.map.MapElements;

public class PathData extends ObstacleData {
	public Borders border;
	public boolean isCrossable;
	public int toX;
	public int toY;
	
	public PathData(MapElements pathType, Borders border, int toX, int toY) {
		super(pathType);
		this.border = border;
		this.toX = toX;
		this.toY = toY;
		isCrossable = true;
	}
}
