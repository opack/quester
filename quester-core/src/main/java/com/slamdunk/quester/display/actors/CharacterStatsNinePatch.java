package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.slamdunk.quester.core.Assets;

public class CharacterStatsNinePatch extends NinePatch {
	private static CharacterStatsNinePatch instance;

	private CharacterStatsNinePatch() {
		super(Assets.menuskin, 8, 8, 8, 8);
	}

	public static CharacterStatsNinePatch getInstance() {
		if (instance == null) {
			instance = new CharacterStatsNinePatch();
		}
		return instance;
	}
}