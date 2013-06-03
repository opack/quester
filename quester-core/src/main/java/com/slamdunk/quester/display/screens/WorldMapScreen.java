package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.model.map.MapBuilder.EMPTY_DATA;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;

public class WorldMapScreen extends MapScreen {

	public WorldMapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight);
	}

	@Override
	public void updateMapPosition(WorldActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		// R�alisation du d�placement
		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Si c'est le joueur qui a boug�...
		if (actor.equals(player)) {
			// Suppression du brouillard autour de sa nouvelle position
			removeFog(newCol, newRow, 1);
			
			// D�termination de l'apparition d'un �v�nement
			if (Math.random() < 0.2) {
				chooseEvent();
			}
		}
	}

	/**
	 * Retire le brouillard autour de la position indiqu�e
	 * @param x
	 * @param y
	 * @param radius
	 */
	private void removeFog(int x, int y, int radius) {
		MapLayer fog = screenMap.getLayer(LAYER_FOG);
		MapArea area = getCurrentArea();		
		for (int row = y + radius; row >= y - radius; row--) {
			for (int col = x - radius; col <= x + radius; col++) {
				fog.removeCell(col, row);
				area.setFogAt(col, row, EMPTY_DATA);
			}
		}
	}
	
	/**
	 * Propose � l'utilisateur de choisir un �v�nement de fa�on
	 * al�atoire
	 */
	private void chooseEvent() {
		System.out.println("WorldMapScreen.chooseEvent() TODO");
		// TODO 
	}
}
