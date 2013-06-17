package com.slamdunk.quester.logic.controlers;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapRenderer;
import com.slamdunk.quester.model.data.DarknessData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapLevels;
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
		
		// Mise � jour du lightfinder
		GameControler.instance.getScreen().getMap().getPathfinder().setWalkable(actor.getWorldX(), actor.getWorldY(), true);
		
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
	 * Choisit l'action � effectuer en fonction de ce qui se trouve sous ce Darkness
	 */
	public void chooseAction() {
		final int x = actor.getWorldX();
		final int y = actor.getWorldY();
		final PlayerControler playerControler = GameControler.instance.getPlayer();
		final int playerX = playerControler.actor.getWorldX();
		final int playerY = playerControler.actor.getWorldY();
		final ActorMap map = GameControler.instance.getScreen().getMap();
		
		CharacterControler target = (CharacterControler)map.getControlerAt(x, y, MapLevels.CHARACTERS);
		boolean isHostilePresent = target != null && target.isHostile();
		boolean actionPlanned = false;
		// Si on est en phase d'attaque
		switch (GameControler.instance.getGamePhase()) {
			case ATTACK:
				// Attaque si on touche un ennemi � port�e
				if (isHostilePresent) {
					actionPlanned = playerControler.attack(target);
				}
				break;
			
			case LIGHT:
				// Si on est en phase d'�clairage et qu'on a touch� une zone sombre
				if (darknessData.torchCount == 0 && !isHostilePresent) {
					// Ajout d'une torche s'il existe un moyen de se rendre (virtuellement) � c�t� de cette case
					// pour y poser une torche
					List<UnmutablePoint> path = map.findPath(playerX, playerY, x, y, true);
					if (path != null) {
						actionPlanned = playerControler.placeTorch(this);
					}
				}
				break;
		}
		// Si on n'a rien fait d'autre et qu'on touche une case �clair�e joignable, on y va
		if (!actionPlanned) {
			List<UnmutablePoint> path = map.findPath(playerX, playerY, x, y, false);
			if (path != null) {
				actionPlanned = playerControler.moveTo(x, y);
			}
		}
	}
}
