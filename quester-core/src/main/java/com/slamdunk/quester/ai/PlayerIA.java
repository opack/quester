package com.slamdunk.quester.ai;

public class PlayerIA extends CharacterAI {
	
	@Override
	public void think() {
		// Ne rien faire ici revient � continuer � appeler think()
		// jusqu'� ce qu'une action ait �t� initi�e par le joueur.
		// nextAction vaudra alors ACTION_MOVE ou ACTION_ATTACK
		// par exemple, et on ne passera plus ici.
	}
}
