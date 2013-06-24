package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

public class RabiteAI extends CharacterAI {
	@Override
	public void think() {
		PlayerControler player = GameControler.instance.getPlayer();
		
		boolean canAct = false;
		
		// On s'approche du joueur pour l'attaquer
		canAct = controler.prepareAttack(player);
		
		// Si aucune action n'a pu être décidée, on finit le tour : le Rabite ne
		// fait rien pendant la phase LIGHT et une fois son tour ATTACK fini,
		// il n'a plus rien à faire.
		if (!canAct) {
			clearActions();
			setNextAction(new EndTurnAction(controler));			
		}
	}
}
