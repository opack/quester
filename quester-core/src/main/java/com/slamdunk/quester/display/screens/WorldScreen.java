package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;

public class WorldScreen extends GameScreen implements CharacterListener {

	public WorldScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		super(builder, worldCellWidth, worldCellHeight);
	}
	
	/**
	 * Propose à l'utilisateur de choisir un évènement de façon
	 * aléatoire
	 */
	private void chooseEvent() {
		System.out.println("WorldMapScreen.chooseEvent() TODO");
		// TODO 
	}
	
	@Override
	public void createPlayer(Point position) {
		super.createPlayer(position);
		
		// Ajout du screen en tant que listener : lorsque le joueur bouge, on
		// veut parfois lui proposer de choisir un évènement
		GameControler.instance.getPlayer().addListener(this);
	}

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCharacterDeath(CharacterControler character) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCharacterMoved(CharacterControler character, int oldX, int oldY) {
		// Si c'est le joueur qui a bougé et qu'on est bien sur la carte du monde...
		if (GameControler.instance.getScreen() == this
		&& character.getData().element == PLAYER) {
			// Détermination de l'apparition d'un évènement
			if (Math.random() < 0.2) {
				chooseEvent();
			}
		}
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
	}
}
