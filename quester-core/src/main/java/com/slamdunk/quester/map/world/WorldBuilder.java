package com.slamdunk.quester.map.world;

import static com.slamdunk.quester.map.world.WorldElements.GRASS;
import static com.slamdunk.quester.map.world.WorldElements.VILLAGE;
import static com.slamdunk.quester.map.world.WorldElements.CASTLE;

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
		
		// Positionnement aléatoire de villages et de châteaux
		double random;
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				random = Math.random();
				if (random < 0.05) {
					region.set(col, row, VILLAGE);
				} else if (random < 0.15){
					region.set(col, row, CASTLE);
				}
			}
		}
		
		// Positionnement du village de départ
		startVillagePosition = pointManager.getPoint(width / 2, height / 2);
		region.set(startVillagePosition.getX(), startVillagePosition.getY(), VILLAGE);
		region.setStartVillagePosition(startVillagePosition);
		
		return region;
	}

	public UnmutablePoint getStartVillage() {
		return startVillagePosition;
	}
}
