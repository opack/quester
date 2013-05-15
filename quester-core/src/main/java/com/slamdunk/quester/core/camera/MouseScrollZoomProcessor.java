package com.slamdunk.quester.core.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.slamdunk.quester.core.Quester;

public class MouseScrollZoomProcessor extends InputAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	// Le zoom max permet d'afficher 2 cases
	private static final float ZOOM_MIN = 2 * Quester.WORLD_CELL_SIZE / Quester.SCREEN_WIDTH; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private static final float ZOOM_MAX = Quester.MAP_WIDTH * Quester.WORLD_CELL_SIZE / Quester.SCREEN_WIDTH + ZOOM_STEP;
	
	private final OrthographicCamera camera;

	public MouseScrollZoomProcessor (OrthographicCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public boolean scrolled(int amount) {
		// Incrémente ou décrément le zoom de 0.1%
		float newZoom = camera.zoom + amount * ZOOM_STEP;
		if (newZoom >= ZOOM_MIN && newZoom <= ZOOM_MAX) {
			camera.zoom = newZoom;
		}
		return true;
	}
}