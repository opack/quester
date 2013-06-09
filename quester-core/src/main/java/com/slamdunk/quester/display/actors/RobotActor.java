package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class RobotActor extends CharacterActor {
	
	private Animation idleAnimation;
	private Animation walkAnimation;
	private Animation attackAnimation;
	private TextureRegion currentFrame;
	
	public RobotActor() {
		super(null);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'attaquer
	        	GameControler.instance.getPlayer().attack(RobotActor.this.controler);
	        }
		});
		
		// DBG Animation
		walkAnimation = Assets.createAnimation("rabite/rabite-move.png", 4, 1, 0.3f);
		idleAnimation = Assets.createAnimation("rabite/rabite-idle.png", 4, 1, 0.3f);
		idleAnimation.setPlayMode(Animation.LOOP_PINGPONG);
		attackAnimation = Assets.createAnimation("rabite/rabite-attack.png", 3, 1, 0.15f);
		stateTime = 0f;
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// DBG Animation
		stateTime += Gdx.graphics.getDeltaTime();
		switch (currentAction) {
		case MOVE:
			currentFrame = walkAnimation.getKeyFrame(stateTime, true);
			break;
		case ATTACK:
			currentFrame = attackAnimation.getKeyFrame(stateTime, true);
			if (attackAnimation.isAnimationFinished(stateTime)) {
				currentAction = QuesterActions.NONE;
			}
			// TODO Créer un objet Animator qui permet de :
			// - retourner la frame à afficher
			// - dessine la frame dans un batch en la retournant si nécesaire
			// - déclenche une action sur certaines keyframes (un son, une action à la fin de l'animation...)
			break;
		case NONE :
		default:
			currentFrame = idleAnimation.getKeyFrame(stateTime, true);
			break;
		}
		float factor = getWidth() / currentFrame.getRegionWidth();
		float frameWidth = currentFrame.getRegionWidth() * factor;
		if (isLookingLeft) {
			// Si on regarde vers la gauche, on inverse la frame
			frameWidth *= -1;
		}
		float frameHeight = currentFrame.getRegionHeight() * factor;
		batch.draw(
			currentFrame,
			getX() + (getWidth() - frameWidth) / 2,
			getY() + getHeight() / 4,
			frameWidth,
			frameHeight);
		super.drawSpecifics(batch);
	}
}
