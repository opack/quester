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
	
	@Override
	public void create () {
		Assets.load();
		worldMapScreen = new WorldMapScreen(
			// Le jeu
			this,
			// Taille du monde (en nombre de régions)
			20, 20,
			// Taille d'une cellule (en pixels)
			96, 96);
		dungeonScreen = new DungeonScreen(
			// Le jeu
			this,
			// Taille du donjon (en nombre de pièces)
			3, 3,
			// Taille d'une pièce (en nombre de cellules)
			8, 10,//13,13,
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
}
