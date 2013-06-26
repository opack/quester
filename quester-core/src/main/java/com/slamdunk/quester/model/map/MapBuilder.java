package com.slamdunk.quester.model.map;

import static com.slamdunk.quester.model.map.Borders.BOTTOM;
import static com.slamdunk.quester.model.map.Borders.LEFT;
import static com.slamdunk.quester.model.map.Borders.RIGHT;
import static com.slamdunk.quester.model.map.Borders.TOP;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.points.PointManager;
import com.slamdunk.quester.model.points.Point;

public abstract class MapBuilder {
	protected int areaHeight;
	protected final MapArea[][] areas;
	protected boolean areasCreated;
	protected int areaWidth;
	protected Point entranceArea;
	
	protected Point entrancePosition;
	private List<Point> linked;
	
	protected boolean mainEntrancesPlaced;
	protected final int mapHeight;
	
	protected final int mapWidth;
	
	private MapElements pathType;
	protected PointManager pointManager;
	private boolean[][] reachableFromEntrance;
	
	private List<Point> unlinked;
	
	/**
	 * @param pathType Type d'élément représentant un chemin entre deux zones
	 */
	public MapBuilder(int width, int height, MapElements pathType) {
		this.mapWidth = width;
		this.mapHeight = height;
		areas = new MapArea[width][height];
		reachableFromEntrance = new boolean[width][height];
		pointManager = new PointManager(width, height);
		
		this.pathType = pathType;
		
		// Préparation de la liste des salles déjà liées à l'entrée
		linked = new ArrayList<Point>();
		// Préparation de la liste des salles à lier à l'entrée
		unlinked = new ArrayList<Point>();
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				unlinked.add(pointManager.getPoint(col, row));
			}
		}
	}
	
	public MapArea[][] build() {
		if (!areasCreated || !mainEntrancesPlaced) {
			throw new IllegalStateException("areasCreated=" + areasCreated + ", mainEntrancesPlaced=" + mainEntrancesPlaced);
		}
		
		// Création des portes entre les zones.
		// Tant qu'il y a des salles qui ne sont pas accessibles depuis l'entrée...
		while (!unlinked.isEmpty()) {
			// 1. Prendre au hasard une salle non-joignable depuis l'entrée.
			Point unlinkedPos = unlinked.get(MathUtils.random(unlinked.size() - 1));
			
			// 2. Choisir une salle joignable au hasard
			Point linkedPos = linked.get(MathUtils.random(linked.size() - 1));
			
			// 3. Connecter cette salle (via un chemin aléatoire) à l'entrée, ou à
			// une salle accessible depuis l'entrée
			createRandomPath(unlinkedPos, linkedPos);
		}
		
		// DBG
		if (!validateDungeon()) {
			throw new IllegalStateException("La carte générée n'est pas valide.");
		}
		return areas;
	}
	
	/**
	 * Crée les zones du donjon, sans portes mais avec du sol.
	 * Penser à passer le flag roomsCreated à true.
	 */
	public void createAreas(int areaWidth, int areaHeight, WorldElementData defaultBackground) {
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		for (int col = 0; col < mapWidth; col ++) {
			for (int row = 0; row < mapHeight; row ++) {
				// La taille de la zone correspond à la taille de la map,
				// car on n'affiche qu'une zone à chaque fois.
				MapArea room = new MapArea(col, row, areaWidth, areaHeight, defaultBackground);
				fillRoom(room);
				areas[col][row] = room;
			}
		}
		areasCreated = true;
	}

	private void createHorizontalPath(MapArea leftArea, MapArea rightArea) {
		int nbPaths = getNbPathsBetweenAreas();
		int position;
		for (int cur = 0; cur < nbPaths; cur ++) {
			// Récupération d'une position pour placer une porte sur un mur vertical
			position = getPathPosition(LEFT);
			
			// On place un chemin au milieu du mur droit de la première zone
			PathData pathToRight = new PathData(pathType, RIGHT, rightArea.getX(), rightArea.getY());
			leftArea.addPath(RIGHT, pathToRight, position);
			
			// On place un chemin au milieu du mur gauche de la seconde zone
			PathData pathToLeft = new PathData(pathType, LEFT, leftArea.getX(), leftArea.getY());
			rightArea.addPath(LEFT, pathToLeft, position);
		}
	}

	/**
	 * Création d'un chemin aléatoire allant d'une zone à l'autre.
	 * On s'arrête cependant dès qu'on atteint une zone rejoignant
	 * l'entrée.
	 * @param unlinkedPos
	 * @param linkedPos
	 */
	private void createRandomPath(Point from, Point to) {
		int curX = from.getX();
		int curY = from.getY();
		int destinationX = to.getX();
		int destinationY = to.getY();
		final List<Borders> borders = new ArrayList<Borders>();

		// On s'arrête si on arrive à la destination ou si on atteint une zone
		// qui permet de rejoindre l'entrée
		while ((curX != destinationX || curY != destinationY)
		&& !reachableFromEntrance[curX][curY]) {
			// Bientôt, cette salle sera accessible depuis l'entrée
			linkArea(curX, curY);
			
			borders.clear();
			// Si la sortie est plus en haut, on autorise un offset vers le haut
			if (destinationY > curY) {
				borders.add(TOP);
			}
			// Si la sortie est plus en bas, on autorise un offset vers le bas
			if (destinationY < curY) {
				borders.add(BOTTOM);
			}
			// Si la sortie est plus à gauche, on autorise un offset vers la gauche
			if (destinationX < curX) {
				borders.add(LEFT);
			}
			// Si la sortie est plus à droite, on autorise un offset vers la droite
			if (destinationX > curX) {
				borders.add(RIGHT);
			}
			// 1. Choix d'un mur
			// 2. Création du chemin entre les zones
			// 3. Màj du tableau des joignables : la zone destination sera connectée à l'entrée au final
			// 4. Déplacement du curseur en direction de la destination
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

	private void createVerticalPath(MapArea topArea, MapArea bottomArea) {
		int nbPaths = getNbPathsBetweenAreas();
		int position;
		for (int cur = 0; cur < nbPaths; cur ++) {
			// Récupération d'une position pour placer une porte sur un mur horizontal
			position = getPathPosition(TOP);
			
			// On place un chemin au milieu du mur bas de la première zone
			PathData pathToBottom = new PathData(pathType, BOTTOM, bottomArea.getX(), bottomArea.getY());
			topArea.addPath(BOTTOM, pathToBottom, position);
			
			// On place un chemin au milieu du mur haut de la seconde zone
			PathData pathToTop = new PathData(pathType, TOP, topArea.getX(), topArea.getY());
			bottomArea.addPath(TOP, pathToTop, position);
		}
	}
	
	/**
	 * Remplit une zone
	 */
	protected abstract void fillRoom(MapArea room);

	public int getAreaHeight() {
		return areaHeight;
	}
	
	public int getAreaWidth() {
		return areaWidth;
	}
	
	public MapElements getPathType() {
		return pathType;
	}

	public Point getEntrancePosition() {
		return entrancePosition;
	}
	
	public Point getEntranceRoom() {
		return entranceArea;
	}

	public int getMapHeight() {
		return mapHeight;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	/**
	 * Retourne le nombre de chemins à créer entre 2 zones. Une classe fille
	 * peut redéfinir cette méthode pour créer plusieurs chemins entre les zones.
	 * @return
	 */
	protected int getNbPathsBetweenAreas() {
		return 1;
	}
	
	/**
	 * Retourne l'emplacement du chemin sur le bord indiqué.
	 * @param border
	 * @return
	 */
	protected int getPathPosition(Borders border) {
		int position = 0;
		switch (border) {
			// Les murs horizontaux
			case TOP:
			case BOTTOM:
				position = areaWidth / 2;
				break;
				
			// Les murs verticaux
			case LEFT:
			case RIGHT:
				position = areaHeight / 2;
				break;
		}
		return position;
	}
	
	/**
	 * Met à jour les différents objets pour indiquer que la zone
	 * aux coordonnées spécifiées est accessible depuis l'entrée
	 * @param curX
	 * @param curY
	 */
	protected void linkArea(int x, int y) {
		linkArea(pointManager.getPoint(x, y));
	}

	protected void linkArea(Point pos) {
		reachableFromEntrance[pos.getX()][pos.getY()] = true;
		unlinked.remove(pos);
		linked.add(pos);
	}

	/**
	 * Choisit l'entrée (et éventuellement la sortie) de la carte.
	 * Penser à passer le flag mainEntrancesPlaced à true.
	 */
	public abstract void placeMainEntrances();

	public void printMap() {
		StringBuilder sb = new StringBuilder();
		for (int row = mapHeight- 1; row >= 0; row --) {
			for (int col = 0; col < mapWidth; col ++) {
				Point pos = pointManager.getPoint(col, row);
				MapArea area = areas[col][row];
				if (area == null) {
					// Dessin d'une salle inaccessible
					sb.append("# ");
				} else {
					// Dessin d'une salle accessible
					if (pos.equals(entranceArea)) {
						sb.append("A");
					} else {
						sb.append("O");
					}
					
					// Y'a-t-il un chemin vers la droite ?
					if (!area.getPaths(RIGHT).isEmpty()) {
						sb.append("-");
					} else {
						sb.append(" ");
					}
				}
			}
			sb.append("\n");
			// Passe n°2 pour dessiner les chemins vers le bas
			for (int col = 0; col < mapWidth; col ++) {
				MapArea area = areas[col][row];
				if (area != null
				&& !area.getPaths(BOTTOM).isEmpty()) {
					sb.append("| ");
				} else {
					sb.append("  ");
				}
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

	public void setLinkType(MapElements doorType) {
		this.pathType = doorType;
	}
	
	protected boolean validateDungeon() {
		// Par défaut, le donjon est valide
		return true;
	}
}
