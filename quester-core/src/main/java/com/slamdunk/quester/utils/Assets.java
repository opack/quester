/*
 * Copyright 2011 Rod Hyde (rod@badlydrawngames.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.slamdunk.quester.utils;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;

public class Assets {

	private static final String TEXT_FONT = Config.asString("fonts.characterFont", "ocr_a.fnt");

	private static Set<Disposable> disposables;
	
	//private static TextureAtlas atlas;
	public static TextureRegion fog;
	public static TextureRegion pathMarker;
	public static TextureRegion wall;
	public static TextureRegion ground;
	public static TextureRegion grass;
	public static TextureRegion entranceDoor;
	public static TextureRegion exitDoor;
	public static TextureRegion commonDoor;
	public static TextureRegion village;
	public static TextureRegion castle;
	public static TextureRegion rock;
	public static TextureRegion pathUp;
	public static TextureRegion pathDown;
	public static TextureRegion pathLeft;
	public static TextureRegion pathRight;
	
	public static TextureRegion hero;
	public static TextureRegion robot;

	public static TextureRegion menuskin;
	public static TextureRegion heart;
	public static TextureRegion sword;
	public static TextureRegion msgBox;
	
	public static TextureRegion hud;
	public static TextureRegion arrowUp;
	public static TextureRegion arrowDown;
	public static TextureRegion arrowLeft;
	public static TextureRegion arrowRight;
	public static TextureRegion padPathUp;
	public static TextureRegion padPathDown;
	public static TextureRegion padPathLeft;
	public static TextureRegion padPathRight;
	public static TextureRegion padSword;
	public static TextureRegion cross;
	public static TextureRegion center;
	public static TextureRegion areaUnvisited;
	public static TextureRegion areaVisited;
	public static TextureRegion areaExit;
	public static TextureRegion areaCurrent;
	public static TextureRegion pathUnknownVertical;
	public static TextureRegion pathUnknownHorizontal;
	public static TextureRegion pathExistsVertical;
	public static TextureRegion pathExistsHorizontal;
	public static TextureRegion minimapBackground;
	
	//public static Animation playerWalkingRightAnimation;

	public static BitmapFont characterFont;
	public static BitmapFont hudFont;

	public static float soundVolume;
	public static Sound[] swordSounds;
	public static Sound[] doorOpenSounds;
	public static Sound stepsSound;
	public static Sound biteSound;
	
	// Musique de fond, instanciée à la demande
	public static float musicVolume;
	public static String[] dungeonMusics;
	public static String[] menuMusics;
	public static String[] villageMusics;
	public static String[] worldmapMusics;
	public static Music music;
	public static String currentMusic = "";

	public static final float VIRTUAL_WIDTH = 30.0f;
	public static final float VIRTUAL_HEIGHT = 20.0f;
	
	public static float pixelDensity;

	public static void load () {
		disposables = new HashSet<Disposable>();
		pixelDensity = calculatePixelDensity();
		//String textureDir = "assets/textures/" + (int)pixelDensity;
		//String textureFile = textureDir + "/pack";
		//atlas = new TextureAtlas(Gdx.files.internal(textureFile), Gdx.files.internal(textureDir));
		loadTextures();
		createAnimations();
		loadFonts();
		loadSounds();
	}

	private static void loadTextures () {
		//pureWhiteTextureRegion = atlas.findRegion("8x8");
		// TODO : utiliser un atlas
		fog = loadTexture("fog.png");
		pathMarker = loadTexture("path-marker.png");
		wall = loadTexture("wall.png");
		ground = loadTexture("ground.png");
		grass = loadTexture("grass.png");
		entranceDoor = loadTexture("browndoor_in_0.png");
		exitDoor = loadTexture("browndoor_out_3.png");
		commonDoor = loadTexture("darkdoor_in_0.png");
		village = loadTexture("village.png");
		castle = loadTexture("castle.png");
		rock = loadTexture("rock.png");
		pathUp = loadTexture("path_up.png");
		pathDown = loadTexture("path_down.png");
		pathLeft = loadTexture("path_left.png");
		pathRight = loadTexture("path_right.png");

		hero = loadTexture("hero.png");
		robot = loadTexture("robot.png");

		menuskin = loadTexture("menuskin.png");
		heart = loadTexture("heart.png");
		sword = loadTexture("sword.png");
		msgBox = loadTexture("msgBox.png");
		
		hud = loadTexture("hud.png");
		arrowUp = loadTexture("pad/arrow_up.png");
		arrowDown = loadTexture("pad/arrow_down.png");
		arrowLeft = loadTexture("pad/arrow_left.png");
		arrowRight = loadTexture("pad/arrow_right.png");
		padPathUp = loadTexture("pad/path_up.png");
		padPathDown = loadTexture("pad/path_down.png");
		padPathLeft = loadTexture("pad/path_left.png");
		padPathRight = loadTexture("pad/path_right.png");
		padSword = loadTexture("pad/sword.png");
		cross = loadTexture("pad/cross.png");
		center = loadTexture("pad/center.png");
		areaUnvisited = loadTexture("minimap/area_unvisited.png");
		areaVisited = loadTexture("minimap/area_visited.png");
		areaExit = loadTexture("minimap/area_exit.png");
		areaCurrent = loadTexture("minimap/area_current.png");
		pathUnknownVertical = loadTexture("minimap/path-unknown_vertical.png");
		pathUnknownHorizontal = loadTexture("minimap/path-unknown_horizontal.png");
		pathExistsVertical = loadTexture("minimap/path-exists_vertical.png");
		pathExistsHorizontal = loadTexture("minimap/path-exists_horizontal.png");
		minimapBackground = loadTexture("minimap/background.png");
	}
	
	private static TextureRegion loadTexture(String file) {
		Texture texture = new Texture(Gdx.files.internal("textures/" + file));
		TextureRegion region = new TextureRegion(texture);
		disposables.add(texture);
		return region;
	}

	private static float calculatePixelDensity () {
		FileHandle textureDir = Gdx.files.internal("textures");
		FileHandle[] availableDensities = textureDir.list();
		FloatArray densities = new FloatArray();
		for (int i = 0; i < availableDensities.length; i++) {
			try {
				float density = Float.parseFloat(availableDensities[i].name());
				densities.add(density);
			} catch (NumberFormatException ex) {
				// Ignore anything non-numeric, such as ".svn" folders.
			}
		}
		densities.shrink(); // Remove empty slots to get rid of zeroes.
		densities.sort(); // Now the lowest density comes first.
		//DBG DDEreturn CameraHelper.bestDensity(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, densities.items);
		return 24;
	}

	private static void createAnimations () {
		//playerWalkingRightAnimation = new Animation(PLAYER_FRAME_DURATION, Assets.playerWalkingRight1, Assets.playerWalkingRight2);
		//robotWalkingLeftAnimation = new Animation(ROBOT_FRAME_DURATION, robotLeft1, robotLeft2, robotLeft3, robotLeft4, robotLeft3,	robotLeft2);
	}

	private static void loadFonts () {
		String fontSubDir = "";// DDE DBG + (int)pixelDensity + "/";
		
		//characterFont.setScale(1.0f / pixelDensity);
		characterFont = loadFont(fontSubDir, TEXT_FONT, 0.7f);
		hudFont = loadFont(fontSubDir, TEXT_FONT, 0.9f);

		
	}

	private static BitmapFont loadFont(String subDir, String name, float fontScale) {
		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/" + subDir + "/" + name), false);
		font.setScale(fontScale);
		disposables.add(font);
		return font;
	}

	private static void loadSounds () {
		// Effets sonores
		soundVolume = Config.asFloat("sounds.sfx.volume", 1.0f);
		swordSounds = new Sound[]{
			loadSound("sword/sword-01.ogg"),
			loadSound("sword/sword-02.ogg")
		};
		doorOpenSounds = new Sound[]{
			loadSound("door/door_open-01.ogg"),
			loadSound("door/door_open-02.ogg"),
			loadSound("door/door_open-03.ogg"),
			loadSound("door/door_open-04.ogg")
		};
		stepsSound = loadSound("steps.ogg");
		biteSound = loadSound("bite_byJoelAzzopardi.ogg");
		
		// Musiques
		musicVolume = Config.asFloat("sounds.music.volume", 0.5f);
		dungeonMusics = new String[]{
			"dungeon/8bit Dungeon Boss.ogg",
			"dungeon/8bit Dungeon Level.ogg",
			"dungeon/Video Dungeon Boss.ogg",
			"dungeon/Video Dungeon Crawl.ogg"
		};
		menuMusics = new String[]{
				"menu/Home Base Groove.ogg"
			};
		villageMusics = new String[]{
				"village/Mellowtron.ogg"
			};
		worldmapMusics = new String[]{
				"worldmap/Ambler.ogg",
				"worldmap/Jaunty Gumption.ogg",
				"worldmap/Move Forward.ogg"
			};
	}

//	private static Sound[] loadSounds (String dir) {
//		// Sur desktop, files.internal ne sait pas récupérer un répertoire dans les
//		// assets, puisque tout le contenu se retrouve dans le classpath. Du coup
//		// c'est dur d'en parcourir un. Pour contourner ça, on fait un cas particulier
//		// dans le cas desktop pour aller regarder dans bin.
//		FileHandle dirHandle;
//		if (Gdx.app.getType() == ApplicationType.Android) {
//		   dirHandle = Gdx.files.internal("sounds/" + dir);
//		} else {
//		  // ApplicationType.Desktop ..
//		  dirHandle = Gdx.files.internal("./assets/sounds/" + dir);
//		}
//		
//		FileHandle[] fhs = dirHandle.list();
//		System.out.println("Assets.loadSounds() "+fhs.length);
//		List<Sound> sounds = new ArrayList<Sound>();
//		for (int i = 0; i < fhs.length; i++) {
//			String name = fhs[i].name();
//			// DDE On ne filtre pas sur les ogg
//			//if (name.endsWith(".ogg")) {
//				sounds.add(loadSound(dir + "/" + name));
//			//}
//		}
//		Sound[] result = new Sound[0];
//		return sounds.toArray(result);
//	}

	private static Sound loadSound (String filename) {
		Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
		disposables.add(sound);
		return sound;
	}

	private static float toWidth (TextureRegion region) {
		return region.getRegionWidth() / pixelDensity;
	}

	private static float toHeight (TextureRegion region) {
		return region.getRegionHeight() / pixelDensity;
	}

	public static void playSound (Sound sound) {
		if (sound == null) {
			return;
		}
		sound.play(soundVolume);
	}
	
	public static boolean playMusic(String file) {
		// Si on cherche à jouer la même musique, rien à faire à part
		// s'assurer qu'elle est bien en train de tourner
		if (currentMusic.equals(file)) {
			if (!music.isPlaying()) {
				music.play();
			}
			return true;
		}
		
		
		// Arrêt de la musique en cours
		stopMusic();
		
		// S'il n'y a aucune musique à jouer, on ne joue rien
		if (file == null || file.isEmpty()) {
			return false;
		}
		
		// Lancement de la musque
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/" + file));
		music.setLooping(true);
		music.setVolume(musicVolume);
		music.play();
		currentMusic = file;
		
		// On s'assure que la musique sera libérée
		disposables.add(music);
		return true;
	}
	
	public static void stopMusic() {
		if (music != null) {
			music.stop();
		}
	}
	
	public static void dispose() {
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}

	public static Animation createAnimation(String spritesFile, int frameCols, int frameRows, float frameDuration) {
		Texture sheet = new Texture(Gdx.files.internal("textures/" + spritesFile));
		TextureRegion[][] tmp = TextureRegion.split(
				sheet,
				sheet.getWidth() / frameCols,
				sheet.getHeight() / frameRows);
		TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
		int index = 0;
		for (int i = 0; i < frameRows; i++) {
			for (int j = 0; j < frameCols; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		return new Animation(frameDuration, frames);
	}
}
