package com.slamdunk.quester.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.core.actors.Character;
import com.slamdunk.quester.core.actors.Ground;
import com.slamdunk.quester.core.actors.Obstacle;
import com.slamdunk.quester.core.actors.Player;
import com.slamdunk.quester.core.actors.Robot;
import com.slamdunk.quester.core.actors.WorldElement;
import com.slamdunk.quester.core.camera.MouseScrollZoomProcessor;
import com.slamdunk.quester.core.camera.TouchGestureListener;
import com.slamdunk.quester.core.pathfinding.UnmutablePoint;
import com.slamdunk.quester.core.screenmap.Cell;
import com.slamdunk.quester.core.screenmap.MapLayer;
import com.slamdunk.quester.core.screenmap.ScreenMap;

public class Quester implements ApplicationListener, GameWorld {
	/**
	 * Taille de l'affichage en pixels
	 */
	public final static int SCREEN_WIDTH = 480;
	public final static int SCREEN_HEIGHT = 800;
	/**
	 * Taille de la map en nombre de cellules
	 */
	public final static int MAP_WIDTH = 20;
	public final static int MAP_HEIGHT = 15;
	/**
	 * Taille d'une cellule (en pixels)
	 */
	public final static float WORLD_CELL_SIZE = 64;//SCREEN_WIDTH / MAP_WIDTH;
	/**
	 * Couches de la map
	 */
	private final static String LAYER_GROUND = "ground";
	private final static String LAYER_OBSTACLES = "obstacles";
	private final static String LAYER_CHARACTERS = "characters";
	
	// DBG Nombre de robots.
	private final static int NB_ROBOTS = 5;
	
	private OrthographicCamera camera;
	private Stage stage;
	private Character player;
	private ScreenMap screenMap;
	
	private List<WorldElement> characters;
	private int curCharacterPlaying;
	
	@Override
	public void create () {
		Assets.load();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.update();
		
		stage = new Stage();
		stage.setCamera(camera);
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new GestureDetector(new TouchGestureListener(camera, stage)));
		multiplexer.addProcessor(new MouseScrollZoomProcessor(camera));
		Gdx.input.setInputProcessor(multiplexer);
		
        screenMap = new ScreenMap(MAP_WIDTH, MAP_HEIGHT, WORLD_CELL_SIZE, WORLD_CELL_SIZE);
        stage.addActor(screenMap);
        
        // Crée une couche de fond
        MapLayer backgroundLayer = screenMap.addLayer(LAYER_GROUND);
	 	for (int col=0; col < MAP_WIDTH; col++) {
   		 	for (int row=0; row < MAP_HEIGHT; row++) {
   		 		float typeIndex = MathUtils.random();
   		 		WorldElement ground;
	   			if (typeIndex < 0.5) {
	   				ground = new Ground(Assets.sand, col, row, this);
	   			} else {
	   				ground = new Ground(Assets.grass, col, row, this);
	   			}
	   			backgroundLayer.setCell(new Cell(String.valueOf(ground.getId()), col, row, ground));
   		 	}
        }
        
        // Crée une couche avec les obstacles et personnages
        MapLayer obstaclesLayer = screenMap.addLayer(LAYER_OBSTACLES);
        for (int col=0; col < MAP_WIDTH; col++) {
   		 	for (int row=0; row < MAP_HEIGHT; row++) {
   		 		float typeIndex = MathUtils.random();
	   			if (typeIndex < 0.1) {
	   				WorldElement rock = new Obstacle(Assets.rock, col, row, this);
					obstaclesLayer.setCell(new Cell(String.valueOf(rock.getId()), col, row, rock));
					screenMap.setWalkable(col, row, false);
	   			}
   		 	}
        }
        
        // Crée la liste des personnages actifs et définit le premier de la liste
        // comme étant le prochain à jouer.
        characters = new ArrayList<WorldElement>();
        MapLayer charactersLayer = screenMap.addLayer(LAYER_CHARACTERS);
        
        player = new Player("Player", this, 0, 0);
        charactersLayer.setCell(new Cell(String.valueOf(player.getId()), 0, 0, player));
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        characters.add(player);
        
        for (int curBot = 0; curBot < NB_ROBOTS; curBot++){
        	int col = MathUtils.random(MAP_WIDTH - 1);
        	int row = MathUtils.random(MAP_HEIGHT - 1);
        	if (screenMap.isEmptyAbove(0, col, row)) {
        		WorldElement robot = new Robot("Robot" + curBot, this, col, row);
        		charactersLayer.setCell(new Cell(String.valueOf(robot.getId()), col, row, robot));
	   			characters.add(robot);
        	} else {
        		// L'emplacement est occupé, on réessaie
        		curBot--;
        	}
        }
        
        // Préparation de la liste des personnages actifs, classée par playRank croissant.
        // C'est parti ! Que le premier personnage (le joueur) joue ! :)
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}

	@Override
	public void resize (int width, int height) {
		stage.setViewport(SCREEN_WIDTH, SCREEN_HEIGHT, true);
	}

	@Override
	public void render () {
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
        //stage.act(Gdx.graphics.getDeltaTime());
		characters.get(curCharacterPlaying).act(Gdx.graphics.getDeltaTime());
        
        // Dessine la scène : appelle la méthode draw() des acteurs
        stage.draw();
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
	public Character getPlayer() {
		return player;
	}

	@Override
	public float getWorldCellSize() {
		return WORLD_CELL_SIZE;
	}

	@Override
	public WorldElement getTopElementAt(int col, int row) {
		Cell cell = screenMap.getTopElement(col, row);
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
	public WorldElement getObstacleAt(int col, int row) {
		Cell cell = screenMap.getTopElementAbove(0, col, row);
		if (cell == null) {
			return null;
		}
		return (WorldElement)cell.getActor();
	}

	@Override
	public void removeElement(WorldElement element) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(element.getId()));
		if (layer != null) {
			Cell removed = layer.removeCell(element.getWorldX(), element.getWorldY());
			characters.remove(removed.getActor());
		}
	}

	@Override
	public boolean isReachable(WorldElement pointOfView, WorldElement target, int weaponRange) {
		MapLayer layer = screenMap.getLayerContainingCell(String.valueOf(pointOfView.getId()));
		if (layer == null) {
			return false;
		}
		return layer.isInSight(
			pointOfView.getWorldX(), pointOfView.getWorldY(),
			target.getWorldX(), target.getWorldY(),
			weaponRange);
	}

	@Override
	public void endCurrentPlayerTurn() {
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        // Quand tout le monde a joué son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que ça ait changé.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}

	@Override
	public List<UnmutablePoint> findPath(WorldElement from, WorldElement to) {
		return findPath(from.getWorldX(), from.getWorldY(), to.getWorldX(), to.getWorldY());
	}

	@Override
	public List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY) {
		return screenMap.findPath(fromX, fromY, toX, toY, true);
	}
}
