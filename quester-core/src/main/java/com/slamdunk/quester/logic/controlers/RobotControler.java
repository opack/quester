package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.RobotActor;
import com.slamdunk.quester.logic.ai.RobotAI;
import com.slamdunk.quester.model.data.CharacterData;

public class RobotControler extends CharacterControler {

	public RobotControler(CharacterData data, RobotActor body) {
		super(data, body, new RobotAI());
	}
}
