package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.map.MapElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.model.map.MapElements.DUNGEON_EXIT_DOOR;
import static com.slamdunk.quester.model.map.MapElements.EMPTY;
import static com.slamdunk.quester.model.map.MapElements.FOG;
import static com.slamdunk.quester.model.map.MapElements.GRASS;
import static com.slamdunk.quester.model.map.MapElements.GROUND;
import static com.slamdunk.quester.model.map.MapElements.ROCK;
import static com.slamdunk.quester.model.map.MapElements.VILLAGE;
import static com.slamdunk.quester.model.map.MapElements.WALL;

public class ElementData {
	/**
	 * Eléments réutilisables
	 */
	public static final PathData DUNGEON_EXIT_DOOR_DATA = new PathData(DUNGEON_EXIT_DOOR, -1, -1);
	public static final PathData DUNGEON_ENTRANCE_DOOR_DATA = new PathData(DUNGEON_ENTRANCE_DOOR, -1, -1);
	public static final ElementData WALL_DATA = new ElementData(WALL);
	public static final ElementData GROUND_DATA = new ElementData(GROUND);
	public static final ElementData VILLAGE_DATA = new ElementData(VILLAGE);
	public static final ElementData ROCK_DATA = new ElementData(ROCK);
	public static final ElementData GRASS_DATA = new ElementData(GRASS);
	public static final ElementData FOG_DATA = new ElementData(FOG);
	
	public MapElements element;
	
	/**
	 * Instances statiques des ElementData très fréquemment utilisés
	 * et identiques à chaque fois
	 */
	public static final ElementData EMPTY_DATA = new ElementData(EMPTY);
	
	public ElementData() {
	}
	
	public ElementData(MapElements element) {
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
