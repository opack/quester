package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.RobotActor;
import com.slamdunk.quester.logic.ai.RobotAI;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.utils.Assets;

public class RobotControler extends CharacterControler {

	public RobotControler(CharacterData data, RobotActor body) {
		super(data, body, new RobotAI());
	}

	@Override
	public Sound getAttackSound() {
		return Assets.biteSound;
	}
}
