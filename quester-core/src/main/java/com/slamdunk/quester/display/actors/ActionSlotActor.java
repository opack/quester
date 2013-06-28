package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.quester.display.hud.actionslots.ActionSlotsHelper;
import com.slamdunk.quester.display.hud.actionslots.SlotData;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;
import com.slamdunk.quester.utils.Config;

public class ActionSlotActor extends WorldElementActor {
	public ActionSlotActor(TextureRegion texture) {
		super(texture);
	}
	
	@Override
	public ActionSlotControler getControler() {
		return (ActionSlotControler)controler;
	}

	/**
	 * Fait apparaître progressivement l'image
	 */
	public void appear() {
		Image image = getImage();
		if (image != null) {
			image.setColor(1.0f, 1.0f, 1.0f, 0.0f);
			image.addAction(Actions.alpha(1.0f, 1.0f));
		}
	}

	/**
	 * Fait tomber le slot vers le slot indiqué
	 */
	public void fallTo(final ActionSlotActor destinationSlot) {
		final float initX = getX();
		final float initY = getY();
		final SlotData sourceSlotData = ActionSlotsHelper.SLOT_DATAS.get(getControler().getData().action);
		addAction(Actions.sequence(
			Actions.moveTo(
				destinationSlot.getX() + (destinationSlot.getWidth() - getWidth()) / 2,
				destinationSlot.getY() + (destinationSlot.getHeight() - getHeight()) / 2,
				Config.asFloat("action.fallDuration", 1.0f)),
			new Action(){
				@Override
				public boolean act(float delta) {
					ActionSlotActor.this.setPosition(initX, initY);
					ActionSlotsHelper.setSlotData(sourceSlotData, destinationSlot);					
					return true;
				}
			}
		));
	}
}
