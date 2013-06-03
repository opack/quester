package com.slamdunk.quester.display.map;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.slamdunk.quester.model.map.AStar;

public class MapLayer extends Group {
	/**
	 * Taille physique d'une cellule sur l'�cran
	 */
	private final float cellWidth;
	private final float cellHeight;
	
	/**
	 * Contient toutes les cellules de la map r�cup�rables par leur id
	 */
	private Map<String, MapCell> cellsById;
	
	/**
	 * Contient toutes les cellules de la map par position
	 */
	private MapCell[][] cells;
	
	/**
	 * R�f�rence vers le pathfinder pour qu'on puisse le mettre � jour
	 * en cas de modification d'une cellule
	 */
	protected AStar pathfinder;
	
	public MapLayer(int mapWidth, int mapHeight, float cellWidth, float cellHeight, AStar pathfinder) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		cellsById = new HashMap<String, MapCell>();
		cells = new MapCell[mapWidth][mapHeight];
		this.pathfinder = pathfinder;
	}
	
	public MapCell getCell(String id) {
		return cellsById.get(id);
	}
	
	public MapCell getCell(int x, int y) {
		if (!isValidPosition(x, y)) {
			return null;
		}
		return cells[x][y];
	}
	

	public void setCell(MapCell cell) {
		if (cell == null
		|| !isValidPosition(cell.getX(), cell.getY())) {
			return;
		}
		// Enregistre la correspondance avec l'id
		if (cell.getId() != null) {
			cellsById.put(cell.getId(), cell);
		}
		// Enregistre l'acteur
		addActor(cell.getActor());
		
		// Place la cellule dans la map et sur l'�cran
		cells[cell.getX()][cell.getY()] = cell;
		layoutCell(cell);
	}
	
	/**
	 * Retire la cellule � l'emplacement indiqu�.
	 * @param x
	 * @param y
	 * @return true si une suppression a bien �t� effectu�e, false
	 * sinon (position invalide ou emplacement vide)
	 */
	public MapCell removeCell(int x, int y) {
		if (!isValidPosition(x, y)) {
			return null;
		}
		MapCell cell = cells[x][y];
		if (cell == null) {
			return null;
		}
		Actor actor = cell.getActor();
		if (actor != null) {
			removeActor(actor);
		}
		cells[x][y] = null;
		// Met � jour le pathfinder
		if (pathfinder != null) {
			pathfinder.setWalkable(cell.getX(), cell.getY(), false);
		}
		return cell;
	}
	
	/**
	 * Place la cellule sur l'�cran
	 * @param layer
	 * @param cell
	 */
	private void layoutCell(MapCell cell) {
		// Place l'acteur o� il faut sur l'�cran
		Actor actor = cell.getActor();
		actor.setX(cell.getX() * cellWidth);
		actor.setY(cell.getY() * cellHeight);
		if (cell.isStretch()) {
			actor.setWidth(cellWidth);
			actor.setHeight(cellHeight);
		}
	}
	
	public boolean moveCell(MapCell cell, int newX, int newY, boolean layoutCell) {
		if (!isValidPosition(newX, newY)
		|| cell == null) {
			return false;
		}
		final int oldX = cell.getX();
		final int oldY = cell.getY();
		
		// Mise � jour de la cellule
		cell.setX(newX);
		cell.setY(newY);
		
		// Mise � jour du tableau de cellules
		cells[oldX][oldY] = null;
		cells[newX][newY] = cell;
		
		// Mise � jour de la taille et position de la cellule
		if (layoutCell) {
			layoutCell(cell);
		}
		return true;
	}
	
	public boolean moveCell(int oldX, int oldY, int newX, int newY, boolean layoutCell) {
		if (!isValidPosition(oldX, oldY)) {
			return false;
		}
		return moveCell(cells[oldX][oldY], newX, newY, layoutCell);
	}

	/**
	 * Retourne true si la position existe et est vide, false sinon.
	 * @return
	 */
	public boolean isEmpty(int x, int y) {
		if (!isValidPosition(x, y)) {
			return false;
		}
		return cells[x][y] == null;
	}
	
	public boolean isValidPosition(int x, int y) {
		return x >= 0 && x < cells.length
			&& y >= 0 && y < cells[0].length;
	}
	
	/**
	 * Indique si la cible est visible en ligne droite depuis le point
	 * de vue indiqu�. Si range est positif, alors la m�thode ne retourne
	 * true que si la distance est inf�rieure ou �gale � range.
	 * @param target
	 * @param range
	 * @return
	 */
	public boolean isInSight(int fromX, int fromY, int targetX, int targetY, int range) {
		// Inutile d'aller plus loin si :
		// Une des positions n'est pas valide
		if (!isValidPosition(fromX, fromY)
		|| !isValidPosition(targetX, targetY)
		// La cible n'est ni dans la m�me colonne ni dans la m�me ligne
		|| (targetX != fromX && targetY != fromY)
		// La cible est trop loin
		|| ScreenMap.distance(fromX, fromY, targetX, targetY) > range) {
			return false;
		}
		// Si la cible est dans la m�me colonne :
		if (targetX == fromX) {
			if (targetY > fromY) {
				// On va vers le haut
				return isTheWayClear(fromX, fromY, targetX, targetY, 0, +1);
			} else {
				// On va vers le bas
				return isTheWayClear(fromX, fromY, targetX, targetY, 0, -1);
			}
		}
		// Si la cible est dans la m�me ligne :
		else {
			if (targetX > fromX) {
				// On va vers la droite
				return isTheWayClear(fromX, fromY, targetX, targetY, +1, 0);
			} else {
				// On va vers la gauche
				return isTheWayClear(fromX, fromY, targetX, targetY, -1, 0);
			}
		}
	}
	
	private boolean isTheWayClear(int fromX, int fromY, int targetX, int targetY, int colIncrement, int rowIncrement) {
		int curX = fromX + colIncrement;
		int curY = fromY + rowIncrement;
		while (curX != targetX && curY != targetY) {
			if (!isEmpty(curX, curY)) {
				return false;
			}
			curX += colIncrement;
			curY += rowIncrement;
		}
		return true;
	}

	/**
	 * Supprime les donn�es de cette couche
	 */
	public void clearLayer() {
		// Supprime les Actors de ce Group
		clear();
		// Vide la table de cellules
		cellsById.clear();
		for (int col = 0; col < cells.length; col++) {
			for (int row = 0; row < cells[0].length; row++) {
				cells[col][row] = null;
			}
		}
	}
}