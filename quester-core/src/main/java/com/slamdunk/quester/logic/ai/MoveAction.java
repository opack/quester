package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

/**
 * D�place le contr�leur vers les coordonn�es sp�cifi�es.
 */
public class MoveAction implements AIAction {
	private CharacterControler character;
	private int destinationX;
	private int destinationY;
	private boolean ignoreArrivalWalkability;
	
	public MoveAction(CharacterControler character, int destinationX, int destinationY) {
		this(character, destinationX, destinationY, false);
	}
	
	public MoveAction(CharacterControler character, int destinationX, int destinationY, boolean ignoreArrivalWalkability) {
		this.character = character;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.ignoreArrivalWalkability = ignoreArrivalWalkability;
	}

	public void act() {
		// Avant de bouger, on s'assure que la case vis�e est toujours disponible.
		if (!ignoreArrivalWalkability
		&& !GameControler.instance.getScreen().getMap().isEmpty(ActorMap.LAYERS_OBSTACLES, destinationX, destinationY)) {
			character.prepareThinking();
			return;
		}
		
		// Fait un bruit de pas
		Assets.playSound(character.getStepSound());
		
		// D�place le personnage
		character.getActor().moveTo(destinationX, destinationY, 1 / character.getData().speed);
		
		// On attend la fin du mouvement puis on termine le tour.
		character.getAI().nextAction();
		character.getAI().setNextActions(new WaitCompletionAction(character), new EndTurnAction(character));
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
