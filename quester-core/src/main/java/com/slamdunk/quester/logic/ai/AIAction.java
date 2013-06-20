package com.slamdunk.quester.logic.ai;

public interface AIAction {
	/**
	 * Effectue l'action
	 */
	void act();
	
	/**
	 * Retourne l'action associée
	 */
	QuesterActions getAction();
}
