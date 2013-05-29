package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.Borders.BOTTOM;
import static com.slamdunk.quester.map.logical.Borders.LEFT;
import static com.slamdunk.quester.map.logical.Borders.RIGHT;
import static com.slamdunk.quester.map.logical.Borders.TOP;
import static com.slamdunk.quester.map.logical.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.GROUND;
import static com.slamdunk.quester.map.logical.MapElements.WALL;

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
			System.out.println("DungeonBuilder.validateDungeon() La validation du donjon ne sera pas faite car aucune sortie n'est définie.");
			return true;
		}
		// On valide enfin que l'on peut atteindre la sortie depuis l'entrée.
		// Pour ce faire, on crée une carte virtuelle représentant le donjon.
		// Chaque pièce du donjon est une case du pathfinder, ainsi que 
		// chaque jonction possible entre les pièces.
		AStar pathfinder = new AStar(width * 2 - 1, height * 2 - 1, false);
		// Par défaut, toutes ces cases sont walkable. Nous allons à présent
		// indiquer que les seules cases walkable sont les pièces de donjon
		// et les cases qui représentent une jonction entre deux pièces où
		// une porte existe bel et bien.
		// Petite optimisation : on parcours le donjon du haut vers le bas, de
		// la gauche vers la droite. Seules les portes sur les murs droit et bas
		// seront donc prises en compte. En effet, une porte vers le haut aura
		// déjà été indiquée par une pièce précédente comme étant une porte vers
		// le bas. Idem pour les portes vers la gauche.
		int colInPathfinder;
		int rowInPathfinder;
		MapArea room;
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				colInPathfinder = col * 2;
				rowInPathfinder = row * 2;
				
				// La pièce de donjon est walkable
				pathfinder.setWalkable(colInPathfinder, rowInPathfinder, true);
				// Chaque porte vers la droite ou le bas est walkable
				room = areas[col][row];
				if (room.getPath(BOTTOM) == COMMON_DOOR) {
					// Porte vers le bas, donc la case du pathfinder correspondant
					// à cette jonction est en bas.
					pathfinder.setWalkable(colInPathfinder, rowInPathfinder - 1, true);
				}
				if (room.getPath(RIGHT) == COMMON_DOOR) {
					// Porte vers la droite, donc la case du pathfinder correspondant
					// à cette jonction est à droite.
					pathfinder.setWalkable(colInPathfinder + 1, rowInPathfinder, true);
				}
			}
		}
		System.out.println(pathfinder);
		List<UnmutablePoint> path = pathfinder.findPath(
			entranceArea.getX() * 2, entranceArea.getY() * 2,
			exitRoom.getX() * 2, exitRoom.getY() * 2);
		if (path == null) {
			System.err.println("Impossible d'atteindre la sortie située à " + exitRoom + " depuis l'entrée située à " + entranceArea);
			return false;
		} else {
			System.out.println("Chemin vers la sortie :" + path);
			return true;
		}
	}

	@Override
	protected void fillRoom(MapArea room) {
		int width = room.getWidth();
		int height = room.getHeight();
		for (int col=0; col < room.getWidth(); col++) {
   		 	for (int row=0; row < room.getHeight(); row++) {
   		 		// On dessine du sol ou des murs sur le tour de la pièce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			room.set(col, row, WALL);
   		 		} else {
	   		 		room.set(col, row, GROUND);
   		 		}
   		 	}
        }
	}

	/**
	 * Choisit aléatoirement une pièce pour y placer l'entrée et la sortie du donjon.
	 * L'algorithme fait en sorte de ne pas mettre les deux sur le même côté du donjon.
	 */
	@Override
	public void placeMainEntrances() {
		List<Borders> walls = new ArrayList<Borders>(Arrays.asList(Borders.values()));
		// Choix d'une pièce d'entrée
		entranceArea = createMainDoor(walls, DUNGEON_ENTRANCE_DOOR);
		// La pièce d'entrée est marquée comme étant accessible depuis l'entrée (logique ^^)
		linkArea(entranceArea);
		// Recherche de la position de départ dans la salle de départ. La boucle est
		// faite pour ne parcourir que les bords de la pièce car on sait que la porte
		// est placée sur un mur.
		MapArea room = areas[entranceArea.getX()][entranceArea.getY()];
		entrancePositionSearch: {
			for (int col = 0; col < room.getWidth(); col ++) {
				for (int row = 0; row < room.getHeight(); row ++) {
					if (room.get(col, row) == DUNGEON_ENTRANCE_DOOR) {
						entrancePosition = new UnmutablePoint(col, row);
						break entrancePositionSearch;
					}
				}
			}
		}
		
		
		// Choix d'une pièce de sortie
		do {
			exitRoom = createMainDoor(walls, DUNGEON_EXIT_DOOR);
		// On continue tant que l'entrée et la sortie sont dans la même pièce
		// sauf s'il n'y a qu'une seule pièce dans le donjon
		} while ((width > 1 && height > 1)
		&& entranceArea.equals(exitRoom));
		
		mainEntrancesPlaced = true;
	}
	
	/**
	 * Crée une porte principale (ENTRANCE_DOOR ou EXIT_DOOR) sur le mur du donjon
	 * indiqué par walls. Ce mur indique si la porte est sur le côté haut, bas,
	 * gauche ou droit du donjon. Un choix aléatoire de la pièce sur ce côté
	 * du donjon sera fait pour déterminer où sera cette porte.
	 * Une fois le mur choisi, il est retiré de la liste des murs possible walls,
	 * pour éviter de mettre la porte d'entrée et de sortie sur le même côté du
	 * donjon.
	 * @param walls
	 * @param door
	 */
	private UnmutablePoint createMainDoor(List<Borders> walls, MapElements door) {
		Borders choosenWall = walls.remove(MathUtils.random(walls.size() - 1));
		int choosenRoom;
		switch (choosenWall) {
			case TOP:
				choosenRoom = MathUtils.random(width - 1);
				areas[choosenRoom][height - 1].setPath(TOP, door);
				return pointManager.getPoint(choosenRoom, height - 1);
			case BOTTOM:
				choosenRoom = MathUtils.random(width - 1);
				areas[choosenRoom][0].setPath(BOTTOM, door);
				return pointManager.getPoint(choosenRoom, 0);
			case LEFT:
				choosenRoom = MathUtils.random(height - 1);
				areas[0][choosenRoom].setPath(LEFT, door);
				return pointManager.getPoint(0, choosenRoom);
			case RIGHT:
				choosenRoom = MathUtils.random(height - 1);
				areas[width - 1][choosenRoom].setPath(RIGHT, door);
				return pointManager.getPoint(width - 1, choosenRoom);
		}
		// Code impossible : le mur est forcément un des 4 qui existent
		return pointManager.getPoint(0, 0);
	}
}
