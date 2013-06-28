package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.data.WorldElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.ROCK_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.VILLAGE_DATA;
import static com.slamdunk.quester.model.map.MapElements.PATH_TO_REGION;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Config;

public class WorldBuilder extends DungeonBuilder{
	private AStar pathfinder;
	private int maxDistance;

	public WorldBuilder(int worldWidth, int worldHeight) {
		super(worldWidth, worldHeight, 0);
		setLinkType(PATH_TO_REGION);
	}
	
	@Override
	public MapArea[][] build() {
		super.build();
		
		// Une fois les zones créées et les liens entre ces zones faits, on peut
		// initialiser le contenu de chaque zone. Il est nécessaire de faire
		// cet appel en dernier car on veut que le contenu dépende de la distance
		// par rapport à la zone de départ, et cette distance ne peut être déduite
		// qu'une fois les zones créées et reliées entre elles.
		
		// On initialise le pathfinder grâce auquel on va déterminer la difficulté de chaque zone
		pathfinder = createPathfinder();
		
		// On va à présent déterminer la distance de chaque zone par rapport au village de départ,
		// ainsi que la distance la plus grande
		List<Point> path;
		int distance;
		maxDistance = 1;
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				// On doit multiplier les positions par 2 car pour le pathfinder, il y a une
				// cellule entre chaque zone (qui représente les chemins)
				path = pathfinder.findPath(0, 0, x * 2, y * 2);
				if (path != null) {
					distance = path.size();
					areas[x][y].setDistance(distance);
					if (distance > maxDistance) {
						maxDistance = distance;
					}
				}
			}
		}
		
		// On peut à présent remplir chaque zone
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				fillRoom(areas[x][y]);
			}
		}
		
		return areas;
	}
	
	@Override
	public void createAreas(int areaWidth, int areaHeight, WorldElementData defaultBackground) {
		// On redéfinit createAreas car on ne veut appeler fillRoom qu'à la fin du processus,
		// quand les liens entre les pièces ont été faits.
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		for (int col = 0; col < mapWidth; col ++) {
			for (int row = 0; row < mapHeight; row ++) {
				// La taille de la zone correspond à la taille de la map,
				// car on n'affiche qu'une zone à chaque fois.
				areas[col][row] = new MapArea(col, row, areaWidth, areaHeight, defaultBackground);
			}
		}
		areasCreated = true;
	}
	
	@Override
	protected void fillRoom(MapArea area) {
		// Plus on s'éloigne du village de départ, plus les châteaux sont vastes.
		double percentage = (double)area.getDistance() / maxDistance;
		int difficulty = 0;
		if (percentage < 0.1) {
			difficulty = 0;
		} else if (percentage < 0.25) {
			difficulty = 1;
		} else if (percentage < 0.5) {
			difficulty = 2;
		} else if (percentage < 0.75) {
			difficulty = 3;
		} else {
			difficulty = 4;
		}
		final String castleDifficultyProperty = "castle.difficulty" + difficulty;
		int castleMinSize = Config.asInt(castleDifficultyProperty + ".castleMinSize", 1);
		int castleMaxSize = Config.asInt(castleDifficultyProperty + ".castleMaxSize", 1);
		int roomWidth = Config.asInt("castle.roomWidth", 8);
		int roomHeight = Config.asInt("castle.roomHeight", 10);
//DBG		int roomMinSize = Config.asInt(castleDifficultyProperty + ".roomMinSize", 2);
//DBG		int roomMaxSize = Config.asInt(castleDifficultyProperty + ".roomMaxSize", 2);
		
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
   		 			// S'il n'y a rien, on met un rocher.
   		 			// On doit faire ce check car comme on intervient après
   		 			// le positionnement des chemins entre les zones, il se
   		 			// peut que sur le pourtour se trouve déjà un chemin
   		 			WorldElementData data = area.getObjectAt(col, row);
   		 			if (data == null || data.element == MapElements.EMPTY) {
   		 				area.setObjectAt(col, row, ROCK_DATA);
   		 			}
   		 		} else {
   		 			// Positionnement aléatoire de villages et de châteaux,
   		 			// ou herbe sur les emplacements vides
   		 			double randomContent = MathUtils.random();
	   		 		if (randomContent < Config.asFloat("village.appearRate", 0.005f)) {
	   		 			area.setObjectAt(col, row, VILLAGE_DATA);
					} else if (randomContent < Config.asFloat("castle.appearRate", 0.08f)){
						area.setObjectAt(col, row, new CastleData(
							MathUtils.random(castleMinSize, castleMaxSize), MathUtils.random(castleMinSize, castleMaxSize),
							//DBGMathUtils.random(roomMinSize, roomMaxSize), MathUtils.random(roomMinSize, roomMaxSize),
							roomWidth, roomHeight,
							difficulty));
					}
   		 		}
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
	
	@Override
	public void placeMainEntrances() {
		// La région de départ est en bas à gauche
		entranceArea = pointManager.getPoint(0, 0);
		MapArea startRegion = areas[entranceArea.getX()][entranceArea.getY()];
		
		// On détermine la position du village de départ. Cette position sera utilisée
		// lors du build() pour placer effectivement le village dans la région qui va bien.
		entrancePosition = new Point(startRegion.getWidth() / 2, startRegion.getHeight() / 2);
		startRegion.setObjectAt(entrancePosition.getX(), entrancePosition.getY(), VILLAGE_DATA);
		
		// La région d'entrée est marquée comme étant accessible depuis l'entrée (logique ^^)
		linkArea(entranceArea);
		
		// Il n'y a pas de sortie, donc rien de plus à faire
		mainEntrancesPlaced = true;
	}
}
