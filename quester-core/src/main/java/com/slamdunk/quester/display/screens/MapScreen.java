package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.model.data.WorldElementData.PATH_MARKER_DATA;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.display.actors.ClipActor;
import com.slamdunk.quester.display.actors.DarknessActor;
import com.slamdunk.quester.display.actors.EntranceDoorActor;
import com.slamdunk.quester.display.actors.ExitDoorActor;
import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.ContextMenuControler;
import com.slamdunk.quester.logic.controlers.DarknessControler;
import com.slamdunk.quester.logic.controlers.DungeonDoorControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PathToAreaControler;
import com.slamdunk.quester.logic.controlers.RabiteControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class MapScreen extends AbstractMapScreen  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final MapArea[][] areas;
	private final Point currentRoom;
	
	private boolean isFirstDisplay;
	
	protected PlayerActor player;
	
	private List<UnmutablePoint> overlayPath;
	
	/**
	 * Musique � jouer sur cet �cran
	 */
	protected String backgroundMusic;
	
	public MapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight) {
		super(builder.getAreaWidth(), builder.getAreaHeight(), worldCellWidth, worldCellHeight);
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		UnmutablePoint entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// Cr�ation de la liste qui contiendra les WorldActor utilis�s pour l'affichage du chemin du joueur
		overlayPath = new ArrayList<UnmutablePoint>();
		
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
		player = new PlayerActor();
		player.setControler(GameControler.instance.getPlayer());
		player.setPositionInWorld(position.getX(), position.getY());
		
		GameControler.instance.getPlayer().setPathfinder(getMap().getLightPathfinder());
	}
	
	public PlayerActor getPlayerActor() {
		return player;
	}

	public String getBackgroundMusic() {
		return backgroundMusic;
	}

	public void setBackgroundMusic(String backgroundMusic) {
		this.backgroundMusic = backgroundMusic;
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
		
		// Mise � jour du menu contextuel, qui doit faire une belle animation pour appara�tre
		if (ContextMenuControler.openedMenu != null) {
			ContextMenuControler.openedMenu.act(delta);
		}
		
		// Le WorldElement dont le tour est en cours joue
		GameControler.instance.getCurrentCharacter().act(delta);
		
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
		Assets.playMusic(backgroundMusic);
		
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
	 	CharacterControler playerControler = player.getControler();
        characters.add(playerControler);
        charactersLayer.setCell(new MapCell(String.valueOf(playerControler.getId()), display.playerX, display.playerY, player));
        
        // Cr�ation des personnages
        for (CharacterData character : area.getCharacters()) {
        	// Recherche d'une position al�atoire disponible
        	int col = -1;
        	int row = -1;
        	do {
	        	col = MathUtils.random(mapWidth - 1);
	        	row = MathUtils.random(mapHeight - 1);
        	} while (!screenMap.isEmpty(LAYERS_OBSTACLES, col, row));
        	
        	// Cr�ation et placement de l'acteur
        	createActor(col, row, character, charactersLayer);
        }
        
        // Mise � jour du pad et de la minimap
        hud.update(display.regionX, display.regionY);
        
        // Centrage de la cam�ra sur le joueur
        centerCameraOnPlayer();
	}
	
//	 TODO Cr�er une m�thode createVisualEffect qui cr�e un ClipActor destin� � contenir
//	 un effet sp�cial, � le jouer et � dispara�tre.
//	 Cette m�thode servira pour la mort des personnages, les coups re�us, les sorts...
//	 Le code sera similaire � celui r�alis� dans CharacterControler.die().
//	 Les effets sp�ciaux seront r�pertori�s dans une table et conserv�s dans un cache
//	 pour �viter de les charger plusieurs fois. Plusieurs ClipActor pourront se servir
//	 du m�me Clip car la position du clip est mise � jour dans ClipActor au moment du dessin.
	public void createVisualEffect(String name, WorldElementActor target) {
		// R�cup�re le clip correspondant � cet effet visuel
		Clip clip = Assets.getVisualEffectClip(name);
		
		// Cr�ation d'un ClipActor pour pouvoir afficher le clip � l'�cran.
		// Le ClipActor est positionn� au m�me endroit que l'Actor qui va dispara�tre
		final ClipActor effect = new ClipActor();
		effect.clip = clip;
		if (target != null) {
			effect.setPosition(target.getX(), target.getY());
			effect.setSize(target.getWidth(), target.getHeight());
		}
		
		// Ajout du ClipActor � la couche d'overlay, pour que l'affichage reste coh�rent
		final MapLayer overlay = getLayer(LAYER_OVERLAY);		
		overlay.addActor(effect);
		
		// Placement du clip au milieu de la zone de dessin
		if (target != null) {
			clip.drawArea.width = target.getWidth();
			clip.drawArea.height = target.getHeight();
		} else {
			clip.drawArea.width = getCellWidth();
			clip.drawArea.height = getCellHeight();
		}
		clip.alignX = 0.5f;
		clip.alignY = 0.5f;
		
		// A la fin du clip, on supprime l'acteur
		clip.setLastKeyFrameRunnable(new Runnable(){
			@Override
			public void run() {
				// Une fois l'animation achev�e, on retire cet acteur
				overlay.removeActor(effect);
			}
		});
	}

	private void createActor(int col, int row, WorldElementData data, MapLayer layer) {
		WorldElementControler controler = null;
		switch (data.element) {
		 	case CASTLE:
		 		controler = new CastleControler(
		 			(CastleData)data, 
		 			new CastleActor(Assets.castle));		 		
				break;
			case COMMON_DOOR:
				controler = new DungeonDoorControler(
					(PathData)data, 
					new PathToAreaActor(Assets.commonDoor));
				screenMap.setLight(col, row, true);
		 		screenMap.setDark(col, row, false);
				break;
			case DUNGEON_ENTRANCE_DOOR:
				controler = new DungeonDoorControler(
					(PathData)data, 
					new EntranceDoorActor());
				screenMap.setLight(col, row, true);
		 		screenMap.setDark(col, row, false);
				break;
		 	case DUNGEON_EXIT_DOOR:
		 		controler = new DungeonDoorControler(
					(PathData)data, 
					new ExitDoorActor());
		 		screenMap.setLight(col, row, true);
		 		screenMap.setDark(col, row, false);
				break;
		 	case DARKNESS:
		 		controler = new DarknessControler(
					data, 
					new DarknessActor(Assets.darkness));
		 		screenMap.setLight(col, row, false);
		 		screenMap.setDark(col, row, true);
				break;
		 	case PATH_MARKER:
		 		controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.pathMarker));
				break;
	 		case GRASS:
	 			controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.grass));
				break;
	 		case GROUND:
	 			controler = new WorldElementControler(
					data, 
					new GroundActor(Assets.ground));
				break;
			case PATH_TO_REGION:
				controler = createPathToArea((PathData)data);
				break;
			case RABITE:
				RabiteControler rabite = new RabiteControler(
					(CharacterData)data, 
					new RabiteActor());
				rabite.addListener(GameControler.instance);
        		rabite.getData().name = "Robot" + rabite.getId();
        		rabite.setPathfinder(getMap().getDarknessPathfinder());
        		characters.add(rabite);
        		controler = rabite;
        		break;
			case ROCK:
				controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.rock));
				break;
	 		case VILLAGE:
	 			controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.village));
				break;
			case WALL:
				controler = new WorldElementControler(
					data, 
					new WorldElementActor(Assets.wall));
				screenMap.setLight(col, row, false);
		 		screenMap.setDark(col, row, false);
				break;
			case EMPTY:
			default:
				// Case vide ou avec une valeur inconnue: rien � faire :)
				return;
		}
		WorldElementActor actor = controler.getActor();
		actor.setControler(controler);
		actor.setPositionInWorld(col, row);
		
		layer.setCell(new MapCell(String.valueOf(controler.getId()), col, row, actor));
		// Si cet �l�ment est solide et que la cellule �tait marqu�e comme walkable, elle ne l'est plus
		if (data.isSolid && screenMap.isWalkable(col, row)) {
			screenMap.setWalkable(col, row, false);
		}
	}

	private PathToAreaControler createPathToArea(PathData data) {
		PathToAreaActor actor = null;
		switch (data.border) {
		case TOP:
			actor = new PathToAreaActor(Assets.pathUp);
			break;
		case BOTTOM:
			actor = new PathToAreaActor(Assets.pathDown);
			break;
		case LEFT:
			actor = new PathToAreaActor(Assets.pathLeft);
			break;
		case RIGHT:
			actor = new PathToAreaActor(Assets.pathRight);
			break;
		}
 		
 		return new PathToAreaControler(
			(PathData)data, 
			actor);
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
		
		// Lancement de la musique
		Assets.playMusic(backgroundMusic);
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
		updateHUD(currentArea.getX(), currentArea.getY());
	}
	
	public void updateHUD(int currentAreaX, int currentAreaY) {
		hud.update(currentAreaX, currentAreaY);
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
	 		overlayPath.add(pos);
		}
	}
	
	public void clearPath() {
		if (!overlayPath.isEmpty()) {
			MapLayer overlayLayer = screenMap.getLayer(LAYER_OVERLAY);
			for (UnmutablePoint pos : overlayPath) {
				overlayLayer.removeCell(pos.getX(), pos.getY());
			}
		}
	}
	
	public void clearOverlay() {
		MapLayer overlayLayer = screenMap.getLayer(LAYER_OVERLAY);
		overlayLayer.clearLayer();
	}
	
	public void centerCameraOnPlayer() {
		centerCameraOn(player);
	}

	public MapLayer getLayer(String layer) {
		return screenMap.getLayer(layer);
	}

	public WorldElementControler getControlerAt(int x, int y, String layerName) {
		MapLayer layer = screenMap.getLayer(layerName);
		MapCell cell = layer.getCell(x, y);
		if (cell == null) {
			return null;
		}
		return ((WorldElementActor)cell.getActor()).getControler();
	}
}
