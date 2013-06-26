package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.data.WorldElementData.EMPTY_DATA;
import static com.slamdunk.quester.model.map.Borders.BOTTOM;
import static com.slamdunk.quester.model.map.Borders.LEFT;
import static com.slamdunk.quester.model.map.Borders.RIGHT;
import static com.slamdunk.quester.model.map.Borders.TOP;
import static com.slamdunk.quester.model.map.MapElements.COMMON_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.model.map.MapElements.PATH_TO_REGION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.data.WorldElementData;

/**
 * Données logiques d'une zone de la carte. Seule la structure de la zone
 * (sols, murs, portes) est retenue ; tout ce qui est éphémère (monstres,
 * trésors...) n'est pas indiqué.
 * @author Didier
 *
 */
public class MapArea {
	/**
	 * Personnages présents dans la pièce. On ne retient pas leurs coordonnées,
	 * ils seront réinstanciés à chaque entrée dans la pièce.
	 */
	private final List<CharacterData> characters;
	/**
	 * Distance de la zone par rapport à la zone de départ
	 */
	private int distance;
	
	private final int height;
	/**
	 * Booléen indiquant si les personnages morts dans cette zone doivent en être
	 * définitivement supprimés. Cela signifie que si le joueur y pénètre de nouveau
	 * par la suite, les personnages morts ne seront pas recréés.
	 */
	private boolean isPermKillCharacters;
	
	/**
	 * Structure de la pièce. Le niveau 0 correspond au fond, et le niveau 1
	 * correspond aux objets présents dans la pièce (portes, trésors...).
	 */
	private final Map<MapLevels, WorldElementData[][]> layout;
	
	/**
	 * Chemins permettant d'accéder à une zone adjacente
	 */
	private final Map<Borders, Set<PathData>> paths;
	
	/**
	 * Taille de la pièce en cellules
	 */
	private final int width;
	
	/**
	 * Position de la zone dans l'ensemble du monde
	 */
	private final int x;
	
	private final int y;
	
	public MapArea(int x, int y, int width, int height, WorldElementData defaultBackground) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		WorldElementData empty = EMPTY_DATA;
		WorldElementData[][] groundLayer = new WorldElementData[width][height];
		WorldElementData[][] objectsLayer = new WorldElementData[width][height];
		WorldElementData[][] fogLayer = new WorldElementData[width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(groundLayer[col], defaultBackground);
			Arrays.fill(objectsLayer[col], empty);
			Arrays.fill(fogLayer[col], empty);
		}
		layout = new HashMap<MapLevels, WorldElementData[][]>();
		layout.put(MapLevels.GROUND, groundLayer);
		layout.put(MapLevels.OBJECTS, objectsLayer);
		layout.put(MapLevels.FOG, fogLayer);		
		
		paths = new HashMap<Borders, Set<PathData>>();
		for (Borders border : Borders.values()) {
			paths.put(border, new HashSet<PathData>());
		}
		
		characters = new ArrayList<CharacterData>();
	}

	public void addCharacter(CharacterData data) {
		characters.add(data);
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

	public boolean containsPath(MapElements path) {
		if (path != COMMON_DOOR
		&& path != DUNGEON_ENTRANCE_DOOR
		&& path != DUNGEON_EXIT_DOOR
		&& path != PATH_TO_REGION) {
			throw new IllegalArgumentException("This method only accepts path elements.");
		}
		return containsPath(paths.get(TOP), path)
		|| containsPath(paths.get(BOTTOM), path)
		|| containsPath(paths.get(LEFT), path)
		|| containsPath(paths.get(RIGHT), path);
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

	private boolean containsPath(Set<PathData> set, MapElements path) {
		for (PathData data : paths.get(TOP)) {
			if (data.element == path) {
				return true;
			}
		}
		return false;
	}
	
	public WorldElementData getAt(MapLevels layer, int x, int y) {
		if (x < 0 || x >= width
		|| y < 0 || y >= height) {
			return null;
		}
		WorldElementData[][] layerData = layout.get(layer);
		if (layerData == null) {
			return null;
		}
		return layerData[x][y];
	}
	
	public List<CharacterData> getCharacters() {
		return characters;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public WorldElementData getFogAt(int x, int y) {
		return getAt(MapLevels.FOG, x, y);
	}
	
	public WorldElementData getGroundAt(int x, int y) {
		return getAt(MapLevels.GROUND, x, y);
	}
	
	public int getHeight() {
		return height;
	}
	
	public WorldElementData getObjectAt(int x, int y) {
		return getAt(MapLevels.OBJECTS, x, y);
	}
	
	public Set<PathData> getPaths(Borders wall) {
		return paths.get(wall);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public boolean isPermKillCharacters() {
		return isPermKillCharacters;
	}
	
	public void setAt(MapLevels layer, int x, int y, WorldElementData data) {
		if (x < 0 || x >= width
		|| y < 0 || y >= height) {
			return;
		}
		WorldElementData[][] layerData = layout.get(layer);
		if (layerData == null) {
			return;
		}
		layerData[x][y] = data;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public void setFogAt(int x, int y, WorldElementData element) {
		setAt(MapLevels.FOG, x, y, element);
	}
	
	public void setGroundAt(int x, int y, WorldElementData element) {
		setAt(MapLevels.GROUND, x, y, element);
	}

	public void setObjectAt(int x, int y, WorldElementData element) {
		setAt(MapLevels.OBJECTS, x, y, element);
	}

	public void setPermKillCharacters(boolean isPermKillCharacters) {
		this.isPermKillCharacters = isPermKillCharacters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		WorldElementData[][] objectsLayer = layout.get(MapLevels.OBJECTS);
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				switch (objectsLayer[col][row].element) {
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
