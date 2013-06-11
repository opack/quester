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
	 * Actions programm�es
	 */
	protected List<ActionData> actions;
	
	/**
	 * Lien vers le contr�leur
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
	 * D�termine la prochaine action � effectuer
	 */
	public void think() {
		// M�thode charg�e de d�cider ce que fera l'�l�ment lorsque ce
		// sera � son tour de jouer. Par d�faut, il ne fait rien et
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
	 * Ajoute une action a ex�cuter � la suite des actions d�j� programm�es
	 */
	public void addAction(ActionData action) {
		actions.add(action);
	}

	/**
	 * Ajoute une action a ex�cuter � la suite des actions d�j� programm�es
	 */
	public void addAction(QuesterActions action, WorldElementControler target) {
		addAction(new ActionData(action, target));
	}

	/**
	 * Ajoute une action a ex�cuter � la suite des actions d�j� programm�es
	 */
	public void addAction(QuesterActions action, int x, int y) {
		addAction(new ActionData(action, x, y));
	}
	
	/**
	 * D�finit la prochaine action � effectuer.
	 */
	public void setNextAction(ActionData action) {
		actions.add(0, action);
	}
	
	/**
	 * D�finit la prochaine action � effectuer.
	 */
	public void setNextAction(QuesterActions action, WorldElementControler target) {
		setNextAction(new ActionData(action, target));
	}
	
	/**
	 * D�finit la prochaine action � effectuer.
	 */
	public void setNextAction(QuesterActions action, int x, int y) {
		setNextAction(new ActionData(action, x, y));
	}
	
	/**
	 * Retourne la prochaine action � effectuer
	 */
	public ActionData getNextAction() {
		if (actions.isEmpty()) {
			return ACTION_NONE;
		}
		return actions.get(0);
	}
	
	/**
	 * Active la prochaine action programm�e, ou NONE s'il n'y en a pas
	 */
	public void nextAction() {
		if (!actions.isEmpty()) {
			actions.remove(0);
		}
	}

	/**
	 * Supprime toutes les actions pr�vues
	 */
	public void clearActions() {
		actions.clear();
	}

	/**
	 * D�finit les actions suivantes (en conservant l'ordre) pour �tre ex�cut�es
	 * d�s le prochaine coup. 
	 * @param actionWaitCompletion
	 * @param actionEndTurn
	 */
	public void setNextActions(ActionData... nextActions) {
		// Les actions sont ins�r�es � l'envers car on les ins�re en t�te de liste.
		for (int cur = nextActions.length - 1; cur >= 0; cur --) {
			setNextAction(nextActions[cur]);
		}
	}
}
