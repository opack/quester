package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.RabiteActor;
import com.slamdunk.quester.logic.ai.RabiteAI;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.utils.Assets;

public class RabiteControler extends CharacterControler {

	public RabiteControler(CharacterData data, RabiteActor body) {
		super(data, body, new RabiteAI());
	}

	@Override
	public Sound getAttackSound() {
		return Assets.biteSound;
	}
}
