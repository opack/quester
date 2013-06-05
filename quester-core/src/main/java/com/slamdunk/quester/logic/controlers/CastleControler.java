package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.model.data.CastleData;

public class CastleControler extends WorldElementControler {

	public CastleControler(CastleData data, CastleActor body) {
		super(data, body);
	}

	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le château
		return false;
	}
	
	@Override
	public CastleData getData() {
		return (CastleData)data;
	}
}
