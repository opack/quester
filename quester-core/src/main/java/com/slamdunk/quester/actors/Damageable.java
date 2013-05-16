package com.slamdunk.quester.actors;

public interface Damageable {

	/**
	 * Reçoit et gère les dégâts.
	 * @param damage
	 */
	void receiveDamage(int damage);

	/**
	 * Retourne le nombre de points de vie restants
	 */
	int getHP();
	
	/**
	 * Définit le nombre de points de vie restants
	 */
	void setHP(int value);
	
	/**
	 * Raccourci vers getHP() == 0
	 */
	boolean isDead();
	
	/**
	 * Méthode qui doit être appelée à la mort
	 */
	void onDeath();
}
