package com.slamdunk.quester.hud.contextpad;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.GameMap;

public class AttackOnClickListener extends ActionOnClickListener {
	public AttackOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		WorldElement target = map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.attack(target);
	}
}
