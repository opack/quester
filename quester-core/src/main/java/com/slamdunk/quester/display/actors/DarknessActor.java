package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.controlers.ContextMenuControler;
import com.slamdunk.quester.logic.controlers.DarknessControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.data.ContextMenuData;

public class DarknessActor extends WorldElementActor {
	public DarknessActor(TextureRegion texture) {
		super(texture);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	DarknessControler darknessControler = ((DarknessControler)DarknessActor.this.controler);
	        	
	        	// Affiche le menu contextuel
	        	ContextMenuData data = new ContextMenuData();
	        	data.sourceX = DarknessActor.this.getWorldX();
	        	data.sourceY = DarknessActor.this.getWorldY();
	        	data.radius = GameControler.instance.getMapScreen().getCellWidth();
	        	
	        	ContextMenuControler controler = new ContextMenuControler(data, darknessControler);
	        	controler.layoutItems();
	        }
		});
	}
}
