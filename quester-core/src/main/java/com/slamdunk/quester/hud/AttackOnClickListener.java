package com.slamdunk.quester.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public class AttackOnClickListener extends ActionOnClickListener {
	public AttackOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	public void clicked(InputEvent event, float x, float y) {
		WorldElement target = map.getTopElementAt(player.getWorldX() + offsetX, player.getWorldY() + offsetY);
		player.attack(target);
		updatePad();
	}
}
