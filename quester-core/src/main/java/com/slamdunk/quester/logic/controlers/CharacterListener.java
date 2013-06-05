package com.slamdunk.quester.logic.controlers;

public interface CharacterListener {
	/**
	 * M�thode appel�e apr�s le changement des HP
	 * @param oldValue
	 * @param newValue
	 */
	void onHealthPointsChanged(int oldValue, int newValue);
	
	/**
	 * M�thode appel�e apr�s le changement des attack points
	 * @param oldValue
	 * @param newValue
	 */
	void onAttackPointsChanged(int oldValue, int newValue);

	/**
	 * M�thode appel�e apr�s la mort d'un personnage
	 */
	void onCharacterDeath(CharacterControler character);
}