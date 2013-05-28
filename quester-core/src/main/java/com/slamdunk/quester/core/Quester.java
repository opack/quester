package com.slamdunk.quester.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.display.screens.DungeonScreen;
import com.slamdunk.quester.display.screens.WorldMapScreen;

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
		
		worldMapScreen = new WorldMapScreen(
			// Taille du monde (en nombre de régions)
			20, 20,
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
		dungeonScreen = new DungeonScreen(
			// Taille du donjon (en nombre de pièces)
			dungeonWidth, dungeonHeight,
			// Taille d'une pièce (en nombre de cellules)
			roomWidth, roomHeight,//13,13,
			// Taille d'une cellule (en pixels)
			96, 96);
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
