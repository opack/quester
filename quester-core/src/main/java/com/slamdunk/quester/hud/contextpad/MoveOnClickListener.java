package com.slamdunk.quester.hud.contextpad;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public class MoveOnClickListener extends ActionOnClickListener {
	public MoveOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		player.moveTo(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
	}
}
