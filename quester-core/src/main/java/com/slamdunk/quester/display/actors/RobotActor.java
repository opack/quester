package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;

public class RobotActor extends CharacterActor {
	
	public RobotActor() {
		super(Assets.robot);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'attaquer
	        	QuesterGame.instance.getPlayer().attack(RobotActor.this.controler);
	        }
		});
	}
}
