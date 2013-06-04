package com.slamdunk.quester.core;

import static com.slamdunk.quester.model.map.ElementData.GRASS_DATA;
import static com.slamdunk.quester.model.map.ElementData.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.display.screens.WorldMapScreen;
import com.slamdunk.quester.model.map.DungeonBuilder;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.map.WorldBuilder;
import com.slamdunk.quester.model.points.UnmutablePoint;

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
		
		// Création d'un joueur
		QuesterGame.instance.createPlayerData(150, 3);
		
		// Arrivée sur la carte du monde
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
		// Si le monde n'est pas encore créé, on le crée
		if (worldMapScreen == null) {
			// Création de la carte
			MapBuilder builder = new WorldBuilder(11, 11);
			builder.createAreas(11, 11, GRASS_DATA);
			builder.placeMainEntrances();
			worldMapScreen = new WorldMapScreen(builder, 96, 96);
			QuesterGame.instance.setMapScreen(worldMapScreen);
			
			// Création de l'acteur représentant le joueur
			UnmutablePoint entrancePosition = builder.getEntrancePosition();
			worldMapScreen.createPlayer(entrancePosition);
			QuesterGame.instance.setPlayer(worldMapScreen.getPlayer());
					
			// Le joueur est créé : création du hud
			worldMapScreen.createHud(100, 100);
			
			// Affichage de la carte
	        UnmutablePoint entranceRoom = builder.getEntranceRoom();
	        DisplayData data = new DisplayData();
	        data.regionX = entranceRoom.getX();
	        data.regionY = entranceRoom.getY();
	        data.playerX = entrancePosition.getX();
	        data.playerY = entrancePosition.getY();
	        QuesterGame.instance.displayWorld(data);
		}
		// Affichage de la carte
		QuesterGame.instance.setMapScreen(worldMapScreen);
		QuesterGame.instance.setPlayer(worldMapScreen.getPlayer());
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight) {
		// Si un donjon existe déjà, on le supprime
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		
		// Construction de la carte
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight);
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		dungeonScreen = new MapScreen(builder, 96, 96);
		QuesterGame.instance.setMapScreen(dungeonScreen);
		
		// Crée l'acteur représentant le joueur
		UnmutablePoint entrancePosition = builder.getEntrancePosition();
		dungeonScreen.createPlayer(entrancePosition);
		QuesterGame.instance.setPlayer(dungeonScreen.getPlayer());
		
		// Le joueur est créé : création du hud
		dungeonScreen.createHud(100, 100);
		
		// Affichage de la carte
        UnmutablePoint entranceRoom = builder.getEntranceRoom();
        DisplayData data = new DisplayData();
        data.regionX = entranceRoom.getX();
        data.regionY = entranceRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        QuesterGame.instance.displayWorld(data);
		setScreen(dungeonScreen);
	}
}
