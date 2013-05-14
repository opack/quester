package com.slamdunk.quester.core.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;

/**
 * Gère le zoom, le pan et transmet le tap au Stage pour qu'il le gère.
 * @author Didier
 *
 */
public class TouchGestureListener extends GestureAdapter {
	private static Logger log = new Logger("quester");
	
	private OrthographicCamera camera;
	private Stage stage;
	
	public TouchGestureListener(OrthographicCamera camera, Stage stage) {
		this.camera = camera;
		this.stage = stage;
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
		log.debug("zooming from " + camera.zoom);
		log.debug("zooming initialDistance=" + initialDistance + ", distance=" + distance);
		camera.zoom *= (distance - initialDistance) / 10;
		log.debug("zooming to " + camera.zoom);
		return true;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,	Vector2 pointer1, Vector2 pointer2) {
		log.debug("zooming initialDistance=" + initialPointer1.dst(initialPointer2) + ", distance=" + pointer1.dst(pointer2));
		return false;
	}
	

}