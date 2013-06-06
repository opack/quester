package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.logic.controlers.PlayerControler;

public class GroundActor extends WorldElementActor {
	public GroundActor(TextureRegion texture) {
		super(texture);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur de se déplacer jusqu'ici
	        	PlayerControler player = QuesterGame.instance.getPlayer();
	        	player.moveTo(GroundActor.this.getWorldX(), GroundActor.this.getWorldY());
	        }
		});
	}
}
