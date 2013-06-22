package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;

public class NoAction implements AIAction {
	private CharacterControler controler;
	
	public NoAction(CharacterControler controler) {
		this.controler = controler;
	}
	
	@Override
	public void act() {
		controler.prepareThink();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.NONE;
	}

}
