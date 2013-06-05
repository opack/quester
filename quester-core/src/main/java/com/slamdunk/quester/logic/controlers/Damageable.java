package com.slamdunk.quester.logic.controlers;

public interface Damageable {

	/**
	 * Reçoit et gère les dégâts.
	 * @param damage
	 */
	void receiveDamage(int damage);

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHealth();
	
	/**
	 * Définit le nombre de points de vie restants
	 */
	void setHealth(int value);
	
	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
}
