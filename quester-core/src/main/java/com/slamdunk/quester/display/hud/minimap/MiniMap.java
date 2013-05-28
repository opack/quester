package com.slamdunk.quester.display.hud.minimap;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.map.dungeon.DungeonRoom;
import com.slamdunk.quester.map.dungeon.RoomElements;
import com.slamdunk.quester.map.points.Point;

public class MiniMap extends Table {
	private final Drawable drawableUnvisited;
	private final Drawable drawableVisited;
	private final Drawable drawableExit;
	private final Drawable drawableCurrent;
	
	private Image[][] images;
	private Point currentPlayerRoom;
	private Point exitRoom;
	
	public MiniMap(DungeonRoom[][] rooms, int miniRoomWidth, int miniRoomHeight) {
		currentPlayerRoom = new Point(-1, -1);
		exitRoom = new Point(-1, -1);
		
		drawableUnvisited = new TextureRegionDrawable(Assets.roomUnvisited);
		drawableVisited = new TextureRegionDrawable(Assets.roomVisited);
		drawableExit = new TextureRegionDrawable(Assets.roomExit);
		drawableCurrent = new TextureRegionDrawable(Assets.roomCurrent);
		images = new Image[rooms.length][rooms[0].length];
		
		build(rooms, miniRoomWidth, miniRoomHeight);
	}
	
	private void build(DungeonRoom[][] rooms, int miniRoomWidth, int miniRoomHeight) {
		for (int row = rooms[0].length - 1; row >= 0; row--) {
			for (int col = 0; col < rooms.length; col++) {
				// Ajout d'une image repr�sentant une pi�ce non visit�e
				images[col][row] = new Image(drawableUnvisited);
				add(images[col][row]).size(miniRoomWidth, miniRoomHeight).pad(1);
				// M�j des coordonn�es de la pi�ce de sortie
				if (rooms[col][row].containsDoor(RoomElements.DUNGEON_EXIT_DOOR)) {
					exitRoom.setXY(col, row);
				}
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
		if (currentPlayerRoom.getX() == x && currentPlayerRoom.getY() == y) {
			return;
		}
		
		// L'actuelle salle o� se trouve le joueur n'est plus la playerRoom. On met � jour l'image
		// si on avait d�j� connaissance de l'emplacement du joueur
		if (currentPlayerRoom.getX() != -1 && currentPlayerRoom.getY() != -1) {
			if (currentPlayerRoom.getX() == exitRoom.getX() && currentPlayerRoom.getY() == exitRoom.getY()) {
				// On vient de quitter la pi�ce de sortie
				images[currentPlayerRoom.getX()][currentPlayerRoom.getY()].setDrawable(drawableExit);
			} else {
				// On vient de quitter une pi�ce banale
				images[currentPlayerRoom.getX()][currentPlayerRoom.getY()].setDrawable(drawableVisited);
			}
		}
		
		// La nouvelle playerRoom est celle indiqu�e
		currentPlayerRoom.setXY(x, y);
		images[x][y].setDrawable(drawableCurrent);
	}
}
