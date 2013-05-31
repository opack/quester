package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.map.logical.MapBuilder.EMPTY_DATA;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.map.logical.MapArea;
import com.slamdunk.quester.map.logical.MapBuilder;
import com.slamdunk.quester.map.physical.MapLayer;

public class WorldMapScreen extends MapScreen {

	public WorldMapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight,
			int miniMapAreaWidth, int miniMapAreaHeight, int miniMapAreaThickness) {
		super(builder, worldCellWidth, worldCellHeight, miniMapAreaWidth, miniMapAreaHeight, miniMapAreaThickness);
	}

	@Override
	public void updateMapPosition(WorldActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		// Réalisation du déplacement
		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Si c'est le joueur qui a bougé...
		if (actor.equals(getPlayer())) {
			// Suppression du brouillard autour de sa nouvelle position
			removeFog(newCol, newRow, 1);
			
			// Détermination de l'apparition d'un évènement
			if (Math.random() < 0.2) {
				chooseEvent();
			}
		}
	}

	/**
	 * Retire le brouillard autour de la position indiquée
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
	 * Propose à l'utilisateur de choisir un évènement de façon
	 * aléatoire
	 */
	private void chooseEvent() {
		System.out.println("WorldMapScreen.chooseEvent() TODO");
		// TODO 
	}
}
