package com.slamdunk.quester.model.map;

import java.util.List;

import com.slamdunk.quester.display.actors.WorldElementActor;
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
	WorldElementActor getTopElementAt(int col, int row);
	
	/**
	 * Retourne l'�l�ment � la position indiqu�e en ne regardant que les couches dont le niveau
	 * est indiqu� dans le tableau layers
	 */
	WorldElementActor getTopElementAt(int col, int row, int... layers);

	/**
	 * Met � jour la carte et l'�l�ment indiqu� en prenant en compte l'ancienne et la nouvelle
	 * position indiqu�es.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	void updateMapPosition(WorldElementActor element, int oldCol, int oldRow, int newCol, int newRow);

	/**
	 * Supprime l'�l�ment indiqu� de la carte
	 * @param element
	 * @return 
	 */
	WorldElementActor removeElement(WorldElementActor element);

	/**
	 * Indique si la cible mentionn�e peut �tre atteinte depuis le point de vue indiqu�.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	boolean isWithinRangeOf(WorldElementActor pointOfView, WorldElementActor target, int range);

	/**
	 * Retourne un chemin depuis un �l�ment du monde vers un autre
	 * @param from
	 * @param to
	 * @return
	 */
	List<UnmutablePoint> findPath(WorldElementActor from, WorldElementActor to);
	
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
	List<WorldElementActor> getCharacters();

	/**
	 * Retourne la zone de la carte sp�cifi�e ou null si aucune zone
	 * n'existe � ces coordonn�es
	 * @param currentArea
	 * @return
	 */
	MapArea getArea(Point currentArea);
}
