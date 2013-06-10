package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.slamdunk.quester.display.Clip;

public class ClipActor extends WorldElementActor {

	public Clip clip;
	
	public ClipActor(Clip clip) {
		super(null);
		this.clip = clip;
	}
	
	public ClipActor() {
		super(null);
	}
	
	public ClipActor(WorldElementActor otherActor) {
		super(null);
		setPosition(otherActor.getX(), otherActor.getY());
		setSize(otherActor.getWidth(), otherActor.getHeight());
	}

	@Override
	protected void drawSpecifics(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		// Il faut mettre à jour ces champs car un même
		// clip peut servir pour plusieurs Actor.
		clip.drawArea.x = getX();
		clip.drawArea.y = getY();
		clip.drawArea.width = getWidth();
		clip.drawArea.height = getHeight();
		clip.flipH = isLookingLeft;
		
		// Dessin du clip
		clip.play(stateTime, batch);
		
		super.drawSpecifics(batch);
	}
}
