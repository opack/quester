package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.CENTER_CAMERA;
import static com.slamdunk.quester.logic.ai.QuesterActions.END_TURN;
import static com.slamdunk.quester.logic.ai.QuesterActions.EAT_ACTION;
import static com.slamdunk.quester.logic.ai.QuesterActions.NONE;
import static com.slamdunk.quester.logic.ai.QuesterActions.THINK;
import static com.slamdunk.quester.logic.ai.QuesterActions.WAIT_COMPLETION;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class AI {
	public static final ActionData ACTION_CENTER_CAMERA = new ActionData(CENTER_CAMERA, null);
	public static final ActionData ACTION_EAT_ACTION = new ActionData(EAT_ACTION, null);
	public static final ActionData ACTION_END_TURN = new ActionData(END_TURN, null);
	public static final ActionData ACTION_NONE = new ActionData(NONE, null);
	public static final ActionData ACTION_THINK = new ActionData(THINK, null);
	public static final ActionData ACTION_WAIT_COMPLETION = new ActionData(WAIT_COMPLETION, null);
	
	/**
	 * Actions programmées
	 */
	protected List<ActionData> actions;
	
	/**
	 * Lien vers le contrôleur
	 */
	protected CharacterControler controler;
	
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
	
	public CharacterControler getControler() {
		return controler;
	}

	public void setControler(CharacterControler controler) {
		this.controler = controler;
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
	public void addAction(QuesterActions action, WorldElementControler target) {
		addAction(new ActionData(action, target));
	}

	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	public void addAction(QuesterActions action, int x, int y) {
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
	public void setNextAction(QuesterActions action, WorldElementControler target) {
		setNextAction(new ActionData(action, target));
	}
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	public void setNextAction(QuesterActions action, int x, int y) {
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
