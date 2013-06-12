package com.slamdunk.quester.logic.controlers;

public interface CharacterListener {
	/**
	 * Méthode appelée après le changement des HP
	 * @param oldValue
	 * @param newValue
	 */
	void onHealthPointsChanged(int oldValue, int newValue);
	
	/**
	 * Méthode appelée après le changement des attack points
	 * @param oldValue
	 * @param newValue
	 */
	void onAttackPointsChanged(int oldValue, int newValue);

	/**
	 * Méthode appelée après la mort d'un personnage
	 */
	void onCharacterDeath(CharacterControler character);
	
	/**
	 * Méthode appelée après l'utilisation d'un PA
	 */
	void onActionPointsChanged(int oldValue, int newValue);
}