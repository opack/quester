package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.dungeon.RoomElements;

public class Door extends PathToRegion {
	private final RoomElements type;
	
	public Door(
		TextureRegion texture,
		int col, int row,
		GameWorld gameWorldListener,
		int destinationRoomX, int destinationRoomY,
		RoomElements type) {
		super(texture, col, row, gameWorldListener, destinationRoomX, destinationRoomY);

		this.type = type;
	}
	
	/**
	 * Ouvre la porte et effectue l'action adéquate en fonction
	 * de ce qui se trouve derrière (une autre pièce, sortie du
	 * donjon...).
	 */
	public void open() {
		switch (type) {
			case DUNGEON_EXIT_DOOR:
				world.exit();
				break;
			case COMMON_DOOR:
				super.open();
				break;
			default:
				break;
		}
	}

	public RoomElements getType() {
		return type;
	}
}
