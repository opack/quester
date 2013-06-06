package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.controlers.CastleControler;
import com.slamdunk.quester.logic.controlers.GameControler;

public class CastleActor extends WorldElementActor {
	public CastleActor(TextureRegion texture) {
		super(texture);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'entrer dans le château
	        	GameControler.instance.getPlayer().enterCastle((CastleControler)CastleActor.this.controler);
	        }
		});
	}
}
