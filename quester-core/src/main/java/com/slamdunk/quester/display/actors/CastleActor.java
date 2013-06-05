package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;

public class CastleActor extends WorldElementActor {
	public CastleActor(TextureRegion texture, int col, int row) {
		super(texture, col, row);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'entrer dans le château
	        	QuesterGame.instance.getPlayer().enterCastle(CastleActor.this);
	        }
		});
	}
}
