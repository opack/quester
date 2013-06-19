package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.data.WorldElementData;

public class WorldElementControler implements Comparable<WorldElementControler> {
	private static int WORLD_ELEMENTS_COUNT = 0;
	
	protected WorldElementActor actor;
	
	protected WorldElementData data;
	private final int id;
	
	public WorldElementControler(WorldElementData data) {
		id = WORLD_ELEMENTS_COUNT++;
		data.playRank = id;
		setData(data);
	}
	
	public WorldElementControler(WorldElementData data, WorldElementActor actor) {
		this(data);
		setActor(actor);
	}
	
	/**
	 * M�thode appel�e lorsque le Stage d�cide qu'il faut faire agir les acteurs
	 * @param delta 
	 */
	public void act(float delta) {
		if (actor != null) {
			actor.act(delta);
		}
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

	public WorldElementActor getActor() {
		return actor;
	}

	public WorldElementData getData() {
		return data;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	public void setActor(WorldElementActor actor) {
		this.actor = actor;
	}
	
	public void setData(WorldElementData data) {
		this.data = data;
	}
}
