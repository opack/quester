package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.ia.Action.CROSS_PATH;
import static com.slamdunk.quester.ia.Action.ENTER_CASTLE;
import static com.slamdunk.quester.ia.Action.THINK;
import static com.slamdunk.quester.ia.Action.WAIT_COMPLETION;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.ia.IA;

public class Player extends Character {

	public Player(String name, IA ia, GameWorld gameWorld, int col, int row) {
		super(name, ia, Assets.hero, gameWorld, col, row);
	}

	public boolean enterCastle(Castle castle) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size == 0
		// Si le d�placement vers le donjon est impossible
		&& moveTo(castle.getWorldX(), castle.getWorldY())) {
			// On entre dans le donjon une fois que le d�placement est fini
			getIA().addAction(WAIT_COMPLETION, null);
			getIA().addAction(ENTER_CASTLE, castle);
			return true;
		}
		return false;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean crossPath(PathToRegion path) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size == 0
		// Si le d�placement vers le chemin est impossible
		&& moveTo(path.getWorldX(), path.getWorldY())) {
			// On traverse le chemin une fois que le d�placement est fini
			getIA().addAction(WAIT_COMPLETION, null);
			getIA().addAction(CROSS_PATH, path);
			return true;
		}
		return false;
	}
	
	@Override
	public void act(float delta) {
		IA ia = getIA();
		switch (ia.getNextAction()) {
			// Entr�e dans un donjon
			case ENTER_CASTLE:
				WorldActor target = ia.getNextTarget();
				if (target != null && (target instanceof Castle)) {
					Castle castle = (Castle)target;
					Quester.getInstance().enterDungeon(
						castle.getDungeonWidth(), castle.getDungeonHeight(),
						castle.getRoomWidth(), castle.getRoomHeight());
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ia.nextAction();
				} else {
					// L'action n'est pas valide : on repart en r�flexion
					ia.setNextAction(THINK);
					ia.setNextTarget(null);
				}
				break;
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_DOOR:	
			case CROSS_PATH:
				WorldActor path = ia.getNextTarget();
				if (path != null && (path instanceof PathToRegion)) {
					// Ouverture de la porte
					((PathToRegion)path).open();

					// L'action est consomm�e : r�alisation de la prochaine action
					ia.nextAction();
				} else {
					// L'action n'est pas valide : on repart en r�flexion
					ia.setNextAction(THINK);
					ia.setNextTarget(null);
				}
				break;
		}
		super.act(delta);
	}
}
