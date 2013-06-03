package com.slamdunk.quester.display.screens;

import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.model.points.Point;

public interface GameScreen extends Screen {
	/**
	 * Largeur d'une cellule de la carte, en pixels.
	 * @return
	 */	
	float getCellWidth();
	
	/**
	 * Hauteur d'une cellule de la carte, en pixels.
	 * @return
	 */
	float getCellHeight();
	
	/**
	 * Centre la cam�ra sur le joueur
	 * @param element
	 */
	void centerCameraOn(WorldActor element);

	/**
	 * Affiche la pi�ce de donjon aux coordonn�es indiqu�es, en placant
	 * le h�ro � l'entr�e de la pi�ce aux coordonn�es indiqu�es.
	 */
	void displayWorld(DisplayData data);
	
	/**
	 * Met � jour le HUD.
	 * @param currentArea
	 */
	void updateHUD(Point currentArea);

	/**
	 * Affiche un message � l'utilisateur
	 * @param message
	 */
	void showMessage(String message);
}
