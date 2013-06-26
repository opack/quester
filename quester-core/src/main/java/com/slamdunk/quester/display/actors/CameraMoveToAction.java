package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.slamdunk.quester.logic.controlers.GameControler;

public class CameraMoveToAction extends TemporalAction {
	private float startX, startY;
	private float endX, endY;
	private OrthographicCamera camera;

	public CameraMoveToAction(float x, float y, float duration) {
		setPosition(x, y);
		setDuration(duration);
	}

	protected void begin () {
		camera = GameControler.instance.getScreen().getMapRenderer().getCamera();
		startX = camera.position.x;
		startY = camera.position.y;
	}

	protected void update (float percent) {
		camera.position.x = startX + (endX - startX) * percent;
		camera.position.y = startY + (endY - startY) * percent;
	}

	public void setPosition (float x, float y) {
		endX = x;
		endY = y;
	}

	public float getX () {
		return endX;
	}

	public void setX (float x) {
		endX = x;
	}

	public float getY () {
		return endY;
	}

	public void setY (float y) {
		endY = y;
	}
}
