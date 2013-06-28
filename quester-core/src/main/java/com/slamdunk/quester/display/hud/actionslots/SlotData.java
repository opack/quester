package com.slamdunk.quester.display.hud.actionslots;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.logic.ai.QuesterActions;

public class SlotData {
	public QuesterActions action;
	public float rate;
	public Drawable drawable;

	public SlotData(QuesterActions action, float rate, TextureRegion image) {
		this.action = action;
		this.rate = rate;
		this.drawable = new TextureRegionDrawable(image);
	}
}