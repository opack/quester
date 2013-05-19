package com.slamdunk.quester.screens;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.CharacterListener;
import com.slamdunk.quester.actors.Ground;
import com.slamdunk.quester.actors.Obstacle;
import com.slamdunk.quester.actors.Player;
import com.slamdunk.quester.actors.Robot;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;

public class DungeonScreen extends AbstractMapScreen implements CharacterListener  {
	// DBG Nombre de robots.
	private final static int NB_ROBOTS = 5;
	
	private Character player;
	private int curCharacterPlaying;
	
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	public DungeonScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		super(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
		
		// Remplit la carte
		createMap();
		
		// Crée le hud
		createHud();
		
        // C'est parti ! Que le premier personnage (le joueur) joue ! :)
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}

	/**
	 * Crée la carte en y ajoutant les différents objets du monde
	 */
	private void createMap() {
		// Crée une couche de fond
        MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
	 	for (int col=0; col < mapWidth; col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		float typeIndex = MathUtils.random();
   		 		WorldElement ground;
	   			if (typeIndex < 0.5) {
	   				ground = new Ground(Assets.sand, col, row, this);
	   			} else {
	   				ground = new Ground(Assets.grass, col, row, this);
	   			}
	   			backgroundLayer.setCell(new MapCell(String.valueOf(ground.getId()), col, row, ground));
   		 	}
        }
        
        // Crée une couche avec les obstacles et personnages
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        for (int col=0; col < mapWidth; col++) {
   		 	for (int row=0; row < mapHeight; row++) {
   		 		float typeIndex = MathUtils.random();
	   			if (typeIndex < 0.1) {
	   				WorldElement rock = new Obstacle(Assets.rock, col, row, this);
					obstaclesLayer.setCell(new MapCell(String.valueOf(rock.getId()), col, row, rock));
					screenMap.setWalkable(col, row, false);
	   			}
   		 	}
        }
        
        // Crée la liste des personnages actifs et définit le premier de la liste
        // comme étant le prochain à jouer.
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
        player = new Player("Player", this, 0, 0);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), 0, 0, player));
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        player.addListener(this);
        characters.add(player);
        
        for (int curBot = 0; curBot < NB_ROBOTS; curBot++){
        	int col = MathUtils.random(mapWidth - 1);
        	int row = MathUtils.random(mapHeight - 1);
        	if (screenMap.isEmptyAbove(0, col, row)) {
        		Robot robot = new Robot("Robot" + curBot, this, col, row);
        		robot.addListener(this);
        		charactersLayer.setCell(new MapCell(String.valueOf(robot.getId()), col, row, robot));
	   			characters.add(robot);
        	} else {
        		// L'emplacement est occupé, on réessaie
        		curBot--;
        	}
        }
	}

	/**
	 * Crée le HUD
	 */
	private void createHud() {
		hud = new HUD(this);
		player.addListener(hud);
		// Comme le Character a déjà été créé, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public Character getPlayer() {
		return player;
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
	public void render (float delta) {
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
        //stage.act(Gdx.graphics.getDeltaTime());
		characters.get(curCharacterPlaying).act(delta);
        
        // Dessine la scène et le hud
        stage.draw();
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
	public void show() {
		// TODO Auto-generated method stub
		
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
		removeElement(character);
	}
}
