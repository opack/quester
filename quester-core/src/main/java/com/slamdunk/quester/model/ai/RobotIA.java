package com.slamdunk.quester.model.ai;

import java.util.List;

import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class RobotIA extends CharacterAI {
	
	private Player player;
	
	public RobotIA(Player player) {
		this.player = player;
	}
	
	@Override
	public void think() {
		// Si le joueur est autour, on l'attaque
		boolean canAct = getCharacter().attack(player);
		
		// Sinon, on s'en approche
		if (!canAct) {
			List<UnmutablePoint> path = getCharacter().findPathTo(player);
			
			if (path != null && !path.isEmpty()) {
				// Un chemin a été trouvé jusqu'au joueur. Bien sûr on ne veut pas que le
				// robot marche sur le joueur, donc on va s'assurer que la prochaine case
				// vers laquelle on se dirige est bien vide.
				UnmutablePoint nextMove = path.get(0);
				canAct = getCharacter().moveTo(nextMove.getX(), nextMove.getY());
			}
		}
		
		// On ne peut pas se déplacer. On va laisser la classe mère voir si elle
		// peut faire quelque chose. A priori, elle ne fera rien d'autre que
		// terminer notre tour.
		if (!canAct) {
			super.think();
		}
	}
}
