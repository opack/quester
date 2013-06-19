package com.slamdunk.quester.display.messagebox;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.slamdunk.quester.utils.Assets;

public class MessageBoxNinePatch extends NinePatch {
	private static MessageBoxNinePatch instance;

	public static MessageBoxNinePatch getInstance() {
		if (instance == null) {
			instance = new MessageBoxNinePatch();
		}
		return instance;
	}

	private MessageBoxNinePatch() {
		super(Assets.msgBox, 8, 8, 8, 8);
	}
}