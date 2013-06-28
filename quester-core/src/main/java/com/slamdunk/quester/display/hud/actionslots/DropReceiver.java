package com.slamdunk.quester.display.hud.actionslots;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public interface DropReceiver {
	/**
	 * Indique si ce receiver serait prêt à accepter ce payload
	 */
	boolean canAcceptDrop(Payload payload);

	/**
	 * Méthode appelée lorsqu'un chargement est lâché sur ce receiver
	 */
	void receiveDrop(ActionSlotControler dropped);
}