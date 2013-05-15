package com.slamdunk.quester.core.camera;

import static com.slamdunk.quester.core.Quester.MAP_WIDTH;
import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;
import static com.slamdunk.quester.core.Quester.WORLD_CELL_SIZE;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;

/**
 * Gère le zoom, le pan et transmet le tap au Stage pour qu'il le gère.
 * @author Didier
 *
 */
public class TouchGestureListener extends GestureAdapter {
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	private static final float ZOOM_STEPS_IN_WIDTH = 10;
	// Le zoom max permet d'afficher 2 cases
	private static final float ZOOM_MIN = 2 * WORLD_CELL_SIZE / SCREEN_WIDTH; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private static final float ZOOM_MAX = MAP_WIDTH * WORLD_CELL_SIZE / SCREEN_WIDTH + ZOOM_STEP;
	
	private OrthographicCamera camera;
	private Stage stage;
	
	private float lastInitialDistance;
	private float initialZoom;
	
	public TouchGestureListener(OrthographicCamera camera, Stage stage) {
		this.camera = camera;
		this.stage = stage;
		lastInitialDistance = -1;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Un tap : on simule un touchDown puis un touchUp
		stage.touchDown((int)x, (int)y, count, button);
		return stage.touchUp((int)x, (int)y, count, button);
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		camera.position.add(-deltaX, deltaY, 0);
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (lastInitialDistance != initialDistance) {
			// Début d'un nouveau zoom
			lastInitialDistance = initialDistance;
			initialZoom = camera.zoom;
			return true;
		} else {
			float newZoom = initialZoom + ((initialDistance - distance) / SCREEN_WIDTH * ZOOM_STEPS_IN_WIDTH * ZOOM_STEP);
			if (newZoom >= ZOOM_MIN && newZoom <= ZOOM_MAX) {
				camera.zoom = newZoom;
				return true;
			} else {
				return false;
			}
		}
	}	

}