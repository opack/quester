package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.Borders.BOTTOM;
import static com.slamdunk.quester.map.logical.Borders.LEFT;
import static com.slamdunk.quester.map.logical.Borders.RIGHT;
import static com.slamdunk.quester.map.logical.Borders.TOP;
import static com.slamdunk.quester.map.logical.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.PATH_TO_REGION;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Données logiques d'une pièce de donjon. Seule la structure de la pièce
 * (sols, murs, portes) est retenue ; tout ce qui est éphémère (monstres, trésors...) n'est pas
 * indiqué.
 * @author Didier
 *
 */
public class MapArea {
	/**
	 * Taille de la pièce en cellules
	 */
	private final int width;
	private final int height;
	
	/**
	 * Structure de la pièce
	 */
	private MapElements[][] layout;
	
	private Map<Borders, Set<MapElements>> paths;
	
	public MapArea(int width, int height) {
		this.width = width;
		this.height = height;
		layout = new MapElements[width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(layout[col], MapElements.EMPTY);
		}
		paths = new HashMap<Borders, Set<MapElements>>();
		for (Borders border : Borders.values()) {
			paths.put(border, new HashSet<MapElements>());
		}
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public MapElements get(int x, int y) {
		return layout[x][y];
	}
	
	public void set(int x, int y, MapElements element) {
		layout[x][y] = element;
	}
	

	public void addPath(Borders wall, MapElements path) {
		addPath(wall, path, -1);
	}
	
	public void addPath(Borders wall, MapElements path, int position) {
		if (path != COMMON_DOOR
		&& path != DUNGEON_ENTRANCE_DOOR
		&& path != DUNGEON_EXIT_DOOR
		&& path != PATH_TO_REGION){
			throw new IllegalArgumentException("DungeonRoom.setPath : " + path + " is not a path !");
		}
		// Ce chemin est à présent sur ce mur
		paths.get(wall).add(path);
		
		// On place effectivement le chemin sur la carte
		switch (wall) {
			case TOP:
				if (position == -1) {
					set(width / 2, height - 1, path);
				} else {
					set(position, height - 1, path);
				}
				break;
			case BOTTOM:
				if (position == -1) {
					set(width / 2, 0, path);
				} else {
					set(position, 0, path);
				}
				break;
			case LEFT:
				if (position == -1) {
					set(0, height / 2, path);
				} else {
					set(0, position, path);
				}
				break;
			case RIGHT:
				if (position == -1) {
					set(width - 1, height / 2, path);
				} else {
					set(width - 1, position, path);
				}
				break;
		}
	}

	public Set<MapElements> getPaths(Borders wall) {
		return paths.get(wall);
	}
	
	public boolean containsPath(MapElements path) {
		if (path != COMMON_DOOR
		&& path != DUNGEON_ENTRANCE_DOOR
		&& path != DUNGEON_EXIT_DOOR) {
			throw new IllegalArgumentException("This method only accepts path elements.");
		}
		return paths.get(TOP).contains(path)
		|| paths.get(BOTTOM).contains(path)
		|| paths.get(LEFT).contains(path)
		|| paths.get(RIGHT).contains(path);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				switch (layout[col][row]) {
				case COMMON_DOOR:
					sb.append("D ");
					break;
				case DUNGEON_ENTRANCE_DOOR:
					sb.append("I ");
					break;
				case DUNGEON_EXIT_DOOR:
					sb.append("O ");
					break;
				case GROUND:
					sb.append("  ");
					break;
				case WALL:
					sb.append("¤ ");
					break;
				case EMPTY:
				default:
					sb.append("  ");
					break;
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
