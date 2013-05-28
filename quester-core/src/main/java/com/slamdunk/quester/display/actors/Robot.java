package com.slamdunk.quester.display.actors;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class Robot extends Character {
	
	public Robot(String name, GameWorld gameWorld, int col, int row) {
		super(
			name,
			Assets.robot,
			gameWorld,
			col, row);
		setSpeed(4);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'attaquer
	        	getPlayer().attack(Robot.this);
	        }
		});
	}
	
	@Override
	public void think() {
		// Si le joueur est autour, on l'attaque
		boolean canAct = attack(getPlayer());
		
		// Sinon, on s'en approche
		if (!canAct) {
			List<UnmutablePoint> path = map.findPath(this, getPlayer());
			
			if (path != null && !path.isEmpty()) {
				// Un chemin a �t� trouv� jusqu'au joueur. Bien s�r on ne veut pas que le
				// robot marche sur le joueur, donc on va s'assurer que la prochaine case
				// vers laquelle on se dirige est bien vide.
				UnmutablePoint nextMove = path.get(0);
				canAct = moveTo(nextMove.getX(), nextMove.getY());
			}
		}
		
		// On ne peut pas se d�placer. On va laisser la classe m�re voir si elle
		// peut faire quelque chose. A priori, elle ne fera rien d'autre que
		// terminer notre tour.
		if (!canAct) {
			super.think();
		}
	}
}
