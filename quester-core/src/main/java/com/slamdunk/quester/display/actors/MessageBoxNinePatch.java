package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.slamdunk.quester.core.Assets;

public class MessageBoxNinePatch extends NinePatch {
	private static MessageBoxNinePatch instance;

	private MessageBoxNinePatch() {
		super(Assets.msgBox, 8, 8, 8, 8);
	}

	public static MessageBoxNinePatch getInstance() {
		if (instance == null) {
			instance = new MessageBoxNinePatch();
		}
		return instance;
	}
}