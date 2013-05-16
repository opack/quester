package com.slamdunk.quester.screens;

import static com.slamdunk.quester.core.Quester.SCREEN_HEIGHT;
import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.camera.TouchGestureListener;
import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;
import com.slamdunk.quester.map.ScreenMap;
import com.slamdunk.quester.pathfinding.UnmutablePoint;

public abstract class AbstractMapScreen implements Screen, GameWorld, GameMap {
	/**
	 * Taille de la map en nombre de cellules
	 */
	protected final int mapWidth;
	protected final int mapHeight;
	/**
	 * Taille d'une cellule (en pixels)
	 */
	protected final float worldCellWidth;//SCREEN_WIDTH / MAP_WIDTH;
	protected final float worldCellHeight;//SCREEN_HEIGHT / MAP_HEIGHT;
	/**
	 * Couches de la map
	 */
	protected final static String LAYER_GROUND = "ground";
	protected final static String LAYER_OBSTACLES = "obstacles";
	protected final static String LAYER_CHARACTERS = "characters";
	
	protected final OrthographicCamera camera;
	protected final Stage stage;
	protected final ScreenMap screenMap;
	
	protected final List<WorldElement> characters;
	
	public AbstractMapScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		// Création de la carte
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.worldCellWidth = worldCellWidth;
		this.worldCellHeight = worldCellHeight;
        screenMap = new ScreenMap(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
        
        // Crée une couche de fond
        screenMap.addLayer(LAYER_GROUND);
        
        // Crée une couche avec les obstacles
        screenMap.addLayer(LAYER_OBSTACLES);
        
        // Crée une couche avec les personnages
        screenMap.addLayer(LAYER_CHARACTERS);
        characters = new ArrayList<WorldElement>();
        
        // Création de la caméra
 		camera = new OrthographicCamera();
 		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
 		camera.update();
 		
 		// Création du Stage
 		stage = new Stage();
 		stage.setCamera(camera);
 		stage.addActor(screenMap);
 		
 		InputMultiplexer multiplexer = new InputMultiplexer();
 		multiplexer.addProcessor(new GestureDetector(new TouchGestureListener(this)));
 		multiplexer.addProcessor(new MouseScrollZoomProcessor(this));
 		Gdx.input.setInputProcessor(multiplexer);
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
	
	@Override
	public GameMap getMap() {
		return this;
	}

	@Override
	public void resize (int width, int height) {
		stage.setViewport(SCREEN_WIDTH, SCREEN_HEIGHT, true);
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		stage.dispose();
	}

	@Override
	public WorldElement getTopElementAt(int col, int row) {
		return getTopElementAt(-1, col, row);
	}
	
	@Override
	public WorldElement getTopElementAt(int aboveLevel, int col, int row) {
		MapCell cell = screenMap.getTopElementAbove(aboveLevel, col, row);
		if (cell == null) {
			return null;
		}
		return (WorldElement)cell.getActor();
	}

	@Override
	public void updateMapPosition(WorldElement element, int oldCol, int oldRow, int newCol, int newRow) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(element.getId()));
		if (layer != null) {
			layer.moveCell(oldCol,  oldRow,  newCol, newRow, false);
		}
	}

	@Override
	public void removeElement(WorldElement element) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(element.getId()));
		if (layer != null) {
			MapCell removed = layer.removeCell(element.getWorldX(), element.getWorldY());
			characters.remove(removed.getActor());
		}
	}

	@Override
	public boolean isWithinRangeOf(WorldElement pointOfView, WorldElement target, int range) {
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
	public List<UnmutablePoint> findPath(WorldElement from, WorldElement to) {
		return findPath(from.getWorldX(), from.getWorldY(), to.getWorldX(), to.getWorldY());
	}

	@Override
	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY) {
		return screenMap.findPath(fromX, fromY, toX, toY, true);
	}

	@Override
	public void hide() {
		// DBG L'écran n'est plus affiché. Il faut avoir sauvegardé avant !
		dispose();
	}
}
