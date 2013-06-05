package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.model.data.ElementData.PATH_MARKER_DATA;

import java.util.List;

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
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.ElementData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class MapScreen extends AbstractMapScreen  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final MapArea[][] areas;
	private final Point currentRoom;
	
	private boolean isFirstDisplay;
	
	protected Player player;
	
	public MapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight) {
		super(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		UnmutablePoint entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// DBG Affichage du donjon en texte
		builder.printMap();
		
        // DBG Rustine pour r�ussir � centrer sur le joueur lors de l'affichage
        // de la toute premi�re pi�ce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute premi�re fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}
	
	/**
	 * Cr�e une repr�sentation physique (WorldActor) du joueur.
	 * @param hp
	 * @param att
	 */
	public void createPlayer(UnmutablePoint position) {
		player = new Player(QuesterGame.instance.getPlayerData(), position.getX(), position.getY());
        player.addListener(QuesterGame.instance);
	}
	
	public Player getPlayer() {
		return player;
	}

	/**
	 * Cr�e le HUD
	 */
	public void createHud(int miniMapWidth, int miniMapHeight) {
		hud = new HUD(player);
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			hud.setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
		
		// Ajout du HUD � la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute m�me en premier pour qu'il g�re les clics avant le reste du donjon.
		getStages().add(0, hud);
	}

	@Override
	public void render (float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOnPlayer();
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
        centerCameraOnPlayer();
	}

	private void createActor(int col, int row, ElementData data, MapLayer layer) {
		WorldActor actor = null;
		switch (data.element) {
		 	case CASTLE:
		 		actor = new Castle((CastleData)data, Assets.castle, col, row);
				break;
			case COMMON_DOOR:
		 		actor = new CommonDoor((PathData)data, Assets.commonDoor, col, row);
				break;
			case DUNGEON_ENTRANCE_DOOR:
				actor = new EntranceDoor((PathData)data, col, row);
				break;
		 	case DUNGEON_EXIT_DOOR:
				actor = new ExitDoor((PathData)data, col, row);
				break;
		 	case FOG:
				actor = new Ground(data, Assets.fog, col, row);
				break;
		 	case PATH_MARKER:
				actor = new Ground(data, Assets.pathMarker, col, row);
				break;
	 		case GRASS:
				actor = new Ground(data, Assets.grass, col, row);
				break;
	 		case GROUND:
				actor = new Ground(data, Assets.ground, col, row);
				break;
			case PATH_TO_REGION:
				actor = createPathToRegion((PathData)data, col, row);
				break;
			case ROBOT:
        		Robot robot = new Robot((CharacterData)data, col, row);
        		robot.getElementData().name = "Robot" + robot.getId();
        		robot.addListener(QuesterGame.instance);
        		actor = robot;
        		characters.add(robot);
        		break;
			case ROCK:
				actor = new WorldActor(data, Assets.rock, col, row);
				break;
	 		case VILLAGE:
				actor = new Village(data, Assets.village, col, row);
				break;
			case WALL:
				actor = new WorldActor(data, Assets.wall, col, row);
				break;
			case EMPTY:
			default:
				// Case vide ou avec une valeur inconnue: rien � faire :)
				return;
		}
		actor.setElementData(data);
		layer.setCell(new MapCell(String.valueOf(actor.getId()), col, row, actor));
		// Si cet �l�ment est solide et que la cellule �tait marqu�e comme walkable, elle ne l'est plus
		if (data.isSolid && screenMap.isWalkable(col, row)) {
			screenMap.setWalkable(col, row, false);
		}
	}

	private WorldActor createPathToRegion(PathData data, int col, int row) {
		// Chemin vers la gauche
		WorldActor element = null;
 		if (col == 0) {
 			element = new PathToRegion(data, Assets.pathLeft, col, row);
 		}
 		// Chemin vers la droite
 		else if (col == mapWidth - 1) {
 			element = new PathToRegion(data, Assets.pathRight, col, row);
 		}
 		// Chemin vers le haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			element = new PathToRegion(data, Assets.pathUp, col, row);
 		}
 		// Chemin vers le bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			element = new PathToRegion(data, Assets.pathDown, col, row);
 		}
 		return element;
	}

	@Override
	public void show() {
		super.show();
		// Centrage de la cam�ra sur le joueur
		// DBG Normalement le centerCameraOn() devrait �tre
		// suffisant pour centrer la cam�ra sur le joueur quand
		// on revient sur la carte du monde. Ca ne marche
		// malheureusement pas et on doit recourir encore
		// une fois � l'astuce du isFirstDisplay :(
		centerCameraOnPlayer();
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
	
	public void showPath(List<UnmutablePoint> path) {
		MapLayer overlayLayer = screenMap.getLayer(LAYER_OVERLAY);
		for (UnmutablePoint pos : path) {
	 		createActor(pos.getX(), pos.getY(), PATH_MARKER_DATA, overlayLayer);
		}
	}
	
	public void clearPath() {
		MapLayer overlayLayer = screenMap.getLayer(LAYER_OVERLAY);
		overlayLayer.clearLayer();
	}
	

	public void centerCameraOnPlayer() {
		centerCameraOn(player);
	}
}
