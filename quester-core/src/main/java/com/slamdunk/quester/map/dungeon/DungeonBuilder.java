package com.slamdunk.quester.map.dungeon;

import static com.slamdunk.quester.map.dungeon.RoomWalls.BOTTOM;
import static com.slamdunk.quester.map.dungeon.RoomWalls.LEFT;
import static com.slamdunk.quester.map.dungeon.RoomWalls.RIGHT;
import static com.slamdunk.quester.map.dungeon.RoomWalls.TOP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.AStar;
import com.slamdunk.quester.map.points.PointManager;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class DungeonBuilder {
	private final DungeonRoom[][] rooms;
	private final int dungeonWidth;
	private final int dungeonHeight;
	
	private UnmutablePoint entrance;
	private UnmutablePoint exit;
	
	private boolean roomsCreated;
	private boolean mainGatesPlaced;
	
	private boolean[][] reachableFromEntrance;
	
	private PointManager pointManager;
	private List<UnmutablePoint> linked;
	private List<UnmutablePoint> unlinked;
	
	public DungeonBuilder(int dungeonWidth, int dungeonHeight) {
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		rooms = new DungeonRoom[dungeonWidth][dungeonHeight];
		reachableFromEntrance = new boolean[dungeonWidth][dungeonHeight];
		pointManager = new PointManager(dungeonWidth, dungeonHeight);
		
		// Pr�paration de la liste des salles d�j� li�es � l'entr�e
		linked = new ArrayList<UnmutablePoint>();
		// Pr�paration de la liste des salles � lier � l'entr�e
		unlinked = new ArrayList<UnmutablePoint>();
		for (int col = 0; col < dungeonWidth; col++) {
			for (int row = 0; row < dungeonHeight; row++) {
				unlinked.add(pointManager.getPoint(col, row));
			}
		}
	}
	
	public DungeonRoom[][] build() {
		if (!roomsCreated || !mainGatesPlaced) {
			throw new IllegalStateException("roomsCreated=" + roomsCreated + ", mainGatesPlaced=" + mainGatesPlaced);
		}
		
		// Cr�ation des portes entre les pi�ces.
		// Tant qu'il y a des salles qui ne sont pas accessibles depuis l'entr�e...
		while (!unlinked.isEmpty()) {
			// 1. Prendre au hasard une salle non-joignable depuis l'entr�e.
			UnmutablePoint unlinkedPos = unlinked.get(MathUtils.random(unlinked.size() - 1));
			
			// 2. Choisir une salle joignable au hasard
			UnmutablePoint linkedPos = linked.get(MathUtils.random(linked.size() - 1));
			
			// 3. Connecter cette salle (via un chemin al�atoire) � l'entr�e, ou �
			// une salle accessible depuis l'entr�e
			createRandomPath(unlinkedPos, linkedPos);
		}
		
		// DBG
		validateDungeon();
		return rooms;
	}

	private void validateDungeon() {
		// On valide enfin que l'on peut atteindre la sortie depuis l'entr�e.
		// Pour ce faire, on cr�e une carte virtuelle repr�sentant le donjon.
		// Chaque pi�ce du donjon est une case du pathfinder, ainsi que 
		// chaque jonction possible entre les pi�ces.
		AStar pathfinder = new AStar(dungeonWidth * 2 - 1, dungeonHeight * 2 - 1, false);
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
		DungeonRoom room;
		for (int row = dungeonHeight - 1; row >= 0; row--) {
			for (int col = 0; col < dungeonWidth; col++) {
				colInPathfinder = col * 2;
				rowInPathfinder = row * 2;
				
				// La pi�ce de donjon est walkable
				pathfinder.setWalkable(colInPathfinder, rowInPathfinder, true);
				// Chaque porte vers la droite ou le bas est walkable
				room = rooms[col][row];
				if (room.getDoor(BOTTOM) == RoomElements.COMMON_DOOR) {
					// Porte vers le bas, donc la case du pathfinder correspondant
					// � cette jonction est en bas.
					pathfinder.setWalkable(colInPathfinder, rowInPathfinder - 1, true);
				}
				if (room.getDoor(RIGHT) == RoomElements.COMMON_DOOR) {
					// Porte vers la droite, donc la case du pathfinder correspondant
					// � cette jonction est � droite.
					pathfinder.setWalkable(colInPathfinder + 1, rowInPathfinder, true);
				}
			}
		}
		System.out.println(pathfinder);
		List<UnmutablePoint> path = pathfinder.findPath(
			entrance.getX() * 2, entrance.getY() * 2,
			exit.getX() * 2, exit.getY() * 2);
		if (path == null) {
			System.err.println("Impossible d'atteindre la sortie situ�e � " + exit + " depuis l'entr�e situ�e � " + entrance);
		} else {
			System.out.println("Chemin vers la sortie :" + path);
		}
	}

	/**
	 * Cr�ation d'un chemin al�atoire allant d'une pi�ce � l'autre.
	 * On s'arr�te cependant d�s qu'on atteint une pi�ce rejoignant
	 * l'entr�e.
	 * @param unlinkedPos
	 * @param linkedPos
	 */
	private void createRandomPath(UnmutablePoint from, UnmutablePoint to) {
		int curX = from.getX();
		int curY = from.getY();
		int exitX = to.getX();
		int exitY = to.getY();
		final List<RoomWalls> walls = new ArrayList<RoomWalls>();

		// On s'arr�te si on arrive � la destination ou si on atteint une pi�ce
		// qui permet de rejoindre l'entr�e
		while ((curX != exitX || curY != exitY)
		&& !reachableFromEntrance[curX][curY]) {
			// Bient�t, cette salle sera accessible depuis l'entr�e
			linkRoom(curX, curY);
			
			walls.clear();
			// Si la sortie est plus en haut, on autorise un offset vers le haut
			if (exitY > curY) {
				walls.add(TOP);
			}
			// Si la sortie est plus en bas, on autorise un offset vers le bas
			if (exitY < curY) {
				walls.add(BOTTOM);
			}
			// Si la sortie est plus � gauche, on autorise un offset vers la gauche
			if (exitX < curX) {
				walls.add(LEFT);
			}
			// Si la sortie est plus � droite, on autorise un offset vers la droite
			if (exitX > curX) {
				walls.add(RIGHT);
			}
			// 1. Choix d'un mur
			// 2. Cr�ation de la porte entre les pi�ces
			// 3. M�j du tableau des joignables : la pi�ce destination sera connect�e � l'entr�e au final
			// 4. D�placement du curseur en direction de la destination
			RoomWalls choosenWall = walls.get(MathUtils.random(walls.size() - 1));
			switch (choosenWall) {
				case TOP:
					createVerticalDoor(rooms[curX][curY + 1], rooms[curX][curY]);
					curY++;
					break;
				case BOTTOM:
					createVerticalDoor(rooms[curX][curY], rooms[curX][curY - 1]);
					curY--;
					break;
				case LEFT:
					createHorizontalDoor(rooms[curX - 1][curY], rooms[curX][curY]);
					curX--;
					break;
				case RIGHT:
					createHorizontalDoor(rooms[curX][curY], rooms[curX + 1][curY]);
					curX++;
					break;
			}
		}
	}
	
	/**
	 * Met � jour les diff�rents objets pour indiquer que la pi�ce
	 * aux coordonn�es sp�cifi�es est accessible depuis l'entr�e
	 * @param curX
	 * @param curY
	 */
	private void linkRoom(int x, int y) {
		linkRoom(pointManager.getPoint(x, y));
	}
	
	private void linkRoom(UnmutablePoint pos) {
		reachableFromEntrance[pos.getX()][pos.getY()] = true;
		unlinked.remove(pos);
		linked.add(pos);
	}

	private void createHorizontalDoor(DungeonRoom leftRoom, DungeonRoom rightRoom) {
		// On place une porte au milieu de la porte du mur droit de la premi�re pi�ce
		leftRoom.setDoor(RIGHT, RoomElements.COMMON_DOOR);
		
		// On place une porte au milieu de la porte du mur gauche de la seconde pi�ce
		rightRoom.setDoor(LEFT, RoomElements.COMMON_DOOR);
	}
	
	private void createVerticalDoor(DungeonRoom topRoom, DungeonRoom bottomRoom) {
		// On place une porte au milieu de la porte du mur droit de la premi�re pi�ce
		topRoom.setDoor(BOTTOM, RoomElements.COMMON_DOOR);
		
		// On place une porte au milieu de la porte du mur gauche de la seconde pi�ce
		bottomRoom.setDoor(TOP, RoomElements.COMMON_DOOR);
	}

	/**
	 * Cr�e les pi�ces du donjon, sans portes mais avec du sol.
	 * @param roomWidth
	 * @param roomHeight
	 */
	public void createRooms(int roomWidth, int roomHeight) {
		for (int col = 0; col < dungeonWidth; col ++) {
			for (int row = 0; row < dungeonHeight; row ++) {
				// La taille de la pi�ce correspond � la taille de la map,
				// car on n'affiche qu'une pi�ce � chaque fois.
				rooms[col][row] = createRoom(roomWidth, roomHeight);
			}
		}
		roomsCreated = true;
	}
	
	/**
	 * Cr�e une pi�ce en instanciant son background (sols, murs...)
	 * de fa�on al�atoire
	 * @param mapWidth
	 * @param mapHeight
	 * @return
	 */
	private DungeonRoom createRoom(int width, int height) {
		DungeonRoom room = new DungeonRoom(width, height);
		for (int col=0; col < width; col++) {
   		 	for (int row=0; row < height; row++) {
   		 		// On dessine du sol ou des murs sur le tour de la pi�ce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == width - 1
   		 		|| row == height - 1) {
   		 			room.set(col, row, RoomElements.WALL);
   		 		} else {
	   		 		room.set(col, row, RoomElements.GROUND);
   		 		}
   		 	}
        }
		return room;
	}

	/**
	 * Choisit al�atoirement une pi�ce pour y placer l'entr�e et la sortie du donjon.
	 * L'algorithme fait en sorte de ne pas mettre les deux sur le m�me c�t� du donjon.
	 */
	public void placeMainGates() {
		List<RoomWalls> walls = new ArrayList<RoomWalls>(Arrays.asList(RoomWalls.values()));
		// Choix d'une pi�ce d'entr�e
		entrance = createMainDoor(walls, RoomElements.DUNGEON_ENTRANCE_DOOR);
		// La pi�ce d'entr�e est marqu�e comme �tant accessible depuis l'entr�e (logique ^^)
		linkRoom(entrance);
		// Choix d'une pi�ce de sortie
		do {
			exit = createMainDoor(walls, RoomElements.DUNGEON_EXIT_DOOR);
		// On continue tant que l'entr�e et la sortie sont dans la m�me pi�ce
		// sauf s'il n'y a qu'une seule pi�ce dans le donjon
		} while ((dungeonWidth > 1 && dungeonHeight > 1)
		&& entrance.equals(exit));
		
		mainGatesPlaced = true;
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
	private UnmutablePoint createMainDoor(List<RoomWalls> walls, RoomElements door) {
		RoomWalls choosenWall = walls.remove(MathUtils.random(walls.size() - 1));
		int choosenRoom;
		switch (choosenWall) {
			case TOP:
				choosenRoom = MathUtils.random(dungeonWidth - 1);
				rooms[choosenRoom][dungeonHeight - 1].setDoor(TOP, door);
				return pointManager.getPoint(choosenRoom, dungeonHeight - 1);
			case BOTTOM:
				choosenRoom = MathUtils.random(dungeonWidth - 1);
				rooms[choosenRoom][0].setDoor(BOTTOM, door);
				return pointManager.getPoint(choosenRoom, 0);
			case LEFT:
				choosenRoom = MathUtils.random(dungeonHeight - 1);
				rooms[0][choosenRoom].setDoor(LEFT, door);
				return pointManager.getPoint(0, choosenRoom);
			case RIGHT:
				choosenRoom = MathUtils.random(dungeonHeight - 1);
				rooms[dungeonWidth - 1][choosenRoom].setDoor(RIGHT, door);
				return pointManager.getPoint(dungeonWidth - 1, choosenRoom);
		}
		// Code impossible : le mur est forc�ment un des 4 qui existent
		return pointManager.getPoint(0, 0);
	}

	public UnmutablePoint getEntrance() {
		return entrance;
	}
}
