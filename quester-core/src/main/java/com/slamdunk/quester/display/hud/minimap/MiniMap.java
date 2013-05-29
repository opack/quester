package com.slamdunk.quester.display.hud.minimap;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.points.Point;

public class MiniMap extends Table {
	protected final Drawable drawableUnvisited;
	protected final Drawable drawableVisited;
	protected final Drawable drawableCurrent;
	
	protected Image[][] images;
	protected Point currentPlayerRegion;
	
	private int mapWidth;
	private int mapHeight;
	
	public MiniMap(int mapWidth, int mapHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		currentPlayerRegion = new Point(-1, -1);
		
		drawableUnvisited = new TextureRegionDrawable(Assets.roomUnvisited);
		drawableVisited = new TextureRegionDrawable(Assets.roomVisited);
		drawableCurrent = new TextureRegionDrawable(Assets.roomCurrent);
		images = new Image[mapWidth][mapHeight];
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void init(int cellWidth, int cellHeight) {
		for (int row = mapHeight - 1; row >= 0; row--) {
			for (int col = 0; col < mapWidth; col++) {
				// Ajout d'une image repr�sentant une pi�ce non visit�e
				images[col][row] = new Image(drawableUnvisited);
				add(images[col][row]).size(cellWidth, cellHeight).pad(1);
			}
			row();
		}
		pack();
	}
	
	/**
	 * Indique o� se trouve le joueur. Cette m�thode va
	 * mettre en �vidence cette pi�ce sur la mini-carte.
	 * @param x
	 * @param y
	 */
	public void setPlayerRoom(int x, int y) {
		// Si la salle n'a pas chang�, on ne fait rien
		if (currentPlayerRegion.getX() == x && currentPlayerRegion.getY() == y) {
			return;
		}
		
		// L'actuelle salle o� se trouve le joueur n'est plus la playerRoom. On met � jour l'image
		// si on avait d�j� connaissance de l'emplacement du joueur
		if (currentPlayerRegion.getX() != -1 && currentPlayerRegion.getY() != -1) {
			images[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableVisited);
		}
		
		// La nouvelle playerRoom est celle indiqu�e
		currentPlayerRegion.setXY(x, y);
		images[x][y].setDrawable(drawableCurrent);
	}
}
