package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.WorldActor;

public class AttackOnClickListener extends ActionOnClickListener {
	public AttackOnClickListener(GameMap map, Player player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		WorldActor target = map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.attack(target);
	}
}
