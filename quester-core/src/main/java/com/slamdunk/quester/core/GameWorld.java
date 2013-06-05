package com.slamdunk.quester.core;

import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.model.data.PlayerData;

/**
 * Centralise l'intelligence du monde.
 * @author didem93n
 *
 */
public interface GameWorld {
	/**
	 * Retourne les donn�es du joueur
	 * @return
	 */
	PlayerData getPlayerData();
	
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
	 * Cr�e les donn�es du joueur, qui seront utilis�es dans chaque �cran de jeu
	 */
	void createPlayerData(int hp, int att);
}
