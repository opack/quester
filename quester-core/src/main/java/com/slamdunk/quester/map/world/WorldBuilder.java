package com.slamdunk.quester.map.world;

import static com.slamdunk.quester.map.world.WorldElements.GRASS;
import static com.slamdunk.quester.map.world.WorldElements.VILLAGE;

import com.slamdunk.quester.map.points.PointManager;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class WorldBuilder {
	private WorldRegion region;
	private final int width;
	private final int height;
	
	private UnmutablePoint startVillagePosition;
	
	private PointManager pointManager;
	
	public WorldBuilder(int width, int height) {
		this.width = width;
		this.height = height;
		
		pointManager = new PointManager(width, height);
	}
	
	public WorldRegion build() {
		// Remplissage du monde avec de l'herbe
		region = new WorldRegion(width, height, GRASS);
		
		// Positionnement du village de départ
		startVillagePosition = pointManager.getPoint(width / 2, height / 2);
		region.set(startVillagePosition.getX(), startVillagePosition.getY(), VILLAGE);
		region.setStartVillagePosition(startVillagePosition);
		
		// Positionnement aléatoire de villages
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				if (Math.random() < 0.1) {
					region.set(col, row, VILLAGE);
				}
			}
		}
		
		return region;
	}

	public UnmutablePoint getStartVillage() {
		return startVillagePosition;
	}
}
