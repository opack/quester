package com.slamdunk.quester.core;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.WorldElement;

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

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	void exit();
	
	/**
	 * Centre la caméra sur le joueur
	 * @param element
	 */
	void centerCameraOn(WorldElement element);

	/**
	 * Affiche la pièce de donjon aux coordonnées indiquées, en placant
	 * le héro à l'entrée de la pièce aux coordonnées indiquées.
	 */
	void displayWorld(Object data);
}
