package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.model.ai.Actions;

public class ActionData {
	public Actions action;
	public WorldActor target;
	public int targetX;
	public int targetY;

	public ActionData(Actions action, WorldActor target) {
		this.action = action;
		this.target = target;
		if (target == null) {
			targetX = -1;
			targetY = -1;
		} else {
			targetX = target.getWorldX();
			targetY = target.getWorldY();
		}
	}

	public ActionData(Actions action, int targetX, int targetY) {
		this.action = action;
		this.targetX = targetX;
		this.targetY = targetY;
		this.target = null;
	}
}