package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.data.CharacterData;

public class EatActionAction implements AIAction {
	private CharacterControler character;
	
	public EatActionAction(CharacterControler character) {
		this.character = character;
	}
	
	@Override
	public void act() {
		character.getAI().nextAction();
		
		CharacterData data = character.getData();
		int oldAP = data.actionsLeft;
		data.actionsLeft--;
		for (CharacterListener listener : character.getListeners()) {
			listener.onActionPointsChanged(oldAP, data.actionsLeft);
		}
		if (data.actionsLeft <= 0) {
			GameControler.instance.nextPhase();
			character.prepareThinking();
		}
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.EAT_ACTION;
	}
}
