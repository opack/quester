package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.model.data.CastleData;

public class Castle extends WorldActor {
	public Castle(CastleData data, TextureRegion texture, int col, int row) {
		super(data, texture, col, row);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'entrer dans le château
	        	QuesterGame.instance.getPlayer().enterCastle(Castle.this);
	        }
		});
	}
	
	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le château
		return false;
	}
	
	@Override
	public CastleData getElementData() {
		return (CastleData)elementData;
	}
}
