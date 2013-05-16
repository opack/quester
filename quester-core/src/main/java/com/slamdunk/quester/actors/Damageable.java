package com.slamdunk.quester.actors;

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
	
	/**
	 * M�thode qui doit �tre appel�e � la mort
	 */
	void onDeath();
}
