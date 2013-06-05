package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.map.GameMap;

public class PathToRegion extends WorldElementActor {
	public PathToRegion(
		PathData data,
		TextureRegion texture,
		int col, int row) {
		super(data, texture, col, row);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur de changer de zone
	    		QuesterGame.instance.getPlayer().crossPath(PathToRegion.this);
	        }
		});
	}
	
	/**
	 * Franchit le chemin
	 */
	public void open() {
		PathData pathData = getElementData();
		DisplayData data = new DisplayData();
		data.regionX = pathData.toX;
		data.regionY = pathData.toY;
		
		GameMap map = QuesterGame.instance.getMapScreen();
		
		// La porte est sur le mur du haut, le perso apparaîtra donc dans la prochaine pièce en bas
		if (getWorldY() == map.getMapHeight() - 1) {
			data.playerX = getWorldX();
			data.playerY = 0;
		}
		// La porte est sur le mur du bas, le perso apparaîtra donc dans la prochaine pièce en haut
		else if (getWorldY() == 0) {
			data.playerX = getWorldX();
			data.playerY = map.getMapHeight() - 1;
		}
		// La porte est sur le mur de gauche, le perso apparaîtra donc dans la prochaine pièce à droite
		else if (getWorldX() == 0) {
			data.playerX =  map.getMapWidth() - 1;
			data.playerY = getWorldY();
		}
		// La porte est sur le mur de droite, le perso apparaîtra donc dans la prochaine pièce à gauche
		else if (getWorldX() == map.getMapWidth() - 1) {
			data.playerX =  0;
			data.playerY = getWorldY();
		}
		QuesterGame.instance.displayWorld(data);
	}
	
	@Override
	public PathData getElementData() {
		return (PathData)elementData;
	}
}
