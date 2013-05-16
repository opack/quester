package com.slamdunk.quester.core;

import com.slamdunk.quester.actors.Character;

/**
 * Centralise l'intelligence du monde.
 * @author didem93n
 *
 */
public interface GameWorld {
	/**
	 * Retourne l'�l�ment du monde qui repr�sente le joueur
	 * @return
	 */
	Character getPlayer();
	
	/**
	 * Ach�ve le tour du joueur courant et d�marre le tour du joueur suivant.
	 */
	void endCurrentPlayerTurn();
	
	/**
	 * Retourne la carte associ�e � ce monde
	 * @return
	 */
	GameMap getMap();
}
