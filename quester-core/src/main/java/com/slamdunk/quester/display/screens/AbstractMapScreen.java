package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.core.Quester.SCREEN_HEIGHT;
import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.display.camera.TouchGestureListener;
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.map.ScreenMap;
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
	public static final int LEVEL_GROUND = 0;
	public static final int LEVEL_OBJECTS = 1;
	public static final int LEVEL_CHARACTERS = 2;
	public static final int LEVEL_FOG = 3;
	public static final int LEVEL_OVERLAY = 4;
	//DBG TODO Uitliser ce tableau plut�t que des indices de couches
	//public static final MapLayer[] LAYERS_OBSTACLES;
	
	protected final OrthographicCamera camera;
	protected final Stage mainStage;
	protected final ScreenMap screenMap;
	protected final List<Stage> stages;
	
	protected final InputMultiplexer inputMultiplexer;
	
	protected final List<WorldActor> characters;
	
	public AbstractMapScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		// Cr�ation de la carte
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.worldCellWidth = worldCellWidth;
		this.worldCellHeight = worldCellHeight;
        screenMap = new ScreenMap(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
        
        // Cr�e une couche de fond
        screenMap.addLayer(LAYER_GROUND);
        
        // Cr�e une couche avec les objets
        screenMap.addLayer(LAYER_OBJECTS);
        
        // Cr�e une couche avec les personnages
        screenMap.addLayer(LAYER_CHARACTERS);
        characters = new ArrayList<WorldActor>();
        
        // Cr�e une couche de brouillard
        screenMap.addLayer(LAYER_FOG);
        
        // Cr�e une couche avec diverses informations
        screenMap.addLayer(LAYER_OVERLAY);
        
        // Cr�ation de la cam�ra
 		camera = new OrthographicCamera();
 		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
 		camera.update();
 		
 		// Cr�ation du Stage
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
	public List<WorldActor> getCharacters() {
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
			stage.setViewport(SCREEN_WIDTH, SCREEN_HEIGHT, true);
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
	public WorldActor getTopElementAt(int col, int row) {
		return getTopElementBetween(-1, -1, col, row);
	}
	
	@Override
	public WorldActor getTopElementBetween(int aboveLevel, int belowLevel, int col, int row) {
		MapCell cell = screenMap.getTopElementBetween(aboveLevel, belowLevel, col, row);
		if (cell == null) {
			return null;
		}
		return (WorldActor)cell.getActor();
	}

	@Override
	public void updateMapPosition(WorldActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(actor.getId()));
		if (layer != null) {
			layer.moveCell(oldCol,  oldRow,  newCol, newRow, false);
		}
		// Mise � jour du pathfinder.
		// On part du principe qu'il n'y a qu'un seul objet solide)
		// par case. Du coup lorsqu'un objet est d�plac�, solide ou non,
		// son ancienne position est walkable.
		screenMap.setWalkable(oldCol, oldRow, true);
		// La walkability de la nouvelle position d�pend de l'acteur
		screenMap.setWalkable(newCol, newRow, !actor.getElementData().isSolid);
	}

	@Override
	public void removeElement(WorldActor actor) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(actor.getId()));
		if (layer != null) {
			MapCell removed = layer.removeCell(actor.getWorldX(), actor.getWorldY());
			for (Stage stage : stages) {
				stage.getActors().removeValue(removed.getActor(), true);
			}
		}
	}

	@Override
	public boolean isWithinRangeOf(WorldActor pointOfView, WorldActor target, int range) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(pointOfView.getId()));
		if (layer == null) {
			return false;
		}
		return layer.isInSight(
			pointOfView.getWorldX(), pointOfView.getWorldY(),
			target.getWorldX(), target.getWorldY(),
			range);
	}

	@Override
	public List<UnmutablePoint> findPath(WorldActor from, WorldActor to) {
		return findPath(from.getWorldX(), from.getWorldY(), to.getWorldX(), to.getWorldY());
	}

	@Override
	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY) {
		return screenMap.findPath(fromX, fromY, toX, toY, true);
	}

	@Override
	public void hide() {
		// DBG L'�cran n'est plus affich�. Il faut avoir sauvegard� avant !
		//dispose();
	}
	
	protected void enableInputListeners(boolean enable) {
		if (enable) {
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	@Override
	public void show() {
		// R�activation des listeners
		enableInputListeners(true);
	}
	
	@Override
	public void clearMap() {
		screenMap.clearMap();
		characters.clear();
	}
	
	@Override
	public void centerCameraOn(WorldActor actor) {
		camera.position.set(
			actor.getX() + actor.getWidth() / 2, 
			actor.getY() + actor.getHeight() / 2, 
			0);
	}
}
