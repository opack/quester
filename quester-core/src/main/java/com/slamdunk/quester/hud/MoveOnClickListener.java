package com.slamdunk.quester.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public class MoveOnClickListener extends ActionOnClickListener {
	public MoveOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	public void clicked(InputEvent event, float x, float y) {
		player.moveTo(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		updatePad();
	}
}
