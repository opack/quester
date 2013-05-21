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

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde
	 */
	void exitDungeon();

	/**
	 * Affiche la pi�ce de donjon aux coordonn�es indiqu�es, en placant
	 * le h�ro � l'entr�e de la pi�ce aux coordonn�es indiqu�es.
	 * @param destinationRoomX
	 * @param destinationRoomY
	 * @param entranceX
	 * @param entranceY
	 */
	void showRoom(int destinationRoomX, int destinationRoomY, int entranceX, int entranceY);

	void centerCameraOn(WorldElement element);
}
