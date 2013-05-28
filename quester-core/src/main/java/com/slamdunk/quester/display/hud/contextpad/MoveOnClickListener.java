package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Character;

public class MoveOnClickListener extends ActionOnClickListener {
	public MoveOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		player.moveTo(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
	}
}
