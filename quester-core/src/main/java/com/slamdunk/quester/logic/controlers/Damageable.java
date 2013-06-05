package com.slamdunk.quester.logic.controlers;

public interface Damageable {

	/**
	 * Re�oit et g�re les d�g�ts.
	 * @param damage
	 */
	void receiveDamage(int damage);

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHealth();
	
	/**
	 * D�finit le nombre de points de vie restants
	 */
	void setHealth(int value);
	
	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
}
