package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.CastleActor;
import com.slamdunk.quester.model.data.CastleData;

public class CastleControler extends WorldElementControler {

	public CastleControler(CastleData data, CastleActor body) {
		super(data, body);
	}
	
	@Override
	public CastleData getData() {
		return (CastleData)data;
	}
}
