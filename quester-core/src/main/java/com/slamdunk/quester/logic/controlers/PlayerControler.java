package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.ENTER_CASTLE;

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
		// S'il n'est pas possible de se rendre à ce chemin
		if (!updatePath(castle.actor.getWorldX(), castle.actor.getWorldY())) {
			return false;
		}
		// Déplace le joueur SUR le château
		MoveActionData moveAction = new MoveActionData(castle);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
		// On entre dans le donjon une fois que le déplacement est fini
		ai.addAction(ENTER_CASTLE, castle);
		return true;		
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le chemin n'est pas traversable
		if (!path.getData().isCrossable
		// S'il n'est pas possible de se rendre à ce chemin
		|| !updatePath(path.actor.getWorldX(), path.actor.getWorldY())) {
			return false;
		}
		// Déplace le joueur SUR le chemin
		MoveActionData moveAction = new MoveActionData(path);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
		// On entre dans le une fois que le déplacement est fini
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	@Override
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Entrée dans un donjon
			case ENTER_CASTLE:
				WorldElementControler target = action.target;
				if (target != null && (target instanceof CastleControler)) {
					CastleData castleData = ((CastleControler)target).getData();
					Quester.getInstance().enterDungeon(
						castleData.dungeonWidth, castleData.dungeonHeight,
						castleData.roomWidth, castleData.roomHeight,
						castleData.difficulty);
					
					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					prepareThinking();
				}
				break;
			// Ouverture de porte/région a été prévue
			case CROSS_PATH:
				WorldElementControler path = action.target;
				if (path != null && (path instanceof PathToAreaControler)) {
					// Ouverture de la porte
					((PathToAreaControler)path).open();

					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					prepareThinking();
				}
				break;
		}
		super.act(delta);
	}
}
