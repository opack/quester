package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ContextMenuControler;

public class ContextMenuActor extends WorldElementActor {
	
	private QuesterActions action;

	public ContextMenuActor(TextureRegion texture, QuesterActions action) {
		super(texture);
		
		this.action = action;
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	ContextMenuControler controler = (ContextMenuControler)ContextMenuActor.this.controler;
	        	controler.onMenuItemClicked(ContextMenuActor.this.getAction());
	        }
		});
	}
	
	public QuesterActions getAction() {
		return action;
	}

	public void setAction(QuesterActions action) {
		this.action = action;
	}
}
