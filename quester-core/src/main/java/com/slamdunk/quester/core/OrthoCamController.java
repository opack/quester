package com.slamdunk.quester.core;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class OrthoCamController extends InputAdapter {
	private final OrthographicCamera camera;
	private final Vector3 curr = new Vector3();
	private final Vector3 last = new Vector3(-1, -1, -1);
	private final Vector3 delta = new Vector3();
	private boolean isDragging;

	public OrthoCamController (OrthographicCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		isDragging = true;
		camera.unproject(curr.set(x, y, 0));
		if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
			camera.unproject(delta.set(last.x, last.y, 0));
			delta.sub(curr);
			camera.position.add(delta.x, delta.y, 0);
		}
		last.set(x, y, 0);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		last.set(-1, -1, -1);
		if (isDragging) {
			// Fin d'un drag
			isDragging = false;
			// Inutile de laisser les autres processeurs gérer cet évènement :
			// on dit qu'on l'a consommé
			return true;
		}
		// Sinon, on ne faisait pas un drag. On laisse les éventuels autres
		// processeurs gérer cet évènement.
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		camera.zoom += amount * 0.2f;
		return true;
	}
}