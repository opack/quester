package com.slamdunk.quester.display.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.display.actors.Castle;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.Obstacle;
import com.slamdunk.quester.display.actors.PathToRegion;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Village;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.ia.CharacterIA;
import com.slamdunk.quester.ia.IA;
import com.slamdunk.quester.map.logical.MapArea;
import com.slamdunk.quester.map.logical.MapBuilder;
import com.slamdunk.quester.map.logical.WorldBuilder;
import com.slamdunk.quester.map.physical.MapCell;
import com.slamdunk.quester.map.physical.MapLayer;
import com.slamdunk.quester.map.points.Point;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class WorldMapScreen extends AbstractMapScreen implements CharacterListener  {
	private int worldWidth;
	private int worldHeight;
	
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private Player player;
	
	private final MapArea[][] regions;
	private final Point currentRegion;
	
	private boolean isFirstDisplay;
	
	public WorldMapScreen(
			int worldWidth, int worldHeight,
			int regionWidth, int regionHeight,
			int worldCellWidth, int worldCellHeight) {
		super(regionWidth, regionHeight, worldCellWidth, worldCellHeight);
		// Crée le mooooooonde !
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		MapBuilder builder = createMapBuilder();
		regions = builder.build();
		
		// Crée le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
		
		// Crée le hud
		createHud();
		
		// Affiche le monde
		UnmutablePoint entrance = builder.getEntranceRoom();
        currentRegion = new Point(entrance.getX(), entrance.getY());
        UnmutablePoint entrancePosition = builder.getEntrancePosition();
        
        DisplayData data = new DisplayData();
        data.regionX = currentRegion.getX();
        data.regionY = currentRegion.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        displayWorld(data);
        
        // DBG Rustine pour réussir à centrer sur le joueur lors de l'affichage
        // de la toute première pièce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute première fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}
	
	private MapBuilder createMapBuilder() {
		MapBuilder builder = new WorldBuilder(worldWidth, worldHeight);
		builder.createRooms(getMapWidth(), getMapHeight());
		builder.placeMainEntrances();
		return builder;
	}
	
	private void createPlayer() {
		IA ia = new CharacterIA();
		player = new Player("Player", ia, this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(3);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        player.addListener(this);
	}

	/**
	 * Crée le HUD
	 */
	private void createHud() {
		hud = new HUD(this);
		hud.setMiniMap(worldWidth, worldHeight, 6, 4);
		// Ajout du HUD à la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute même en premier pour qu'il gère les clics avant le reste du donjon.
		getStages().add(0, hud);
		player.addListener(hud);
		// Comme le Character a déjà été créé, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public void render (float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(player);
		}
		
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
		player.act(delta);
		
        // Dessine la scène et le hud
        mainStage.draw();
        hud.draw();
        
        fpsLogger.log();
	}

	@Override
	public void pause () {
		// TODO Sauvegarde de l'état courant
	}

	@Override
	public void resume () {
		// TODO Restauration de l'état précédent
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
		// Mise à jour du hud et en particulier du pad
		hud.update(currentRegion.getX(), currentRegion.getY());
		
		// Rien d'autre à faire : seul le joueur joue. Une fois son tour fini, c'est encore à lui de jouer :)
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void displayWorld(Object data) {
		DisplayData display = (DisplayData)data;
		
		MapArea region = regions[display.regionX][display.regionY];
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
        // Nettoyage de la pièce actuelle
		clearMap();

		// Création du fond
		WorldActor element = null;
		for (int col = 0; col < region.getWidth(); col++) {
			for (int row = 0; row < region.getHeight(); row++) {
				// On met toujours de l'herbe
				element = new Ground(Assets.grass, col, row, this);
				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
				
				// Et on ajoute éventuellement des choses dessus
				switch (region.get(col, row)) {
					case ROCK:
						element = new Obstacle(Assets.rock, col, row, this);
						obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
						screenMap.setWalkable(col, row, false);
						break;
					case PATH_TO_REGION:
						element = createPathToRegion(col, row, display.regionX, display.regionY);
						obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
						break;
					case VILLAGE:
						element = new Village(Assets.village, col, row, this);
						obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
						break;
					case CASTLE:
						Castle castle = new Castle(Assets.castle, col, row, this);
						// TODO Ne pas mettre des valeurs en dur
						castle.setDungeonWidth(3);
						castle.setDungeonHeight(3);
						castle.setRoomWidth(9);
						castle.setRoomHeight(11);
						obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, castle));
						break;
				}
			}
		}

		// Création de la liste des personnages
		player.setPositionInWorld(display.playerX, display.playerY);
		characters.add(player);
		charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.playerX, display.playerY, player));

		// La salle actuellement affichée a changé
        currentRegion.setXY(display.regionX, display.regionY);
        
        // Mise à jour du pad et de la minimap
     	hud.update(display.regionX, display.regionY);

		// Centrage de la caméra sur le joueur
		centerCameraOn(player);
	}
	
	private WorldActor createPathToRegion(int col, int row, int regionX, int regionY) {
		// Chemin vers la gauche
		WorldActor element = null;
 		if (col == 0) {
 			element = new PathToRegion(Assets.pathToRegionLeft, col, row, this, regionX - 1, regionY);
 		}
 		// Chemin vers la droite
 		else if (col == mapWidth - 1) {
 			element = new PathToRegion(Assets.pathToRegionRight, col, row, this, regionX + 1, regionY);
 		}
 		// Chemin vers le haut (la ligne 0 est en bas)
 		else if (row == mapHeight - 1) {
 			element = new PathToRegion(Assets.pathToRegionUp, col, row, this, regionX, regionY + 1);
 		}
 		// Chemin vers le bas (la ligne 0 est en bas)
 		else if (row == 0) {
 			element = new PathToRegion(Assets.pathToRegionDown, col, row, this, regionX, regionY - 1);
 		}
 		return element;
	}

	@Override
	public void onCharacterDeath(Character character) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void show() {
		// DBG Normalement le centerCameraOn() dans le super.show
		// devrait être suffisant pour centrer la caméra sur le
		// joueur quand on revient sur la carte du monde. Ca ne
		// marche malheureusement pas et on doit recourir encore
		// une fois à l'astuce du isFirstDisplay :(
		super.show();
		isFirstDisplay = true;
	}
}
