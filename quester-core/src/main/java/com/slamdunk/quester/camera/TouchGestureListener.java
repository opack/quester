package com.slamdunk.quester.camera;

import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.quester.screens.AbstractMapScreen;

/**
 * Gère le zoom, le pan et transmet le tap au Stage pour qu'il le gère.
 * @author Didier
 *
 */
public class TouchGestureListener extends GestureAdapter {
	// Marges autour de la map
	private static final float MARGIN_LEFT = 10;
	private static final float MARGIN_BOTTOM = 70;
	// Pas du zoom
	private static final float ZOOM_STEP = 0.1f;
	private static final float ZOOM_STEPS_IN_WIDTH = 10;
	// Le zoom max permet d'afficher 2 cases
	private final float zoomMin; 
	// Le zoom max permet d'afficher toute la largeur de la carte
	private final float zoomMax;
	
	private OrthographicCamera camera;
	private Stage stage;
	
	private float lastInitialDistance;
	private float initialZoom;
	
	public TouchGestureListener(AbstractMapScreen screen) {
		this.camera = screen.getCamera();
		this.stage = screen.getStage();
		lastInitialDistance = -1;
		
		zoomMin = 2 * screen.getCellWidth() / SCREEN_WIDTH; 
		zoomMax = screen.getMapWidth() * screen.getCellWidth() / SCREEN_WIDTH + ZOOM_STEP;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		// Un tap : on simule un touchDown puis un touchUp
		stage.touchDown((int)x, (int)y, count, button);
		return stage.touchUp((int)x, (int)y, count, button);
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// Sauvegarde de l'ancienne position
		float oldPositionX = camera.position.x;
		float oldPositionY = camera.position.y;
		
		// Modification de la position
		camera.position.add(-deltaX, deltaY, 0);
		System.out.println("TouchGestureListener.pan()" + camera.position.x * camera.zoom);
		
		// Vérification de la borne X
//		if (camera.view.val[Matrix4.M03] < - (SCREEN_WIDTH / 2) + MARGIN_LEFT) {
//			camera.position.x = oldPositionX;
//		}
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
			if (newZoom >= zoomMin && newZoom <= zoomMax) {
				camera.zoom = newZoom;
				return true;
			} else {
				return false;
			}
		}
	}	

}