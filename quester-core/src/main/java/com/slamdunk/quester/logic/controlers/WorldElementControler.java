package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.model.data.ElementData;

public class WorldElementControler implements Comparable<WorldElementControler> {
	private static int WORLD_ELEMENTS_COUNT = 0;
	
	private final int id;
	
	protected ElementData data;
	protected WorldElementActor actor;
	
	public WorldElementControler(ElementData data) {
		id = WORLD_ELEMENTS_COUNT++;
		data.playRank = id;
		setData(data);
	}
	
	public WorldElementControler(ElementData data, WorldElementActor actor) {
		this(data);
		setActor(actor);
	}
	
	public void setData(ElementData data) {
		this.data = data;
	}
	
	public ElementData getData() {
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
	 * Retourne true si le tour peut se terminer
	 * @return
	 */
	protected boolean shouldEndTurn() {
		// Si n'a plus d'action en cours, alors c'est que son tour peut s'achever.
		return actor.getActions().size == 0;
	}

	public void endTurn() {
		QuesterGame.instance.endCurrentPlayerTurn();
	}

	/**
	 * Méthode appelée lorsque le Stage décide qu'il faut faire agir les acteurs
	 * @param delta 
	 */
	public void act(float delta) {
		if (actor != null) {
			actor.act(delta);
		}
		if (shouldEndTurn()) {
			endTurn();
		}
	}
}
