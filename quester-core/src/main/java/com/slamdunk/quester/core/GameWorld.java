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
	 * Retourne l'�l�ment du monde qui repr�sente le joueur
	 * @return
	 */
	Player getPlayer();
	
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
	 * Cr�e le joueur, qui sera utilis� dans chaque �cran de jeu
	 */
	void createPlayer(int hp, int att);
}
