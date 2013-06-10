package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.AI.ACTION_END_TURN;
import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.ENTER_CASTLE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.MoveActionData;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.PlayerData;
import com.slamdunk.quester.utils.Assets;

public class PlayerControler extends CharacterControler {

	public PlayerControler(PlayerData data, PlayerActor body) {
		super(data, body, new PlayerAI());
		setShowDestination(true);
	}
	
	@Override
	public Sound getStepSound() {
		return Assets.stepsSound;
	}
	
	@Override
	public Sound getAttackSound() {
		return Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
	}

	public boolean enterCastle(CastleControler castle) {
		// Ignorer l'action dans les conditions suivantes :
		// S'il n'est pas possible de se rendre � ce chemin
		if (!updatePath(castle.actor.getWorldX(), castle.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le ch�teau
		MoveActionData moveAction = new MoveActionData(castle);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
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
		// S'il n'est pas possible de se rendre � ce chemin
		|| !updatePath(path.actor.getWorldX(), path.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le chemin
		MoveActionData moveAction = new MoveActionData(path);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
		// On entre dans le une fois que le d�placement est fini
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	/**
	 * Tente de placer une torche � la place de la zone d'ombre indiqu�e
	 * @param darknessActor
	 */
	public boolean placeTorch(DarknessControler darknessControler) {
		// Ignorer l'action dans les conditions suivantes :
		// Si la zone d'ombre est d�j� �clair�e par 3 torches
		if (darknessControler.darknessData.torchCount == 3
		// Si la zone d'ombre n'est pas accessible depuis le h�ros
		|| GameControler.instance.getMapScreen().getMap().findLightPath(
				actor.getWorldX(), actor.getWorldY(), 
				darknessControler.actor.getWorldX(), darknessControler.actor.getWorldY()) == null) {
			return false;
		}
		
		// Retrait de la zone d'ombre et cr�ation d'une torche
		ai.addAction(PLACE_TORCH, darknessControler);
		ai.addAction(ACTION_END_TURN);
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
						castleData.roomWidth, castleData.roomHeight,
						castleData.difficulty);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_PATH:
				WorldElementControler path = action.target;
				if (path != null && (path instanceof PathToAreaControler)) {
					// Ouverture de la porte
					((PathToAreaControler)path).open();

					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
			// Positionnement d'une torche
			case PLACE_TORCH:
				DarknessControler darknessControler = (DarknessControler)action.target;
				if (GameControler.instance.getMapScreen().getMap().findLightPath(
						actor.getWorldX(), actor.getWorldY(), 
						darknessControler.actor.getWorldX(), darknessControler.actor.getWorldY()) != null) {
					// Ajout d'une torche � la zone
					darknessControler.addTorch();
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
		}
		super.act(delta);
	}
}
