package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class RabiteActor extends CharacterActor {
	
	private Clip idleClip;
	private Clip walkClip;
	private Clip attackClip;
	private Clip deathClip;
	
	public RabiteActor() {
		super(null);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'attaquer
	        	GameControler.instance.getPlayer().attack(RabiteActor.this.controler);
	        }
		});
		
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
		
		deathClip = Assets.createClip("rabite/rabite-death.png", 4, 1, 0.15f);
		// DBG Tristesse ! Quand on joue le son de la mort, il entre "en collision" avec le bruit de l'�p�e :(
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
			case DIE:
				return deathClip;
			case NONE:
			default:
				return  idleClip;
		}
	}
	
	private Clip initClip(Clip clip) {
		// La taille de la zone de dessin est la taille du WorldElementActor
		GameScreen screen = GameControler.instance.getMapScreen();
		clip.drawArea.width = screen.getCellWidth();
		clip.drawArea.height = screen.getCellHeight();
		
		// La frame est agrandie en X et en Y d'un facteur permettant d'occuper toute la largeur
		TextureRegion aFrame = clip.getKeyFrame(0);
		clip.scaleX = clip.drawArea.width / aFrame.getRegionWidth();
		clip.scaleY = clip.scaleX;
		
		// Les frames doivent �tre dessin�es au centre horizontal et � 25% du bas
		clip.alignX = 0.5f;
		clip.offsetY = 0.25f;
		return clip;
	}
}
