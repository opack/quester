package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.utils.Assets;
public class PlayerActor extends CharacterActor {

	private Clip walkRightClip;
	private Clip walkUpClip;
	private Clip walkDownClip;
	private Clip walkClip;
	
	private Clip attackRightClip;
	private Clip attackUpClip;
	private Clip attackDownClip;
	private Clip attackClip;
	
	private Clip idleRightClip;
	private Clip idleUpClip;
	private Clip idleDownClip;
	private Clip idleClip;
	
	public PlayerActor() {
		super(null);
		
		walkRightClip = createWalkClip("player-move_right.png");
		walkUpClip = createWalkClip("player-move_up.png");
		walkDownClip = createWalkClip("player-move_down.png");
		walkClip = walkDownClip;
		
		attackRightClip = createAttackClip("player-attack_right.png");
		attackUpClip = createAttackClip("player-attack_up.png");
		attackDownClip = createAttackClip("player-attack_down.png");
		attackClip = attackDownClip;
		
		idleRightClip = createIdleClip("player-idle_right.png");
		idleUpClip = createIdleClip("player-idle_up.png");
		idleDownClip = createIdleClip("player-idle_down.png");
		idleClip = idleDownClip;
	}
	
	private Clip createWalkClip(String spriteSheet) {
		Clip walkClip = Assets.createClip("player/" + spriteSheet, 6, 1, 0.16f);
		walkClip.setPlayMode(Animation.LOOP);
		initClip(walkClip);
		return walkClip;
	}
	
	private Clip createIdleClip(String spriteSheet) {
		Clip idleClip = Assets.createClip("player/" + spriteSheet, 1, 1, 1f);
		idleClip.setPlayMode(Animation.LOOP);
		initClip(idleClip);
		return idleClip;
	}

	private Clip createAttackClip(String spriteSheet) {
		Clip attackClip = Assets.createClip("player/" + spriteSheet, 2, 1, 0.33f);
		attackClip.setPlayMode(Animation.LOOP);
		attackClip.setLastKeyFrameRunnable(new Runnable(){
			@Override
			public void run() {
				Assets.playSound(Assets.punchSound);
				currentAction = QuesterActions.NONE;
			}});
		initClip(attackClip);
		return attackClip;
	}

	@Override
	public void moveTo(int destinationX, int destinationY, float duration) {
		// Si on se déplace vers le haut ou le bas, on modifie le clip
		if (destinationY > getWorldY()) {
			walkClip = walkUpClip;
			attackClip = attackUpClip;
			idleClip = idleUpClip;
		} else if (destinationY < getWorldY()) {
			walkClip = walkDownClip;
			attackClip = attackDownClip;
			idleClip = idleDownClip;
		} else {
			walkClip = walkRightClip;
			attackClip = attackRightClip;
			idleClip = idleRightClip;
		}
		
		super.moveTo(destinationX, destinationY, duration);
	}

	@Override
	public Clip getClip(QuesterActions action) {
		switch (action) {
			case MOVE:
				return walkClip;
			case ATTACK:
				return attackClip;
			case NONE:
			default:
				return idleClip;
		}
	}
}
