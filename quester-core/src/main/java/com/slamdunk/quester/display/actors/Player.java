package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.logic.ai.AI.ACTION_THINK;
import static com.slamdunk.quester.logic.ai.Actions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.Actions.ENTER_CASTLE;
import static com.slamdunk.quester.logic.ai.Actions.STEP_ON;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.model.map.CastleData;
import com.slamdunk.quester.model.map.PlayerData;
public class Player extends Character {

	public Player(PlayerData data, int col, int row) {
		super(data, Assets.hero, col, row);
	}

	public boolean enterCastle(Castle castle) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le déplacement vers le donjon est impossible
		if (moveNear(castle.getWorldX(), castle.getWorldY())) {
			// Déplace le joueur SUR le château
			getIA().addAction(new ActionData(STEP_ON, castle));
			// On entre dans le donjon une fois que le déplacement est fini
			getIA().addAction(ENTER_CASTLE, castle);
			return true;
		}
		return false;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean crossPath(PathToRegion path) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le déplacement vers le chemin est impossible
		if (moveNear(path.getWorldX(), path.getWorldY())) {
			// Déplace le joueur SUR le chemin
			getIA().addAction(new ActionData(STEP_ON, path));
			// On entre dans le une fois que le déplacement est fini
			getIA().addAction(CROSS_PATH, path);
			return true;
		}
		return false;
	}
	
	@Override
	public void act(float delta) {
		AI ai = getIA();
		ActionData action = data.ai.getNextAction();
		switch (action.action) {
			// Entrée dans un donjon
			case ENTER_CASTLE:
				WorldActor target = action.target;
				if (target != null && (target instanceof Castle)) {
					CastleData castleData = ((Castle)target).getElementData();
					Quester.getInstance().enterDungeon(
						castleData.dungeonWidth, castleData.dungeonHeight,
						castleData.roomWidth, castleData.roomHeight);
					
					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					data.ai.clearActions();
					data.ai.addAction(ACTION_THINK);
				}
				break;
			// Ouverture de porte/région a été prévue
			case CROSS_DOOR:	
			case CROSS_PATH:
				WorldActor path = action.target;
				if (path != null && (path instanceof PathToRegion)) {
					// Ouverture de la porte
					((PathToRegion)path).open();

					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					data.ai.clearActions();
					data.ai.addAction(ACTION_THINK);
				}
				break;
		}
		super.act(delta);
	}
}
