package com.slamdunk.quester.core;

import java.util.List;

import com.slamdunk.quester.display.actors.WorldElement;
import com.slamdunk.quester.map.points.UnmutablePoint;

/**
 * Carte du jeu
 * @author didem93n
 *
 */
public interface GameMap {
	/**
	 * Efface la carte en supprimant les données qu'elle contient
	 * mais pas les différentes couches (qui sont alors vides).
	 */
	void clearMap();
	
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
	 * Largeur de la carte, en cellules.
	 * @return
	 */
	int getMapWidth();
	
	/**
	 * Hauteur de la carte, en cellules.
	 * @return
	 */
	int getMapHeight();
	
	/**
	 * Retourne l'élément à la position indiquée sur la couche la plus élevée de la carte
	 * @param col
	 * @param row
	 * @return
	 */
	WorldElement getTopElementAt(int col, int row);
	
	/**
	 * Retourne l'élément à la position indiquée sur la couche la plus élevée de la carte
	 * strictement au-dessus du niveau indiqué
	 * @param col
	 * @param row
	 * @return
	 */
	WorldElement getTopElementAt(int aboveLevel, int col, int row);

	/**
	 * Met à jour la carte et l'élément indiqué en prenant en compte l'ancienne et la nouvelle
	 * position indiquées.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	void updateMapPosition(WorldElement element, int oldCol, int oldRow, int newCol, int newRow);

	/**
	 * Supprime l'élément indiqué de la carte
	 * @param element
	 */
	void removeElement(WorldElement element);

	/**
	 * Indique si la cible mentionnée peut être atteinte depuis le point de vue indiqué.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	boolean isWithinRangeOf(WorldElement pointOfView, WorldElement target, int range);

	/**
	 * Retourne un chemin depuis un élément du monde vers un autre
	 * @param from
	 * @param to
	 * @return
	 */
	List<UnmutablePoint> findPath(WorldElement from, WorldElement to);
	
	/**
	 * Retourne un chemin depuis une position de la carte vers une autre
	 * @param from
	 * @param to
	 * @return
	 */
	List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY);
}
