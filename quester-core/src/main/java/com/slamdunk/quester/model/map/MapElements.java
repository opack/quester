package com.slamdunk.quester.model.map;

public enum MapElements {
	/**
	 * Château
	 */
	CASTLE,
	/**
	 * Porte normale menant d'une pièce à une autre
	 */
	COMMON_DOOR,
	/**
	 * Ombre ou lumière
	 */
	DARKNESS,
	/**
	 * Porte d'entrée du donjon
	 */
	DUNGEON_ENTRANCE_DOOR,
	/**
	 * Porte de sortie du donjon
	 */
	DUNGEON_EXIT_DOOR,
	/**
	 * Aucun élément.
	 */
	EMPTY,
	/**
	 * Herbe
	 */
	GRASS,
	/**
	 * Sol
	 */
	GROUND,
	/**
	 * Marqueur de chemin
	 */
	PATH_MARKER,
	/**
	 * Similaire à une porte dans un donjon, cet élément
	 * permet de se déplacer vers la région adjacente.
	 */
	PATH_TO_REGION,
	/**
	 * Joueur
	 */
	PLAYER,
	/**
	 * Robot
	 */
	RABITE,
	/**
	 * Rocher
	 */
	ROCK,
	/**
	 * Village
	 */
	VILLAGE,
	/**
	 * Rocher
	 */
	WALL;
};
