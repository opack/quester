package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.map.MapElements.CASTLE;

public class CastleData extends ElementData {
	public int dungeonWidth;
	public int dungeonHeight;
	public int roomWidth;
	public int roomHeight;
	
	CastleData(int dungeonWidth, int dungeonHeight, int roomWidth, int roomHeight) {
		super(CASTLE);
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
	}
}
