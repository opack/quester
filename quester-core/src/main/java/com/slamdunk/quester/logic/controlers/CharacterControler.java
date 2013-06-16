package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.AI.ACTION_EAT_ACTION;
import static com.slamdunk.quester.logic.ai.AI.ACTION_END_TURN;
import static com.slamdunk.quester.logic.ai.AI.ACTION_THINK;
import static com.slamdunk.quester.logic.ai.AI.ACTION_WAIT_COMPLETION;
import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;
import static com.slamdunk.quester.logic.ai.QuesterActions.END_TURN;
import static com.slamdunk.quester.logic.ai.QuesterActions.NONE;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.logic.ai.MoveActionData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.AStar;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class CharacterControler extends WorldElementControler implements Damageable {

	protected CharacterData characterData;
	
	/**
	 * Objets int�ress�s par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<UnmutablePoint> path;
	
	/**
	 * Objet choissant les actions � effectuer
	 */
	protected AI ai;
	
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
	 * Objet � utiliser pour trouver un chemin entre 2 points.
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
	public void setData(WorldElementData data) {
		super.setData(data);
		characterData = (CharacterData)data;
	}

	@Override
	public CharacterData getData() {
		return characterData;
	}
	
	public AStar getPathfinder() {
		return pathfinder;
	}

	public void setPathfinder(AStar pathfinder) {
		this.pathfinder = pathfinder;
	}

	@Override
	public int getHealth() {
		return characterData.health;
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
	
	@Override
	public void receiveDamage(int damage) {
		// TODO Retirer la valeur d'armure �ventuellement
		characterData.health -= damage;
		
		// Si un d�placement �tait en cours, il est interrompu
		stopMove();
		
		if (isDead()) {
			die();
		}
	}

	private void die() {
		// R�cup�ration du clip de mort de cet acteur
		GameControler.instance.getScreen().getMap().createVisualEffect("explosion-death", actor);
		
		// On pr�vient les listeners que le Character meurt
		for (CharacterListener listener : listeners) {
			listener.onCharacterDeath(this);
		}
	}

	@Override
	public boolean isDead() {
		return characterData.health <= 0;
	}
	
	public Sound getStepSound() {
		return null;
	}

	public Sound getAttackSound() {
		return null;
	}

	protected boolean updatePath(int x, int y) {
		path = GameControler.instance.getScreen().getMap().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		return path != null && !path.isEmpty();
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
		// Impossible d'aller � l'emplacement :
		// Si aucun chemin n'existe
		return litPath != null && !litPath.isEmpty();
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
		ai.addAction(ATTACK, target);
		return true;
	}
	
	public boolean moveNear(int x, int y) {
		return moveTo(x, y, true);
	}
	
	/**
	 * D�place le personnage jusqu'� ce qu'il soit autour des coordonn�es indiqu�es,
	 * en placant � chaque fois une torche.
	 */
	private boolean moveTo(int x, int y, boolean stopNear) {
		if (pathfinder == null) {
			return false;
		}
		
		// Calcule le chemin qu'il faudrait emprunter si on ne s'emb�tait pas avec la lumi�re
		final List<UnmutablePoint> walkPath = pathfinder.findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y,
				true);
		
		// S'il n'y a pas de chemin, on ne fait rien
		if (walkPath == null) {
			return false;
		}
		
		// Comme on veut se d�placer "pr�s" de la position, on retire le dernier point
		if (stopNear) {
			walkPath.remove(walkPath.size() - 1);
		}
		
		// Pour aller jusqu'� ce point, on doit prendre chaque position et s'assurer qu'elle
		// est �clair�e puis s'y d�placer
		for (UnmutablePoint pos : walkPath) {
			ai.addAction(new MoveActionData(pos.getX(), pos.getY()));
		}
		return true;
	}

	/**
	 * D�place le personnage jusqu'� ce qu'il atteigne les coordonn�es indiqu�es.
	 */
	public boolean moveTo(int x, int y) {
		return moveTo(x, y, false);
	}
	
	/**
	 * Arr�te le d�placement en cours
	 */
	public void stopMove() {
		prepareThinking();
	}
	
	/**
	 * Arr�te le tour courant
	 */
	public void endTurn() {
		// On arr�te le tour courant si c'est � notre tour de jouer
		// et que la prochaine action n'est pas un END_TURN (sinon
		// �a va nous faire sauter 2 tours)
		// TODO Le check pour �viter 2 end turn est inutile car le end_turn fait un clearActions
		if (isPlaying() && ai.getNextAction().action != END_TURN) {
			ai.setNextAction(ACTION_END_TURN);
		}
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public boolean isShowDestination() {
		return isShowDestination;
	}

	public void setShowDestination(boolean isShowDestination) {
		this.isShowDestination = isShowDestination;
	}

	/**
	 * Annule toutes les actions en cours et pr�pare le think()
	 */
	protected void prepareThinking() {
		path = null;
		if (isShowDestination) {
			GameControler.instance.getScreen().getMap().clearPath();
		}
		ai.clearActions();
		ai.setNextActions(ACTION_THINK);
	}
	
	@Override
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Une frappe a �t� pr�vue, on attaque
			case ATTACK:
				// Lance l'animation de l'attaque
				actor.setCurrentAction(ATTACK, action.targetX);
				
				// Fait un bruit d'�p�e
				Assets.playSound(getAttackSound());
				
				// Retire des PV � la cible
				((Damageable)action.target).receiveDamage(characterData.attack);
				
				// L'action est consomm�e : r�alisation de la prochaine action
				ai.nextAction();
				ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_EAT_ACTION);
				break;
			
			// Consomme un point d'action et arr�te le tour si n�cessaire
			case EAT_ACTION:
				ai.nextAction();
				
				int oldAP = characterData.actionsLeft;
				characterData.actionsLeft--;
				for (CharacterListener listener : listeners) {
					listener.onActionPointsChanged(oldAP, characterData.actionsLeft);
				}
				if (characterData.actionsLeft <= 0) {
					GameControler.instance.nextPhase();
					prepareThinking();
				}
				break;
				
			// Le tour doit s'achever : toutes les actions encore en cours sont annul�es
			case END_TURN:
				GameControler.instance.nextPlayer();
				prepareThinking();
				break;
				
			// Un d�placement a �t� pr�vu, on se d�place
			case MOVE:
				// Fait un bruit de pas
				Assets.playSound(getStepSound());
				
				// D�place le personnage
				actor.moveTo(action.targetX, action.targetY, 1 / characterData.speed);
				
				// On attend la fin du mouvement puis on termine le tour.
				ai.nextAction();
				ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_EAT_ACTION);
				break;
					
			// Rien � faire. Ce n'est pas vraiment productif, donc on
			// va terminer le tour puis r�fl�chir � une meilleure action
			// la prochaine fois.
			case NONE:
				prepareThinking();
				break;
				
			// D�termination de la prochaine action.
			case THINK:
				ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (actor.getCurrentAction() == NONE) {
					// L'attente est finie, on ex�cute l'action suivante
					ai.nextAction();
				}
				break;
			
			// Une action inconnue a �t� demand�e : on ne fait rien
			default:
				break;
				
		}
		super.act(delta);
	}
	
	/**
	 * Retourne true si other est sur une case voisine
	 * @param actor
	 * @param target
	 * @return
	 */
	public boolean isAround(WorldElementActor other) {
		// A c�t� s'ils sont sur le m�me X et avec 1 seule case d'�cart en Y...
		return actor.getWorldX() == other.getWorldX() && Math.abs(actor.getWorldY() - other.getWorldY()) == 1
		// ... ou sur le m�me Y et avec une seule case d'�cart en X
		|| actor.getWorldY() == other.getWorldY() && Math.abs(actor.getWorldX() - other.getWorldX()) == 1;
	}
	
	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	public AI getAI() {
		return ai;
	}

	public List<UnmutablePoint> getPath() {
		return path;
	}

	/**
	 * Compte le nombre de points d'actions � attribuer � ce personnage et 
	 * met � jour la data en concordance.
	 */
	public void updateActionPoints() {
		final int oldValue = characterData.actionsLeft;
		characterData.actionsLeft = countActionPoints();
		for (CharacterListener listener : listeners) {
			listener.onActionPointsChanged(oldValue, characterData.actionsLeft);
		}
	}
	
	/**
	 * Compte et retourne le nombre de points d'action que devrait re�evoir ce personnage.
	 * @return
	 */
	protected int countActionPoints() {
		return 0;
	}

	public boolean isHostile() {
		return false;
	}
}
