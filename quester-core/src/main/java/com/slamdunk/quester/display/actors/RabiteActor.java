package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class RabiteActor extends CharacterActor {
	
	private Clip idleAnimation;
	private Clip walkAnimation;
	private Clip attackAnimation;
	private boolean clipReady;
	
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
		
		walkAnimation = Assets.createClip("rabite/rabite-move.png", 4, 1, 0.3f);
		
		idleAnimation = Assets.createClip("rabite/rabite-idle.png", 4, 1, 0.3f);
		idleAnimation.setPlayMode(Animation.LOOP_PINGPONG);
		
		attackAnimation = Assets.createClip("rabite/rabite-attack.png", 3, 1, 0.15f);
		attackAnimation.setLastKeyFrameRunnable(new Runnable(){
			@Override
			public void run() {
				Assets.playSound(Assets.biteSound);
				currentAction = QuesterActions.NONE;
			}});
		
		clipReady = false;
		stateTime = 0f;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// On ne fait l'initialisation des propriétés des clips qu'au premier
		// affichage car avant la taille n'est pas connue
		if (!clipReady) {
			initClip(walkAnimation);
			initClip(idleAnimation);
			initClip(attackAnimation);
			clipReady = true;
		}
		
		super.draw(batch, parentAlpha);
	}
	
	private Clip initClip(Clip clip) {
		// La taille de la zone de dessin est la taille du WorldElementActor
		clip.drawArea.width = getWidth();
		clip.drawArea.height = getHeight();
		
		// La frame est agrandie en X et en Y d'un facteur permettant d'occuper toute la largeur
		TextureRegion aFrame = clip.getKeyFrame(0);
		clip.scaleX = clip.drawArea.width / aFrame.getRegionWidth();
		clip.scaleY = clip.scaleX;
		
		// Les frames doivent être dessinées au centre horizontal et à 25% du bas
		clip.alignX = 0.5f;
		clip.offsetY = 0.25f;
		return clip;
	}

	@Override
	public void drawSpecifics(SpriteBatch batch) {
		Clip clip;
		switch (currentAction) {
			case MOVE:
				clip = walkAnimation;
				break;
			case ATTACK:
				clip = attackAnimation;
				break;
			case NONE :
			default:
				clip = idleAnimation;
				break;
		}
		stateTime += Gdx.graphics.getDeltaTime();
		clip.drawArea.x = getX();
		clip.drawArea.y = getY();
		clip.flipH = isLookingLeft;
		clip.play(stateTime, batch);
		
		super.drawSpecifics(batch);
	}
}
