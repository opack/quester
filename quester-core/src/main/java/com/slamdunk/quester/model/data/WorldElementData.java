package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.EMPTY;
import static com.slamdunk.quester.model.map.MapElements.GRASS;
import static com.slamdunk.quester.model.map.MapElements.GROUND;
import static com.slamdunk.quester.model.map.MapElements.PATH_MARKER;
import static com.slamdunk.quester.model.map.MapElements.ROCK;
import static com.slamdunk.quester.model.map.MapElements.VILLAGE;
import static com.slamdunk.quester.model.map.MapElements.WALL;

import com.slamdunk.quester.model.map.MapElements;

public class WorldElementData {
	/**
	 * Instances statiques des ElementData très fréquemment utilisés
	 * et identiques à chaque fois
	 */
	public static final WorldElementData EMPTY_DATA = new WorldElementData(EMPTY);
	public static final WorldElementData WALL_DATA = new ObstacleData(WALL);
	public static final WorldElementData GROUND_DATA = new WorldElementData(GROUND);
	public static final WorldElementData VILLAGE_DATA = new ObstacleData(VILLAGE);
	public static final WorldElementData ROCK_DATA = new ObstacleData(ROCK);
	public static final WorldElementData GRASS_DATA = new WorldElementData(GRASS);
	public static final WorldElementData PATH_MARKER_DATA = new WorldElementData(PATH_MARKER);
	
	public MapElements element;
	public boolean isSolid;
	/**
	 * Indique l'ordre de jeu de cet élément
	 */
	public int playRank;
	
	public WorldElementData() {
		isSolid = false;
	}
	
	public WorldElementData(MapElements element) {
		this();
		this.element = element;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorldElementData)) {
			return false;
		}
		return ((WorldElementData)obj).element == element;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal();
	}
}
