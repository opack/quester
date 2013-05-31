package com.slamdunk.quester.map.logical;

public enum MapElements {
	/**
	 * Aucun �l�ment.
	 */
	EMPTY,
	/**
	 * Brouillard
	 */
	FOG,
	/**
	 * Porte d'entr�e du donjon
	 */
	DUNGEON_ENTRANCE_DOOR,
	/**
	 * Porte de sortie du donjon
	 */
	DUNGEON_EXIT_DOOR,
	/**
	 * Porte normale menant d'une pi�ce � une autre
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
	 * Ch�teau
	 */
	CASTLE,
	/**
	 * Rocher
	 */
	ROCK,
	/**
	 * Similaire � une porte dans un donjon, cet �l�ment
	 * permet de se d�placer vers la r�gion adjacente.
	 */
	PATH_TO_REGION,
	/**
	 * Robot
	 */
	ROBOT;
};
