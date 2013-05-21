package com.slamdunk.quester.hud;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public class ActionOnClickListener extends ClickListener {
	protected GameMap map;
	protected Character player;
	
	protected int offsetX;
	protected int offsetY;
	
	public ActionOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		this.map = map;
		this.player = player;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public void setOffsets(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
}
