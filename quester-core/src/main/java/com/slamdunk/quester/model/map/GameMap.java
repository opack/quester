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
	 * Efface la carte en supprimant les données qu'elle contient
	 * mais pas les différentes couches (qui sont alors vides).
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
	 * Retourne l'élément à la position indiquée sur la couche la plus élevée de la carte
	 * @param col
	 * @param row
	 * @return
	 */
	WorldElementActor getTopElementAt(int col, int row);
	
	/**
	 * Retourne l'élément à la position indiquée en ne regardant que les couches dont le niveau
	 * est indiqué dans le tableau layers
	 */
	WorldElementActor getTopElementAt(int col, int row, int... layers);

	/**
	 * Met à jour la carte et l'élément indiqué en prenant en compte l'ancienne et la nouvelle
	 * position indiquées.
	 * @param element
	 * @param oldCol
	 * @param oldRow
	 * @param newCol
	 * @param newRow
	 */
	void updateMapPosition(WorldElementActor element, int oldCol, int oldRow, int newCol, int newRow);

	/**
	 * Supprime l'élément indiqué de la carte
	 * @param element
	 * @return 
	 */
	WorldElementActor removeElement(WorldElementActor element);

	/**
	 * Indique si la cible mentionnée peut être atteinte depuis le point de vue indiqué.
	 * @param pointOfView
	 * @param target
	 * @param range
	 * @return
	 */
	boolean isWithinRangeOf(WorldElementActor pointOfView, WorldElementActor target, int range);

	/**
	 * Retourne un chemin depuis un élément du monde vers un autre
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
	 * Retourne la zone de la carte spécifiée ou null si aucune zone
	 * n'existe à ces coordonnées
	 * @param currentArea
	 * @return
	 */
	MapArea getArea(Point currentArea);
}
