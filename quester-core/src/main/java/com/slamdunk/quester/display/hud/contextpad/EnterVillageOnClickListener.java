package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Village;

public class EnterVillageOnClickListener extends ActionOnClickListener {
	public EnterVillageOnClickListener(Player player, int offsetX, int offsetY) {
		super(player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		//Village village = (Village)QuesterGame.instance.getMapScreen().getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		// TODO Entrer dans le village...
	}
}
