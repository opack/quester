package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.Village;

public class EnterVillageOnClickListener extends ActionOnClickListener {
	public EnterVillageOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		Village village = (Village)map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		// TODO Entrer dans le village...
	}
}
