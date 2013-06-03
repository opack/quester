package com.slamdunk.quester.display.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Castle;
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
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.model.ai.AI;
import com.slamdunk.quester.model.ai.RobotIA;
import com.slamdunk.quester.model.map.CastleData;
import com.slamdunk.quester.model.map.CharacterData;
import com.slamdunk.quester.model.map.ElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;

public class MapScreen extends AbstractMapScreen  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final MapArea[][] areas;
	private final Point currentRoom;
	
	private boolean isFirstDisplay;
	
	public MapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight) {
		super(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		currentRoom = new Point(-1, -1);
		
		// DBG Affichage du donjon en texte
		builder.printMap();
		
        // DBG Rustine pour r�ussir � centrer sur le joueur lors de l'affichage
        // de la toute premi�re pi�ce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute premi�re fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}

	/**
	 * Cr�e le HUD
	 */
	public void createHud(int miniMapWidth, int miniMapHeight) {
		hud = new HUD();
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			hud.setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
		
		// Ajout du HUD � la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute m�me en premier pour qu'il g�re les clics avant le reste du donjon.
		getStages().add(0, hud);

		// Comme le Character a d�j� �t� cr��, on initialise l'HUD
		//DBGhud.update();
	}

	@Override
	public void render (float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(QuesterGame.instance.getPlayer());
		}
		// Efface l'�cran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
		QuesterGame.instance.getCurrentCharacter().act(delta);
		
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

	/**
	 * Affiche la pi�ce � l'indice indiqu�.
	 * @param roomX, roomY Coordonn�es de la pi�ce dans le donjon
	 * @param entranceX, entranceY Coordonn�es du joueur dans la pi�ce � son arriv�e. Si -1,-1, les
	 * coordonn�es sont mises � jour avec celles de la porte d'entr�e du donjon (s'il y en a une).
	 */
	@Override
	public void displayWorld(DisplayData display) {
		MapArea area = areas[display.regionX][display.regionY];
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
	 	for (int col=0; col < area.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		createActor(col, row, area.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, area.getObjectAt(col, row), objectsLayer);
   		 		createActor(col, row, area.getFogAt(col, row), fogLayer);
   		 	}
        }

	 	// Cr�ation de la liste des personnages actifs et d�finit le premier de la liste
        // comme �tant le prochain � jouer.
	 	Player player = QuesterGame.instance.getPlayer();
	 	player.setPositionInWorld(display.playerX, display.playerY);
        characters.add(player);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.playerX, display.playerY, player));
        
        // Cr�ation des personnages
        for (CharacterData character : area.getCharacters()) {
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
				Castle castle = new Castle(Assets.castle, col, row);
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
				actor = new EntranceDoor(col, row);
				screenMap.setWalkable(col, row, false);
				break;
		 	case DUNGEON_EXIT_DOOR:
				actor = new ExitDoor(col, row);
				break;
		 	case FOG:
				actor = new Ground(Assets.fog, col, row);
				break;
	 		case GRASS:
				actor = new Ground(Assets.grass, col, row);
				break;
	 		case GROUND:
				actor = new Ground(Assets.ground, col, row);
				break;
			case PATH_TO_REGION:
				actor = createPathToRegion(col, row, currentRoom.getX(), currentRoom.getY());
				break;
			case ROBOT:
				CharacterData characterData = (CharacterData)data;
				AI ia = new RobotIA();
        		Robot robot = new Robot("Robot", ia, col, row);
        		robot.setHP(characterData.hp);
        		robot.setAttackPoints(characterData.att);
        		robot.addListener(QuesterGame.instance);
        		actor = robot;
        		characters.add(robot);
        		break;
			case ROCK:
				actor = new Obstacle(Assets.rock, col, row);
				screenMap.setWalkable(col, row, false);
				break;
	 		case VILLAGE:
				actor = new Village(Assets.village, col, row);
				break;
			case WALL:
				actor = new Obstacle(Assets.wall, col, row);
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
 			actor = new CommonDoor(Assets.commonDoor, col, row, curRoomX - 1, curRoomY);
 		}
 		// Porte � droite
 		else if (col == mapWidth - 1) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, curRoomX + 1, curRoomY);
 		}
 		// Porte en haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, curRoomX, curRoomY + 1);
 		}
 		// Porte en bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			actor = new CommonDoor(Assets.commonDoor, col, row, curRoomX, curRoomY - 1);
 		}
 		return actor;
	}

	private WorldActor createPathToRegion(int col, int row, int regionX, int regionY) {
		// Chemin vers la gauche
		WorldActor element = null;
 		if (col == 0) {
 			element = new PathToRegion(Assets.pathLeft, col, row, regionX - 1, regionY);
 		}
 		// Chemin vers la droite
 		else if (col == mapWidth - 1) {
 			element = new PathToRegion(Assets.pathRight, col, row, regionX + 1, regionY);
 		}
 		// Chemin vers le haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			element = new PathToRegion(Assets.pathUp, col, row, regionX, regionY + 1);
 		}
 		// Chemin vers le bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			element = new PathToRegion(Assets.pathDown, col, row, regionX, regionY - 1);
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
	
	/**
	 * Retourne la zone du monde aux coordonn�es indiqu�es
	 */
	public MapArea getArea(int x, int y) {
		return areas[x][y];
	}
	
	/**
	 * Retourne la zone du monde courante
	 */
	public MapArea getCurrentArea() {
		return areas[currentRoom.getX()][currentRoom.getY()];
	}

	@Override
	public void updateHUD(Point currentArea) {
		hud.update(currentArea.getX(), currentArea.getY());
	}

	@Override
	public MapArea getArea(Point currentArea) {
		return areas[currentArea.getX()][currentArea.getY()];
	}
	
	@Override
	public void showMessage(String message) {
		MessageBox msg = MessageBoxFactory.createSimpleMessage(message, hud);
		msg.show();
	}
}
