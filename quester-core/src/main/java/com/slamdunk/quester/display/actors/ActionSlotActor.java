package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;

public class ActionSlotActor extends WorldElementActor {
	public ActionSlotActor(TextureRegion texture) {
		super(texture);
	}
	
	@Override
	public ActionSlotControler getControler() {
		return (ActionSlotControler)controler;
	}
}
