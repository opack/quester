package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.model.ai.AI;

public class Robot extends Character {
	
	public Robot(String name, AI ai, int col, int row) {
		super(
			name, ai,
			Assets.robot,
			col, row);
		ai.setBody(this);
		
		setSpeed(4);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'attaquer
	        	QuesterGame.instance.getPlayer().attack(Robot.this);
	        }
		});
	}
}
