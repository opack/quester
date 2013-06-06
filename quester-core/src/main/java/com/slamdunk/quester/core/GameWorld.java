package com.slamdunk.quester.core;

import com.slamdunk.quester.display.screens.GameScreen;

/**
 * Centralise l'intelligence du monde.
 * @author didem93n
 *
 */
public interface GameWorld {
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
	 * Crée le contrôleur du joueur, qui sera utilisé dans chaque écran de jeu
	 */
	void createPlayerControler(int hp, int att);
}
