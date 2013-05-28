package com.slamdunk.quester.display.actors;

public interface Damageable {

	/**
	 * Re�oit et g�re les d�g�ts.
	 * @param damage
	 */
	void receiveDamage(int damage);

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHP();
	
	/**
	 * D�finit le nombre de points de vie restants
	 */
	void setHP(int value);
	
	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
}
