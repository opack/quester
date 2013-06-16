package com.slamdunk.quester.display.screens;

import com.slamdunk.quester.model.map.MapBuilder;

public class DungeonScreen extends GameScreen {
	public DungeonScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight);
	}

//	@Override
//	public void updateMapPosition(WorldElementActor actor, int oldCol, int oldRow, int newCol, int newRow) {
//		// R�alisation du d�placement
//		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
//		
//		// Si c'est le joueur qui a boug�...
//		if (actor.getControler().getData().element == PLAYER) {
//			// Suppression du brouillard autour de sa nouvelle position
//			removeDarkness(newCol, newRow, 1);
//		}
//	}
//
//	/**
//	 * Retire le brouillard autour de la position indiqu�e
//	 * @param x
//	 * @param y
//	 * @param radius
//	 */
//	private void removeDarkness(int x, int y, int radius) {
//		MapLayer fog = screenMap.getLayer(LAYER_FOG);
//		MapArea area = getCurrentArea();
//		for (int row = y + radius; row >= y - radius; row--) {
//			for (int col = x - radius; col <= x + radius; col++) {
//				if (col < 0 || col >= getMapWidth()
//				|| row < 0 || row >= getMapHeight()) {
//					continue;
//				}
//				removeElementAt(fog, col, row);
//				area.setFogAt(col, row, EMPTY_DATA);
//				
//				// Met � jour la carte d'ombre
//				screenMap.setLight(col, row, true);
//			}
//		}
//	}
}
