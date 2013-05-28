package com.slamdunk.quester.display.actors;

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

	public void enterCastle(Castle castle) {
		// On d�place le joueur vers le ch�teau
		if (moveTo(castle.getWorldX(), castle.getWorldY())) {
			// On entre dans le donjon une fois que le d�placement est fini
			getIA().addAction(WAIT_COMPLETION, null);
			getIA().addAction(ENTER_CASTLE, castle);
		}
	}
	
	@Override
	public void act(float delta) {
		IA ia = getIA();
		if (ia.getNextAction() == ENTER_CASTLE) {
			// Entr�e dans un donjon
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
		}
		super.act(delta);
	}
}
