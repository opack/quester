package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.model.data.ElementData;

public class Ground extends WorldElementActor {
	public Ground(ElementData data, TextureRegion texture, int col, int row) {
		super(data, texture, col, row);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur de se déplacer jusqu'ici
	        	Player player = QuesterGame.instance.getPlayer();
	        	player.moveTo(Ground.this.getWorldX(), Ground.this.getWorldY());
	        }
		});
	}
}
