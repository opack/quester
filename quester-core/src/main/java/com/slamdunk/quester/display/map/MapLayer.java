package com.slamdunk.quester.display.map;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.slamdunk.quester.model.map.MapLevels;

public class MapLayer extends Group {
	private final float cellHeight;
	
	/**
	 * Contient toutes les cellules de la map par position
	 */
	private LayerCell[][] cells;
	/**
	 * Contient toutes les cellules de la map récupérables par leur id
	 */
	private Map<String, LayerCell> cellsById;
	
	/**
	 * Taille physique d'une cellule sur l'écran
	 */
	private final float cellWidth;
	
	/**
	 * Hauteur de la couche
	 */
	private MapLevels level;
	
	public MapLayer(int mapWidth, int mapHeight, float cellWidth, float cellHeight) {
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		cellsById = new HashMap<String, LayerCell>();
		cells = new LayerCell[mapWidth][mapHeight];
	}
	
	/**
	 * Supprime les données de cette couche
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

	public LayerCell getCell(int x, int y) {
		if (!isValidPosition(x, y)) {
			return null;
		}
		return cells[x][y];
	}

	public LayerCell getCell(String id) {
		return cellsById.get(id);
	}
	
	public MapLevels getLevel() {
		return level;
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
	
	/**
	 * Indique si la cible est visible en ligne droite depuis le point
	 * de vue indiqué. Si range est positif, alors la méthode ne retourne
	 * true que si la distance est inférieure ou égale à range.
	 * @param target
	 * @param range
	 * @return
	 */
	public boolean isInSight(int fromX, int fromY, int targetX, int targetY, int range) {
		// Inutile d'aller plus loin si :
		// Une des positions n'est pas valide
		if (!isValidPosition(fromX, fromY)
		|| !isValidPosition(targetX, targetY)
		// La cible n'est ni dans la même colonne ni dans la même ligne
		|| (targetX != fromX && targetY != fromY)
		// La cible est trop loin
		|| ActorMap.distance(fromX, fromY, targetX, targetY) > range) {
			return false;
		}
		// Si la cible est dans la même colonne :
		if (targetX == fromX) {
			if (targetY > fromY) {
				// On va vers le haut
				return isTheWayClear(fromX, fromY, targetX, targetY, 0, +1);
			} else {
				// On va vers le bas
				return isTheWayClear(fromX, fromY, targetX, targetY, 0, -1);
			}
		}
		// Si la cible est dans la même ligne :
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
	
	public boolean isValidPosition(int x, int y) {
		return x >= 0 && x < cells.length
			&& y >= 0 && y < cells[0].length;
	}
	
	/**
	 * Place la cellule sur l'écran
	 * @param layer
	 * @param cell
	 */
	private void layoutCell(LayerCell cell) {
		// Place l'acteur où il faut sur l'écran
		Actor actor = cell.getActor();
		actor.setX(cell.getX() * cellWidth);
		actor.setY(cell.getY() * cellHeight);
		if (cell.isStretch()) {
			actor.setWidth(cellWidth);
			actor.setHeight(cellHeight);
		}
	}

	public boolean moveCell(int oldX, int oldY, int newX, int newY, boolean layoutCell) {
		if (!isValidPosition(oldX, oldY)) {
			return false;
		}
		return moveCell(cells[oldX][oldY], newX, newY, layoutCell);
	}
	
	public boolean moveCell(LayerCell cell, int newX, int newY, boolean layoutCell) {
		if (!isValidPosition(newX, newY)
		|| cell == null) {
			return false;
		}
		final int oldX = cell.getX();
		final int oldY = cell.getY();
		
		// Mise à jour de la cellule
		cell.setX(newX);
		cell.setY(newY);
		
		// Mise à jour du tableau de cellules
		cells[oldX][oldY] = null;
		cells[newX][newY] = cell;
		
		// Mise à jour de la taille et position de la cellule
		if (layoutCell) {
			layoutCell(cell);
		}
		return true;
	}
	
	/**
	 * Retire la cellule à l'emplacement indiqué.
	 * @param x
	 * @param y
	 * @return true si une suppression a bien été effectuée, false
	 * sinon (position invalide ou emplacement vide)
	 */
	public LayerCell removeCell(int x, int y) {
		if (!isValidPosition(x, y)) {
			return null;
		}
		LayerCell cell = cells[x][y];
		if (cell == null) {
			return null;
		}
		Actor actor = cell.getActor();
		if (actor != null) {
			removeActor(actor);
		}
		cells[x][y] = null;
		return cell;
	}
	
	public void setCell(LayerCell cell) {
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
		
		// Place la cellule dans la map et sur l'écran
		cells[cell.getX()][cell.getY()] = cell;
		layoutCell(cell);
	}

	public void setLevel(MapLevels level) {
		this.level = level;
	}
}
