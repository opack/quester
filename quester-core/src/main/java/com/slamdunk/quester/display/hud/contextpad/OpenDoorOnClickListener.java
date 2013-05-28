package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.Door;

public class OpenDoorOnClickListener extends ActionOnClickListener {
	public OpenDoorOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		Door door = (Door)map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.openDoor(door);
	}
}
