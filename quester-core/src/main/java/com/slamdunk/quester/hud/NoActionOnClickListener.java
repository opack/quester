package com.slamdunk.quester.hud;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public class NoActionOnClickListener extends ActionOnClickListener {
	public NoActionOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		// Rien à faire
	}
}