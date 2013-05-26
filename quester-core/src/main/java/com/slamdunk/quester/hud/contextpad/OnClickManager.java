package com.slamdunk.quester.hud.contextpad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class OnClickManager {
	protected final int id;
	protected final ActionOnClickListener listener;
	protected final ButtonStyle style;

	public OnClickManager(int id, ActionOnClickListener listener, TextureRegion imageUp, TextureRegion imageDown) {
		this.id = id;
		this.listener = listener;
		
		this.style = new ButtonStyle();
		style.up = new TextureRegionDrawable(imageUp);
		style.up = new TextureRegionDrawable(imageDown);
		style.pressedOffsetY = 1f;
	}
	
	public int getId() {
		return id;
	}

	public void apply(Button button) {
		button.setStyle(style);
		Array<EventListener> listeners = button.getListeners();
		for (EventListener curListener : listeners) {
			if (curListener instanceof ActionOnClickListener) {
				listeners.removeValue(curListener, true);
			}
		}
		button.addListener(listener);
	}
	
	public void setDrawables(TextureRegion up, TextureRegion down) {
		style.up = new TextureRegionDrawable(up);
		style.down = new TextureRegionDrawable(down);
	}

	public ButtonStyle getStyle() {
		return style;
	}
}
