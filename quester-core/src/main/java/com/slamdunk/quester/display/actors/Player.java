package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.ia.Action.ENTER_CASTLE;
import static com.slamdunk.quester.ia.Action.NONE;
import static com.slamdunk.quester.ia.Action.THINK;

import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.core.Quester;
import com.slamdunk.quester.ia.IA;

public class Player extends Character {

	public Player(String name, IA ia, GameWorld gameWorld, int col, int row) {
		super(name, ia, Assets.hero, gameWorld, col, row);
	}

	public void enterCastle(Castle castle) {
		// On déplace le joueur vers le château
		moveTo(castle.getWorldX(), castle.getWorldY());
		
		// On entre dans le donjon une fois que le déplacement est fini
		getIA().addAction(ENTER_CASTLE, castle);
	}
	
	@Override
	public void act(float delta) {
		IA ia = getIA();
		if (ia.getNextAction() != NONE) {
			switch (ia.getNextAction()) {
				// Entrée dans un donjon
				case ENTER_CASTLE:
					WorldActor target = ia.getNextTarget();
					if (target != null && (target instanceof Castle)) {
						Castle castle = (Castle)target;
						Quester.getInstance().enterDungeon(
							castle.getDungeonWidth(), castle.getDungeonHeight(),
							castle.getRoomWidth(), castle.getRoomHeight());
						
						// L'action est consommée : réalisation de la prochaine action
						ia.nextAction();
					} else {
						// L'action n'est pas valide : on repart en réflexion
						ia.setNextAction(THINK);
						ia.setNextTarget(null);
					}
					break;
			}
		}
		super.act(delta);
	}
}
