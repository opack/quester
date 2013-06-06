package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.model.map.MapElements.EMPTY;
import static com.slamdunk.quester.model.map.MapElements.FOG;
import static com.slamdunk.quester.model.map.MapElements.GRASS;
import static com.slamdunk.quester.model.map.MapElements.GROUND;
import static com.slamdunk.quester.model.map.MapElements.ROCK;
import static com.slamdunk.quester.model.map.MapElements.VILLAGE;
import static com.slamdunk.quester.model.map.MapElements.WALL;
import static com.slamdunk.quester.model.map.MapElements.PATH_MARKER;
import static com.slamdunk.quester.model.map.Borders.TOP;

import com.slamdunk.quester.model.map.MapElements;

public class ElementData {
	/**
	 * El�ments r�utilisables
	 */
	public static final PathData DUNGEON_EXIT_DOOR_DATA = new PathData(DUNGEON_EXIT_DOOR, TOP, -1, -1); // TOP est choisi au hasard et ne sera jamais utilis�
	public static final PathData DUNGEON_ENTRANCE_DOOR_DATA = new PathData(DUNGEON_ENTRANCE_DOOR, TOP, -1, -1); // TOP est choisi au hasard et ne sera jamais utilis�
	public static final ElementData WALL_DATA = new ObstacleData(WALL);
	public static final ElementData GROUND_DATA = new ElementData(GROUND);
	public static final ElementData VILLAGE_DATA = new ObstacleData(VILLAGE);
	public static final ElementData ROCK_DATA = new ObstacleData(ROCK);
	public static final ElementData GRASS_DATA = new ElementData(GRASS);
	public static final ElementData FOG_DATA = new ElementData(FOG);
	public static final ElementData PATH_MARKER_DATA = new ElementData(PATH_MARKER);
	
	public MapElements element;
	public boolean isSolid;
	/**
	 * Indique l'ordre de jeu de cet �l�ment
	 */
	public int playRank;
	
	/**
	 * Instances statiques des ElementData tr�s fr�quemment utilis�s
	 * et identiques � chaque fois
	 */
	public static final ElementData EMPTY_DATA = new ElementData(EMPTY);
	
	public ElementData() {
		isSolid = false;
	}
	
	public ElementData(MapElements element) {
		this();
		this.element = element;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementData)) {
			return false;
		}
		return ((ElementData)obj).element == element;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal();
	}
}
