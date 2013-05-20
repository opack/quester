package com.slamdunk.quester.screens;

import static com.slamdunk.quester.screens.RoomElements.COMMON_DOOR;
import static com.slamdunk.quester.screens.RoomElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.screens.RoomElements.DUNGEON_EXIT_DOOR;

import java.util.Arrays;

/**
 * Données logiques d'une pièce de donjon. Seule la structure de la pièce
 * (sols, murs, portes) est retenue ; tout ce qui est éphémère (monstres, trésors...) n'est pas
 * indiqué.
 * @author Didier
 *
 */
public class DungeonRoom {
	/**
	 * Taille de la pièce en cellules
	 */
	private final int width;
	private final int height;
	
	/**
	 * Structure de la pièce
	 */
	private RoomElements[][] layout;
	
	private RoomElements[] doors;
	
	public DungeonRoom(int width, int height) {
		this.width = width;
		this.height = height;
		layout = new RoomElements[width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(layout[col], RoomElements.EMPTY);
		}
		doors = new RoomElements[4];
		Arrays.fill(doors, RoomElements.EMPTY);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public RoomElements get(int x, int y) {
		return layout[x][y];
	}
	
	public void set(int x, int y, RoomElements element) {
		layout[x][y] = element;
	}
	
	public void setDoor(RoomWalls wall, RoomElements door) {
		if (door != COMMON_DOOR
		&& door != DUNGEON_ENTRANCE_DOOR
		&& door != DUNGEON_EXIT_DOOR){
			throw new IllegalArgumentException("DungeonRoom.setDoor : " + door + " is not a door !");
		}
		doors[wall.ordinal()] = door;
		switch (wall) {
			case TOP:
				set(width / 2, 0, door);
				break;
			case BOTTOM:
				set(width / 2, height - 1, door);
				break;
			case LEFT:
				set(0, height / 2, door);
				break;
			case RIGHT:
				set(width - 1, height / 2, door);
				break;
		}
	}

	public RoomElements getDoor(RoomWalls wall) {
		return doors[wall.ordinal()];
	}
	
	public boolean containsCommonDoor() {
		return doors[RoomWalls.TOP.ordinal()] == COMMON_DOOR
		|| doors[RoomWalls.BOTTOM.ordinal()] == COMMON_DOOR
		|| doors[RoomWalls.LEFT.ordinal()] == COMMON_DOOR
		|| doors[RoomWalls.RIGHT.ordinal()] == COMMON_DOOR;
	}
}
