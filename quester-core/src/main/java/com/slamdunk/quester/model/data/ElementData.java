package com.slamdunk.quester.model.data;

import static com.slamdunk.quester.model.map.MapElements.EMPTY;
import static com.slamdunk.quester.model.map.MapElements.FOG;
import static com.slamdunk.quester.model.map.MapElements.GRASS;
import static com.slamdunk.quester.model.map.MapElements.GROUND;
import static com.slamdunk.quester.model.map.MapElements.PATH_MARKER;
import static com.slamdunk.quester.model.map.MapElements.ROCK;
import static com.slamdunk.quester.model.map.MapElements.VILLAGE;
import static com.slamdunk.quester.model.map.MapElements.WALL;

import com.slamdunk.quester.model.map.MapElements;

public class ElementData {
	/**
	 * Instances statiques des ElementData très fréquemment utilisés
	 * et identiques à chaque fois
	 */
	public static final ElementData EMPTY_DATA = new ElementData(EMPTY);
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
	 * Indique l'ordre de jeu de cet élément
	 */
	public int playRank;
	
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
