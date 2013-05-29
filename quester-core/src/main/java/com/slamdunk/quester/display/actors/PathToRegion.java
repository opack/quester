package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.screens.WorldDisplayData;

public class PathToRegion extends WorldActor {
	private final int destinationRegionX;
	private final int destinationRegionY;
	
	private boolean isCrossable;

	public PathToRegion(
		TextureRegion texture,
		int col, int row,
		GameWorld gameWorldListener,
		int destinationRegionX, int destinationRegionY) {
		super(texture, gameWorldListener, col, row);

		this.destinationRegionX = destinationRegionX;
		this.destinationRegionY = destinationRegionY;
		
		isCrossable = true;
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur de changer de salle
	        	PathToRegion.this.open();
	        }
		});
	}
	
	public boolean isCrossable() {
		return isCrossable;
	}

	public void setCrossable(boolean isCrossable) {
		this.isCrossable = isCrossable;
	}

	/**
	 * Franchit le chemin
	 */
	public void open() {
		WorldDisplayData data = new WorldDisplayData();
		data.regionX = destinationRegionX;
		data.regionY = destinationRegionY;
		
		// La porte est sur le mur du haut, le perso appara�tra donc dans la prochaine pi�ce en bas
		if (getWorldY() == map.getMapHeight() - 1) {
			data.playerX = getWorldX();
			data.playerY = 0;
		}
		// La porte est sur le mur du bas, le perso appara�tra donc dans la prochaine pi�ce en haut
		else if (getWorldY() == 0) {
			data.playerX = getWorldX();
			data.playerY = map.getMapHeight() - 1;
		}
		// La porte est sur le mur de gauche, le perso appara�tra donc dans la prochaine pi�ce � droite
		else if (getWorldX() == 0) {
			data.playerX =  map.getMapWidth() - 1;
			data.playerY = getWorldY();
		}
		// La porte est sur le mur de droite, le perso appara�tra donc dans la prochaine pi�ce � gauche
		else if (getWorldX() == map.getMapWidth() - 1) {
			data.playerX =  0;
			data.playerY = getWorldY();
		}
		world.displayWorld(data);
	}

	public int getDestinationRegionX() {
		return destinationRegionX;
	}

	public int getDestinationRegionY() {
		return destinationRegionY;
	}
}
