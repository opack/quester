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
	 * Efface la carte en supprimant les donn�es qu'elle contient
	 * mais pas les diff�rentes couches (qui sont alors vides).
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
	 * Retourne l'�l�ment � la position indiqu�e sur la couche la plus �lev�e de la carte
	 * @param col
	 * @param row
	 * @return
	 */
	WorldElement getTopElementAt(int col, int row);
	
	/**
	 * Retourne l'�l�ment � la position indiqu�e sur la couche la plus �lev�e de la carte
	 * strictement au-dessus du niveau indiqu�
	 * @param col
	 * @param row
	 * @return
	 */
	WorldElement getTopElementAt(int aboveLevel, int col, int row);

	/**
	 * Met � jour la carte et l'�l�ment indiqu� en prenant en compte l'ancienne et la nouvelle
	 * position indiqu�es.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	void updateMapPosition(WorldElement element, int oldCol, int oldRow, int newCol, int newRow);

	/**
	 * Supprime l'�l�ment indiqu� de la carte
	 * @param element
	 */
	void removeElement(WorldElement element);

	/**
	 * Indique si la cible mentionn�e peut �tre atteinte depuis le point de vue indiqu�.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	boolean isWithinRangeOf(WorldElement pointOfView, WorldElement target, int range);

	/**
	 * Retourne un chemin depuis un �l�ment du monde vers un autre
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
