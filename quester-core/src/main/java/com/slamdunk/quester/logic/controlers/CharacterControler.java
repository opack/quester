package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.QuesterActions.END_TURN;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.AIAction;
import com.slamdunk.quester.logic.ai.AttackAction;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.logic.ai.EndTurnAction;
import com.slamdunk.quester.logic.ai.MoveAction;
import com.slamdunk.quester.logic.ai.ThinkAction;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class CharacterControler extends WorldElementControler implements Damageable {

	/**
	 * Objet choissant les actions à effectuer
	 */
	protected AI ai;
	
	protected CharacterData characterData;
	
	/**
	 * Indique si ce Character est dans son tour de jeu
	 */
	private boolean isPlaying;
	
	/**
	 * Indique s'il faut afficher la destination et le chemin
	 * du personnage sur la carte
	 */
	private boolean isShowDestination;
	
	/**
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<UnmutablePoint> path;
	
	/**
	 * Objet à utiliser pour trouver un chemin entre 2 points.
	 */
	private AStar pathfinder;
	
	public CharacterControler(CharacterData data, CharacterActor body, AI ai) {
		super(data, body);
		listeners = new ArrayList<CharacterListener>();
		
		if (ai == null) {
			this.ai = new CharacterAI();
		} else {
			this.ai = ai;
		}
		ai.setControler(this);
		ai.init();
	}
	
	@Override
	public void act(float delta) {
		AIAction action = ai.getNextAction();
		action.act();
		super.act(delta);
	}

	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Approche le personnage de la cible puis l'attaque.
	 */
	public boolean attack(WorldElementControler target) {
		// Approche de la cible
		if (!moveNear(target.getActor().getWorldX(), target.getActor().getWorldY())) {
			return false;
		}
		
		// Attaque
		ai.addAction(new AttackAction(this, (Damageable)target));
		return true;
	}

	public boolean canAttack(WorldElementControler target) {
		// Impossible d'attaquer :
		// Si la cible n'est pas Damageable
		return (target instanceof Damageable)
		// Si la cible est morte
		&& !((Damageable)target).isDead();
	}

	public boolean canMoveTo(int x, int y) {
		final List<UnmutablePoint> litPath = GameControler.instance.getScreen().getMap().findPath(
			actor.getWorldX(), actor.getWorldY(), 
			x, y);
		// Impossible d'aller à l'emplacement :
		// Si aucun chemin n'existe
		return litPath != null && !litPath.isEmpty();
	}
	
	private void die() {
		// Récupération du clip de mort de cet acteur
		GameControler.instance.getScreen().getMapRenderer().createVisualEffect("explosion-death", actor);
		
		// On prévient les listeners que le Character meurt
		for (CharacterListener listener : listeners) {
			listener.onCharacterDeath(this);
		}
	}

	/**
	 * Arrête le tour courant
	 */
	public void endTurn() {
		// On arrête le tour courant si c'est à notre tour de jouer
		// et que la prochaine action n'est pas un END_TURN (sinon
		// ça va nous faire sauter 2 tours)
		// TODO Le check pour éviter 2 end turn est inutile car le end_turn fait un clearActions
		if (isPlaying() && ai.getNextAction().getAction() != END_TURN) {
			ai.setNextAction(new EndTurnAction(this));
		}
	}

	public AI getAI() {
		return ai;
	}
	
	public Sound getAttackSound() {
		return null;
	}

	@Override
	public CharacterData getData() {
		return characterData;
	}

	@Override
	public int getHealth() {
		return characterData.health;
	}
	
	public List<UnmutablePoint> getPath() {
		return path;
	}
	
	public AStar getPathfinder() {
		return pathfinder;
	}
	
	public Sound getStepSound() {
		return null;
	}
	
	/**
	 * Retourne true si other est sur une case voisine
	 * @param actor
	 * @param target
	 * @return
	 */
	public boolean isAround(WorldElementActor other) {
		// A côté s'ils sont sur le même X et avec 1 seule case d'écart en Y...
		return actor.getWorldX() == other.getWorldX() && Math.abs(actor.getWorldY() - other.getWorldY()) == 1
		// ... ou sur le même Y et avec une seule case d'écart en X
		|| actor.getWorldY() == other.getWorldY() && Math.abs(actor.getWorldX() - other.getWorldX()) == 1;
	}
	
	@Override
	public boolean isDead() {
		return characterData.health <= 0;
	}

	public boolean isHostile() {
		return false;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public boolean isShowDestination() {
		return isShowDestination;
	}
	
	public boolean moveNear(int x, int y) {
		return moveTo(x, y, true);
	}

	/**
	 * Déplace le personnage jusqu'à ce qu'il atteigne les coordonnées indiquées.
	 */
	public boolean moveTo(int x, int y) {
		return moveTo(x, y, false);
	}
	
	/**
	 * Déplace le personnage jusqu'à ce qu'il soit autour des coordonnées indiquées,
	 * en placant à chaque fois une torche.
	 */
	private boolean moveTo(int x, int y, boolean stopNear) {
		if (pathfinder == null) {
			return false;
		}
		
		// Calcule le chemin qu'il faudrait emprunter si on ne s'embêtait pas avec la lumière
		final List<UnmutablePoint> walkPath = pathfinder.findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y,
				true);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (walkPath == null) {
			return false;
		}
		
		// Comme on veut se déplacer "près" de la position, on retire le dernier point
		if (stopNear) {
			walkPath.remove(walkPath.size() - 1);
		}
		
		// Pour aller jusqu'à ce point, on doit prendre chaque position et s'assurer qu'elle
		// est éclairée puis s'y déplacer
		for (UnmutablePoint pos : walkPath) {
			ai.addAction(new MoveAction(this, pos.getX(), pos.getY()));
		}
		return true;
	}

	/**
	 * Annule toutes les actions en cours et prépare le think()
	 */
	public void prepareThinking() {
		path = null;
		if (isShowDestination) {
			GameControler.instance.getScreen().getMapRenderer().clearPath();
		}
		ai.clearActions();
		ai.setNextActions(new ThinkAction(this));
	}

	@Override
	public void receiveDamage(int damage) {
		// TODO Retirer la valeur d'armure éventuellement
		characterData.health -= damage;
		
		// Si un déplacement était en cours, il est interrompu
		stopMove();
		
		if (isDead()) {
			die();
		}
	}
	
	@Override
	public void setData(WorldElementData data) {
		super.setData(data);
		characterData = (CharacterData)data;
	}
	
	@Override
	public void setHealth(int value) {
		int oldValue = characterData.health;
		characterData.health = value;
		for (CharacterListener listener : listeners) {
			listener.onHealthPointsChanged(oldValue, value);
		}
		if (isDead()) {
			die();
		}
	}
	
	public void setPathfinder(AStar pathfinder) {
		this.pathfinder = pathfinder;
	}
	
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void setShowDestination(boolean isShowDestination) {
		this.isShowDestination = isShowDestination;
	}

	/**
	 * Arrête le déplacement en cours
	 */
	public void stopMove() {
		prepareThinking();
	}
	
	protected boolean updatePath(int x, int y) {
		path = GameControler.instance.getScreen().getMap().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		return path != null && !path.isEmpty();
	}

	public List<CharacterListener> getListeners() {
		return listeners;
	}
}
