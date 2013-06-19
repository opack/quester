package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.CASTLE;

public class CastleData extends ObstacleData {
	public int difficulty;
	public int dungeonHeight;
	public int dungeonWidth;
	public int roomHeight;
	public int roomWidth;
	
	public CastleData(int dungeonWidth, int dungeonHeight, int roomWidth, int roomHeight, int difficulty) {
		super(CASTLE);
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
		this.difficulty = difficulty;
	}
}
