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
	protected final Drawable drawablePathUnknownVertical;
	protected final Drawable drawablePathUnknownHorizontal;
	protected final Drawable drawablePathExistsVertical;
	protected final Drawable drawablePathExistsHorizontal;
	
	protected Image[][] areas;
	protected Image[][] verticalPaths;
	protected Image[][] horizontalPaths;
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
		drawablePathUnknownVertical = new TextureRegionDrawable(Assets.pathUnknownVertical);
		drawablePathUnknownHorizontal = new TextureRegionDrawable(Assets.pathUnknownHorizontal);
		drawablePathExistsVertical = new TextureRegionDrawable(Assets.pathExistsVertical);
		drawablePathExistsHorizontal = new TextureRegionDrawable(Assets.pathExistsHorizontal);
		areas = new Image[mapWidth][mapHeight];
		horizontalPaths = new Image[mapWidth][mapHeight - 1];
		verticalPaths = new Image[mapWidth - 1][mapHeight];
		
		setBackground(new TextureRegionDrawable(Assets.minimapBackground));
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void init(int cellWidth, int cellHeight, int pathThickness) {
		for (int row = mapHeight - 1; row >= 0; row--) {
			for (int col = 0; col < mapWidth; col++) {
				// Ajout d'une image représentant une pièce non visitée
				areas[col][row] = new Image(drawableUnvisited);
				add(areas[col][row]).size(cellWidth, cellHeight);
				// Ajout d'une image représentant un chemin inconnu
				if (col < mapWidth - 1) {
					verticalPaths[col][row] = new Image(drawablePathUnknownVertical);
					add(verticalPaths[col][row]).size(pathThickness, cellHeight);
				}
			}
			row();
			if (row > 0) {
				// Ajout d'une ligne d'images représentant un chemin inconnu
				for (int col = 0; col < mapWidth; col++) {
					// Si on n'est pas sur la première colonne, on ajoute une cellule vide
					// avant pour que tout soit bien aligné avec les cellules
					if (col > 0) {
						add().size(pathThickness, pathThickness);
					}
					horizontalPaths[col][row - 1] = new Image(drawablePathUnknownHorizontal);
					add(horizontalPaths[col][row - 1]).size(cellWidth, pathThickness);
				}
				row();
			}
		}
		pack();
	}
	
	/**
	 * Indique où se trouve le joueur. Cette méthode va
	 * mettre en évidence cette pièce sur la mini-carte.
	 * @param x
	 * @param y
	 */
	public void setPlayerRoom(int x, int y) {
		// Si la salle n'a pas changé, on ne fait rien
		if (currentPlayerRegion.getX() == x && currentPlayerRegion.getY() == y) {
			return;
		}
		
		// L'actuelle salle où se trouve le joueur n'est plus la playerRoom. On met à jour l'image
		// si on avait déjà connaissance de l'emplacement du joueur
		if (currentPlayerRegion.getX() != -1 && currentPlayerRegion.getY() != -1) {
			areas[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableVisited);
		}
		
		// La nouvelle playerRoom est celle indiquée
		Point oldPlayerRegion = new Point(currentPlayerRegion);
		currentPlayerRegion.setXY(x, y);
		areas[x][y].setDrawable(drawableCurrent);
		
		// Mise à jour des chemins
		updatePaths(oldPlayerRegion, currentPlayerRegion);
	}

	protected void updatePaths(Point oldPlayerRegion, Point currentPlayerRegion2) {
		int pathPosition;
		if (oldPlayerRegion.getY() == currentPlayerRegion.getY()) {
			pathPosition = Math.min(oldPlayerRegion.getX(), currentPlayerRegion.getX());
			// Déplacement horizontal, donc le chemin est vertical
			verticalPaths[pathPosition][currentPlayerRegion.getY()].setDrawable(drawablePathExistsVertical);
		} else if (oldPlayerRegion.getX() == currentPlayerRegion.getX()) {
			pathPosition = Math.min(oldPlayerRegion.getY(), currentPlayerRegion.getY());
			// Déplacement vertical, donc le chemin est horizontal
			horizontalPaths[currentPlayerRegion.getX()][pathPosition].setDrawable(drawablePathExistsHorizontal);
		}
	}
}
