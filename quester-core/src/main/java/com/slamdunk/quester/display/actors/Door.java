package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.model.map.MapElements;

public class Door extends PathToRegion {
	private final MapElements type;
	
	public Door(
		TextureRegion texture,
		int col, int row,
		int destinationRoomX, int destinationRoomY,
		MapElements type) {
		super(texture, col, row, destinationRoomX, destinationRoomY);

		this.type = type;
	}
	
	/**
	 * Ouvre la porte et effectue l'action ad�quate en fonction
	 * de ce qui se trouve derri�re (une autre pi�ce, sortie du
	 * donjon...).
	 */
	public void open() {
		switch (type) {
			case DUNGEON_EXIT_DOOR:
				QuesterGame.instance.exit();
				break;
			case COMMON_DOOR:
				super.open();
				break;
			default:
				break;
		}
	}

	public MapElements getType() {
		return type;
	}
}
