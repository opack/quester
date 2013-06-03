package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.WorldActor;

public class AttackOnClickListener extends ActionOnClickListener {
	public AttackOnClickListener(Player player, int offsetX, int offsetY) {
		super(player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		WorldActor target = QuesterGame.instance.getMapScreen().getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.attack(target);
	}
}
