package com.slamdunk.quester.map.world;

import static com.slamdunk.quester.map.world.WorldElements.CASTLE;
import static com.slamdunk.quester.map.world.WorldElements.GRASS;
import static com.slamdunk.quester.map.world.WorldElements.PATH_TO_REGION;
import static com.slamdunk.quester.map.world.WorldElements.ROCK;
import static com.slamdunk.quester.map.world.WorldElements.VILLAGE;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.points.Point;

public class WorldBuilder {
	private WorldRegion[][] regions;
	private final int worldWidth;
	private final int worldHeight;
	
	private boolean regionsCreated;
	
	private Point startRegionPosition;
	private Point startVillagePosition;
	
	public WorldBuilder(int worldWidth, int worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		
		regions = new WorldRegion[worldWidth][worldHeight];
		startRegionPosition = new Point(-1, -1);
		startVillagePosition = new Point(-1, -1);
	}
	
	public Point getStartVillagePosition() {
		return startVillagePosition;
	}
	
	public Point getStartRegionPosition() {
		return startRegionPosition;
	}

	public WorldRegion[][] build() {
		if (!regionsCreated) {
			throw new IllegalStateException("regionsCreated=" + regionsCreated);
		}

		// Positionnement du village de départ au centre du monde
		startRegionPosition.setXY(worldWidth / 2, worldHeight / 2);
		WorldRegion centerRegion = regions[startRegionPosition.getX()][startRegionPosition.getY()];
		centerRegion.set(startVillagePosition.getX(), startVillagePosition.getY(), VILLAGE);
		centerRegion.setStartVillagePosition(startVillagePosition);
		
		return regions;
	}
	
	/**
	 * Crée les régions du monde, ne contenant essentiellement que du sol.
	 * @param roomWidth
	 * @param roomHeight
	 */
	public void createRegions(int regionWidth, int regionHeight) {
		// Création des régions
		for (int col = 0; col < worldWidth; col ++) {
			for (int row = 0; row < worldHeight; row ++) {
				// La taille de la pièce correspond à la taille de la map,
				// car on n'affiche qu'une pièce à chaque fois.
				regions[col][row] = createRegion(
					regionWidth, regionHeight,
					col, row,
					worldWidth - 1, worldHeight - 1);
			}
		}
		
		// On détermine la position du village de départ. Cette position sera utilisée
		// lors du build() pour placer effectivement le village dans la région qui va bien.
		startVillagePosition.setXY(regionWidth / 2, regionHeight / 2);
		
		regionsCreated = true;
	}

	/**
	 * Crée une région en instanciant son background (sols, zones de transfert
	 * vers une autre région...)
	 * @param width, height Taille de la région à créer, en nombre de cellules
	 * @param regionX, regionY Coordonnées de la région dans le monde
	 * @param lastRegionX, lastRegionY Coordonnées de la région du monde la plus
	 * à l'extrémité. Utilisées pour déterminer si cette région est sur un bord
	 * du monde, car dans ce cas on ne met pas de chemin vers une région adjacente.
	 */
	private WorldRegion createRegion(
			int width, int height, 
			int regionX, int regionY, 
			int lastRegionX, int lastRegionY) {
		WorldRegion region = new WorldRegion(width, height);
		for (int col = 0; col < width; col++) {
   		 	for (int row = 0; row < height; row++) {
   		 		// Sur le pourtour, on a un accès à la région adjacente,
   		 		// sauf si on est sur le bord du monde
   		 		if ((col == 0 && regionX > 0)
   		 		|| (row == 0 && regionY > 0)
   		 		|| (col == width - 1 && regionX < lastRegionX)
   		 		|| (row == height - 1 && regionY < lastRegionY) ) {
	 				region.set(col, row, PATH_TO_REGION);
   		 		} else {
   		 			// Positionnement aléatoire de villages et de châteaux,
   		 			// ou herbe sur les emplacements vides
   		 			double randomContent = MathUtils.random();
	   		 		if (randomContent < 0.01) {
						region.set(col, row, VILLAGE);
					} else if (randomContent < 0.03){
	   		 			region.set(col, row, ROCK);
					} else if (randomContent < 0.10){
						region.set(col, row, CASTLE);
					} else {
   		 				region.set(col, row, GRASS);
					}
   		 		}
   		 	}
        }
		return region;
	}
}
