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
		clip.drawArea.x = getX();
		clip.drawArea.y = getY();
		clip.flipH = isLookingLeft;
		clip.play(stateTime, batch);
		
		super.drawSpecifics(batch);
	}
}
