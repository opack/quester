package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.slamdunk.quester.logic.ai.QuesterActions;
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
		// On peut toujours mettre une action dans un ActionSlot enregistré comme target
		// quelle que soit l'action, si ce slot est vide.
		// Les slots d'arrivée ne seront pas enregistrés en tant que target donc pas de
		// soucis.
		return getData().action == QuesterActions.NONE;
	}
	
	@Override
	public void receiveDrop(ActionSlotControler dropped) {
		// Un ActionSlot qui reçoit un autre ActionSlot en copie le contenu
//		ActionSlots.copySlot((ActionSlotActor)dropped.actor, (ActionSlotActor)actor);
		getData().action = dropped.getData().action;
		actor.getImage().setDrawable(dropped.actor.getImage().getDrawable());
	}
}
