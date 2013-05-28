package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Player;

public class MoveOnClickListener extends ActionOnClickListener {
	public MoveOnClickListener(GameMap map, Player player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		player.moveTo(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
	}
}
