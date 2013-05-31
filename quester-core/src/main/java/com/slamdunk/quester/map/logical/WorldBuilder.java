package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.MapElements.PATH_TO_REGION;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class WorldBuilder extends DungeonBuilder{
	public WorldBuilder(int worldWidth, int worldHeight) {
		super(worldWidth, worldHeight);
		setLinkType(PATH_TO_REGION);
	}
	
	@Override
	public void placeMainEntrances() {
		// Choix d'une région de départ
		entranceArea = pointManager.getPoint(mapWidth / 2, mapHeight / 2);
		MapArea centerRegion = areas[entranceArea.getX()][entranceArea.getY()];
		centerRegion.setObjectAt(entrancePosition.getX(), entrancePosition.getY(), VILLAGE_DATA);
		
		// La région d'entrée est marquée comme étant accessible depuis l'entrée (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus à faire
		mainEntrancesPlaced = true;
	}

	@Override
	public void createAreas(int roomWidth, int roomHeight, ElementData defaultBackground) {
		// On détermine la position du village de départ. Cette position sera utilisée
		// lors du build() pour placer effectivement le village dans la région qui va bien.
		entrancePosition = new UnmutablePoint(roomWidth / 2, roomHeight / 2);
		
		// La classe mère fera le reste de la génération des salles
		super.createAreas(roomWidth, roomHeight, defaultBackground);
	}

	@Override
	protected void fillRoom(MapArea area) {
		// Création de la structure de la zone
		int width = area.getWidth();
		int height = area.getHeight();
		for (int col = 0; col < width; col++) {
   		 	for (int row = 0; row < height; row++) {
   		 		// On place du sol partout
   		 		area.setGroundAt(col, row, GRASS_DATA);
   		 		
   		 		// Et on ajoute quelques éléments : des rochers sur le tour
   		 		// et des villages et châteaux à l'intérieur de la carte.
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			area.setObjectAt(col, row, ROCK_DATA);
   		 		} else {
   		 			// Positionnement aléatoire de villages et de châteaux,
   		 			// ou herbe sur les emplacements vides
   		 			double randomContent = MathUtils.random();
	   		 		if (randomContent < 0.005) {
	   		 			area.setObjectAt(col, row, VILLAGE_DATA);
					} else if (randomContent < 0.08){
						area.setObjectAt(col, row, new CastleData(3, 3, 9, 11));
					}
   		 		}
   		 		
   		 		// Ensuite, on ajoute du brouillard
   		 		area.setFogAt(col, row, FOG_DATA);
   		 	}
        }
	}
}
