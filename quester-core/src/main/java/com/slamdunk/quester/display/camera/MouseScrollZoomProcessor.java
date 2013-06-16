package com.slamdunk.quester.display.camera;

import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.slamdunk.quester.display.screens.MapRenderer;

public class MouseScrollZoomProcessor extends InputAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	// Le zoom max permet d'afficher 2 cases
	private float zoomMin; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private float zoomMax;
	
	private final OrthographicCamera camera;
	
	public MouseScrollZoomProcessor (MapRenderer screen) {
		this.camera = screen.getCamera();
		zoomMin = 2 * screen.getCellWidth() / screenWidth;
		zoomMax = screen.getMapWidth() * screen.getCellWidth() / screenWidth + ZOOM_STEP * 2;
	}
	
	@Override
	public boolean scrolled(int amount) {
		// Incr�mente ou d�cr�ment le zoom de 0.1%
		float newZoom = camera.zoom + amount * ZOOM_STEP;
		if (newZoom >= zoomMin && newZoom <= zoomMax) {
			camera.zoom = newZoom;
		}
		return true;
	}
}