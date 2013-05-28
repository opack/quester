package com.slamdunk.quester.ia;

public class PlayerIA extends CharacterIA {
	
	@Override
	public void think() {
		// Ne rien faire ici revient à continuer à appeler think()
		// jusqu'à ce qu'une action ait été initiée par le joueur.
		// nextAction vaudra alors ACTION_MOVE ou ACTION_ATTACK
		// par exemple, et on ne passera plus ici.
	}
}
