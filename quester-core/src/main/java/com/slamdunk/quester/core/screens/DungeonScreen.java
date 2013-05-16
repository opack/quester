package com.slamdunk.quester.core.screens;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.actors.Character;
import com.slamdunk.quester.core.actors.Ground;
import com.slamdunk.quester.core.actors.Obstacle;
import com.slamdunk.quester.core.actors.Player;
import com.slamdunk.quester.core.actors.Robot;
import com.slamdunk.quester.core.actors.WorldElement;
import com.slamdunk.quester.core.screenmap.Cell;
import com.slamdunk.quester.core.screenmap.MapLayer;

public class DungeonScreen extends AbstractMapScreen  {
	// DBG Nombre de robots.
	private final static int NB_ROBOTS = 5;
	
	private Character player;
	private int curCharacterPlaying;
	
	public DungeonScreen(int mapWidth, int mapHeight, int worldCellWidth, int worldCellHeight) {
		super(mapWidth, mapHeight, worldCellWidth, worldCellHeight);
		
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
	   			backgroundLayer.setCell(new Cell(String.valueOf(ground.getId()), col, row, ground));
   		 	}
        }
        
        // Crée une couche avec les obstacles et personnages
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        for (int col=0; col < mapWidth; col++) {
   		 	for (int row=0; row < mapHeight; row++) {
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
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
        player = new Player("Player", this, 0, 0);
        charactersLayer.setCell(new Cell(String.valueOf(player.getId()), 0, 0, player));
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        characters.add(player);
        
        for (int curBot = 0; curBot < NB_ROBOTS; curBot++){
        	int col = MathUtils.random(mapWidth - 1);
        	int row = MathUtils.random(mapHeight - 1);
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
        
        // Dessine la scène : appelle la méthode draw() des acteurs
        stage.draw();
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
}
