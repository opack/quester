package com.slamdunk.quester.map.physical;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class MapCell {
	private String id;
	private int x;
	private int y;
	private boolean stretch;
	private Actor actor;

	public MapCell(String id) {
		this.id = id;
		stretch = true;
	}

	public MapCell(String id, int x, int y, Actor actor) {
		this(id);
		this.x = x;
		this.y = y;
		this.actor = actor;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isStretch() {
		return stretch;
	}

	public void setStretch(boolean stretch) {
		this.stretch = stretch;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public String getId() {
		return id;
	}
}