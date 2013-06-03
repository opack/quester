package com.slamdunk.quester.model.ai;

import static com.slamdunk.quester.model.ai.Action.NONE;
import static com.slamdunk.quester.model.ai.Action.THINK;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.model.points.Point;

public class CharacterAI implements AI {
	private class ActionData {
		Action action;
		WorldActor target;
		int targetX;
		int targetY;
		
		ActionData(Action action, WorldActor target) {
			this.action = action;
			this.target = target;
			if (target == null) {
				targetX = -1;
				targetY = -1;
			} else {
				targetX = target.getWorldX();
				targetY = target.getWorldY();
			}
		}

		ActionData(Action action, int targetX, int targetY) {
			this.action = action;
			this.targetX = targetX;
			this.targetY = targetY;
			this.target = null;
		}
	}
	
	// Cible de la prochaine action, utile notamment en cas d'attaque
	private WorldActor nextTarget;
	// Position de la prochaine cible, utile notamment en cas de déplacement
	private Point nextTargetPosition;
	// Prochaine action à réaliser
	private Action nextAction;
	/**
	 * Corps associé à cette IA
	 */
	private WorldActor body;
	
	/**
	 * Actions programmées
	 */
	private List<ActionData> actions;
	
	public CharacterAI() {
		nextTargetPosition = new Point(-1, -1);
		actions = new ArrayList<ActionData>();
		init();
	}

	@Override
	public void init() {
		nextAction = THINK;
		nextTarget = null;
		nextTargetPosition.setXY(-1, -1);
		actions.clear();
	}

	@Override
	public void setNextTarget(WorldActor target) {
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
	public Action getNextAction() {
		return nextAction;
	}

	@Override
	public WorldActor getNextTarget() {
		return nextTarget;
	}

	@Override
	public Point getNextTargetPosition() {
		return nextTargetPosition;
	}

	@Override
	public void setNextTargetPosition(int x, int y) {
		nextTargetPosition.setXY(x, y);
	}
	
	public void setNextTargetPosition(WorldActor target) {
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
	public WorldActor getBody() {
		return body;
	}
	
	public Character getCharacter() {
		return (Character)body;
	}

	@Override
	public void setBody(WorldActor body) {
		this.body = body;
	}

	@Override
	public void addAction(Action action, WorldActor target) {
		actions.add(new ActionData(action, target));
	}

	@Override
	public void addAction(Action action, int x, int y) {
		actions.add(new ActionData(action, x, y));
	}
	
	@Override
	public void nextAction() {
		if (actions.isEmpty()) {
			setNextAction(NONE);
			setNextTarget(null);
		} else {
			ActionData data = actions.remove(0);
			setNextAction(data.action);
			setNextTarget(data.target);
			// S'il n'y a pas de cible, on assigne les coordonnées comme target.
			// NB : si la cible est valide, setNextTargetPosition() sera tout
			// de même appelé par setNextTarget().
			if (data.target == null) {
				setNextTargetPosition(data.targetX, data.targetY);
			}
		}
	}
}
