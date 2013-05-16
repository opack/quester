package com.slamdunk.quester.screens;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.esotericsoftware.tablelayout.Cell;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.Ground;
import com.slamdunk.quester.actors.Obstacle;
import com.slamdunk.quester.actors.Player;
import com.slamdunk.quester.actors.Robot;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;

public class DungeonScreen extends AbstractMapScreen  {
	// DBG Nombre de robots.
	private final static int NB_ROBOTS = 5;
	
	private Character player;
	private int curCharacterPlaying;
	
	private Stage hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	public DungeonScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		super(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
		
		// Cr�e le hud
		createHud();
		
        // Remplit la carte
		createMap();
        
        // C'est parti ! Que le premier personnage (le joueur) joue ! :)
        curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}

	/**
	 * Cr�e la carte en y ajoutant les diff�rents objets du monde
	 */
	private void createMap() {
		// Cr�e une couche de fond
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
        
        // Cr�e une couche avec les obstacles et personnages
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
        
        // Cr�e la liste des personnages actifs et d�finit le premier de la liste
        // comme �tant le prochain � jouer.
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
        player = new Player("Player", this, 0, 0);
        charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), 0, 0, player));
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier � jouer
        characters.add(player);
        
        for (int curBot = 0; curBot < NB_ROBOTS; curBot++){
        	int col = MathUtils.random(mapWidth - 1);
        	int row = MathUtils.random(mapHeight - 1);
        	if (screenMap.isEmptyAbove(0, col, row)) {
        		WorldElement robot = new Robot("Robot" + curBot, this, col, row);
        		charactersLayer.setCell(new MapCell(String.valueOf(robot.getId()), col, row, robot));
	   			characters.add(robot);
        	} else {
        		// L'emplacement est occup�, on r�essaie
        		curBot--;
        	}
        }
	}

	/**
	 * Cr�e le HUD
	 */
	private void createHud() {
		hud = new Stage();
		
		Table table = new Table();
		table.add(new Image(Assets.heart)).height(64).width(64).fill();
		LabelStyle style = new LabelStyle();
		style.font = Assets.characterFont;
		table.add(new Label("150", style)).height(64).width(64).fill();
		table.pack();
		table.setPosition(0, table.getHeight());
		
		hud.addActor(table);
	}

	@Override
	public Character getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        // Quand tout le monde a jou� son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que �a ait chang�.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}

	@Override
	public void render (float delta) {
		// Efface l'�cran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
        //stage.act(Gdx.graphics.getDeltaTime());
		characters.get(curCharacterPlaying).act(delta);
        
        // Dessine la sc�ne : appelle la m�thode draw() des acteurs
        stage.draw();
        
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
	public void show() {
		// TODO Auto-generated method stub
		
	}
}