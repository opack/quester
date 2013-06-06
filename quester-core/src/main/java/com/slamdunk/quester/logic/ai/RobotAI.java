package com.slamdunk.quester.logic.ai;

import java.util.List;

import com.slamdunk.quester.logic.controlers.PlayerControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class RobotAI extends CharacterAI {
	
	@Override
	public void think() {
		PlayerControler player = GameControler.instance.getPlayer();
		
		// Si le joueur est autour, on l'attaque
		boolean canAct = controler.attack(player);
		
		// Sinon, on s'en approche.
		if (!canAct) {
			List<UnmutablePoint> path = GameControler.instance.getMapScreen().findPath(controler.getActor(), player.getActor());
			if (path != null && !path.isEmpty()) {
				// Un chemin a été trouvé jusqu'au joueur. On n'avance que d'une case,
				// car au prochain tour le joueur aura certainement bougé et on devra
				// recalculer une nouvelle action.
				UnmutablePoint nextMove = path.get(0);
				canAct = controler.moveTo(nextMove.getX(), nextMove.getY());
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
