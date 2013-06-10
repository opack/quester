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
		// R�alisation du d�placement
		super.updateMapPosition(actor, oldCol, oldRow, newCol, newRow);
		
		// Si c'est le joueur qui a boug�...
		if (actor.getControler().getData().element == PLAYER) {
			// D�termination de l'apparition d'un �v�nement
			if (Math.random() < 0.2) {
				chooseEvent();
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
