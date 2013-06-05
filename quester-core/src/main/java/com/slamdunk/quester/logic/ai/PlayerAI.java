package com.slamdunk.quester.logic.ai;


public class PlayerAI extends CharacterAI {
	
	@Override
	public void think() {
		// Ne rien faire ici revient à continuer à appeler think()
		// jusqu'à ce qu'une action ait été initiée par le joueur.
		// Il y aura alors une autre action que THINK
		if (actions.size() > 1) {
			// Si une action a été choisie, on quitte la réflexion
			// et on l'exécute
			nextAction();
		}
	}
}
