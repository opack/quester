package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;

public class ThinkAction implements AIAction {
	private CharacterControler controler;
	
	public ThinkAction(CharacterControler controler) {
		this.controler = controler;
	}
	
	@Override
	public void act() {
		controler.getAI().think();
		controler.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.THINK;
	}

}
