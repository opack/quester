package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.utils.Assets;

public class RabiteActor extends DamageableActor {
	
	private Clip attackClip;
	private Clip idleClip;
	private Clip walkClip;
	
	public RabiteActor() {
		super(null);

		walkClip = Assets.createClip("rabite/rabite-move.png", 4, 1, 0.3f);
		initClip(walkClip);
		
		idleClip = Assets.createClip("rabite/rabite-idle.png", 4, 1, 0.3f);
		idleClip.setPlayMode(Animation.LOOP_PINGPONG);
		initClip(idleClip);
		
		attackClip = Assets.createClip("rabite/rabite-attack.png", 3, 1, 0.15f);
		attackClip.setLastKeyFrameRunnable(new Runnable(){
			@Override
			public void run() {
				Assets.playSound(Assets.biteSound);
				currentAction = QuesterActions.NONE;
			}});
		initClip(attackClip);
	}
	
	@Override
	public Clip getClip(QuesterActions action) {
		switch (action) {
			case MOVE:
				return walkClip;
			case ATTACK:
				return  attackClip;
			case NONE:
			default:
				return  idleClip;
		}
	}
}
