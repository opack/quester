package com.slamdunk.quester.ia;

import static com.slamdunk.quester.ia.Action.NONE;
import static com.slamdunk.quester.ia.Action.THINK;

import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.WorldElement;
import com.slamdunk.quester.map.points.Point;

public class CharacterIA implements IA {
	// Cible de la prochaine action, utile notamment en cas d'attaque
	private WorldElement nextTarget;
	// Position de la prochaine cible, utile notamment en cas de déplacement
	private Point nextTargetPosition;
	// Prochaine action à réaliser
	private Action nextAction;
	/**
	 * Corps associé à cette IA
	 */
	private WorldElement body;
	
	public CharacterIA() {
		nextTargetPosition = new Point(-1, -1);
		init();
	}

	@Override
	public void init() {
		nextAction = THINK;
		nextTarget = null;
		nextTargetPosition.setXY(-1, -1);
	}

	@Override
	public void setNextTarget(WorldElement target) {
		this.nextTarget = target;
		setNextTargetPosition(target);
	}

	@Override
	public void setNextAction(Action action) {
		this.nextAction = action;
		if (action == NONE) {
			setNextTarget(null);
		}
	}

	@Override
	public WorldElement getNextTarget() {
		return nextTarget;
	}

	@Override
	public Action getNextAction() {
		return nextAction;
	}

	@Override
	public Point getNextTargetPosition() {
		return nextTargetPosition;
	}

	@Override
	public void setNextTargetPosition(int x, int y) {
		nextTargetPosition.setXY(x, y);
	}
	
	public void setNextTargetPosition(WorldElement target) {
		if (target == null) {
			nextTargetPosition.setXY(-1, -1);
		} else {
			nextTargetPosition.setXY(target.getWorldX(), target.getWorldY());
		}
	}
	
	@Override
	public void think() {
		// Méthode chargée de décider ce que fera l'élément lorsque ce
		// sera à son tour de jouer. Par défaut, il ne fait rien et
		// termine son tour.
		setNextAction(NONE);
	}

	@Override
	public WorldElement getBody() {
		return body;
	}
	
	public Character getCharacter() {
		return (Character)body;
	}

	@Override
	public void setBody(WorldElement body) {
		this.body = body;
	}
}
