package com.slamdunk.quester.display.screens;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.ai.AI;
import com.slamdunk.quester.ai.PlayerIA;
import com.slamdunk.quester.ai.RobotIA;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.display.actors.Castle;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.CommonDoor;
import com.slamdunk.quester.display.actors.EntranceDoor;
import com.slamdunk.quester.display.actors.ExitDoor;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.Obstacle;
import com.slamdunk.quester.display.actors.PathToRegion;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Robot;
import com.slamdunk.quester.display.actors.Village;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.map.logical.CastleData;
import com.slamdunk.quester.map.logical.CharacterData;
import com.slamdunk.quester.map.logical.ElementData;
import com.slamdunk.quester.map.logical.MapArea;
import com.slamdunk.quester.map.logical.MapBuilder;
import com.slamdunk.quester.map.physical.MapCell;
import com.slamdunk.quester.map.physical.MapLayer;
import com.slamdunk.quester.map.points.Point;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class MapScreen extends AbstractMapScreen implements CharacterListener  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final MapArea[][] areas;
	private final Point currentRoom;
	
	private Player player;
	private int curCharacterPlaying;
	
	private boolean isFirstDisplay;
	
	public MapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight,
			int miniMapAreaWidth, int miniMapAreaHeight, int miniMapAreaThickness) {
		super(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		// Crée les pièces du donjon
		areas = builder.build();
		
		// DBG Affichage du donjon en texte
		for (int row = builder.getMapHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < builder.getMapWidth(); col++) {
				System.out.println("Room " + col + ";" + row);
				System.out.println(areas[col][row]);
			}
		}
		
		// Crée le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
				
		// Crée le hud
		createHud(miniMapAreaWidth, miniMapAreaHeight, miniMapAreaThickness);

        // Affiche la première pièce
        UnmutablePoint entrance = builder.getEntranceRoom();
        currentRoom = new Point(entrance.getX(), entrance.getY());
        UnmutablePoint entrancePosition = builder.getEntrancePosition();
        
        DisplayData data = new DisplayData();
        data.regionX = currentRoom.getX();
        data.regionY = currentRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        displayWorld(data);
        
        // Réordonne la liste d'ordre de jeu
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
        
        // DBG Rustine pour réussir à centrer sur le joueur lors de l'affichage
        // de la toute première pièce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute première fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}

	private void createPlayer() {
		AI ai = new PlayerIA();
		player = new Player("Player", ai, this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(3);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        player.addListener(this);
	}
	
	/**
	 * Crée le HUD
	 */
	private void createHud(int miniMapAreaWidth, int miniMapAreaHeight, int miniMapAreaThickness) {
		hud = new HUD(this);
		if (miniMapAreaWidth > 0
		&& miniMapAreaHeight > 0
		&& miniMapAreaThickness > 0) {
			hud.setMiniMap(areas, miniMapAreaWidth, miniMapAreaHeight, miniMapAreaThickness);
		}
		
		// Ajout du HUD à la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute même en premier pour qu'il gère les clics avant le reste du donjon.
		getStages().add(0, hud);
		player.addListener(hud);
		// Comme le Character a déjà été créé, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public void render (float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(player);
		}
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
		characters.get(curCharacterPlaying).act(delta);
		
        // Dessine la scène et le hud
        mainStage.draw();
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
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterDeath(Character character) {
		// On recherche l'indice du personnage à supprimer dans la liste
		int index = characters.indexOf(character);
		// Si le perso supprimé devait jouer après le joueur actuel (index > curCharacterPlaying),
		// alors l'ordre de jeu n'est pas impacté.
		// Si le perso supprimé devait jouer avant (index < curCharacterPlaying), alors l'ordre de
		// jeu est impacté car les indices changent. Si on ne fait rien, un joueur risque de passer
		// son tour.
		// Si le perso supprimé est le joueur actuel (index = curCharacterPlaying), alors le
		// raisonnement est le même
		if (index <= curCharacterPlaying) {
			curCharacterPlaying --;
		}
		
		// Suppression du character dans la liste et de la pièce
		removeElement(character);
		MapArea area = areas[currentRoom.getX()][currentRoom.getY()];
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(character.getElementData());
		}
		
		// Si c'est le joueur qui est mort, le jeu s'achève
		if (character.equals(player)) {
			MessageBox msg = MessageBoxFactory.createSimpleMessage("Bouh ! T'es mort !", hud);
			msg.show();
		}
	}
	
	/**
	 * Affiche la pièce à l'indice indiqué.
	 * @param roomX, roomY Coordonnées de la pièce dans le donjon
	 * @param entranceX, entranceY Coordonnées du joueur dans la pièce à son arrivée. Si -1,-1, les
	 * coordonnées sont mises à jour avec celles de la porte d'entrée du donjon (s'il y en a une).
	 */
	@Override
	public void displayWorld(Object data) {
		DisplayData display = (DisplayData)data;
		
		MapArea room = areas[display.regionX][display.regionY];
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer objectsLayer = screenMap.getLayer(LAYER_OBJECTS);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        MapLayer fogLayer = screenMap.getLayer(LAYER_FOG);
        
		// Nettoyage de la pièce actuelle
		clearMap();
        
		// La salle actuellement affichée a changé
		// Certains éléments (portes et chemins) ont besoin de connaître la position
		// de la salle courante. Il faut donc mettre à jour currentRoom avant de créer
		// les éléments.
        currentRoom.setXY(display.regionX, display.regionY);
        
        // Création du fond, des objets et du brouillard
	 	for (int col=0; col < room.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		createActor(col, row, room.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, room.getObjectAt(col, row), objectsLayer);
   		 		createActor(col, row, room.getFogAt(col, row), fogLayer);
   		 	}
        }

	 	// Création de la liste des personnages actifs et définit le premier de la liste
        // comme étant le prochain à jouer.
	 	player.setPositionInWorld(display.playerX, display.playerY);
        characters.add(player);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.playerX, display.playerY, player));
        
        // Création des personnages
        for (CharacterData character : room.getCharacters()) {
        	// Recherche d'une position aléatoire disponible
        	int col = -1;
        	int row = -1;
        	do {
	        	col = MathUtils.random(mapWidth - 1);
	        	row = MathUtils.random(mapHeight - 1);
        	} while (!screenMap.isEmptyAbove(0, col, row));
        	
        	// Création et placement de l'acteur
        	createActor(col, row, character, charactersLayer);
        }
        
        // Mise à jour du pad et de la minimap
        hud.update(display.regionX, display.regionY);
        
        // Centrage de la caméra sur le joueur
        centerCameraOn(player);
	}

	private void createActor(int col, int row, ElementData data, MapLayer layer) {
		WorldActor actor = null;
		switch (data.element) {
		 	case CASTLE:
				CastleData castleData = (CastleData)data;
				Castle castle = new Castle(Assets.castle, col, row, this);
				// TODO Ne pas mettre des valeurs en dur
				castle.setDungeonWidth(castleData.dungeonWidth);
				castle.setDungeonHeight(castleData.dungeonHeight);
				castle.setRoomWidth(castleData.roomWidth);
				castle.setRoomHeight(castleData.roomHeight);
				actor = castle;
				break;
			case COMMON_DOOR:
		 		actor = createCommonDoor(col, row, currentRoom.getX(), currentRoom.getY());
				break;
			case DUNGEON_ENTRANCE_DOOR:
				actor = new EntranceDoor(col, row, this);
				screenMap.setWalkable(col, row, false);
				break;
		 	case DUNGEON_EXIT_DOOR:
				actor = new ExitDoor(col, row, this);
				break;
		 	case FOG:
				actor = new Ground(Assets.fog, col, row, this);
				break;
	 		case GRASS:
				actor = new Ground(Assets.grass, col, row, this);
				break;
	 		case GROUND:
				actor = new Ground(Assets.ground, col, row, this);
				break;
			case PATH_TO_REGION:
				actor = createPathToRegion(col, row, currentRoom.getX(), currentRoom.getY());
				break;
			case ROBOT:
				CharacterData characterData = (CharacterData)data;
				AI ia = new RobotIA(player);
        		Robot robot = new Robot("Robot", ia, this, col, row);
        		robot.setHP(characterData.hp);
        		robot.setAttackPoints(characterData.att);
        		robot.addListener(this);
        		actor = robot;
        		characters.add(robot);
        		break;
			case ROCK:
				actor = new Obstacle(Assets.rock, col, row, this);
				screenMap.setWalkable(col, row, false);
				break;
	 		case VILLAGE:
				actor = new Village(Assets.village, col, row, this);
				break;
			case WALL:
				actor = new Obstacle(Assets.wall, col, row, this);
				screenMap.setWalkable(col, row, false);
				break;
			case EMPTY:
			default:
				// Case vide ou avec une valeur inconnue: rien à faire :)
				return;
		}
		actor.setElementData(data);
		layer.setCell(new MapCell(String.valueOf(actor.getId()), col, row, actor));
	}

	private WorldActor createCommonDoor(int col, int row, int curRoomX, int curRoomY) {
		// Porte à gauche
		WorldActor actor = null;
 		if (col == 0) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, this, curRoomX - 1, curRoomY);
 		}
 		// Porte à droite
 		else if (col == mapWidth - 1) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, this, curRoomX + 1, curRoomY);
 		}
 		// Porte en haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, this, curRoomX, curRoomY + 1);
 		}
 		// Porte en bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, this, curRoomX, curRoomY - 1);
 		}
 		return actor;
	}

	@Override
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
		// Mise à jour du pad et de la minimap
		hud.update(currentRoom.getX(), currentRoom.getY());
     	
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        
        // Quand tout le monde a joué son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que ça ait changé.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}
	
	private WorldActor createPathToRegion(int col, int row, int regionX, int regionY) {
		// Chemin vers la gauche
		WorldActor element = null;
 		if (col == 0) {
 			element = new PathToRegion(Assets.pathLeft, col, row, this, regionX - 1, regionY);
 		}
 		// Chemin vers la droite
 		else if (col == mapWidth - 1) {
 			element = new PathToRegion(Assets.pathRight, col, row, this, regionX + 1, regionY);
 		}
 		// Chemin vers le haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			element = new PathToRegion(Assets.pathUp, col, row, this, regionX, regionY + 1);
 		}
 		// Chemin vers le bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			element = new PathToRegion(Assets.pathDown, col, row, this, regionX, regionY - 1);
 		}
 		return element;
	}

	@Override
	public void show() {
		// DBG Normalement le centerCameraOn() dans le super.show
		// devrait être suffisant pour centrer la caméra sur le
		// joueur quand on revient sur la carte du monde. Ca ne
		// marche malheureusement pas et on doit recourir encore
		// une fois à l'astuce du isFirstDisplay :(
		super.show();
		isFirstDisplay = true;
	}
}
