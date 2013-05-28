package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.actors.Player;

public class CenterCameraOnClickListener extends ActionOnClickListener {
	private final GameWorld world;
	
	public CenterCameraOnClickListener(GameWorld world, Player player) {
		super(null, player, 0, 0);
		this.world = world;
	}

	@Override
	public void onClick() {
		world.centerCameraOn(player);
	}
}
