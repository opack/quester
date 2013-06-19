package com.slamdunk.quester.display.map;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class LayerCell {
	private Actor actor;
	private String id;
	private boolean stretch;
	private int x;
	private int y;

	public LayerCell(String id) {
		this.id = id;
		stretch = true;
	}

	public LayerCell(String id, int x, int y, Actor actor) {
		this(id);
		this.x = x;
		this.y = y;
		this.actor = actor;
	}

	public Actor getActor() {
		return actor;
	}

	public String getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isStretch() {
		return stretch;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public void setStretch(boolean stretch) {
		this.stretch = stretch;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}