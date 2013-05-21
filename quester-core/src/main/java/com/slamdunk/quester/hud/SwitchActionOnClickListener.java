package com.slamdunk.quester.hud;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SwitchActionOnClickListener extends ClickListener {
	private ActionOnClickListener currentListener;
	private Map<Class<? extends ActionOnClickListener>, ActionOnClickListener> listeners;

	public SwitchActionOnClickListener() {
		listeners = new HashMap<Class<? extends ActionOnClickListener>, ActionOnClickListener>();
	}

	public void putListener(Class<? extends ActionOnClickListener> clazz,
			ActionOnClickListener listener) {
		listeners.put(clazz, listener);
	}

	public void setCurrentListener(Class<? extends ActionOnClickListener> clazz) {
		currentListener = listeners.get(clazz);
	}

	public ActionOnClickListener getCurrentListener() {
		return currentListener;
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		currentListener.clicked(event, x, y);
	}
}
