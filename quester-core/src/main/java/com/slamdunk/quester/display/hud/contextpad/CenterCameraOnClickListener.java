package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Player;

public class CenterCameraOnClickListener extends ActionOnClickListener {
	
	public CenterCameraOnClickListener(Player player) {
		super(player, 0, 0);
	}

	@Override
	public void onClick() {
		QuesterGame.instance.getMapScreen().centerCameraOn(player);
	}
}
