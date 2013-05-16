package com.slamdunk.quester.camera;

import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.slamdunk.quester.screens.AbstractMapScreen;

public class MouseScrollZoomProcessor extends InputAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	// Le zoom max permet d'afficher 2 cases
	private float zoomMin; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private float zoomMax;
	
	private final OrthographicCamera camera;

	public MouseScrollZoomProcessor (AbstractMapScreen screen) {
		this.camera = screen.getCamera();
		zoomMin = 2 * screen.getCellWidth() / SCREEN_WIDTH;
		zoomMax = screen.getMapWidth() * screen.getCellWidth() / SCREEN_WIDTH + ZOOM_STEP;
	}
	
	@Override
	public boolean scrolled(int amount) {
		// Incrémente ou décrément le zoom de 0.1%
		float newZoom = camera.zoom + amount * ZOOM_STEP;
		if (newZoom >= zoomMin && newZoom <= zoomMax) {
			camera.zoom = newZoom;
		}
		return true;
	}
}