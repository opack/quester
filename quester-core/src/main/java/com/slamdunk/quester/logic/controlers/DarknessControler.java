package com.slamdunk.quester.logic.controlers;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.ScreenMap;
import com.slamdunk.quester.display.screens.AbstractMapScreen;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.data.DarknessData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class DarknessControler extends WorldElementControler {

	protected DarknessData darknessData;
	
	public DarknessControler(WorldElementData data, WorldElementActor actor) {
		super(data, actor);
	}

	@Override
	public void setData(WorldElementData data) {
		super.setData(data);
		darknessData = (DarknessData)data;
	}

	@Override
	public DarknessData getData() {
		return darknessData;
	}

	public void addTorch() {
		// Ajout d'une torche
		darknessData.torchCount++;
		
		// Mise à jour du lightfinder
		ScreenMap map = GameControler.instance.getMapScreen().getMap();
		map.setLight(actor.getWorldX(), actor.getWorldY(), true);
		map.setDark(actor.getWorldX(), actor.getWorldY(), false);
		
		// Modification de l'image
		updateImage();
	}

	private void updateImage() {
		if (darknessData.torchCount == 0) {
			actor.getImage().setDrawable(new TextureRegionDrawable(Assets.darkness));
		} else {
			actor.getImage().setDrawable(new TextureRegionDrawable(Assets.torch));
		}
	}

	/**
	 * Choisit l'action à effectuer en fonction de ce qui se trouve sous ce Darkness
	 */
	public void chooseAction() {
		final int x = actor.getWorldX();
		final int y = actor.getWorldY();
		final PlayerControler playerControler = GameControler.instance.getPlayer();
		final int playerX = playerControler.actor.getWorldX();
		final int playerY = playerControler.actor.getWorldY();
		final MapScreen mapScreen = GameControler.instance.getMapScreen();
		
		boolean actionPlanned = false;
		// Si on est en phase d'attaque
		switch (GameControler.instance.getGamePhase()) {
			case ATTACK:
				// Attaque si on touche un ennemi à portée
				WorldElementControler target = mapScreen.getControlerAt(x, y, AbstractMapScreen.LAYER_CHARACTERS);
				if (target instanceof Damageable) {
					actionPlanned = playerControler.attack(target);
				}
				break;
			
			case LIGHT:
				// Si on est en phase d'éclairage et qu'on a touché une zone sombre
				if (darknessData.torchCount == 0) {
					// Ajout d'une torche s'il existe un moyen de se rendre (virtuellement) à côté de cette case
					// pour y poser une torche
					List<UnmutablePoint> path = mapScreen.getMap().findLightPath(playerX, playerY, x, y, true);
					if (path != null) {
						actionPlanned = playerControler.placeTorch(this);
					}
				}
				break;
		}
		// Si on n'a rien fait d'autre et qu'on touche une case éclairée joignable, on y va
		if (!actionPlanned) {
			List<UnmutablePoint> path = mapScreen.getMap().findLightPath(playerX, playerY, x, y, false);
			if (path != null) {
				actionPlanned = playerControler.moveTo(x, y);
			}
		}
	}
}
