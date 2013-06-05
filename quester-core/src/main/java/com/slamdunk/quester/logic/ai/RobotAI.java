package com.slamdunk.quester.logic.ai;

import java.util.List;

import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class RobotAI extends CharacterAI {
	
	@Override
	public void think() {
		Player player = QuesterGame.instance.getPlayer();
		
		// Si le joueur est autour, on l'attaque
		boolean canAct = getCharacter().attack(player);
		
		// Sinon, on s'en approche.
		if (!canAct) {
			List<UnmutablePoint> path = getCharacter().findPathTo(player);
			if (path != null && !path.isEmpty()) {
				// Un chemin a été trouvé jusqu'au joueur. On n'avance que d'une case,
				// car au prochain tour le joueur aura certainement bougé et on devra
				// recalculer une nouvelle action.
				UnmutablePoint nextMove = path.get(0);
				canAct = getCharacter().moveTo(nextMove.getX(), nextMove.getY());
			}
		}
		
		// Une action a été décidée : on l'effectue
		if (canAct) {
			nextAction();
		} else {
			// Aucune action n'a pu être décidée. On va laisser la classe mère 
			// voir si elle peut faire quelque chose. A priori, elle ne fera 
			// rien d'autre que terminer notre tour.
			super.think();
		}
	}
}
