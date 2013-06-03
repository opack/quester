package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.model.ai.Action.CROSS_PATH;
import static com.slamdunk.quester.model.ai.Action.ENTER_CASTLE;
import static com.slamdunk.quester.model.ai.Action.THINK;
import static com.slamdunk.quester.model.ai.Action.WAIT_COMPLETION;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.model.ai.AI;
import com.slamdunk.quester.model.map.CastleData;
import com.slamdunk.quester.model.map.PlayerData;

public class Player extends Character {

	public Player(PlayerData data, int col, int row) {
		super(data, Assets.hero, col, row);
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
		AI ai = getIA();
		switch (ai.getNextAction()) {
			// Entr�e dans un donjon
			case ENTER_CASTLE:
				WorldActor target = ai.getNextTarget();
				if (target != null && (target instanceof Castle)) {
					CastleData castleData = ((Castle)target).getElementData();
					Quester.getInstance().enterDungeon(
						castleData.dungeonWidth, castleData.dungeonHeight,
						castleData.roomWidth, castleData.roomHeight);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// L'action n'est pas valide : on repart en r�flexion
					ai.setNextAction(THINK);
					ai.setNextTarget(null);
				}
				break;
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_DOOR:	
			case CROSS_PATH:
				WorldActor path = ai.getNextTarget();
				if (path != null && (path instanceof PathToRegion)) {
					// Ouverture de la porte
					((PathToRegion)path).open();

					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// L'action n'est pas valide : on repart en r�flexion
					ai.setNextAction(THINK);
					ai.setNextTarget(null);
				}
				break;
		}
		super.act(delta);
	}
}
