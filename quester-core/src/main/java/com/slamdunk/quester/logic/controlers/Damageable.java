package com.slamdunk.quester.logic.controlers;

public interface Damageable {

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHealth();

	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
	
	/**
	 * Reçoit et gère les dégâts.
	 * @param damage
	 */
	void receiveDamage(int damage);
	
	/**
	 * Définit le nombre de points de vie restants
	 */
	void setHealth(int value);
}
