package com.slamdunk.quester.hud;


public class NoActionOnClickListener extends ActionOnClickListener {
	public NoActionOnClickListener() {
		super(null, null, 0, 0);
	}

	@Override
	public void onClick() {
		// Rien � faire
	}
}