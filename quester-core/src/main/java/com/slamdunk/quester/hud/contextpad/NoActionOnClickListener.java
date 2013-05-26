package com.slamdunk.quester.hud.contextpad;


public class NoActionOnClickListener extends ActionOnClickListener {
	public NoActionOnClickListener() {
		super(null, null, 0, 0);
	}

	@Override
	public void onClick() {
		// Rien à faire
	}
}