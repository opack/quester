package com.slamdunk.quester.logic.ai;

import java.util.List;

import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.CharacterListener;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

/**
 * Déplace le contrôleur vers les coordonnées spécifiées.
 */
public class MoveNearAction implements AIAction {
	private CharacterControler character;
	private WorldElementActor destination;
	
	public MoveNearAction(CharacterControler character, WorldElementActor destination) {
		this.character = character;
		this.destination = destination;
	}

	public void act() {
		// Met à jour le chemin menant près de la destination
		final List<UnmutablePoint> walkPath = character.getPathfinder().findPath(
			character.getActor().getWorldX(), character.getActor().getWorldY(), 
			destination.getWorldX(), destination.getWorldY(),
			true);
		if (walkPath == null) {
			// Pas de chemin ? Fin de l'action
			character.getAI().nextAction();
			return;
		}
		
		// On souhaite s'arrêter à côté
		walkPath.remove(walkPath.size() - 1);
		
		// Déplacement vers la prochaine position
		if (!walkPath.isEmpty()) {
			// Fait un bruit de pas
			Assets.playSound(character.getStepSound());
			
			// Déplace le personnage
			UnmutablePoint pos = walkPath.remove(0);
			int oldX = character.getActor().getWorldX();
			int oldY = character.getActor().getWorldY();
			character.getActor().moveTo(pos.getX(), pos.getY(), 1 / character.getData().speed);
			
			// Avertit les listeners que le personnage bouge
			for (CharacterListener listener : character.getListeners()) {
				listener.onCharacterMoved(character, oldX, oldY);
			}
		}
		
		// Si c'était la dernière position, alors on peut finir cette action
		if (walkPath.isEmpty()) {
			character.getAI().nextAction();
		}
		
		// On attend la fin du mouvement puis on termine le tour.
		character.getAI().setNextActions(new WaitCompletionAction(character), new EndTurnAction(character));
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.MOVE;
	}
}
