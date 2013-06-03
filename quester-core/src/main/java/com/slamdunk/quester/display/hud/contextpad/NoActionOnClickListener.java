package com.slamdunk.quester.display.hud.contextpad;


public class NoActionOnClickListener extends ActionOnClickListener {
	public NoActionOnClickListener() {
		super(null, 0, 0);
	}

	@Override
	public void onClick() {
		// Rien à faire
	}
}