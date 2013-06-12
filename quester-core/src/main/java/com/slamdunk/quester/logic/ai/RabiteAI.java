package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

public class RabiteAI extends CharacterAI {
	
	@Override
	public void think() {
		PlayerControler player = GameControler.instance.getPlayer();
		
		clearActions();
		
		// On s'approche du joueur pour l'attaquer
		boolean canAct = controler.attack(player);
		
		// Si aucune action n'a pu être décidée. On va laisser la classe mère 
		// voir si elle peut faire quelque chose. A priori, elle ne fera 
		// rien d'autre que terminer notre tour.
		if (!canAct) {
			setNextAction(ACTION_END_TURN);			
		}
	}
}
