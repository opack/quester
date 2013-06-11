package com.slamdunk.quester.model.map;

public enum MapElements {
	/**
	 * Aucun élément.
	 */
	EMPTY,
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
	 * Porte normale menant d'une pièce à une autre
	 */
	COMMON_DOOR,
	/**
	 * Rocher
	 */
	WALL,
	/**
	 * Sol
	 */
	GROUND,
	/**
	 * Herbe
	 */
	GRASS,
	/**
	 * Village
	 */
	VILLAGE,
	/**
	 * Château
	 */
	CASTLE,
	/**
	 * Rocher
	 */
	ROCK,
	/**
	 * Similaire à une porte dans un donjon, cet élément
	 * permet de se déplacer vers la région adjacente.
	 */
	PATH_TO_REGION,
	/**
	 * Robot
	 */
	RABITE,
	/**
	 * Joueur
	 */
	PLAYER,
	/**
	 * Marqueur de chemin
	 */
	PATH_MARKER;
};
