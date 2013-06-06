package com.slamdunk.quester;

import static com.slamdunk.quester.model.data.ElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.ElementData.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.display.screens.WorldMapScreen;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.DungeonBuilder;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.map.WorldBuilder;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class Quester extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public final static int SCREEN_WIDTH = 480;
	public final static int SCREEN_HEIGHT = 800;
	
	/**
	 * Ecrans du jeu
	 */
	private MapScreen worldMapScreen;
	private MapScreen dungeonScreen;
	
	private static Quester instance;
	
	@Override
	public void create () {
		instance = this;
		
		// Chargement des assets
		Assets.load();
		
		// Cr�ation d'un joueur
		GameControler.instance.createPlayerControler(150, 3);
		
		// Arriv�e sur la carte du monde
		enterWorldMap();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		if (worldMapScreen != null) {
			worldMapScreen.dispose();
		}
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
	}

	public static Quester getInstance() {
		return instance;
	}

	public Screen getWorldMapScreen() {
		return worldMapScreen;
	}

	public Screen getDungeonScreen() {
		return dungeonScreen;
	}
	
	public void enterWorldMap() {
		// Si le monde n'est pas encore cr��, on le cr�e
		if (worldMapScreen == null) {
			// Cr�ation de la carte
			MapBuilder builder = new WorldBuilder(11, 11);
			builder.createAreas(11, 11, GRASS_DATA);
			builder.placeMainEntrances();
			worldMapScreen = new WorldMapScreen(builder, 96, 96);
			GameControler.instance.setMapScreen(worldMapScreen);
			
			// Cr�ation de l'acteur repr�sentant le joueur
			UnmutablePoint entrancePosition = builder.getEntrancePosition();
			worldMapScreen.createPlayer(entrancePosition);
			//DBGQuesterGame.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
					
			// Le joueur est cr�� : cr�ation du hud
			worldMapScreen.createHud(100, 100);
			
			// Affichage de la carte
	        UnmutablePoint entranceRoom = builder.getEntranceRoom();
	        DisplayData data = new DisplayData();
	        data.regionX = entranceRoom.getX();
	        data.regionY = entranceRoom.getY();
	        data.playerX = entrancePosition.getX();
	        data.playerY = entrancePosition.getY();
	        GameControler.instance.displayWorld(data);
		}
		// Affichage de la carte
		GameControler.instance.setMapScreen(worldMapScreen);
		GameControler.instance.setCurrentArea(worldMapScreen.getCurrentArea().getX(), worldMapScreen.getCurrentArea().getY());
		GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
		worldMapScreen.updateHUD(GameControler.instance.getCurrentArea());
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight) {
		// Si un donjon existe d�j�, on le supprime
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		
		// Construction de la carte
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight);
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		dungeonScreen = new MapScreen(builder, 96, 96);
		GameControler.instance.setMapScreen(dungeonScreen);
		
		// Cr�e l'acteur repr�sentant le joueur
		UnmutablePoint entrancePosition = builder.getEntrancePosition();
		dungeonScreen.createPlayer(entrancePosition);
		GameControler.instance.getPlayer().setActor(dungeonScreen.getPlayerActor());
		
		// Le joueur est cr�� : cr�ation du hud
		dungeonScreen.createHud(100, 100);
		
		// Affichage de la carte
        UnmutablePoint entranceRoom = builder.getEntranceRoom();
        DisplayData data = new DisplayData();
        data.regionX = entranceRoom.getX();
        data.regionY = entranceRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        GameControler.instance.displayWorld(data);
		setScreen(dungeonScreen);
	}
}