package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.controlers.DarknessControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

public class DarknessActor extends WorldElementActor {
	public DarknessActor(TextureRegion texture) {
		super(texture);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	PlayerControler player = GameControler.instance.getPlayer();
	        	DarknessControler controler = (DarknessControler)DarknessActor.this.controler;
	        	// Si la zone est dans l'ombre, on tente de placer une torche
	        	// TODO Vérifier s'il n'y a pas d'ennemi dans cette zone
	        	if (controler.getData().torchCount == 0) {
	        		player.placeTorch(controler);
	        	}
	        	// Si la zone est éclairée, le joueur s'y déplace
	        	else {
	        		player.moveTo(DarknessActor.this.getWorldX(), DarknessActor.this.getWorldY());
	        	}
	        }
		});
	}
}
