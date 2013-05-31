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
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		
		// DBG Affichage du donjon en texte
		for (int row = builder.getMapHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < builder.getMapWidth(); col++) {
				System.out.println("Room " + col + ";" + row);
				System.out.println(areas[col][row]);
			}
		}
		
		// Cr�e le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
				
		// Cr�e le hud
		createHud(miniMapAreaWidth, miniMapAreaHeight, miniMapAreaThickness);

        // Affiche la premi�re pi�ce
        UnmutablePoint entrance = builder.getEntranceRoom();
        currentRoom = new Point(entrance.getX(), entrance.getY());
        UnmutablePoint entrancePosition = builder.getEntrancePosition();
        
        DisplayData data = new DisplayData();
        data.regionX = currentRoom.getX();
        data.regionY = currentRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
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
		AI ai = new PlayerIA();
		player = new Player("Player", ai, this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(3);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier � jouer
        player.addListener(this);
	}
	
	/**
	 * Cr�e le HUD
	 */
	private void createHud(int miniMapAreaWidth, int miniMapAreaHeight, int miniMapAreaThickness) {
		hud = new HUD(this);
		if (miniMapAreaWidth > 0
		&& miniMapAreaHeight > 0
		&& miniMapAreaThickness > 0) {
			hud.setMiniMap(areas, miniMapAreaWidth, miniMapAreaHeight, miniMapAreaThickness);
		}
		
		// Ajout du HUD � la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute m�me en premier pour qu'il g�re les clics avant le reste du donjon.
		getStages().add(0, hud);
		player.addListener(hud);
		// Comme le Character a d�j� �t� cr��, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
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
		
		// Suppression du character dans la liste et de la pi�ce
		removeElement(character);
		MapArea area = areas[currentRoom.getX()][currentRoom.getY()];
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(character.getElementData());
		}
		
		// Si c'est le joueur qui est mort, le jeu s'ach�ve
		if (character.equals(player)) {
			MessageBox msg = MessageBoxFactory.createSimpleMessage("Bouh ! T'es mort !", hud);
			msg.show();
		}
	}
	
	/**
	 * Affiche la pi�ce � l'indice indiqu�.
	 * @param roomX, roomY Coordonn�es de la pi�ce dans le donjon
	 * @param entranceX, entranceY Coordonn�es du joueur dans la pi�ce � son arriv�e. Si -1,-1, les
	 * coordonn�es sont mises � jour avec celles de la porte d'entr�e du donjon (s'il y en a une).
	 */
	@Override
	public void displayWorld(Object data) {
		DisplayData display = (DisplayData)data;
		
		MapArea room = areas[display.regionX][display.regionY];
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer objectsLayer = screenMap.getLayer(LAYER_OBJECTS);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        MapLayer fogLayer = screenMap.getLayer(LAYER_FOG);
        
		// Nettoyage de la pi�ce actuelle
		clearMap();
        
		// La salle actuellement affich�e a chang�
		// Certains �l�ments (portes et chemins) ont besoin de conna�tre la position
		// de la salle courante. Il faut donc mettre � jour currentRoom avant de cr�er
		// les �l�ments.
        currentRoom.setXY(display.regionX, display.regionY);
        
        // Cr�ation du fond, des objets et du brouillard
	 	for (int col=0; col < room.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		createActor(col, row, room.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, room.getObjectAt(col, row), objectsLayer);
   		 		createActor(col, row, room.getFogAt(col, row), fogLayer);
   		 	}
        }

	 	// Cr�ation de la liste des personnages actifs et d�finit le premier de la liste
        // comme �tant le prochain � jouer.
	 	player.setPositionInWorld(display.playerX, display.playerY);
        characters.add(player);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.playerX, display.playerY, player));
        
        // Cr�ation des personnages
        for (CharacterData character : room.getCharacters()) {
        	// Recherche d'une position al�atoire disponible
        	int col = -1;
        	int row = -1;
        	do {
	        	col = MathUtils.random(mapWidth - 1);
	        	row = MathUtils.random(mapHeight - 1);
        	} while (!screenMap.isEmptyAbove(0, col, row));
        	
        	// Cr�ation et placement de l'acteur
        	createActor(col, row, character, charactersLayer);
        }
        
        // Mise � jour du pad et de la minimap
        hud.update(display.regionX, display.regionY);
        
        // Centrage de la cam�ra sur le joueur
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
				// Case vide ou avec une valeur inconnue: rien � faire :)
				return;
		}
		actor.setElementData(data);
		layer.setCell(new MapCell(String.valueOf(actor.getId()), col, row, actor));
	}

	private WorldActor createCommonDoor(int col, int row, int curRoomX, int curRoomY) {
		// Porte � gauche
		WorldActor actor = null;
 		if (col == 0) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, this, curRoomX - 1, curRoomY);
 		}
 		// Porte � droite
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
		// devrait �tre suffisant pour centrer la cam�ra sur le
		// joueur quand on revient sur la carte du monde. Ca ne
		// marche malheureusement pas et on doit recourir encore
		// une fois � l'astuce du isFirstDisplay :(
		super.show();
		isFirstDisplay = true;
	}
}
