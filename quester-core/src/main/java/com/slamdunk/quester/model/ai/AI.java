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
	 * Corps associ� � cette IA
	 */
	protected WorldActor body;
	
	/**
	 * Actions programm�es
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
	 * D�termine la prochaine action � effectuer
	 */
	public void think() {
		// M�thode charg�e de d�cider ce que fera l'�l�ment lorsque ce
		// sera � son tour de jouer. Par d�faut, il ne fait rien et
		// termine son tour.
		nextAction();
	}

	/**
	 * Retourne le corps physique associ� � cette IA
	 */
	public WorldActor getBody() {
		return body;
	}
	
	/**
	 * D�finit le corps physique associ� � cette IA
	 */
	public void setBody(WorldActor body) {
		this.body = body;
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
	public void addAction(Actions action, WorldActor target) {
		addAction(new ActionData(action, target));
	}

	/**
	 * Ajoute une action a ex�cuter � la suite des actions d�j� programm�es
	 */
	public void addAction(Actions action, int x, int y) {
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
	public void setNextAction(Actions action, WorldActor target) {
		setNextAction(new ActionData(action, target));
	}
	
	/**
	 * D�finit la prochaine action � effectuer.
	 */
	public void setNextAction(Actions action, int x, int y) {
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
