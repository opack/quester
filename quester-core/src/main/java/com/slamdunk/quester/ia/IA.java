package com.slamdunk.quester.ia;

import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.map.points.Point;

public interface IA {

	/**
	 * Initialise l'IA
	 */
	void init();
	
	/**
	 * D�termine la prochaine action � effectuer
	 */
	void think();
	
	/**
	 * Retourne la prochaine action � effectuer
	 */
	Action getNextAction();
	
	/**
	 * D�finit la prochaine action � effectuer
	 */
	void setNextAction(Action action);

	/**
	 * Retourne les coordonn�es de la prochaine cible
	 */
	Point getNextTargetPosition();
	
	/**
	 * D�finit les coordonn�es de la prochaine cible
	 */
	void setNextTargetPosition(int x, int y);
	
	/**
	 * Retourne la prochaine cible
	 */
	WorldActor getNextTarget();
	
	/**
	 * D�finit la prochaine cible
	 */
	void setNextTarget(WorldActor target);
	
	/**
	 * Retourne le corps physique associ� � cette IA
	 */
	WorldActor getBody();
	
	/**
	 * D�finit leorps physique associ� � cette IA
	 */
	void setBody(WorldActor body);
}