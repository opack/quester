package com.slamdunk.quester.display.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.points.UnmutablePoint;

/**
 * Représentation logique de l'écran, découpée en cases ayant une position et un nom.
 * Ce n'est pas ici que l'on va dessiner la carte : on se contente de la représenter
 * de façon logique. En revanche, les cellules sont placées dans le vrai monde en
 * fonction de leur position logique.
 * Cette classe gère également le pathfinding et le raycasting.
 * @author Didier
 *
 */
public class ScreenMap extends Group {
	/**
	 * Contient chaque couche de la map
	 */
	private Map<String, MapLayer> layersByName;
	private List<MapLayer> layersByLevel;
	
	/**
	 * Taille logique de la map
	 */
	private final int mapWidth;
	private final int mapHeight;
	
	/**
	 * Taille physique d'une cellule sur l'écran
	 */
	private final float cellWidth;
	private final float cellHeight;
	/**
	 * Map servant de support au pathfinding
	 */
	private AStar standardPathfinder;
	private AStar lightPathfinder;
	private AStar darknessPathfinder;
	
	public ScreenMap(int mapWidth, int mapHeight, float cellWidth, float cellHeight) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		
		layersByName = new HashMap<String, MapLayer>();
		layersByLevel = new ArrayList<MapLayer>();
		
		standardPathfinder = new AStar(mapWidth, mapHeight);
		lightPathfinder = new AStar(mapWidth, mapHeight);
		darknessPathfinder = new AStar(mapWidth, mapHeight);
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public float getCellHeight() {
		return cellHeight;
	}

	public MapLayer addLayer(String id) {
		MapLayer layer = new MapLayer(mapWidth, mapHeight, cellWidth, cellHeight);
		layer.setLevel(layersByLevel.size());
		
		layersByName.put(id, layer);
		layersByLevel.add(layer);		
		addActor(layer);
		return layer;
	}
	
	public MapLayer getLayer(String id) {
		return layersByName.get(id);
	}
	
	public MapLayer getLayer(int level) {
		return layersByLevel.get(level);
	}
	
	public List<MapLayer> getLayersByLevel() {
		return layersByLevel;
	}

	public MapLayer getLayerContainingCell(String cellId) {
		for (MapLayer layer : layersByLevel) {
			if (layer.getCell(cellId) != null) {
				return layer;
			}
		}
		return null;
	}
	
	public boolean isValidPosition(int x, int y) {
		return x >= 0 && x < mapWidth
			&& y >= 0 && y < mapHeight;
	}

	public boolean setCell(String layerId, MapCell cell) {
		if (layerId == null) {
			return false;
		}
		MapLayer layer = layersByName.get(layerId);
		if (layer == null) {
			return false;
		}
		layer.setCell(cell);
		return true;
	}
	
	/**
	 * Vérifie que toutes les couches STRICTEMENT supérieures et
	 * inférieures aux niveaux indiqués n'ont aucune cellule aux
	 * coordonnées indiquées.
	 * @param aboveLevel
	 * @param belowLevel
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmptyBetween(int aboveLevel, int belowLevel, int x, int y) {
		for (int level = belowLevel - 1; level > aboveLevel; level--) {
			if (!layersByLevel.get(level).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Vérifie que toutes les couches STRICTEMENT supérieures au niveau
	 * indiqué n'ont aucune cellule aux coordonnées indiquées.
	 * @param aboveLevel
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmptyAbove(int aboveLevel, int x, int y) {
		final int maxLevel = layersByLevel.size() - 1;
		for (int level = maxLevel; level > aboveLevel; level--) {
			if (!layersByLevel.get(level).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty(int aboveLevel, int x, int y) {
		return isEmptyAbove(-1, x, y);
	}
	
	public boolean isEmpty(int[] levels, int x, int y) {
		for (int level : levels) {
			if (!layersByLevel.get(level).isEmpty(x, y)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retourne la première cellule non vide trouvée dans une des couches
	 * dont le niveau est indiqué dans le tableau
	 */
	public MapCell getTopElementAt(int x, int y, int... layersLevels) {
		MapCell cell;
		for (int level : layersLevels) {
			cell = layersByLevel.get(level).getCell(x, y);
			if (cell != null) {
				return cell;
			}
		}
		return null;
	}
	

	public MapCell getTopElementAt(int x, int y, String... layersIds) {
		MapCell cell;
		for (String level : layersIds) {
			cell = layersByName.get(level).getCell(x, y);
			if (cell != null) {
				return cell;
			}
		}
		return null;
	}
	
	public MapCell getTopElementAt(int x, int y) {
		MapCell cell;
		for (MapLayer layer : layersByLevel) {
			cell = layer.getCell(x, y);
			if (cell != null) {
				return cell;
			}
		}
		return null;
	}
	
	/**
	 * Retourne la distance en ligne droite entre les deux positions
	 * indiquées.
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return
	 */
	public static double distance(int fromX, int fromY, int toX, int toY) {
		return Math.hypot((double)(fromX - toX), (double)(fromY - toY));
	}
	
	public void clearMap() {
		// Nettoyage des couches
		for (MapLayer layer : layersByLevel) {
			layer.clearLayer();
		}
		// RAZ des pathfinders
		standardPathfinder.reset();
		lightPathfinder.reset();
		darknessPathfinder.reset();
	}

	public List<UnmutablePoint> findWalkPath(int fromX, int fromY, int toX, int toY, boolean ignoreArrivalWalkable) {
		return standardPathfinder.findPath(fromX, fromY, toX, toY, ignoreArrivalWalkable);
	}
	
	public List<UnmutablePoint> findWalkPath(int fromX, int fromY, int toX, int toY) {
		return standardPathfinder.findPath(fromX, fromY, toX, toY, true);
	}
	
	public void setWalkable(int col, int row, boolean isWalkable) {
		standardPathfinder.setWalkable(col, row, isWalkable);
	}

	public AStar getStandardPathfinder() {
		return standardPathfinder;		
	}

	public boolean isWalkable(int col, int row) {
		return standardPathfinder.isWalkable(col, row);
	}

	public List<UnmutablePoint> findLightPath(int fromX, int fromY, int toX, int toY, boolean ignoreArrivalLit) {
		return lightPathfinder.findPath(fromX, fromY, toX, toY, ignoreArrivalLit);
	}
	
	public List<UnmutablePoint> findLightPath(int fromX, int fromY, int toX, int toY) {
		return lightPathfinder.findPath(fromX, fromY, toX, toY, true);
	}
	
	public void setLight(int col, int row, boolean isLit) {
		lightPathfinder.setWalkable(col, row, isLit);
	}
	
	public void setDark(int col, int row, boolean isDark) {
		darknessPathfinder.setWalkable(col, row, isDark);
	}
	
	public AStar getLightPathfinder() {
		return lightPathfinder;		
	}
	
	public AStar getDarknessPathfinder() {
		return darknessPathfinder;		
	}

	public boolean isLit(int col, int row) {
		return lightPathfinder.isWalkable(col, row);
	}
}
