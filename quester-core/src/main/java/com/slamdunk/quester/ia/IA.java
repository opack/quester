package com.slamdunk.quester.ia;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.map.points.Point;

public interface IA {

	/**
	 * Initialise l'IA
	 */
	void init();
	
	/**
	 * Détermine la prochaine action à effectuer
	 */
	void think();
	
	/**
	 * Retourne la prochaine action à effectuer
	 */
	Action getNextAction();
	
	/**
	 * Définit la prochaine action à effectuer.
	 */
	void setNextAction(Action action);

	/**
	 * Retourne les coordonnées de la prochaine cible
	 */
	Point getNextTargetPosition();
	
	/**
	 * Définit les coordonnées de la prochaine cible
	 */
	void setNextTargetPosition(int x, int y);
	
	/**
	 * Retourne la prochaine cible
	 */
	WorldActor getNextTarget();
	
	/**
	 * Définit la prochaine cible
	 */
	void setNextTarget(WorldActor target);
	
	/**
	 * Retourne le corps physique associé à cette IA
	 */
	WorldActor getBody();
	
	/**
	 * Définit leorps physique associé à cette IA
	 */
	void setBody(WorldActor body);

	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	void addAction(Action action, WorldActor target);
	
	/**
	 * Ajoute une action a exécuter à la suite des actions déjà programmées
	 */
	void addAction(Action action, int x, int y);

	/**
	 * Active la prochaine action programmée, ou NONE s'il n'y en a pas
	 */
	void nextAction();
}
