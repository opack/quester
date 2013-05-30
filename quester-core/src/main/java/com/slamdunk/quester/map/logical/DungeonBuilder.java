package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.Borders.BOTTOM;
import static com.slamdunk.quester.map.logical.Borders.LEFT;
import static com.slamdunk.quester.map.logical.Borders.RIGHT;
import static com.slamdunk.quester.map.logical.Borders.TOP;
import static com.slamdunk.quester.map.logical.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.ROBOT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class DungeonBuilder extends MapBuilder {
	private UnmutablePoint exitRoom;
	
	public DungeonBuilder(int dungeonWidth, int dungeonHeight) {
		super(dungeonWidth, dungeonHeight, COMMON_DOOR);
	}
	
	@Override
	protected boolean validateDungeon() {
		if (exitRoom == null) {
			System.out.println("DungeonBuilder.validateDungeon() La validation du donjon ne sera pas faite car aucune sortie n'est d�finie.");
			return true;
		}
		// On valide enfin que l'on peut atteindre la sortie depuis l'entr�e.
		// Pour ce faire, on cr�e une carte virtuelle repr�sentant le donjon.
		// Chaque pi�ce du donjon est une case du pathfinder, ainsi que 
		// chaque jonction possible entre les pi�ces.
		AStar pathfinder = new AStar(mapWidth * 2 - 1, mapHeight * 2 - 1, false);
		// Par d�faut, toutes ces cases sont walkable. Nous allons � pr�sent
		// indiquer que les seules cases walkable sont les pi�ces de donjon
		// et les cases qui repr�sentent une jonction entre deux pi�ces o�
		// une porte existe bel et bien.
		// Petite optimisation : on parcours le donjon du haut vers le bas, de
		// la gauche vers la droite. Seules les portes sur les murs droit et bas
		// seront donc prises en compte. En effet, une porte vers le haut aura
		// d�j� �t� indiqu�e par une pi�ce pr�c�dente comme �tant une porte vers
		// le bas. Idem pour les portes vers la gauche.
		int colInPathfinder;
		int rowInPathfinder;
		MapArea room;
		ElementData commonDoorData = new ElementData(COMMON_DOOR);
		for (int row = mapHeight - 1; row >= 0; row--) {
			for (int col = 0; col < mapWidth; col++) {
				colInPathfinder = col * 2;
				rowInPathfinder = row * 2;
				
				// La pi�ce de donjon est walkable
				pathfinder.setWalkable(colInPathfinder, rowInPathfinder, true);
				// Chaque porte vers la droite ou le bas est walkable
				room = areas[col][row];
				if (room.getPaths(BOTTOM).contains(commonDoorData)) {
					// Porte vers le bas, donc la case du pathfinder correspondant
					// � cette jonction est en bas.
					pathfinder.setWalkable(colInPathfinder, rowInPathfinder - 1, true);
				}
				if (room.getPaths(RIGHT).contains(commonDoorData)) {
					// Porte vers la droite, donc la case du pathfinder correspondant
					// � cette jonction est � droite.
					pathfinder.setWalkable(colInPathfinder + 1, rowInPathfinder, true);
				}
			}
		}
		System.out.println("DungeonBuilder.validateDungeon() " + pathfinder);
		List<UnmutablePoint> path = pathfinder.findPath(
			entranceArea.getX() * 2, entranceArea.getY() * 2,
			exitRoom.getX() * 2, exitRoom.getY() * 2);
		if (path == null) {
			System.err.println("Impossible d'atteindre la sortie situ�e � " + exitRoom + " depuis l'entr�e situ�e � " + entranceArea);
			return false;
		} else {
			System.out.println("Chemin vers la sortie :" + path);
			return true;
		}
	}

	@Override
	protected void fillRoom(MapArea area) {
		// Dans le donjon, la mort d'un personnage est d�finitive
		area.setPermKillCharacters(true);
		
		// Cr�ation de la structure de la zone
		int width = area.getWidth();
		int height = area.getHeight();
		for (int col=0; col < area.getWidth(); col++) {
   		 	for (int row=0; row < area.getHeight(); row++) {
   		 		// On dessine du sol partout
   		 		area.setBackgroundAt(col, row, GROUND_DATA);
   		 		// Et des murs sur le pourtour de la pi�ce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			area.setObjectAt(col, row, WALL_DATA);
   		 		}
   		 	}
        }

		// Ajout des personnages
		int nbRobots = MathUtils.random(1, 5);
		for (int count = 0; count < nbRobots; count++) {
			area.addCharacter(new CharacterData (
				ROBOT,
				MathUtils.random(2, 10),
				MathUtils.random(1, 2)));
		}
	}

	/**
	 * Choisit al�atoirement une pi�ce pour y placer l'entr�e et la sortie du donjon.
	 * L'algorithme fait en sorte de ne pas mettre les deux sur le m�me c�t� du donjon.
	 */
	@Override
	public void placeMainEntrances() {
		List<Borders> walls = new ArrayList<Borders>(Arrays.asList(Borders.values()));
		// Choix d'une pi�ce d'entr�e
		entranceArea = createMainDoor(walls, DUNGEON_ENTRANCE_DOOR);
		// La pi�ce d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkArea(entranceArea);
		// Recherche de la position de d�part dans la salle de d�part. La boucle est
		// faite pour ne parcourir que les bords de la pi�ce car on sait que la porte
		// est plac�e sur un mur.
		MapArea room = areas[entranceArea.getX()][entranceArea.getY()];
		entrancePositionSearch: {
			for (int col = 0; col < room.getWidth(); col ++) {
				for (int row = 0; row < room.getHeight(); row ++) {
					if (room.getObjectAt(col, row).element == DUNGEON_ENTRANCE_DOOR) {
						entrancePosition = new UnmutablePoint(col, row);
						break entrancePositionSearch;
					}
				}
			}
		}
		
		
		// Choix d'une pi�ce de sortie
		do {
			exitRoom = createMainDoor(walls, DUNGEON_EXIT_DOOR);
		// On continue tant que l'entr�e et la sortie sont dans la m�me pi�ce
		// sauf s'il n'y a qu'une seule pi�ce dans le donjon
		} while ((mapWidth > 1 && mapHeight > 1)
		&& entranceArea.equals(exitRoom));
		
		mainEntrancesPlaced = true;
	}
	
	/**
	 * Cr�e une porte principale (ENTRANCE_DOOR ou EXIT_DOOR) sur le mur du donjon
	 * indiqu� par walls. Ce mur indique si la porte est sur le c�t� haut, bas,
	 * gauche ou droit du donjon. Un choix al�atoire de la pi�ce sur ce c�t�
	 * du donjon sera fait pour d�terminer o� sera cette porte.
	 * Une fois le mur choisi, il est retir� de la liste des murs possible walls,
	 * pour �viter de mettre la porte d'entr�e et de sortie sur le m�me c�t� du
	 * donjon.
	 * @param walls
	 * @param door
	 */
	private UnmutablePoint createMainDoor(List<Borders> walls, MapElements door) {
		Borders choosenWall = walls.remove(MathUtils.random(walls.size() - 1));
		int choosenRoom;
		PathData path = new PathData(door, -1, -1);
		switch (choosenWall) {
			case TOP:
				choosenRoom = MathUtils.random(mapWidth - 1);
				areas[choosenRoom][mapHeight - 1].addPath(TOP, path);
				return pointManager.getPoint(choosenRoom, mapHeight - 1);
			case BOTTOM:
				choosenRoom = MathUtils.random(mapWidth - 1);
				areas[choosenRoom][0].addPath(BOTTOM, path);
				return pointManager.getPoint(choosenRoom, 0);
			case LEFT:
				choosenRoom = MathUtils.random(mapHeight - 1);
				areas[0][choosenRoom].addPath(LEFT, path);
				return pointManager.getPoint(0, choosenRoom);
			case RIGHT:
				choosenRoom = MathUtils.random(mapHeight - 1);
				areas[mapWidth - 1][choosenRoom].addPath(RIGHT, path);
				return pointManager.getPoint(mapWidth - 1, choosenRoom);
		}
		// Code impossible : le mur est forc�ment un des 4 qui existent
		return pointManager.getPoint(0, 0);
	}
}
