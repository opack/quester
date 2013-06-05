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
	 * Retourne les données du joueur
	 * @return
	 */
	PlayerData getPlayerData();
	
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
	 * Crée les données du joueur, qui seront utilisées dans chaque écran de jeu
	 */
	void createPlayerData(int hp, int att);
}
