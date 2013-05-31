package com.slamdunk.quester.display.screens;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.map.logical.MapBuilder;
import com.slamdunk.quester.map.physical.MapLayer;

public class WorldMapScreen extends MapScreen {

	public WorldMapScreen(
			MapBuilder builder,
			int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight, 0, 0, 0);
	}

	@Override
	public void updateMapPosition(WorldActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		// Réalisation du déplacement
		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Si c'est le joueur qui a bougé, suppression du brouillard autour de sa nouvelle position
		if (actor.equals(getPlayer())) {
			MapLayer fog = screenMap.getLayer(LAYER_FOG);
			// Suppression du brouillard sur la ligne au-dessus du joueur
			fog.removeCell(newCol - 1, newRow + 1);
			fog.removeCell(newCol, newRow + 1);
			fog.removeCell(newCol + 1, newRow + 1);
			// Suppression du brouillard sur la même ligne que le joueur
			fog.removeCell(newCol - 1, newRow + 1);
			fog.removeCell(newCol, newRow);
			fog.removeCell(newCol + 1, newRow);
			// Suppression du brouillard sur la ligne au-dessous du joueur
			fog.removeCell(newCol - 1, newRow - 1);
			fog.removeCell(newCol, newRow - 1);
			fog.removeCell(newCol + 1, newRow - 1);
		}
	}
}
