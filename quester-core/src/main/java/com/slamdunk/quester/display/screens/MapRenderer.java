package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;
import static com.slamdunk.quester.model.data.WorldElementData.PATH_MARKER_DATA;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.display.actors.ClipActor;
import com.slamdunk.quester.display.actors.DarknessActor;
import com.slamdunk.quester.display.actors.EntranceDoorActor;
import com.slamdunk.quester.display.actors.ExitDoorActor;
import com.slamdunk.quester.display.actors.GroundActor;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.display.camera.TouchGestureListener;
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.map.ScreenMap;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.DarknessControler;
import com.slamdunk.quester.logic.controlers.DungeonDoorControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PathToAreaControler;
import com.slamdunk.quester.logic.controlers.RabiteControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.map.GameMap;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class MapRenderer implements GameMap {
	/**
	 * Taille de la map en nombre de cellules
	 */
	protected final int mapWidth;
	protected final int mapHeight;
	/**
	 * Taille d'une cellule (en pixels)
	 */
	protected final float worldCellWidth;
	protected final float worldCellHeight;
	/**
	 * Couches de la map
	 */
	public final static String LAYER_GROUND = "ground";
	public final static String LAYER_OBJECTS = "objects";
	public final static String LAYER_CHARACTERS = "characters";
	public final static String LAYER_FOG = "fog";
	public final static String LAYER_OVERLAY = "overlay";
	public static int[] LAYERS_OBSTACLES;
	
	protected final OrthographicCamera camera;
	protected final Stage stage;
	protected final ScreenMap screenMap;
	
	protected final InputMultiplexer inputMultiplexer;
	
	protected final List<CharacterControler> characters;
	
	private List<UnmutablePoint> overlayPath;
	
	private AStar pathfinder;
	
	public MapRenderer(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		// Création de la carte
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.worldCellWidth = worldCellWidth;
		this.worldCellHeight = worldCellHeight;
		
        screenMap = new ScreenMap(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
        
        // Crée une couche de fond
        screenMap.addLayer(LAYER_GROUND);
        
        // Crée une couche avec les objets
        MapLayer layerObjects = screenMap.addLayer(LAYER_OBJECTS);
        
        // Crée une couche avec les personnages
        MapLayer layerCharacters = screenMap.addLayer(LAYER_CHARACTERS);
        characters = new ArrayList<CharacterControler>();
        
        // Crée une couche de brouillard
        screenMap.addLayer(LAYER_FOG);
        
        // Crée une couche avec diverses informations
        screenMap.addLayer(LAYER_OVERLAY);
        
        // Crée un tableau regroupant les couches pouvant contenir des obstacles, du plus haut au plus bas
        LAYERS_OBSTACLES = new int[]{layerCharacters.getLevel(), layerObjects.getLevel()};
        
        // Création de la caméra
 		camera = new OrthographicCamera();
 		camera.setToOrtho(false, screenWidth, screenHeight);
 		camera.update();
 		
 		// Création du Stage
 		stage = new Stage();
 		stage.setCamera(camera);
 		stage.addActor(screenMap);
 		
 		inputMultiplexer = new InputMultiplexer();
 		inputMultiplexer.addProcessor(new GestureDetector(new TouchGestureListener(this)));
 		inputMultiplexer.addProcessor(new MouseScrollZoomProcessor(this));
 		enableInputListeners(true);
 		
 		// Création de la liste qui contiendra les WorldActor utilisés pour l'affichage du chemin du joueur
		overlayPath = new ArrayList<UnmutablePoint>();

		// Création du pathfinder
		pathfinder = new AStar(mapWidth, mapHeight);
	}
	
	@Override
	public List<CharacterControler> getCharacters() {
		return characters;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public Stage getStage() {
		return stage;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}
	
	public float getCellWidth() {
		return worldCellWidth;
	}

	public float getCellHeight() {
		return worldCellHeight;
	}
	
	public void dispose () {
		stage.dispose();
	}

	@Override
	public WorldElementActor getTopElementAt(int col, int row) {
		MapCell cell = screenMap.getTopElementAt(col, row);
		if (cell == null) {
			return null;
		}
		return (WorldElementActor)cell.getActor();
	}
	
	@Override
	public WorldElementActor getTopElementAt(int col, int row, int... layerLevels) {
		MapCell cell = screenMap.getTopElementAt(col, row, layerLevels);
		if (cell == null) {
			return null;
		}
		return (WorldElementActor)cell.getActor();
	}
	
	public WorldElementActor getTopElementAt(int col, int row, String... layerIds) {
		MapCell cell = screenMap.getTopElementAt(col, row, layerIds);
		if (cell == null) {
			return null;
		}
		return (WorldElementActor)cell.getActor();
	}
	
	@Override
	public List<WorldElementActor> getElementsAt(int x, int y) {
		final List<WorldElementActor> actors = new ArrayList<WorldElementActor>();
		MapCell cell;
		for (MapLayer layer : screenMap.getLayersByLevel()) {
			cell = layer.getCell(x, y);
			if (cell != null) {
				actors.add((WorldElementActor)cell.getActor());
			}
		}
		return actors;
	}

	@Override
	public void updateMapPosition(WorldElementActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		WorldElementControler controler = actor.getControler();
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(controler.getId()));
		if (layer != null) {
			layer.moveCell(oldCol,  oldRow,  newCol, newRow, false);
			// Mise à jour du pathfinder si l'objet appartenait à une couche d'obstacles
			if (containsObstacles(layer.getLevel())) {
				// On part du principe qu'il n'y a qu'un seul objet solide)
				// par case. Du coup lorsqu'un objet est déplacé, solide ou non,
				// son ancienne position est walkable.
				screenMap.setWalkable(oldCol, oldRow, true);
				// Concernant les lumières, c'est plus compliqué : le contrôleur à cet emplacement définit cette valeur
				DarknessActor darkActor = (DarknessActor)getTopElementAt(oldCol, oldRow, LAYER_FOG);
				if (darkActor != null) {
					DarknessControler darkControler = (DarknessControler)darkActor.getControler();
					boolean isLit = darkControler.getData().torchCount > 0;
					screenMap.setLight(oldCol, oldRow, isLit);
					screenMap.setDark(oldCol, oldRow, !isLit);
				}
				// La walkability de la nouvelle position dépend de l'acteur.
				// Pour les lumières et ombre c'est identique : la cellule n'est pas traversable
				// si l'acteur est solide.
				boolean isWalkable = !controler.getData().isSolid;
				screenMap.setWalkable(newCol, newRow, isWalkable);
				screenMap.setLight(oldCol, oldRow, isWalkable);
				screenMap.setDark(oldCol, oldRow, !isWalkable);
			}
		}
	}

	/**
	 * Retourne true si la couche à ce niveau peut contenir des obstacles
	 */
	private boolean containsObstacles(int level) {
		for (int obstacleLayer : LAYERS_OBSTACLES) {
			if (level == obstacleLayer) {
				return true;
			}
		}
		return false;
	}

	@Override
	public WorldElementActor removeElement(WorldElementActor actor) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(actor.getControler().getId()));
		if (layer != null) {
			return removeElementAt(layer, actor.getWorldX(), actor.getWorldY());
		}
		return null;
	}
	
	public WorldElementActor removeElementAt(MapLayer layer, int x, int y) {
		if (layer != null) {
			MapCell removed = layer.removeCell(x, y);
			if (removed != null) {
				WorldElementActor actor = (WorldElementActor)removed.getActor();
				stage.getActors().removeValue(actor, true);
				
				// Met à jour le pathfinder. Si l'élément était solide,
				// alors sa disparition rend l'emplacement walkable.
				if (actor.getControler().getData().isSolid) {
					screenMap.setWalkable(actor.getWorldX(), actor.getWorldY(), true);
				}
				return actor;
			}			
		}
		return null;
	}

	@Override
	public boolean isWithinRangeOf(WorldElementActor pointOfView, WorldElementActor target, int range) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(pointOfView.getControler().getId()));
		if (layer == null) {
			return false;
		}
		return layer.isInSight(
			pointOfView.getWorldX(), pointOfView.getWorldY(),
			target.getWorldX(), target.getWorldY(),
			range);
	}

	public ScreenMap getMap() {
		return screenMap;
	}

	public void enableInputListeners(boolean enable) {
		if (enable) {
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	@Override
	public void clearMap() {
		screenMap.clearMap();
		characters.clear();
	}
	
	public void addCharacter(CharacterControler character) {
		characters.add(character);
		MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        charactersLayer.setCell(new MapCell(String.valueOf(character.getId()), character.getActor().getWorldX(), character.getActor().getWorldY(), character.getActor()));
	}
	

	public void buildMap(MapArea area, Point currentRoom) {
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer objectsLayer = screenMap.getLayer(LAYER_OBJECTS);
        MapLayer fogLayer = screenMap.getLayer(LAYER_FOG);
        
		// Nettoyage de la pièce actuelle
		clearMap();
        
        // Création du fond, des objets et du brouillard
	 	for (int col=0; col < area.getWidth(); col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		createActor(col, row, area.getGroundAt(col, row), backgroundLayer);
   		 		createActor(col, row, area.getObjectAt(col, row), objectsLayer);
   		 		createActor(col, row, area.getFogAt(col, row), fogLayer);
   		 	}
        }
	}
	
//	 TODO Créer une méthode createVisualEffect qui crée un ClipActor destiné à contenir
//	 un effet spécial, à le jouer et à disparaître.
//	 Cette méthode servira pour la mort des personnages, les coups reçus, les sorts...
//	 Le code sera similaire à celui réalisé dans CharacterControler.die().
//	 Les effets spéciaux seront répertoriés dans une table et conservés dans un cache
//	 pour éviter de les charger plusieurs fois. Plusieurs ClipActor pourront se servir
//	 du même Clip car la position du clip est mise à jour dans ClipActor au moment du dessin.
	public void createVisualEffect(String name, WorldElementActor target) {
		// Récupère le clip correspondant à cet effet visuel
		Clip clip = Assets.getVisualEffectClip(name);
		
		// Création d'un ClipActor pour pouvoir afficher le clip à l'écran.
		// Le ClipActor est positionné au même endroit que l'Actor qui va disparaître
		final ClipActor effect = new ClipActor();
		effect.clip = clip;
		if (target != null) {
			effect.setPosition(target.getX(), target.getY());
			effect.setSize(target.getWidth(), target.getHeight());
		}
		
		// Ajout du ClipActor à la couche d'overlay, pour que l'affichage reste cohérent
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
				// Une fois l'animation achevée, on retire cet acteur
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
				// Case vide ou avec une valeur inconnue: rien à faire :)
				return;
		}
		WorldElementActor actor = controler.getActor();
		actor.setControler(controler);
		actor.setPositionInWorld(col, row);
		
		layer.setCell(new MapCell(String.valueOf(controler.getId()), col, row, actor));
		// Si cet élément est solide et que la cellule était marquée comme walkable, elle ne l'est plus
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

	public void render() {
		stage.draw();
	}

	public void createCharacters(MapArea area) {
		MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
		
		// Création des personnages
        for (CharacterData character : area.getCharacters()) {
        	// Recherche d'une position aléatoire disponible
        	int col = -1;
        	int row = -1;
        	do {
	        	col = MathUtils.random(mapWidth - 1);
	        	row = MathUtils.random(mapHeight - 1);
        	} while (!screenMap.isEmpty(LAYERS_OBSTACLES, col, row));
        	
        	// Création et placement de l'acteur
        	createActor(col, row, character, charactersLayer);
        }
	}

	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY) {
		return pathfinder.findPath(fromX, fromY, toX, toY);
	}

	public AStar getPathfinder() {
		return pathfinder;
	}
}
