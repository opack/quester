package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.actors.Character;

public class CenterCameraOnClickListener extends ActionOnClickListener {
	private final GameWorld world;
	
	public CenterCameraOnClickListener(GameWorld world, Character player) {
		super(null, player, 0, 0);
		this.world = world;
	}

	@Override
	public void onClick() {
		world.centerCameraOn(player);
	}
}
