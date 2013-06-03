package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.PathToRegion;
import com.slamdunk.quester.display.actors.Player;

public class CrossPathOnClickListener extends ActionOnClickListener {
	public CrossPathOnClickListener(Player player, int offsetX, int offsetY) {
		super(player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		PathToRegion path = (PathToRegion)QuesterGame.instance.getMapScreen().getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.crossPath(path);
	}
}
