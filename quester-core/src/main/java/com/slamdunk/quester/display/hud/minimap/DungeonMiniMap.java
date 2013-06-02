package com.slamdunk.quester.display.hud.minimap;

import static com.slamdunk.quester.map.logical.MapBuilder.DUNGEON_EXIT_DOOR_DATA;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.logical.MapArea;
import com.slamdunk.quester.map.points.Point;

public class DungeonMiniMap extends MiniMap {
	private final Drawable drawableExit;
	private Point exitRoom;
	
	public DungeonMiniMap(int mapWidth, int mapHeight) {
		super(mapWidth, mapHeight);
		exitRoom = new Point(-1, -1);
		drawableExit = new TextureRegionDrawable(Assets.roomExit);
	}
	
	public void init(int miniMapWidth, int miniMapHeight, MapArea[][] rooms) {
		// Initialisation standard
		super.init(miniMapWidth, miniMapHeight);
		
		// Recherche de la pièce de sortie
		for (int row = getMapHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < getMapWidth(); col++) {
				// Màj des coordonnées de la pièce de sortie
				if (rooms[col][row].containsPath(DUNGEON_EXIT_DOOR_DATA)) {
					exitRoom.setXY(col, row);
					return;
				}
			}
		}
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
			if (currentPlayerRegion.getX() == exitRoom.getX() && currentPlayerRegion.getY() == exitRoom.getY()) {
				// On vient de quitter la pièce de sortie
				areas[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableExit);
			} else {
				// On vient de quitter une pièce banale
				areas[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableVisited);
			}
		}
		
		// La nouvelle playerRoom est celle indiquée
		Point oldPlayerRegion = new Point(currentPlayerRegion);
		currentPlayerRegion.setXY(x, y);
		areas[x][y].setDrawable(drawableCurrent);
		
		// Mise à jour des chemins
		updatePaths(oldPlayerRegion, currentPlayerRegion);
	}
}
