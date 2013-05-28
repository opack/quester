package com.slamdunk.quester.map.dungeon;

import static com.slamdunk.quester.map.dungeon.RoomElements.COMMON_DOOR;
import static com.slamdunk.quester.map.dungeon.RoomElements.DUNGEON_ENTRANCE_DOOR;
import static com.slamdunk.quester.map.dungeon.RoomElements.DUNGEON_EXIT_DOOR;

import java.util.Arrays;

/**
 * Donn�es logiques d'une pi�ce de donjon. Seule la structure de la pi�ce
 * (sols, murs, portes) est retenue ; tout ce qui est �ph�m�re (monstres, tr�sors...) n'est pas
 * indiqu�.
 * @author Didier
 *
 */
public class DungeonRoom {
	/**
	 * Taille de la pi�ce en cellules
	 */
	private final int width;
	private final int height;
	
	/**
	 * Structure de la pi�ce
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
				set(width / 2, height - 1, door);
				break;
			case BOTTOM:
				set(width / 2, 0, door);
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
	
	public boolean containsDoor(RoomElements door) {
		if (door != COMMON_DOOR
		&& door != DUNGEON_ENTRANCE_DOOR
		&& door != DUNGEON_EXIT_DOOR) {
			throw new IllegalArgumentException("This method only accepts door elements.");
		}
		return doors[RoomWalls.TOP.ordinal()] == door
		|| doors[RoomWalls.BOTTOM.ordinal()] == door
		|| doors[RoomWalls.LEFT.ordinal()] == door
		|| doors[RoomWalls.RIGHT.ordinal()] == door;
	}
	
	public boolean containsCommonDoor() {
		return containsDoor(COMMON_DOOR);
	}
	
	public boolean isEntranceRoom() {
		return containsDoor(DUNGEON_ENTRANCE_DOOR);
	}
	
	public boolean isExitRoom() {
		return containsDoor(DUNGEON_EXIT_DOOR);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				switch (layout[col][row]) {
				case COMMON_DOOR:
					sb.append("D ");
					break;
				case DUNGEON_ENTRANCE_DOOR:
					sb.append("I ");
					break;
				case DUNGEON_EXIT_DOOR:
					sb.append("O ");
					break;
				case GROUND:
					sb.append("  ");
					break;
				case WALL:
					sb.append("� ");
					break;
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