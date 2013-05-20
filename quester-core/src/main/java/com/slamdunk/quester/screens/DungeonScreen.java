package com.slamdunk.quester.screens;

import static com.slamdunk.quester.dungeon.RoomWalls.BOTTOM;
import static com.slamdunk.quester.dungeon.RoomWalls.LEFT;
import static com.slamdunk.quester.dungeon.RoomWalls.RIGHT;
import static com.slamdunk.quester.dungeon.RoomWalls.TOP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.CharacterListener;
import com.slamdunk.quester.actors.CommonDoor;
import com.slamdunk.quester.actors.EntranceDoor;
import com.slamdunk.quester.actors.ExitDoor;
import com.slamdunk.quester.actors.Ground;
import com.slamdunk.quester.actors.Obstacle;
import com.slamdunk.quester.actors.Player;
import com.slamdunk.quester.actors.Robot;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.dungeon.DungeonRoom;
import com.slamdunk.quester.dungeon.RoomElements;
import com.slamdunk.quester.dungeon.RoomWalls;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;

public class DungeonScreen extends AbstractMapScreen implements CharacterListener  {
	// DBG Nombre de robots.
	private final static int NB_ROBOTS = 5;
	
	private Character player;
	private int curCharacterPlaying;
	
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final int dungeonWidth;
	private final int dungeonHeight;
	private final DungeonRoom[][] rooms;
	
	private Vector2 entryRoom;
	private Vector2 exitRoom;
	
	public DungeonScreen(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight,
			int worldCellWidth, int worldCellHeight) {
		super(roomWidth, roomHeight, worldCellWidth, worldCellHeight);
		// Crée les pièces et affiche la première
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		rooms = new DungeonRoom[dungeonWidth][dungeonHeight];
		createDungeon();
		createPlayer();
		showRoom((int)entryRoom.x, (int)entryRoom.y, -1, -1);
		
		// Crée le hud
		createHud();
		
        // C'est parti ! Que le premier personnage (le joueur) joue ! :)
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}

	private void createPlayer() {
		player = new Player("Player", this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(2);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        player.addListener(this);
	}

	/**
	 * Crée la carte en y ajoutant les différents objets du monde
	 */
	private void createDungeon() {
		// Création des pièces du donjon
		for (int col = 0; col < dungeonWidth; col ++) {
			for (int row = 0; row < dungeonHeight; row ++) {
				// La taille de la pièce correspond à la taille de la map,
				// car on n'affiche qu'une pièce à chaque fois.
				rooms[col][row] = createRoom(getMapWidth(), getMapHeight());
			}
		}
		// Place les portes entre les pièces
		boolean createDoor;
		// Pour chaque "inter-colonne", on définit aléatoirement s'il y a une porte.
		// Cela permet de faire communiquer les pièces horizontalement.
		for (int curRow = 0; curRow < dungeonHeight; curRow++) {
			for (int interCol = 0; interCol < dungeonWidth - 1; interCol++) {
				createDoor = MathUtils.randomBoolean();
				if (createDoor) {
					createHorizontalDoor(rooms[interCol][curRow], rooms[interCol + 1][curRow]);
				}
			}
		}
		// On procède ensuite de la même façon pour les "inter-lignes".
		for (int curCol = 0; curCol < dungeonWidth; curCol++) {
			for (int interRow = 0; interRow < dungeonHeight - 1; interRow++) {
				createDoor = MathUtils.randomBoolean();
				if (createDoor) {
					createVerticalDoor(rooms[curCol][interRow], rooms[curCol][interRow + 1]);
				}
			}
		}
		// Enfin, on vérifie que toutes les pièces ont au moins une porte menant à une
		// autre pièce (donc une porte normale), sinon on en ajoute une aléatoirement.
		DungeonRoom room;
		List<RoomWalls> walls = new ArrayList<RoomWalls>();
		for (int curCol = 0; curCol < dungeonWidth; curCol++) {
			for (int curRow = 0; curRow < dungeonHeight; curRow++) {
				room = rooms[curCol][curRow];
				if (!room.containsCommonDoor()) {
					// On prépare la liste des murs sur lesquels pourrait se trouver une porte. Le principe
					// est de regarder si ce mur contient déjà une porte et s'il y a bien une pièce vers
					// laquelle on pourrait aller.
					if (curRow > 0 && room.getDoor(BOTTOM) == RoomElements.EMPTY) {
						walls.add(BOTTOM);
					}
					if (curRow < dungeonHeight - 1 && room.getDoor(TOP) == RoomElements.EMPTY) {
						walls.add(TOP);
					}
					if (curCol > 0 && room.getDoor(LEFT) == RoomElements.EMPTY) {
						walls.add(LEFT);
					}
					if (curCol < dungeonWidth - 1 && room.getDoor(RIGHT) == RoomElements.EMPTY) {
						walls.add(RIGHT);
					}
					
					// On choisit un de ces murs au hasard
					RoomWalls choosenWall = walls.get(MathUtils.random(walls.size() - 1));
					
					// On peut à présent poser une porte entre cette pièce et la pièce adjacente.
					switch (choosenWall) {
						case TOP:
							createVerticalDoor(rooms[curCol][curRow + 1], room);
							break;
						case BOTTOM:
							createVerticalDoor(room, rooms[curCol][curRow - 1]);
							break;
						case LEFT:
							createHorizontalDoor(rooms[curCol - 1][curRow], room);
							break;
						case RIGHT:
							createHorizontalDoor(room, rooms[curCol + 1][curRow]);
							break;
					}
				}
			}
		}
		
		// Choix d'une des pièces pour y placer l'entrée du donjon. Idem pour la sortie.
		walls = Arrays.asList(RoomWalls.values());
		entryRoom = createMainDoor(walls, RoomElements.DUNGEON_ENTRANCE_DOOR);
		do {
			exitRoom = createMainDoor(walls, RoomElements.DUNGEON_EXIT_DOOR);
		} while (entryRoom.equals(exitRoom)); // On continue tant que l'entrée et la sortie sont dans la même pièce
	
		// DBG Affichage du donjon en text
		for (int row = dungeonHeight - 1; row >= 0; row--) {
			for (int col = 0; col < dungeonWidth; col++) {
				System.out.println("Room " + col + ";" + row);
				System.out.println(rooms[col][row]);
			}
		}
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
	private Vector2 createMainDoor(List<RoomWalls> walls, RoomElements door) {
		RoomWalls choosenWall = walls.get(MathUtils.random(walls.size() - 1));
		int choosenRoom;
		switch (choosenWall) {
			case TOP:
				choosenRoom = MathUtils.random(dungeonWidth - 1);
				rooms[choosenRoom][0].setDoor(TOP, door);
				return new Vector2(choosenRoom, 0);
			case BOTTOM:
				choosenRoom = MathUtils.random(dungeonWidth - 1);
				rooms[choosenRoom][dungeonHeight - 1].setDoor(BOTTOM, door);
				return new Vector2(choosenRoom, dungeonHeight - 1);
			case LEFT:
				choosenRoom = MathUtils.random(dungeonHeight - 1);
				rooms[0][choosenRoom].setDoor(LEFT, door);
				return new Vector2(0, choosenRoom);
			case RIGHT:
				choosenRoom = MathUtils.random(dungeonHeight - 1);
				rooms[dungeonWidth - 1][choosenRoom].setDoor(RIGHT, door);
				return new Vector2(dungeonWidth - 1, choosenRoom);
		}
		// Code impossible : le mur est forcément un des 4 qui existent
		return new Vector2(0, 0);
	}

	private void createHorizontalDoor(DungeonRoom leftRoom, DungeonRoom rightRoom) {
		// On place une porte au milieu de la porte du mur droit de la première pièce
		leftRoom.setDoor(RIGHT, RoomElements.COMMON_DOOR);
		
		// On place une porte au milieu de la porte du mur gauche de la seconde pièce
		rightRoom.setDoor(LEFT, RoomElements.COMMON_DOOR);
	}
	
	private void createVerticalDoor(DungeonRoom topRoom, DungeonRoom bottomRoom) {
		// On place une porte au milieu de la porte du mur droit de la première pièce
		topRoom.setDoor(BOTTOM, RoomElements.COMMON_DOOR);
		
		// On place une porte au milieu de la porte du mur gauche de la seconde pièce
		bottomRoom.setDoor(TOP, RoomElements.COMMON_DOOR);
	}

	/**
	 * Crée une pièce en instanciant son background (sols, murs...)
	 * de façon aléatoire
	 * @param mapWidth
	 * @param mapHeight
	 * @return
	 */
	private DungeonRoom createRoom(int width, int height) {
		DungeonRoom room = new DungeonRoom(width, height);
		for (int col=0; col < mapWidth; col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		// On dessine du rocher ou des murs sur le tour de la pièce
   		 		if (col == 0
   		 		|| row == 0
   		 		|| col == mapWidth - 1
   		 		|| row == mapHeight - 1) {
   		 			room.set(col, row, RoomElements.ROCK);
   		 		} else {
	   		 		// Sinon, on choisit aléatoirement
	   		 		float typeIndex = MathUtils.random();
		   			if (typeIndex < /*DBG0.45*/0.5) {
		   				room.set(col, row, RoomElements.SAND);
		   			} else /*DBGif (typeIndex < 0.9)*/ {
		   				room.set(col, row, RoomElements.GRASS);
		   			}/*DBG else {
		   				room.set(col, row, RoomElements.ROCK);
		   			}*/
   		 		}
   		 	}
        }
		return room;
	}
	
	/**
	 * Affiche la pièce à l'indice indiqué.
	 * @param room
	 */
	@Override
	public void showRoom(int roomX, int roomY, int entranceX, int entranceY) {
		System.out.println("DungeonScreen.showRoom(" + roomX + ", " + roomY + ", " + entranceX + ", " + entranceY + ")");
		DungeonRoom room = rooms[roomX][roomY];
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
		// Nettoyage de la pièce actuelle
		clearMap();
        
        // Création du fond
        WorldElement element = null;
	 	for (int col=0; col < room.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		switch (room.get(col, row)) {
   		 			case EMPTY:
   		 				// Case vide : rien à faire :)
   		 				break;
		   		 	case DUNGEON_ENTRANCE_DOOR:
		 				element = new EntranceDoor(col, row, this, getWall(col, row));
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				screenMap.setWalkable(col, row, false);
		 				// Si le joueur est arrivé dans cette pièce par l'entrée du donjon
		 				// on conserve ces coordonnées
		 				if (entranceX == -1 && entranceY == -1) {
		 					entranceX = col;
		 					entranceY = row;
		 				}
		 				break;
		   		 	case DUNGEON_EXIT_DOOR:
		 				element = new ExitDoor(col, row, this, getWall(col, row));
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;
		   		 	case COMMON_DOOR:
		   		 		element = createCommonDoor(col, row, roomX, roomY);
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;  
   		 			case ROCK:
   		 				element = new Obstacle(Assets.rock, col, row, this);
   		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
   		 				screenMap.setWalkable(col, row, false);
		 				break;
   		 			case GRASS:
   		 				element = new Ground(Assets.grass, col, row, this);
   		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;
   		 			case SAND:
   		 				element = new Ground(Assets.sand, col, row, this);
   		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;
   		 		}
   		 	}
        }

	 	// Création de la liste des personnages actifs et définit le premier de la liste
        // comme étant le prochain à jouer.
	 	player.setPositionInWorld(entranceX, entranceY);
        characters.add(player);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), entranceX, entranceY, player));
        
        for (int curBot = 0; curBot < NB_ROBOTS; curBot++){
        	int col = MathUtils.random(mapWidth - 1);
        	int row = MathUtils.random(mapHeight - 1);
        	if (screenMap.isEmptyAbove(0, col, row)) {
        		Robot robot = new Robot("Robot" + curBot, this, col, row);
        		robot.setHP(MathUtils.random(2, 10));
        		robot.setAttackPoints(MathUtils.random(1, 5));
        		robot.addListener(this);
        		characters.add(robot);
        		charactersLayer.setCell(new MapCell(String.valueOf(robot.getId()), col, row, robot));
        	} else {
        		// L'emplacement est occupé, on réessaie
        		curBot--;
        	}
        }
	}
	
	/**
	 * Retourne le mur correspondant aux coordonnées indiquées.
	 * Si ce n'est pas un mur, retourne null.
	 * @param col
	 * @param row
	 * @return
	 */
	private RoomWalls getWall(int col, int row) {
		RoomWalls wall = null;
		if (col == 0) {
			wall = LEFT;
 		}
 		else if (col == mapWidth - 1) {
 			wall = RIGHT;
 		}
 		else if (row == 0) {
 			wall = TOP;
 		}
 		else if (row == mapHeight - 1) {
 			wall = BOTTOM;
 		}
		return wall;
	}



	private WorldElement createCommonDoor(int col, int row, int roomX, int roomY) {
		// Porte à gauche
		WorldElement element = null;
 		if (col == 0) {
 			element = new CommonDoor(Assets.commonDoor, col, row, this, LEFT, roomX - 1, roomY);
 		}
 		// Porte à droite
 		else if (col == mapWidth - 1) {
 			element = new CommonDoor(Assets.commonDoor, col, row, this, RIGHT, roomX + 1, roomY);
 		}
 		// Porte en haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			element = new CommonDoor(Assets.commonDoor, col, row, this, TOP, roomX, roomY + 1);
 		}
 		// Porte en bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			element = new CommonDoor(Assets.commonDoor, col, row, this, BOTTOM, roomX, roomY - 1);
 		}
 		return element;
	}



	@Override
	public void exitDungeon() {
		System.out.println("DungeonScreen.exitDungeon()");
	}

	/**
	 * Crée le HUD
	 */
	private void createHud() {
		hud = new HUD(this);
		player.addListener(hud);
		// Comme le Character a déjà été créé, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public Character getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        // Quand tout le monde a joué son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que ça ait changé.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}

	@Override
	public void render (float delta) {
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
        //stage.act(Gdx.graphics.getDeltaTime());
		characters.get(curCharacterPlaying).act(delta);
        
        // Dessine la scène et le hud
        stage.draw();
        hud.draw();
        
        fpsLogger.log();
	}

	@Override
	public void pause () {
		// TODO Sauvegarde de l'état courant
	}

	@Override
	public void resume () {
		// TODO Restauration de l'état précédent
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterDeath(Character character) {
		removeElement(character);
	}
}
