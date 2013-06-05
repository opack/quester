package com.slamdunk.quester.model.map;

import java.util.List;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;

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
	WorldActor getTopElementAt(int col, int row);
	
	/**
	 * Retourne l'�l�ment � la position indiqu�e sur la couche la plus �lev�e de la carte
	 * strictement au-dessus du niveau indiqu�
	 * @param col
	 * @param row
	 * @return
	 */
	WorldActor getTopElementBetween(int aboveLevel, int belowLevel, int col, int row);

	/**
	 * Met � jour la carte et l'�l�ment indiqu� en prenant en compte l'ancienne et la nouvelle
	 * position indiqu�es.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	void updateMapPosition(WorldActor element, int oldCol, int oldRow, int newCol, int newRow);

	/**
	 * Supprime l'�l�ment indiqu� de la carte
	 * @param element
	 */
	void removeElement(WorldActor element);

	/**
	 * Indique si la cible mentionn�e peut �tre atteinte depuis le point de vue indiqu�.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	boolean isWithinRangeOf(WorldActor pointOfView, WorldActor target, int range);

	/**
	 * Retourne un chemin depuis un �l�ment du monde vers un autre
	 * @param from
	 * @param to
	 * @return
	 */
	List<UnmutablePoint> findPath(WorldActor from, WorldActor to);
	
	/**
	 * Retourne un chemin depuis une position de la carte vers une autre
	 * @param from
	 * @param to
	 * @return
	 */
	List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY);
	
	/**
	 * Renvoit la liste des personnages de la carte
	 */
	List<WorldActor> getCharacters();

	/**
	 * Retourne la zone de la carte sp�cifi�e ou null si aucune zone
	 * n'existe � ces coordonn�es
	 * @param currentArea
	 * @return
	 */
	MapArea getArea(Point currentArea);
}
