package com.slamdunk.quester.display.hud.minimap;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.dungeon.DungeonRoom;
import com.slamdunk.quester.map.dungeon.RoomElements;
import com.slamdunk.quester.map.points.Point;

public class DungeonMiniMap extends MiniMap {
	private final Drawable drawableExit;
	private Point exitRoom;
	
	public DungeonMiniMap(int mapWidth, int mapHeight) {
		super(mapWidth, mapHeight);
		exitRoom = new Point(-1, -1);
		drawableExit = new TextureRegionDrawable(Assets.roomExit);
	}
	
	public void init(int miniRoomWidth, int miniRoomHeight, DungeonRoom[][] rooms) {
		// Initialisation standard
		super.init(miniRoomWidth, miniRoomHeight);
		
		// Recherche de la pi�ce de sortie
		for (int row = getMapHeight() - 1; row >= 0; row--) {
			for (int col = 0; col < getMapWidth(); col++) {
				// M�j des coordonn�es de la pi�ce de sortie
				if (rooms[col][row].containsDoor(RoomElements.DUNGEON_EXIT_DOOR)) {
					exitRoom.setXY(col, row);
					return;
				}
			}
		}
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
			if (currentPlayerRegion.getX() == exitRoom.getX() && currentPlayerRegion.getY() == exitRoom.getY()) {
				// On vient de quitter la pi�ce de sortie
				images[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableExit);
			} else {
				// On vient de quitter une pi�ce banale
				images[currentPlayerRegion.getX()][currentPlayerRegion.getY()].setDrawable(drawableVisited);
			}
		}
		
		// La nouvelle playerRoom est celle indiqu�e
		currentPlayerRegion.setXY(x, y);
		images[x][y].setDrawable(drawableCurrent);
	}
}
