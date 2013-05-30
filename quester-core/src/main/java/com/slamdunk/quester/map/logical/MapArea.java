package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.Borders.BOTTOM;
import static com.slamdunk.quester.map.logical.Borders.LEFT;
import static com.slamdunk.quester.map.logical.Borders.RIGHT;
import static com.slamdunk.quester.map.logical.Borders.TOP;
import static com.slamdunk.quester.map.logical.MapBuilder.EMPTY_DATA;
import static com.slamdunk.quester.map.logical.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.map.logical.MapElements.PATH_TO_REGION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private static final int LEVEL_BACKGROUND = 0;
	private static final int LEVEL_OBJECTS = 1;
	
	/**
	 * Position de la zone dans l'ensemble du monde
	 */
	private final int x;
	private final int y;
	
	/**
	 * Taille de la pièce en cellules
	 */
	private final int width;
	private final int height;
	
	/**
	 * Structure de la pièce. Le niveau 0 correspond au fond, et le niveau 1
	 * correspond aux objets présents dans la pièce (portes, trésors...).
	 */
	private final ElementData[][][] layout;
	
	/**
	 * Chemins permettant d'accéder à une zone adjacente
	 */
	private final Map<Borders, Set<PathData>> paths;
	
	/**
	 * Personnages présents dans la pièce. On ne retient pas leurs coordonnées,
	 * ils seront réinstanciés à chaque entrée dans la pièce.
	 */
	private final List<CharacterData> characters;
	
	public MapArea(int x, int y, int width, int height, ElementData defaultBackground) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		ElementData empty = EMPTY_DATA;
		layout = new ElementData[2][width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(layout[LEVEL_BACKGROUND][col], defaultBackground);
			Arrays.fill(layout[LEVEL_OBJECTS][col], empty);
		}
		
		paths = new HashMap<Borders, Set<PathData>>();
		for (Borders border : Borders.values()) {
			paths.put(border, new HashSet<PathData>());
		}
		
		characters = new ArrayList<CharacterData>();
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public ElementData getBackgroundAt(int x, int y) {
		return layout[LEVEL_BACKGROUND][x][y];
	}
	
	public ElementData getObjectAt(int x, int y) {
		return layout[LEVEL_OBJECTS][x][y];
	}
	
	public void setBackgroundAt(int x, int y, ElementData element) {
		layout[LEVEL_BACKGROUND][x][y] = element;
	}
	
	public void setObjectAt(int x, int y, ElementData element) {
		layout[LEVEL_OBJECTS][x][y] = element;
	}
	
	public void addPath(Borders wall, PathData path) {
		addPath(wall, path, -1);
	}
	
	public void addPath(Borders wall, PathData path, int position) {
		if (path.element != COMMON_DOOR
		&& path.element != DUNGEON_ENTRANCE_DOOR
		&& path.element != DUNGEON_EXIT_DOOR
		&& path.element != PATH_TO_REGION){
			throw new IllegalArgumentException("DungeonRoom.setPath : " + path.element + " is not a path !");
		}
		// Ce chemin est à présent sur ce mur
		paths.get(wall).add(path);
		
		// On place effectivement le chemin sur la carte
		switch (wall) {
			case TOP:
				if (position == -1) {
					setObjectAt(width / 2, height - 1, path);
				} else {
					setObjectAt(position, height - 1, path);
				}
				break;
			case BOTTOM:
				if (position == -1) {
					setObjectAt(width / 2, 0, path);
				} else {
					setObjectAt(position, 0, path);
				}
				break;
			case LEFT:
				if (position == -1) {
					setObjectAt(0, height / 2, path);
				} else {
					setObjectAt(0, position, path);
				}
				break;
			case RIGHT:
				if (position == -1) {
					setObjectAt(width - 1, height / 2, path);
				} else {
					setObjectAt(width - 1, position, path);
				}
				break;
		}
	}
	
	public void addCharacter(CharacterData data) {
		characters.add(data);
	}

	public Set<PathData> getPaths(Borders wall) {
		return paths.get(wall);
	}
	
	public boolean containsPath(PathData path) {
		if (path.element != COMMON_DOOR
		&& path.element != DUNGEON_ENTRANCE_DOOR
		&& path.element != DUNGEON_EXIT_DOOR
		&& path.element != PATH_TO_REGION) {
			throw new IllegalArgumentException("This method only accepts path elements.");
		}
		return paths.get(TOP).contains(path)
		|| paths.get(BOTTOM).contains(path)
		|| paths.get(LEFT).contains(path)
		|| paths.get(RIGHT).contains(path);
	}
	
	public List<CharacterData> getCharacters() {
		return characters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				switch (layout[LEVEL_OBJECTS][col][row].element) {
					case COMMON_DOOR:
						sb.append("D ");
						break;
					case DUNGEON_ENTRANCE_DOOR:
						sb.append("I ");
						break;
					case DUNGEON_EXIT_DOOR:
						sb.append("O ");
						break;
					case PATH_TO_REGION:
						sb.append("P ");
						break;
					case ROCK:
						sb.append("R ");
						break;
					case VILLAGE:
						sb.append("V ");
						break;
					case CASTLE:
						sb.append("C ");
						break;
					case WALL:
						sb.append("¤ ");
						break;
					case GROUND:
					case GRASS:
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
