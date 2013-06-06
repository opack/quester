package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class ActionData {
	public Actions action;
	public WorldElementControler target;
	public int targetX;
	public int targetY;

	public ActionData(Actions action, WorldElementControler target) {
		this.action = action;
		this.target = target;
		if (target == null) {
			targetX = -1;
			targetY = -1;
		} else {
			WorldElementActor actor = target.getActor();
			targetX = actor.getWorldX();
			targetY = actor.getWorldY();
		}
	}

	public ActionData(Actions action, int targetX, int targetY) {
		this.action = action;
		this.targetX = targetX;
		this.targetY = targetY;
		this.target = null;
	}
}