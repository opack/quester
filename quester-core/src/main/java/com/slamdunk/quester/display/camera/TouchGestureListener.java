package com.slamdunk.quester.display.camera;

import static com.slamdunk.quester.Quester.screenHeight;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.display.hud.HUDRenderer;
import com.slamdunk.quester.display.map.MapRenderer;

/**
 * Gère le zoom, le pan et transmet le tap au Stage pour qu'il le gère.
 * @author Didier
 *
 */
public class TouchGestureListener extends GestureAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	private static final float ZOOM_STEPS_IN_WIDTH = 10;
	private OrthographicCamera camera; 
	private float initialZoom;
	
	private float lastInitialDistance;
	private Stage[] stages;
	
	// Le zoom max permet d'afficher toute la largeur de la carte
	private final float zoomMax;
	// Le zoom max permet d'afficher 2 cases
	private final float zoomMin;
	
	public TouchGestureListener(MapRenderer mapRenderer, HUDRenderer hudRenderer) {
		this.camera = mapRenderer.getCamera();
		this.stages = new Stage[]{hudRenderer, mapRenderer.getStage()};
		lastInitialDistance = -1;
		
		zoomMin = 2 * mapRenderer.getMap().getCellHeight() / screenHeight; 
		zoomMax = mapRenderer.getMap().getMapHeight() * mapRenderer.getMap().getCellHeight() / screenHeight + ZOOM_STEP * 2;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// Modification de la position
		camera.position.add(-deltaX, deltaY, 0);
		return true;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Un tap : on simule un touchDown puis un touchUp
		for (Stage stage : stages) {
			stage.touchDown((int)x, (int)y, count, button);
			if (stage.touchUp((int)x, (int)y, count, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (lastInitialDistance != initialDistance) {
			// Début d'un nouveau zoom
			lastInitialDistance = initialDistance;
			initialZoom = camera.zoom;
			return true;
		} else {
			float newZoom = initialZoom + ((initialDistance - distance) / screenHeight * ZOOM_STEPS_IN_WIDTH * ZOOM_STEP);
			if (newZoom >= zoomMin && newZoom <= zoomMax) {
				camera.zoom = newZoom;
				return true;
			} else {
				return false;
			}
		}
		
	}
}