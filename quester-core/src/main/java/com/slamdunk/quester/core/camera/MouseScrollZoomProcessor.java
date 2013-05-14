package com.slamdunk.quester.core.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MouseScrollZoomProcessor extends InputAdapter {
	private final OrthographicCamera camera;

	public MouseScrollZoomProcessor (OrthographicCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public boolean scrolled(int amount) {
		camera.zoom += amount * 0.2f;
		return true;
	}
}