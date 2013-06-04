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

package com.slamdunk.quester.core;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.FloatArray;

public class Assets {

	private static final String TEXT_FONT = Config.asString("Global.characterFont", "ocr_a.fnt");

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

	//public static Sound[] chickenTaunts;
	//public static Sound buttonSound;

	public static final float VIRTUAL_WIDTH = 30.0f;
	public static final float VIRTUAL_HEIGHT = 20.0f;
	
	public static float pixelDensity;

	public static void load () {
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
		fog = new TextureRegion(new Texture(Gdx.files.internal("textures/fog.png")));
		pathMarker = new TextureRegion(new Texture(Gdx.files.internal("textures/path-marker.png")));
		wall = new TextureRegion(new Texture(Gdx.files.internal("textures/wall.png")));
		ground = new TextureRegion(new Texture(Gdx.files.internal("textures/ground.png")));
		grass = new TextureRegion(new Texture(Gdx.files.internal("textures/grass.png")));
		entranceDoor = new TextureRegion(new Texture(Gdx.files.internal("textures/browndoor_in_0.png")));
		exitDoor = new TextureRegion(new Texture(Gdx.files.internal("textures/browndoor_out_3.png")));
		commonDoor = new TextureRegion(new Texture(Gdx.files.internal("textures/darkdoor_in_0.png")));
		village = new TextureRegion(new Texture(Gdx.files.internal("textures/village.png")));
		castle = new TextureRegion(new Texture(Gdx.files.internal("textures/castle.png")));
		rock = new TextureRegion(new Texture(Gdx.files.internal("textures/rock.png")));
		pathUp = new TextureRegion(new Texture(Gdx.files.internal("textures/path_up.png")));
		pathDown = new TextureRegion(new Texture(Gdx.files.internal("textures/path_down.png")));
		pathLeft = new TextureRegion(new Texture(Gdx.files.internal("textures/path_left.png")));
		pathRight = new TextureRegion(new Texture(Gdx.files.internal("textures/path_right.png")));

		hero = new TextureRegion(new Texture(Gdx.files.internal("textures/hero.png")));
		robot = new TextureRegion(new Texture(Gdx.files.internal("textures/robot.png")));

		menuskin = new TextureRegion(new Texture(Gdx.files.internal("textures/menuskin.png")));
		heart = new TextureRegion(new Texture(Gdx.files.internal("textures/heart.png")));
		sword = new TextureRegion(new Texture(Gdx.files.internal("textures/sword.png")));
		msgBox = new TextureRegion(new Texture(Gdx.files.internal("textures/msgBox.png")));
		
		hud = new TextureRegion(new Texture(Gdx.files.internal("textures/hud.png")));
		arrowUp = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/arrow_up.png")));
		arrowDown = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/arrow_down.png")));
		arrowLeft = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/arrow_left.png")));
		arrowRight = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/arrow_right.png")));
		padPathUp = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/path_up.png")));
		padPathDown = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/path_down.png")));
		padPathLeft = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/path_left.png")));
		padPathRight = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/path_right.png")));
		padSword = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/sword.png")));
		cross = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/cross.png")));
		center = new TextureRegion(new Texture(Gdx.files.internal("textures/pad/center.png")));
		areaUnvisited = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/area_unvisited.png")));
		areaVisited = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/area_visited.png")));
		areaExit = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/area_exit.png")));
		areaCurrent = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/area_current.png")));
		pathUnknownVertical = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/path-unknown_vertical.png")));
		pathUnknownHorizontal = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/path-unknown_horizontal.png")));
		pathExistsVertical = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/path-exists_vertical.png")));
		pathExistsHorizontal = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/path-exists_horizontal.png")));
		minimapBackground = new TextureRegion(new Texture(Gdx.files.internal("textures/minimap/background.png")));
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
		String fontDir = "fonts/";// DDE DBG + (int)pixelDensity + "/";

		characterFont = new BitmapFont(Gdx.files.internal(fontDir + TEXT_FONT), false);
		hudFont = new BitmapFont(Gdx.files.internal(fontDir + TEXT_FONT), false);

		//characterFont.setScale(1.0f / pixelDensity);
		characterFont.setScale(0.7f);
		hudFont.setScale(0.9f);
	}

	private static void loadSounds () {
		//standardTaunts = loadSounds("standard_taunts");
	}

	private static Sound[] loadSounds (String dir) {
		FileHandle dh = Gdx.files.internal("sounds/" + dir);
		FileHandle[] fhs = dh.list();
		List<Sound> sounds = new ArrayList<Sound>();
		for (int i = 0; i < fhs.length; i++) {
			String name = fhs[i].name();
			if (name.endsWith(".ogg")) {
				sounds.add(loadSound(dir + "/" + name));
			}
		}
		Sound[] result = new Sound[0];
		return sounds.toArray(result);
	}

	private static Sound loadSound (String filename) {
		return Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
	}

	private static float toWidth (TextureRegion region) {
		return region.getRegionWidth() / pixelDensity;
	}

	private static float toHeight (TextureRegion region) {
		return region.getRegionHeight() / pixelDensity;
	}

	public static void playSound (Sound sound) {
		sound.play(1);
	}
}
