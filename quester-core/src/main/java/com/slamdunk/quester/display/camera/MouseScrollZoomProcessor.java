package com.slamdunk.quester.display.camera;

import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.map.MapRenderer;

public class MouseScrollZoomProcessor extends InputAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	private final OrthographicCamera camera; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private float zoomMax;
	
	// Le zoom max permet d'afficher 2 cases
	private float zoomMin;
	
	public MouseScrollZoomProcessor (MapRenderer renderer) {
		this.camera = renderer.getCamera();
		ActorMap map = renderer.getMap();
		zoomMin = 2 * map.getCellWidth() / screenWidth;
		zoomMax = map.getMapWidth() * map.getCellWidth() / screenWidth + ZOOM_STEP * 2;
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