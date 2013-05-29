package com.slamdunk.quester.map.logical;

import static com.slamdunk.quester.map.logical.Borders.BOTTOM;
import static com.slamdunk.quester.map.logical.Borders.LEFT;
import static com.slamdunk.quester.map.logical.Borders.RIGHT;
import static com.slamdunk.quester.map.logical.Borders.TOP;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.map.points.PointManager;
import com.slamdunk.quester.map.points.UnmutablePoint;

public abstract class MapBuilder {
	protected final MapArea[][] areas;
	protected final int width;
	protected final int height;
	
	protected UnmutablePoint entranceArea;
	protected UnmutablePoint entrancePosition;
	
	protected boolean areasCreated;
	protected boolean mainEntrancesPlaced;
	
	private boolean[][] reachableFromEntrance;
	
	protected PointManager pointManager;
	private List<UnmutablePoint> linked;
	private List<UnmutablePoint> unlinked;
	
	private MapElements pathType;
	
	/**
	 * @param pathType Type d'�l�ment repr�sentant un chemin entre deux zones
	 */
	public MapBuilder(int width, int height, MapElements pathType) {
		this.width = width;
		this.height = height;
		areas = new MapArea[width][height];
		reachableFromEntrance = new boolean[width][height];
		pointManager = new PointManager(width, height);
		
		this.pathType = pathType;
		
		// Pr�paration de la liste des salles d�j� li�es � l'entr�e
		linked = new ArrayList<UnmutablePoint>();
		// Pr�paration de la liste des salles � lier � l'entr�e
		unlinked = new ArrayList<UnmutablePoint>();
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				unlinked.add(pointManager.getPoint(col, row));
			}
		}
	}
	
	public MapElements getDoorType() {
		return pathType;
	}

	public void setLinkType(MapElements doorType) {
		this.pathType = doorType;
	}

	public MapArea[][] build() {
		if (!areasCreated || !mainEntrancesPlaced) {
			throw new IllegalStateException("areasCreated=" + areasCreated + ", mainEntrancesPlaced=" + mainEntrancesPlaced);
		}
		
		// Cr�ation des portes entre les zones.
		// Tant qu'il y a des salles qui ne sont pas accessibles depuis l'entr�e...
		while (!unlinked.isEmpty()) {
			// 1. Prendre au hasard une salle non-joignable depuis l'entr�e.
			UnmutablePoint unlinkedPos = unlinked.get(MathUtils.random(unlinked.size() - 1));
			
			// 2. Choisir une salle joignable au hasard
			UnmutablePoint linkedPos = linked.get(MathUtils.random(linked.size() - 1));
			
			// 3. Connecter cette salle (via un chemin al�atoire) � l'entr�e, ou �
			// une salle accessible depuis l'entr�e
			createRandomPath(unlinkedPos, linkedPos);
		}
		
		// DBG
		if (!validateDungeon()) {
			throw new IllegalStateException("La carte g�n�r�e n'est pas valide.");
		}
		return areas;
	}
	
	protected boolean validateDungeon() {
		// Par d�faut, le donjon est valide
		return true;
	}

	/**
	 * Cr�ation d'un chemin al�atoire allant d'une zone � l'autre.
	 * On s'arr�te cependant d�s qu'on atteint une zone rejoignant
	 * l'entr�e.
	 * @param unlinkedPos
	 * @param linkedPos
	 */
	private void createRandomPath(UnmutablePoint from, UnmutablePoint to) {
		int curX = from.getX();
		int curY = from.getY();
		int exitX = to.getX();
		int exitY = to.getY();
		final List<Borders> borders = new ArrayList<Borders>();

		// On s'arr�te si on arrive � la destination ou si on atteint une zone
		// qui permet de rejoindre l'entr�e
		while ((curX != exitX || curY != exitY)
		&& !reachableFromEntrance[curX][curY]) {
			// Bient�t, cette salle sera accessible depuis l'entr�e
			linkArea(curX, curY);
			
			borders.clear();
			// Si la sortie est plus en haut, on autorise un offset vers le haut
			if (exitY > curY) {
				borders.add(TOP);
			}
			// Si la sortie est plus en bas, on autorise un offset vers le bas
			if (exitY < curY) {
				borders.add(BOTTOM);
			}
			// Si la sortie est plus � gauche, on autorise un offset vers la gauche
			if (exitX < curX) {
				borders.add(LEFT);
			}
			// Si la sortie est plus � droite, on autorise un offset vers la droite
			if (exitX > curX) {
				borders.add(RIGHT);
			}
			// 1. Choix d'un mur
			// 2. Cr�ation du chemin entre les zones
			// 3. M�j du tableau des joignables : la zone destination sera connect�e � l'entr�e au final
			// 4. D�placement du curseur en direction de la destination
			Borders choosenBorder = borders.get(MathUtils.random(borders.size() - 1));
			switch (choosenBorder) {
				case TOP:
					createVerticalPath(areas[curX][curY + 1], areas[curX][curY]);
					curY++;
					break;
				case BOTTOM:
					createVerticalPath(areas[curX][curY], areas[curX][curY - 1]);
					curY--;
					break;
				case LEFT:
					createHorizontalPath(areas[curX - 1][curY], areas[curX][curY]);
					curX--;
					break;
				case RIGHT:
					createHorizontalPath(areas[curX][curY], areas[curX + 1][curY]);
					curX++;
					break;
			}
		}
	}
	
	/**
	 * Met � jour les diff�rents objets pour indiquer que la zone
	 * aux coordonn�es sp�cifi�es est accessible depuis l'entr�e
	 * @param curX
	 * @param curY
	 */
	protected void linkArea(int x, int y) {
		linkArea(pointManager.getPoint(x, y));
	}
	
	protected void linkArea(UnmutablePoint pos) {
		reachableFromEntrance[pos.getX()][pos.getY()] = true;
		unlinked.remove(pos);
		linked.add(pos);
	}

	private void createHorizontalPath(MapArea leftArea, MapArea rightArea) {
		// On place un chemin au milieu du mur droit de la premi�re zone
		leftArea.setPath(RIGHT, pathType);
		
		// On place un chemin au milieu du mur gauche de la seconde zone
		rightArea.setPath(LEFT, pathType);
	}
	
	private void createVerticalPath(MapArea topArea, MapArea bottomArea) {
		// On place un chemin au milieu du mur bas de la premi�re zone
		topArea.setPath(BOTTOM, pathType);
		
		// On place un chemin au milieu du mur haut de la seconde zone
		bottomArea.setPath(TOP, pathType);
	}

	/**
	 * Cr�e les zones du donjon, sans portes mais avec du sol.
	 * Penser � passer le flag roomsCreated � true.
	 */
	public void createRooms(int roomWidth, int roomHeight) {
		for (int col = 0; col < width; col ++) {
			for (int row = 0; row < height; row ++) {
				// La taille de la zone correspond � la taille de la map,
				// car on n'affiche qu'une zone � chaque fois.
				MapArea room = new MapArea(roomWidth, roomHeight);
				fillRoom(room);
				areas[col][row] = room;
			}
		}
		areasCreated = true;
	}
	
	/**
	 * Remplit une zone
	 */
	protected abstract void fillRoom(MapArea room);

	/**
	 * Choisit l'entr�e (et �ventuellement la sortie) de la carte.
	 * Penser � passer le flag mainEntrancesPlaced � true.
	 */
	public abstract void placeMainEntrances();
	
	public UnmutablePoint getEntranceRoom() {
		return entranceArea;
	}
	
	public UnmutablePoint getEntrancePosition() {
		return entrancePosition;
	}
}
