package com.slamdunk.quester.hud.contextpad;

import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.Door;
import com.slamdunk.quester.core.GameMap;

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