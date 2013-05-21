package com.slamdunk.quester.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class OnClickManager {
	protected Button button;
	protected ActionOnClickListener listener;
	protected final TextButtonStyle style;

	public OnClickManager(ActionOnClickListener listener) {
		this.listener = listener;
		
		style = new TextButtonStyle();
		style.pressedOffsetY = 1f;
	}
	
	public void apply() {
		button.setStyle(style);
		Array<EventListener> listeners = button.getListeners();
		for (EventListener curListener : listeners) {
			if (curListener instanceof ActionOnClickListener) {
				listeners.removeValue(curListener, true);
			}
		}
		button.addListener(listener);
	}
	
	public void setButton(Button button) {
		this.button = button;
	}

	public Button getButton() {
		return button;
	}

	public void setDrawables(TextureRegion up, TextureRegion down) {
		style.up = new TextureRegionDrawable(up);
		style.down = new TextureRegionDrawable(down);
	}

	public TextButtonStyle getStyle() {
		return style;
	}
}
