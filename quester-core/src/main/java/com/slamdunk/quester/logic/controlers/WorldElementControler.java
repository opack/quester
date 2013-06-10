package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.data.WorldElementData;

public class WorldElementControler implements Comparable<WorldElementControler> {
	private static int WORLD_ELEMENTS_COUNT = 0;
	
	private final int id;
	
	protected WorldElementData data;
	protected WorldElementActor actor;
	
	public WorldElementControler(WorldElementData data) {
		id = WORLD_ELEMENTS_COUNT++;
		data.playRank = id;
		setData(data);
	}
	
	public WorldElementControler(WorldElementData data, WorldElementActor actor) {
		this(data);
		setActor(actor);
	}
	
	public void setData(WorldElementData data) {
		this.data = data;
	}
	
	public WorldElementData getData() {
		return data;
	}
	
	public WorldElementActor getActor() {
		return actor;
	}

	public void setActor(WorldElementActor actor) {
		this.actor = actor;
	}

	public long getId() {
		return id;
	}

	@Override
	public int compareTo(WorldElementControler o) {
		return data.playRank - o.data.playRank;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldElementControler) {
			return id == ((WorldElementControler)obj).id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	/**
	 * Méthode appelée lorsque le Stage décide qu'il faut faire agir les acteurs
	 * @param delta 
	 */
	public void act(float delta) {
		if (actor != null) {
			actor.act(delta);
		}
	}
}
