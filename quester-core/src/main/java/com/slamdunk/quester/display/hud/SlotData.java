package com.slamdunk.quester.display.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.logic.ai.QuesterActions;

public class SlotData {
	QuesterActions action;
	float rate;
	Drawable drawable;

	public SlotData(QuesterActions action, float rate, TextureRegion image) {
		this.action = action;
		this.rate = rate;
		this.drawable = new TextureRegionDrawable(image);
	}
}