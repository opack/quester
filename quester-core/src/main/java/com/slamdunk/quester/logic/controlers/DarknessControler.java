package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.data.DarknessData;
import com.slamdunk.quester.model.data.WorldElementData;
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
		GameControler.instance.getMapScreen().getMap().setLight(actor.getWorldX(), actor.getWorldY(), true);
		
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
}
