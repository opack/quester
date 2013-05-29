package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.PATH_TO_REGION;

import java.util.Arrays;

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
	
	private MapElements[] paths;
	
	public MapArea(int width, int height) {
		this.width = width;
		this.height = height;
		layout = new MapElements[width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(layout[col], MapElements.EMPTY);
		}
		paths = new MapElements[4];
		Arrays.fill(paths, MapElements.EMPTY);
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
	
	public void setPath(Borders wall, MapElements path) {
		if (path != COMMON_DOOR
		&& path != DUNGEON_ENTRANCE_DOOR
		&& path != DUNGEON_EXIT_DOOR
		&& path != PATH_TO_REGION){
			throw new IllegalArgumentException("DungeonRoom.setPath : " + path + " is not a path !");
		}
		paths[wall.ordinal()] = path;
		switch (wall) {
			case TOP:
				set(width / 2, height - 1, path);
				break;
			case BOTTOM:
				set(width / 2, 0, path);
				break;
			case LEFT:
				set(0, height / 2, path);
				break;
			case RIGHT:
				set(width - 1, height / 2, path);
				break;
		}
	}

	public MapElements getPath(Borders wall) {
		return paths[wall.ordinal()];
	}
	
	public boolean containsPath(MapElements path) {
		if (path != COMMON_DOOR
		&& path != DUNGEON_ENTRANCE_DOOR
		&& path != DUNGEON_EXIT_DOOR) {
			throw new IllegalArgumentException("This method only accepts path elements.");
		}
		return paths[Borders.TOP.ordinal()] == path
		|| paths[Borders.BOTTOM.ordinal()] == path
		|| paths[Borders.LEFT.ordinal()] == path
		|| paths[Borders.RIGHT.ordinal()] == path;
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
