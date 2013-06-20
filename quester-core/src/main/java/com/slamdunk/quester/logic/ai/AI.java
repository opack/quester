package com.slamdunk.quester.logic.ai;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.logic.controlers.CharacterControler;

public class AI {
	/**
	 * Actions programmées
	 */
	protected List<AIAction> actions;
	
	/**
	 * Lien vers le contrôleur
	 */
	protected CharacterControler controler;
	
	public AI() {
		actions = new ArrayList<AIAction>();
		init();
	}
	
	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	public void addAction(AIAction action) {
		actions.add(action);
	}

	/**
	 * Supprime toutes les actions prévues
	 */
	public void clearActions() {
		actions.clear();
	}

	public CharacterControler getControler() {
		return controler;
	}

	/**
	 * Retourne la prochaine action à effectuer
	 */
	public AIAction getNextAction() {
		if (actions.isEmpty()) {
			return new NoAction(controler);
		}
		return actions.get(0);
	}

	/**
	 * Initialise l'IA
	 */
	public void init() {
		clearActions();
	}
	
	/**
	 * Active la prochaine action programmée, ou NONE s'il n'y en a pas
	 */
	public void nextAction() {
		if (!actions.isEmpty()) {
			actions.remove(0);
		}
	}
	
	public void setControler(CharacterControler controler) {
		this.controler = controler;
	}
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	public void setNextAction(AIAction action) {
		actions.add(0, action);
	}

	/**
	 * Définit les actions suivantes (en conservant l'ordre) pour être exécutées
	 * dès le prochaine coup. 
	 * @param actionWaitCompletion
	 * @param actionEndTurn
	 */
	public void setNextActions(AIAction... nextActions) {
		// Les actions sont insérées à l'envers car on les insère en tête de liste.
		for (int cur = nextActions.length - 1; cur >= 0; cur --) {
			setNextAction(nextActions[cur]);
		}
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
}
