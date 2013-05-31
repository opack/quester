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
		// Choix d'une r�gion de d�part
		entranceArea = pointManager.getPoint(mapWidth / 2, mapHeight / 2);
		MapArea centerRegion = areas[entranceArea.getX()][entranceArea.getY()];
		centerRegion.setObjectAt(entrancePosition.getX(), entrancePosition.getY(), VILLAGE_DATA);
		
		// La r�gion d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus � faire
		mainEntrancesPlaced = true;
	}

	@Override
	public void createAreas(int roomWidth, int roomHeight, ElementData defaultBackground) {
		// On d�termine la position du village de d�part. Cette position sera utilis�e
		// lors du build() pour placer effectivement le village dans la r�gion qui va bien.
		entrancePosition = new UnmutablePoint(roomWidth / 2, roomHeight / 2);
		
		// La classe m�re fera le reste de la g�n�ration des salles
		super.createAreas(roomWidth, roomHeight, defaultBackground);
	}

	@Override
	protected void fillRoom(MapArea area) {
		// Cr�ation de la structure de la zone
		int width = area.getWidth();
		int height = area.getHeight();
		for (int col = 0; col < width; col++) {
   		 	for (int row = 0; row < height; row++) {
   		 		// On place du sol partout
   		 		area.setGroundAt(col, row, GRASS_DATA);
   		 		
   		 		// Et on ajoute quelques �l�ments : des rochers sur le tour
   		 		// et des villages et ch�teaux � l'int�rieur de la carte.
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			area.setObjectAt(col, row, ROCK_DATA);
   		 		} else {
   		 			// Positionnement al�atoire de villages et de ch�teaux,
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
