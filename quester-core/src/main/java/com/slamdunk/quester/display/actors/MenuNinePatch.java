package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.slamdunk.quester.core.Assets;

public class MenuNinePatch extends NinePatch {
	private static MenuNinePatch instance;

	private MenuNinePatch() {
		super(Assets.menuskin, 8, 8, 8, 8);
	}

	public static MenuNinePatch getInstance() {
		if (instance == null) {
			instance = new MenuNinePatch();
		}
		return instance;
	}
}