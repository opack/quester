package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class RabiteActor extends DamageableActor {
	
	private Clip attackClip;
//	private Clip deathClip;
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
		// DBG Tristesse ! Quand on joue le son de la mort, il entre "en collision" avec le bruit de l'épée :(
//		deathAnimation.setFirstKeyFrameRunnable(new Runnable(){
//			@Override
//			public void run() {
//				Assets.playSound(Assets.dieSound);
//			}});
	}
	
	@Override
	public Clip getClip(QuesterActions action) {
		switch (action) {
			case MOVE:
				return walkClip;
			case ATTACK:
				return  attackClip;
//			case DIE:
//				return Assets.getVisualEffectClip("explosion-death");
			case NONE:
			default:
				return  idleClip;
		}
	}
	
	private Clip initClip(Clip clip) {
		// La taille de la zone de dessin est la taille du WorldElementActor
		ActorMap map = GameControler.instance.getScreen().getMap();
		clip.drawArea.width = map.getCellWidth();
		clip.drawArea.height = map.getCellHeight();
		
		// La frame est agrandie en X et en Y d'un facteur permettant d'occuper toute la largeur
		TextureRegion aFrame = clip.getKeyFrame(0);
		clip.scaleX = clip.drawArea.width / aFrame.getRegionWidth();
		clip.scaleY = clip.scaleX;
		
		// Les frames doivent être dessinées au centre horizontal et à 25% du bas
		clip.alignX = 0.5f;
		clip.offsetY = 0.25f;
		return clip;
	}
}
