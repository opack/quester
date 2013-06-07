package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.AI.ACTION_THINK;
import static com.slamdunk.quester.logic.ai.Actions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.Actions.ENTER_CASTLE;
import static com.slamdunk.quester.logic.ai.Actions.STEP_ON;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.PlayerData;

public class PlayerControler extends CharacterControler {

	public PlayerControler(PlayerData data, PlayerActor body) {
		super(data, body, new PlayerAI());
	}

	public boolean enterCastle(CastleControler castle) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le d�placement vers le donjon est impossible
		if (!moveNear(castle.actor.getWorldX(), castle.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le ch�teau
		ai.addAction(new ActionData(STEP_ON, castle));
		// On entre dans le donjon une fois que le d�placement est fini
		ai.addAction(ENTER_CASTLE, castle);
		return true;		
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le chemin n'est pas traversable
		if (!path.getData().isCrossable
		// Si le d�placement vers le chemin est impossible
		|| !moveNear(path.actor.getWorldX(), path.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le chemin
		ai.addAction(new ActionData(STEP_ON, path));
		// On entre dans le une fois que le d�placement est fini
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	@Override
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Entr�e dans un donjon
			case ENTER_CASTLE:
				WorldElementControler target = action.target;
				if (target != null && (target instanceof CastleControler)) {
					CastleData castleData = ((CastleControler)target).getData();
					Quester.getInstance().enterDungeon(
						castleData.dungeonWidth, castleData.dungeonHeight,
						castleData.roomWidth, castleData.roomHeight);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					ai.clearActions();
					ai.addAction(ACTION_THINK);
				}
				break;
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_DOOR:
			case CROSS_PATH:
				WorldElementControler path = action.target;
				if (path != null && (path instanceof PathToAreaControler)) {
					// Ouverture de la porte
					((PathToAreaControler)path).open();

					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					ai.clearActions();
					ai.addAction(ACTION_THINK);
				}
				break;
		}
		super.act(delta);
	}
}
