package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.MapElements.CASTLE;
import static com.slamdunk.quester.map.logical.MapElements.GRASS;
import static com.slamdunk.quester.map.logical.MapElements.PATH_TO_REGION;
import static com.slamdunk.quester.map.logical.MapElements.VILLAGE;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class WorldBuilder2 extends DungeonBuilder{
	public WorldBuilder2(int worldWidth, int worldHeight) {
		super(worldWidth, worldHeight);
		setLinkType(PATH_TO_REGION);
	}
	
	@Override
	public void placeMainEntrances() {
		// Choix d'une r�gion de d�part
		entranceArea = pointManager.getPoint(width / 2, height / 2);
		MapArea centerRegion = areas[entranceArea.getX()][entranceArea.getY()];
		centerRegion.set(entrancePosition.getX(), entrancePosition.getY(), VILLAGE);
		
		// La r�gion d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus � faire
		mainEntrancesPlaced = true;
	}

	@Override
	public void createRooms(int roomWidth, int roomHeight) {
		// On d�termine la position du village de d�part. Cette position sera utilis�e
		// lors du build() pour placer effectivement le village dans la r�gion qui va bien.
		entrancePosition = new UnmutablePoint(roomWidth / 2, roomHeight / 2);
		
		// La classe m�re fera le reste de la g�n�ration des salles
		super.createRooms(roomWidth, roomHeight);
	}

	@Override
	protected void fillRoom(MapArea room) {
		int width = room.getWidth();
		int height = room.getHeight();
		for (int col = 0; col < width; col++) {
   		 	for (int row = 0; row < height; row++) {
   		 		// On dessine du sol ou des rochers sur le tour de la pi�ce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			room.set(col, row, MapElements.ROCK);
   		 		} else {
   		 			// Positionnement al�atoire de villages et de ch�teaux,
   		 			// ou herbe sur les emplacements vides
   		 			double randomContent = MathUtils.random();
	   		 		if (randomContent < 0.01) {
	   		 			room.set(col, row, VILLAGE);
					} else if (randomContent < 0.08){
						room.set(col, row, CASTLE);
					} else {
						room.set(col, row, GRASS);
					}
   		 		}
   		 	}
        }
	}
}
