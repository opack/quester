package com.slamdunk.quester;

import static com.slamdunk.quester.model.data.WorldElementData.GRASS_DATA;
import static com.slamdunk.quester.model.data.WorldElementData.GROUND_DATA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.display.screens.WorldScreen;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.DungeonBuilder;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.map.WorldBuilder;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;
public class Quester extends Game {
	/**
	 * Taille de l'affichage en pixels
	 */
	public static int screenWidth;
	public static int screenHeight;
	
	/**
	 * Ecrans du jeu
	 */
	private GameScreen worldMapScreen;
	private GameScreen dungeonScreen;
	
	private static Quester instance;
	
	@Override
	public void create () {
		instance = this;
		
		// Chargement de la taille de l'écran
		screenWidth = Config.asInt("screen.width", 480);
		screenHeight = Config.asInt("screen.height", 800);
		
		// Chargement des assets
		Assets.load();
		
		// Création d'un joueur
		GameControler.instance.createPlayerControler(150, 3);
		
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
		Assets.dispose();
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
			worldMapScreen = new WorldScreen(builder, 96, 96);
			GameControler.instance.setScreen(worldMapScreen);
			
			// Choix de la musique de fond
			worldMapScreen.setBackgroundMusic(Assets.worldmapMusics[MathUtils.random(Assets.worldmapMusics.length - 1)]);
			
			// Création de l'acteur représentant le joueur
			UnmutablePoint entrancePosition = builder.getEntrancePosition();
			worldMapScreen.createPlayer(entrancePosition);
			GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
			GameControler.instance.getPlayer().getAI().init();
			
			// Le joueur est créé : création du hud
			worldMapScreen.initHud(100, 100);
			
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
		GameControler.instance.setScreen(worldMapScreen);
		GameControler.instance.setCurrentArea(worldMapScreen.getCurrentArea().getX(), worldMapScreen.getCurrentArea().getY());
		GameControler.instance.getPlayer().setActor(worldMapScreen.getPlayerActor());
		worldMapScreen.updateHUD(GameControler.instance.getCurrentArea());
		setScreen(worldMapScreen);
	}
	
	public void enterDungeon(
			int dungeonWidth, int dungeonHeight,
			int roomWidth, int roomHeight,
			int difficulty) {
		// Si un donjon existe déjà, on le supprime
		if (dungeonScreen != null) {
			dungeonScreen.dispose();
		}
		
		// Construction de la carte
		MapBuilder builder = new DungeonBuilder(dungeonWidth, dungeonHeight, difficulty);
		builder.createAreas(roomWidth, roomHeight, GROUND_DATA);
		builder.placeMainEntrances();
		dungeonScreen = new GameScreen(builder, 96, 96);
		GameControler.instance.setScreen(dungeonScreen);
		
		// Choix de la musique de fond
		dungeonScreen.setBackgroundMusic(Assets.dungeonMusics[MathUtils.random(Assets.dungeonMusics.length - 1)]);
		
		// Crée l'acteur représentant le joueur
		UnmutablePoint entrancePosition = builder.getEntrancePosition();
		dungeonScreen.createPlayer(entrancePosition);
		GameControler.instance.getPlayer().setActor(dungeonScreen.getPlayerActor());
		GameControler.instance.getPlayer().getAI().init();
		
		// Le joueur est créé : création du hud
		dungeonScreen.initHud(100, 100);
		
		// Affichage de la carte
        UnmutablePoint entranceRoom = builder.getEntranceRoom();
        DisplayData data = new DisplayData();
        data.regionX = entranceRoom.getX();
        data.regionY = entranceRoom.getY();
        data.playerX = entrancePosition.getX();
        data.playerY = entrancePosition.getY();
        GameControler.instance.displayWorld(data);
        
        dungeonScreen.updateHUD(data.regionX, data.regionY);
        
		setScreen(dungeonScreen);
	}
}
