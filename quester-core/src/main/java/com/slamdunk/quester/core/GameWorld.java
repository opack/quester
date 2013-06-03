package com.slamdunk.quester.core;

import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.screens.GameScreen;

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
	Player getPlayer();
	
	/**
	 * Achève le tour du joueur courant et démarre le tour du joueur suivant.
	 */
	void endCurrentPlayerTurn();
	
	/**
	 * Retourne la carte associée à ce monde
	 * @return
	 */
	GameScreen getMapScreen();

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	void exit();

	/**
	 * Crée le joueur, qui sera utilisé dans chaque écran de jeu
	 */
	void createPlayer(int hp, int att);
}
