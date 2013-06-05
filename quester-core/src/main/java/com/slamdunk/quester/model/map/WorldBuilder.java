package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.data.ElementData.FOG_DATA;
import static com.slamdunk.quester.model.data.ElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.ElementData.ROCK_DATA;
import static com.slamdunk.quester.model.data.ElementData.VILLAGE_DATA;
import static com.slamdunk.quester.model.map.MapElements.PATH_TO_REGION;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.points.UnmutablePoint;

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
		
		// On détermine la position du village de départ. Cette position sera utilisée
		// lors du build() pour placer effectivement le village dans la région qui va bien.
		entrancePosition = new UnmutablePoint(centerRegion.getWidth() / 2, centerRegion.getHeight() / 2);
		centerRegion.setObjectAt(entrancePosition.getX(), entrancePosition.getY(), VILLAGE_DATA);
		
		// La région d'entrée est marquée comme étant accessible depuis l'entrée (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus à faire
		mainEntrancesPlaced = true;
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
						area.setObjectAt(col, row, new CastleData(
							MathUtils.random(2, 5), MathUtils.random(2, 5),
							MathUtils.random(7, 11), MathUtils.random(9, 13)));
					}
   		 		}
   		 		
   		 		// Ensuite, on ajoute du brouillard
   		 		area.setFogAt(col, row, FOG_DATA);
   		 	}
        }
	}
	
	@Override
	protected int getNbPathsBetweenAreas() {
		return MathUtils.random(1, 5);
	}
	
	@Override
	protected int getPathPosition(Borders border) {
		int position = 0;
		switch (border) {
			// Les côtés horizontaux
			case TOP:
			case BOTTOM:
				// Choix d'un nombre entre 1 et taille -2 pour s'assurer qu'on ne
				// place pas un chemin dans un coin
				position = MathUtils.random(1, areaWidth - 2);
				break;
				
			// Les côtés verticaux
			case LEFT:
			case RIGHT:
				// Choix d'un nombre entre 1 et taille -2 pour s'assurer qu'on ne
				// place pas un chemin dans un coin
				position = MathUtils.random(1, areaHeight - 2);
				break;
		}
		return position;
	}
}
