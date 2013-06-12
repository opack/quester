package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.controlers.GamePhases.ATTACK;

import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;
public class RabiteAI extends CharacterAI {
	
	@Override
	public void think() {
		PlayerControler player = GameControler.instance.getPlayer();
		
		clearActions();
		boolean canAct = false;
		
		// On s'approche du joueur pour l'attaquer
		if (GameControler.instance.getGamePhase() == ATTACK) {
			canAct = controler.attack(player);
		}
		
		// Si aucune action n'a pu être décidée, on finit le tour : le Rabite ne
		// fait rien pendant la phase LIGHT et une fois son tour ATTACK fini,
		// il n'a plus rien à faire.
		if (!canAct) {
			setNextAction(ACTION_END_TURN);			
		}
	}
}
