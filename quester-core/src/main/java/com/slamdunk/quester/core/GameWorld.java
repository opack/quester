package com.slamdunk.quester.core;

import com.slamdunk.quester.actors.Character;

/**
 * Centralise l'intelligence du monde.
 * @author didem93n
 *
 */
public interface GameWorld {
	/**
	 * Retourne l'élément du monde qui représente le joueur
	 * @return
	 */
	Character getPlayer();
	
	/**
	 * Achève le tour du joueur courant et démarre le tour du joueur suivant.
	 */
	void endCurrentPlayerTurn();
	
	/**
	 * Retourne la carte associée à ce monde
	 * @return
	 */
	GameMap getMap();
}
