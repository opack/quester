package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.model.data.PathData;

public class Door extends PathToRegion {
	public Door(
		PathData data,
		TextureRegion texture,
		int col, int row) {
		super(data, texture, col, row);
	}
	
	/**
	 * Ouvre la porte et effectue l'action adéquate en fonction
	 * de ce qui se trouve derrière (une autre pièce, sortie du
	 * donjon...).
	 */
	public void open() {
		PathData data = (PathData)elementData;
		switch (data.element) {
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
}
