package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.slamdunk.quester.model.data.ActionSlotData;

public class ActionSlotControler extends WorldElementControler {

	public ActionSlotControler(ActionSlotData data) {
		super(data);
	}
	
	@Override
	public ActionSlotData getData() {
		return (ActionSlotData)data;
	}
	
	@Override
	public boolean canAcceptDrop(Payload payload) {
		// On peut toujours mettre une action dans un ActionSlot enregistr� comme target
		// quelle que soit l'action.
		// Les slots d'arriv�e ne seront pas enregistr�s en tant que target donc pas de
		// soucis.
		return true;
	}
}
