package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.map.dungeon.RoomWalls.BOTTOM;
import static com.slamdunk.quester.map.dungeon.RoomWalls.LEFT;
import static com.slamdunk.quester.map.dungeon.RoomWalls.RIGHT;
import static com.slamdunk.quester.map.dungeon.RoomWalls.TOP;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.CommonDoor;
import com.slamdunk.quester.display.actors.EntranceDoor;
import com.slamdunk.quester.display.actors.ExitDoor;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.Obstacle;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Robot;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.ia.IA;
import com.slamdunk.quester.ia.PlayerIA;
import com.slamdunk.quester.ia.RobotIA;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;
import com.slamdunk.quester.map.dungeon.DungeonBuilder;
import com.slamdunk.quester.map.dungeon.DungeonRoom;
import com.slamdunk.quester.map.dungeon.RoomWalls;
import com.slamdunk.quester.map.points.Point;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class DungeonScreen extends AbstractMapScreen implements CharacterListener  {
	private Player player;
	private int curCharacterPlaying;
	
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final int dungeonWidth;
	private final int dungeonHeight;
	private final DungeonRoom[][] rooms;
	private final Point currentRoom;
	
	private boolean isFirstDisplay;
	
	public DungeonScreen(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight,
			int worldCellWidth, int worldCellHeight) {
		super(roomWidth, roomHeight, worldCellWidth, worldCellHeight);
		// Cr�e les pi�ces du donjon
		this.dungeonWidth = dungeonWidth;
		this.dungeonHeight = dungeonHeight;
		
		DungeonBuilder builder = createDungeonBuilder();
		rooms = builder.build();
		// DBG Affichage du donjon en texte
		for (int row = dungeonHeight - 1; row >= 0; row--) {
			for (int col = 0; col < dungeonWidth; col++) {
				System.out.println("Room " + col + ";" + row);
				System.out.println(rooms[col][row]);
			}
		}
		
		// Cr�e le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
				
		// Cr�e le hud
		createHud();

        // Affiche la premi�re pi�ce
        UnmutablePoint entrance = builder.getEntrance();
        currentRoom = new Point(entrance.getX(), entrance.getY());
        DungeonDisplayData data = new DungeonDisplayData();
        data.roomX = currentRoom.getX();
        data.roomY = currentRoom.getY();
        data.entranceX = -1;
        data.entranceY = -1;
        displayWorld(data);
        
        // R�ordonne la liste d'ordre de jeu
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
        
        // DBG Rustine pour r�ussir � centrer sur le joueur lors de l'affichage
        // de la toute premi�re pi�ce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute premi�re fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}

	private void createPlayer() {
		IA ia = new PlayerIA();
		player = new Player("Player", ia, this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(3);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier � jouer
        player.addListener(this);
	}
	
	private DungeonBuilder createDungeonBuilder() {
		DungeonBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight);
		builder.createRooms(getMapWidth(), getMapHeight());
		builder.placeMainGates();
		return builder;
	}
	
	/**
	 * Affiche la pi�ce � l'indice indiqu�.
	 * @param roomX, roomY Coordonn�es de la pi�ce dans le donjon
	 * @param entranceX, entranceY Coordonn�es du joueur dans la pi�ce � son arriv�e. Si -1,-1, les
	 * coordonn�es sont mises � jour avec celles de la porte d'entr�e du donjon (s'il y en a une).
	 */
	@Override
	public void displayWorld(Object data) {
		DungeonDisplayData display = (DungeonDisplayData)data;
		
		DungeonRoom room = rooms[display.roomX][display.roomY];
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
		// Nettoyage de la pi�ce actuelle
		clearMap();
        
        // Cr�ation du fond
        WorldActor element = null;
	 	for (int col=0; col < room.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		switch (room.get(col, row)) {
   		 			case EMPTY:
   		 				// Case vide : rien � faire :)
   		 				break;
		   		 	case DUNGEON_ENTRANCE_DOOR:
		   		 		// Pour faire joli, on met un mur sous la porte
		   		 		element = new Obstacle(Assets.wall, col, row, this);
		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				// On place � pr�sent la porte
		 				element = new EntranceDoor(col, row, this, getWall(col, row));
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				screenMap.setWalkable(col, row, false);
		 				// Si le joueur est arriv� dans cette pi�ce par l'entr�e du donjon
		 				// on conserve ces coordonn�es
		 				if (display.entranceX == -1 && display.entranceY == -1) {
		 					display.entranceX = col;
		 					display.entranceY = row;
		 				}
		 				break;
		   		 	case DUNGEON_EXIT_DOOR:
		   		 		// Pour faire joli, on met un mur sous la porte
		   		 		element = new Obstacle(Assets.wall, col, row, this);
		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				// On place � pr�sent la porte
		 				element = new ExitDoor(col, row, this, getWall(col, row));
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;
		   		 	case COMMON_DOOR:
		   		 		// Pour faire joli, on met un mur sous la porte
		   		 		element = new Obstacle(Assets.wall, col, row, this);
		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				// On place � pr�sent la porte
		   		 		element = createCommonDoor(col, row, display.roomX, display.roomY);
		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;  
   		 			case WALL:
   		 				element = new Obstacle(Assets.wall, col, row, this);
   		 				obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
   		 				screenMap.setWalkable(col, row, false);
		 				break;
   		 			case GROUND:
   		 				element = new Ground(Assets.ground, col, row, this);
   		 				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
		 				break;
   		 		}
   		 	}
        }

	 	// Cr�ation de la liste des personnages actifs et d�finit le premier de la liste
        // comme �tant le prochain � jouer.
	 	player.setPositionInWorld(display.entranceX, display.entranceY);
        characters.add(player);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.entranceX, display.entranceY, player));
        
        final int nbRobots = MathUtils.random(1, 5);
        for (int curBot = 0; curBot < nbRobots; curBot++){
        	int col = MathUtils.random(mapWidth - 1);
        	int row = MathUtils.random(mapHeight - 1);
        	if (screenMap.isEmptyAbove(0, col, row)) {
        		IA ia = new RobotIA(player);
        		Robot robot = new Robot("Robot" + curBot, ia, this, col, row);
        		robot.setHP(MathUtils.random(2, 10));
        		robot.setAttackPoints(MathUtils.random(1, 2));
        		robot.addListener(this);
        		characters.add(robot);
        		charactersLayer.setCell(new MapCell(String.valueOf(robot.getId()), col, row, robot));
        	} else {
        		// L'emplacement est occup�, on r�essaie
        		curBot--;
        	}
        }
        
        // La salle actuellement affich�e a chang�
        currentRoom.setXY(display.roomX, display.roomY);
        
        // Mise � jour du pad et de la minimap
     	hud.update(currentRoom.getX(), currentRoom.getY());
        
        // Centrage de la cam�ra sur le joueur
        centerCameraOn(player);
	}

	/**
	 * Retourne le mur correspondant aux coordonn�es indiqu�es.
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

	private WorldActor createCommonDoor(int col, int row, int roomX, int roomY) {
		// Porte � gauche
		WorldActor element = null;
 		if (col == 0) {
 			element = new CommonDoor(Assets.commonDoor, col, row, this, LEFT, roomX - 1, roomY);
 		}
 		// Porte � droite
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
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}

	/**
	 * Cr�e le HUD
	 */
	private void createHud() {
		hud = new HUD(this, rooms);
		// Ajout du HUD � la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute m�me en premier pour qu'il g�re les clics avant le reste du donjon.
		getStages().add(0, hud);
		player.addListener(hud);
		// Comme le Character a d�j� �t� cr��, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
		// Mise � jour du pad et de la minimap
		hud.update(currentRoom.getX(), currentRoom.getY());
     	
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        
        // Quand tout le monde a jou� son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que �a ait chang�.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}

	@Override
	public void render (float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(player);
		}
		// Efface l'�cran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
		characters.get(curCharacterPlaying).act(delta);
		
        // Dessine la sc�ne et le hud
        mainStage.draw();
        hud.draw();
        
        fpsLogger.log();
	}

	@Override
	public void pause () {
		// TODO Sauvegarde de l'�tat courant
	}

	@Override
	public void resume () {
		// TODO Restauration de l'�tat pr�c�dent
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
		// On recherche l'indice du personnage � supprimer dans la liste
		int index = characters.indexOf(character);
		// Si le perso supprim� devait jouer apr�s le joueur actuel (index > curCharacterPlaying),
		// alors l'ordre de jeu n'est pas impact�.
		// Si le perso supprim� devait jouer avant (index < curCharacterPlaying), alors l'ordre de
		// jeu est impact� car les indices changent. Si on ne fait rien, un joueur risque de passer
		// son tour.
		// Si le perso supprim� est le joueur actuel (index = curCharacterPlaying), alors le
		// raisonnement est le m�me
		if (index <= curCharacterPlaying) {
			curCharacterPlaying --;
		}
		
		// Suppression du character dans la liste
		removeElement(character);
		
		// Si c'est le joueur qui est mort, le jeu s'ach�ve
		if (character.equals(player)) {
			MessageBox msg = MessageBoxFactory.createSimpleMessage("Bouh ! T'es mort !", hud);
			msg.show();
		}
	}
}
