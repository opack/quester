package com.slamdunk.quester.core;

import static com.slamdunk.quester.map.logical.MapBuilder.GRASS_DATA;
import static com.slamdunk.quester.map.logical.MapBuilder.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.display.screens.WorldMapScreen;
import com.slamdunk.quester.map.logical.DungeonBuilder;
import com.slamdunk.quester.map.logical.MapBuilder;
import com.slamdunk.quester.map.logical.WorldBuilder;

public class Quester extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public final static int SCREEN_WIDTH = 480;
	public final static int SCREEN_HEIGHT = 800;
	
	/**
	 * Ecrans du jeu
	 */
	private Screen worldMapScreen;
	private Screen dungeonScreen;
	
	private static Quester instance;
	
	@Override
	public void create () {
		instance = this;
		
		Assets.load();
		
		// Taille du monde (en nombre de régions)
		MapBuilder builder = new WorldBuilder(1, 1);
		// Taille d'une région (en nombre de cases)
		builder.createAreas(50, 50, GRASS_DATA);
		builder.placeMainEntrances();
		
		worldMapScreen = new WorldMapScreen(
			builder,
			// Taille d'une cellule (en pixels)
			96, 96);
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
		getScreen().dispose();
	}
	
	public void enterWorldMap() {
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight) {
		
		// Taille du donjon (en nombre de pièces)
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight);
		// Taille d'une pièce (en nombre de cellules)
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		
		dungeonScreen = new MapScreen(
			builder,
			// Taille d'une cellule (en pixels)
			96, 96,
			// Taille d'une zone de la minimap
			48, 32, 4);
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
