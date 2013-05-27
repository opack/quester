package com.slamdunk.quester.hud.contextpad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.core.GameMap;

public abstract class ActionOnClickListener extends ClickListener {
	protected GameMap map;
	protected Character player;
	
	protected int offsetX;
	protected int offsetY;
	
	protected boolean isActive;
	
	public ActionOnClickListener(GameMap map, Character player, int offsetX, int offsetY) {
		this.map = map;
		this.player = player;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.isActive = true;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setOffsets(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	@Override
	public void clicked(InputEvent event, float x, float y) {
		if (isActive) {
			onClick();
		}
	}

	/**
	 * M�thode appel�e lors d'un clic si le listener est actif.
	 */
	protected abstract void onClick();
}