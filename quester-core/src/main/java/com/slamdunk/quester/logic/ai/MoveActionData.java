package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.MOVE;

import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class MoveActionData extends ActionData {

	/**
	 * Indique s'il faut suivre la cible
	 */
	public boolean isTracking;
	
	/**
	 * Indique s'il faut s'arrêter autour de la cible
	 */
	public boolean isMoveNearTarget;

	/**
	 * Indique s'il faut marcher sur la cible, sans tenir
	 * compte du fait qu'elle soit ou non un obstacle
	 */
	public boolean isStepOnTarget;
	
	public MoveActionData(int targetX, int targetY) {
		super(MOVE, targetX, targetY);
	}
	
	public MoveActionData(WorldElementControler target) {
		super(MOVE, target);
	}
}
