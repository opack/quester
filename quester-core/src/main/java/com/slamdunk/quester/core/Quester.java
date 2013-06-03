package com.slamdunk.quester.core;

import static com.slamdunk.quester.model.map.MapBuilder.GRASS_DATA;
import static com.slamdunk.quester.model.map.MapBuilder.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.actors.Player;
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
		
		// Taille du monde (en nombre de régions)
		MapBuilder builder = new WorldBuilder(5, 5);
		// Taille d'une région (en nombre de cases)
		builder.createAreas(11, 11, GRASS_DATA);
		builder.placeMainEntrances();
		
		worldMapScreen = new WorldMapScreen(
			builder,
			// Taille d'une cellule (en pixels)
			96, 96);
		QuesterGame.instance.setMapScreen(worldMapScreen);
		
		// Crée l'acteur représentant le joueur
		worldMapScreen.createPlayer();
		QuesterGame.instance.setPlayer(worldMapScreen.getPlayer());
				
		// Le joueur est créé : on peut créer le hud
		worldMapScreen.createHud(100, 100);
		
		// Affichage du monde
        UnmutablePoint entrance = builder.getEntranceRoom();
        UnmutablePoint entrancePosition = builder.getEntrancePosition();
        DisplayData data = new DisplayData();
        data.regionX = entrance.getX();
        data.regionY = entrance.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        QuesterGame.instance.displayWorld(data);
		setScreen(worldMapScreen);
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
	
	public void enterWorldMap() {
		QuesterGame.instance.setPlayer(worldMapScreen.getPlayer());
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight) {
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		
		// Taille du donjon (en nombre de pièces)
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight);
		// Taille d'une pièce (en nombre de cellules)
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		
		dungeonScreen = new MapScreen(
			builder,
			// Taille d'une cellule (en pixels)
			96, 96);
		QuesterGame.instance.setMapScreen(dungeonScreen);
		
		// Crée l'acteur représentant le joueur
		dungeonScreen.createPlayer();
		QuesterGame.instance.setPlayer(dungeonScreen.getPlayer());
		
		// Le joueur est créé : on peut créer le hud
		dungeonScreen.createHud(100, 100);
		
		// Affichage du monde
        UnmutablePoint entrance = builder.getEntranceRoom();
        UnmutablePoint entrancePosition = builder.getEntrancePosition();
        DisplayData data = new DisplayData();
        data.regionX = entrance.getX();
        data.regionY = entrance.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        QuesterGame.instance.displayWorld(data);
		setScreen(dungeonScreen);
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
}
