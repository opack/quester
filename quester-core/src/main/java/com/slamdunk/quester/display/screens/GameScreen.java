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
	 * Centre la caméra sur le joueur
	 * @param element
	 */
	void centerCameraOn(WorldActor element);

	/**
	 * Affiche la pièce de donjon aux coordonnées indiquées, en placant
	 * le héro à l'entrée de la pièce aux coordonnées indiquées.
	 */
	void displayWorld(DisplayData data);
	
	/**
	 * Met à jour le HUD.
	 * @param currentArea
	 */
	void updateHUD(Point currentArea);

	/**
	 * Affiche un message à l'utilisateur
	 * @param message
	 */
	void showMessage(String message);
}
