package com.slamdunk.quester.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.slamdunk.quester.screens.DungeonScreen;

public class Quester extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public final static int SCREEN_WIDTH = 480;
	public final static int SCREEN_HEIGHT = 800;
	
	/**
	 * Ecrans du jeu
	 */
	Screen dungeonScreen;
	
	@Override
	public void create () {
		Assets.load();
		dungeonScreen = new DungeonScreen(20, 15, 64, 64);
		setScreen(dungeonScreen);
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
