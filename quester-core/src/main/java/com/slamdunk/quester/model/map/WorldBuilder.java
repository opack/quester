package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.data.WorldElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.ROCK_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.VILLAGE_DATA;
import static com.slamdunk.quester.model.map.MapElements.PATH_TO_REGION;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class WorldBuilder extends DungeonBuilder{

	public WorldBuilder(int worldWidth, int worldHeight) {
		super(worldWidth, worldHeight, 0);
		setLinkType(PATH_TO_REGION);
	}
	
	@Override
	public void placeMainEntrances() {
		// Choix d'une r�gion de d�part
		entranceArea = pointManager.getPoint(mapWidth / 2, mapHeight / 2);
		MapArea centerRegion = areas[entranceArea.getX()][entranceArea.getY()];
		
		// On d�termine la position du village de d�part. Cette position sera utilis�e
		// lors du build() pour placer effectivement le village dans la r�gion qui va bien.
		entrancePosition = new UnmutablePoint(centerRegion.getWidth() / 2, centerRegion.getHeight() / 2);
		centerRegion.setObjectAt(entrancePosition.getX(), entrancePosition.getY(), VILLAGE_DATA);
		
		// La r�gion d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus � faire
		mainEntrancesPlaced = true;
	}

	@Override
	protected void fillRoom(MapArea area) {
		// Plus on s'�loigne du centre de la carte, plus les ch�teaux sont vastes.
		// Calcul du centre du monde pour le placement des ch�teaux
		double worldOriginX = mapWidth / 2.0f;
		double worldOriginY = mapHeight / 2.0f;
		double distanceMax = distanceTo(mapWidth, mapHeight, worldOriginX, worldOriginY);
		double distanceToOrigin = distanceTo(area.getX(), area.getY(), worldOriginX, worldOriginY);
		double percentage = distanceToOrigin / distanceMax;
		int castleMinSize = 0;
		int castleMaxSize = 0;
		int roomMinSize = 0;
		int roomMaxSize = 0;
		int difficulty = 0;
		if (percentage < 0.25) {
			castleMinSize = 2;
			castleMaxSize = 2;
			roomMinSize = 7;
			roomMaxSize = 9;
			difficulty = 0;
		} else if (percentage < 0.5) {
			castleMinSize = 3;
			castleMaxSize = 5;
			roomMinSize = 7;
			roomMaxSize = 13;
			difficulty = 1;
		} else if (percentage < 0.75) {
			castleMinSize = 5;
			castleMaxSize = 7;
			roomMinSize = 9;
			roomMaxSize = 15;
			difficulty = 2;
		} else {
			castleMinSize = 5;
			castleMaxSize = 7;
			roomMinSize = 11;
			roomMaxSize = 15;
			difficulty = 3;
		}
		
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
						area.setObjectAt(col, row, new CastleData(
							MathUtils.random(castleMinSize, castleMaxSize), MathUtils.random(castleMinSize, castleMaxSize),
							MathUtils.random(roomMinSize, roomMaxSize), MathUtils.random(roomMinSize, roomMaxSize),
							difficulty));
					}
   		 		}
   		 	}
        }
	}
	
	private double distanceTo(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt(
			Math.pow(toX - fromX, 2)
			+ Math.pow(toY - fromY, 2));
	}
	
	@Override
	protected int getNbPathsBetweenAreas() {
		return MathUtils.random(1, 5);
	}
	
	@Override
	protected int getPathPosition(Borders border) {
		int position = 0;
		switch (border) {
			// Les c�t�s horizontaux
			case TOP:
			case BOTTOM:
				// Choix d'un nombre entre 1 et taille -2 pour s'assurer qu'on ne
				// place pas un chemin dans un coin
				position = MathUtils.random(1, areaWidth - 2);
				break;
				
			// Les c�t�s verticaux
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
