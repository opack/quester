package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.display.camera.TouchGestureListener;
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.map.ScreenMap;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.map.GameMap;
import com.slamdunk.quester.model.points.UnmutablePoint;

public abstract class AbstractMapScreen implements GameMap, GameScreen {
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
	protected final static String LAYER_GROUND = "ground";
	protected final static String LAYER_OBJECTS = "objects";
	protected final static String LAYER_CHARACTERS = "characters";
	protected final static String LAYER_FOG = "fog";
	protected final static String LAYER_OVERLAY = "overlay";
	public static int[] LAYERS_OBSTACLES;
	
	protected final OrthographicCamera camera;
	protected final Stage mainStage;
	protected final ScreenMap screenMap;
	protected final List<Stage> stages;
	
	protected final InputMultiplexer inputMultiplexer;
	
	protected final List<WorldElementControler> characters;
	
	public AbstractMapScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
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
        characters = new ArrayList<WorldElementControler>();
        
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
 		mainStage = new Stage();
 		mainStage.setCamera(camera);
 		mainStage.addActor(screenMap);
 		
 		stages = new ArrayList<Stage>();
 		stages.add(mainStage);
 		
 		inputMultiplexer = new InputMultiplexer();
 		inputMultiplexer.addProcessor(new GestureDetector(new TouchGestureListener(this)));
 		inputMultiplexer.addProcessor(new MouseScrollZoomProcessor(this));
 		enableInputListeners(true);
	}
	
	@Override
	public List<WorldElementControler> getCharacters() {
		return characters;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public List<Stage> getStages() {
		return stages;
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
	
	@Override
	public void resize (int width, int height) {
		for (Stage stage : stages) {
			stage.setViewport(screenWidth, screenHeight, true);
		}
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		for (Stage stage : stages) {
			stage.dispose();
		}
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
	public WorldElementActor getTopElementAt(int col, int row, int... layers) {
		MapCell cell = screenMap.getTopElementAt(col, row, layers);
		if (cell == null) {
			return null;
		}
		return (WorldElementActor)cell.getActor();
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
				// La walkability de la nouvelle position dépend de l'acteur
				screenMap.setWalkable(newCol, newRow, !controler.getData().isSolid);
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
				for (Stage stage : stages) {
					stage.getActors().removeValue(actor, true);
				}
				
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

	@Override
	public List<UnmutablePoint> findPath(WorldElementActor from, WorldElementActor to) {
		return findPath(from.getWorldX(), from.getWorldY(), to.getWorldX(), to.getWorldY());
	}

	@Override
	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY) {
		return screenMap.findPath(fromX, fromY, toX, toY, true);
	}

	@Override
	public void hide() {
		// DBG L'écran n'est plus affiché. Il faut avoir sauvegardé avant !
		//dispose();
	}
	
	protected void enableInputListeners(boolean enable) {
		if (enable) {
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	@Override
	public void show() {
		// Réactivation des listeners
		enableInputListeners(true);
	}
	
	@Override
	public void clearMap() {
		screenMap.clearMap();
		characters.clear();
	}
	
	@Override
	public void centerCameraOn(WorldElementActor actor) {
		camera.position.set(
			actor.getX() + actor.getWidth() / 2, 
			actor.getY() + actor.getHeight() / 2, 
			0);
	}
}
