package com.slamdunk.quester.core;

import com.slamdunk.quester.display.screens.GameScreen;

/**
 * Centralise l'intelligence du monde.
 * @author didem93n
 *
 */
public interface GameWorld {
	/**
	 * Ach�ve le tour du joueur courant et d�marre le tour du joueur suivant.
	 */
	void endCurrentPlayerTurn();
	
	/**
	 * Retourne la carte associ�e � ce monde
	 * @return
	 */
	GameScreen getMapScreen();

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	void exit();

	/**
	 * Cr�e le contr�leur du joueur, qui sera utilis� dans chaque �cran de jeu
	 */
	void createPlayerControler(int hp, int att);
}
