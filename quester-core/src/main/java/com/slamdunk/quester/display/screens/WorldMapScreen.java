package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.map.MapBuilder;

public class WorldMapScreen extends MapScreen {

	public WorldMapScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight);
	}
	
	@Override
	public void updateMapPosition(WorldElementActor actor, int oldCol, int oldRow, int newCol, int newRow) {
		// Réalisation du déplacement
		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Si c'est le joueur qui a bougé...
		if (actor.getControler().getData().element == PLAYER) {
			// Détermination de l'apparition d'un évènement
			if (Math.random() < 0.2) {
				chooseEvent();
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
