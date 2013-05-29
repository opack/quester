package com.slamdunk.quester.map.world;

import static com.slamdunk.quester.map.world.WorldElements.EMPTY;

import java.util.Arrays;

import com.slamdunk.quester.map.points.Point;

/**
 * Donn�es logiques d'une r�gion du monde. Seule la structure (sols, villages...)
 * est retenue ; tout ce qui est �ph�m�re (monstres, tr�sors...) n'est pas indiqu�.
 * @author Didier
 *
 */
public class WorldRegion {
	/**
	 * Taille du monde cellules
	 */
	private final int width;
	private final int height;
	
	/**
	 * Structure de la r�gion
	 */
	private WorldElements[][] layout;
	
	/**
	 * Position du village de d�part
	 */
	private Point startVillagePosition;
	
	public WorldRegion(int width, int height, WorldElements defaultElement) {
		this.width = width;
		this.height = height;
		layout = new WorldElements[width][height];
		for (int col = 0; col < width; col++) {
			Arrays.fill(layout[col], defaultElement);
		}
	}
	
	public WorldRegion(int width, int height) {
		this(width, height, EMPTY);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public WorldElements get(int x, int y) {
		return layout[x][y];
	}
	
	public void set(int x, int y, WorldElements element) {
		layout[x][y] = element;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = height - 1; row >= 0; row--) {
			for (int col = 0; col < width; col++) {
				switch (layout[col][row]) {
					case GRASS:
						sb.append("  ");
						break;
					case ROCK:
						sb.append("� ");
						break;
					case PATH_TO_REGION:
						sb.append("D ");
						break;
					case VILLAGE:
						sb.append("V ");
						break;
					case CASTLE:
						sb.append("C ");
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

	public Point getStartVillagePosition() {
		return startVillagePosition;
	}

	public void setStartVillagePosition(Point startVillagePosition) {
		this.startVillagePosition = startVillagePosition;
	}
}
