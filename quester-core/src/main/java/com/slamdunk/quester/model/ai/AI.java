package com.slamdunk.quester.model.ai;

import static com.slamdunk.quester.model.ai.Actions.NONE;
import static com.slamdunk.quester.model.ai.Actions.WAIT_COMPLETION;
import static com.slamdunk.quester.model.ai.Actions.CENTER_CAMERA;
import static com.slamdunk.quester.model.ai.Actions.END_TURN;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.display.actors.WorldActor;

public class AI {
	public static final ActionData ACTION_NONE = new ActionData(NONE, null);
	public static final ActionData ACTION_WAIT_COMPLETION = new ActionData(WAIT_COMPLETION, null);
	public static final ActionData ACTION_CENTER_CAMERA = new ActionData(CENTER_CAMERA, null);
	public static final ActionData ACTION_END_TURN = new ActionData(END_TURN, null);
	
	/**
	 * Corps associé à cette IA
	 */
	protected WorldActor body;
	
	/**
	 * Actions programmées
	 */
	protected List<ActionData> actions;
	
	public AI() {
		actions = new ArrayList<ActionData>();
		init();
	}

	/**
	 * Initialise l'IA
	 */
	public void init() {
		clearActions();
	}

	/**
	 * Détermine la prochaine action à effectuer
	 */
	public void think() {
		// Méthode chargée de décider ce que fera l'élément lorsque ce
		// sera à son tour de jouer. Par défaut, il ne fait rien et
		// termine son tour.
		nextAction();
	}

	/**
	 * Retourne le corps physique associé à cette IA
	 */
	public WorldActor getBody() {
		return body;
	}
	
	/**
	 * Définit le corps physique associé à cette IA
	 */
	public void setBody(WorldActor body) {
		this.body = body;
	}
	
	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	public void addAction(ActionData action) {
		actions.add(action);
	}

	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	public void addAction(Actions action, WorldActor target) {
		addAction(new ActionData(action, target));
	}

	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	public void addAction(Actions action, int x, int y) {
		addAction(new ActionData(action, x, y));
	}
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	public void setNextAction(ActionData action) {
		actions.add(0, action);
	}
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	public void setNextAction(Actions action, WorldActor target) {
		setNextAction(new ActionData(action, target));
	}
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	public void setNextAction(Actions action, int x, int y) {
		setNextAction(new ActionData(action, x, y));
	}
	
	/**
	 * Retourne la prochaine action à effectuer
	 */
	public ActionData getNextAction() {
		if (actions.isEmpty()) {
			return ACTION_NONE;
		}
		return actions.get(0);
	}
	
	/**
	 * Active la prochaine action programmée, ou NONE s'il n'y en a pas
	 */
	public void nextAction() {
		if (!actions.isEmpty()) {
			actions.remove(0);
		}
	}

	/**
	 * Supprime toutes les actions prévues
	 */
	public void clearActions() {
		actions.clear();
	}

	/**
	 * Définit les actions suivantes (en conservant l'ordre) pour être exécutées
	 * dès le prochaine coup. 
	 * @param actionWaitCompletion
	 * @param actionEndTurn
	 */
	public void setNextActions(ActionData... nextActions) {
		// Les actions sont insérées à l'envers car on les insère en tête de liste.
		for (int cur = nextActions.length - 1; cur >= 0; cur --) {
			setNextAction(nextActions[cur]);
		}
	}
}
