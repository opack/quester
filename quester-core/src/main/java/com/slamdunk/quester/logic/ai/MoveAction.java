package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapElements;
import com.slamdunk.quester.utils.Assets;

/**
 * Déplace le contrôleur vers les coordonnées spécifiées.
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
		// Avant de bouger, on s'assure que la case visée est toujours disponible.
		if (!ignoreArrivalWalkability
		&& !GameControler.instance.getScreen().getMap().isEmpty(ActorMap.LAYERS_OBSTACLES, destinationX, destinationY)) {
			character.prepareThink();
			return;
		}
		
		// Fait un bruit de pas
		Assets.playSound(character.getStepSound());
		
		// Déplace le personnage
		int oldX = character.getActor().getWorldX();
		int oldY = character.getActor().getWorldY();
		character.getActor().moveTo(destinationX, destinationY, 1 / character.getData().speed);
		
		// Avertit les listeners que le personnage bouge
		for (CharacterListener listener : character.getListeners()) {
			listener.onCharacterMoved(character, oldX, oldY);
		}
		
		// On attend la fin du mouvement puis on termine le tour.
		character.getAI().nextAction();
		if (GameControler.instance.getCurrentCharacter().getData().element == MapElements.PLAYER) {
			// Si c'est le joueur qui vient de se déplacer, on centre la caméra sur lui
			character.getAI().setNextActions(new WaitCompletionAction(character), new CenterCameraOnPlayerAction(),new EndTurnAction(character));
		} else {
			character.getAI().setNextActions(new WaitCompletionAction(character), new EndTurnAction(character));
		}
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
