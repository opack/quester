package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.utils.Assets;

/**
 * Déplace le contrôleur vers les coordonnées spécifiées.
 */
public class MoveAction implements AIAction {
	private CharacterControler character;
	private int destinationX;
	private int destinationY;
	
	public MoveAction(CharacterControler character, int destinationX, int destinationY) {
		this.character = character;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
	}
	
	public void act() {
		// Fait un bruit de pas
		Assets.playSound(character.getStepSound());
		
		// Déplace le personnage
		character.getActor().moveTo(destinationX, destinationY, 1 / character.getData().speed);
		
		// On attend la fin du mouvement puis on termine le tour.
		character.getAI().nextAction();
		character.getAI().setNextActions(new WaitCompletionAction(character));
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
