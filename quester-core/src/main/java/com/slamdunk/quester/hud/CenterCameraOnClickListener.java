package com.slamdunk.quester.hud;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameWorld;

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
