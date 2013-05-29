package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.PathToRegion;
import com.slamdunk.quester.display.actors.Player;

public class CrossPathOnClickListener extends ActionOnClickListener {
	public CrossPathOnClickListener(GameMap map, Player player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		PathToRegion path = (PathToRegion)map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.crossPath(path);
	}
}
