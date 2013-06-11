package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYERS_OBSTACLES;
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
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.logic.ai.MoveActionData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class CharacterControler extends WorldElementControler implements Damageable {

	protected CharacterData characterData;
	
	/**
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<UnmutablePoint> path;
	
	/**
	 * Objet choissant les actions à effectuer
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
		// TODO Retirer la valeur d'armure éventuellement
		characterData.health -= damage;
		
		// Si un déplacement était en cours, il est interrompu
		stopMove();
		
		if (isDead()) {
			die();
		}
	}

	private void die() {
		// Récupération du clip de mort de cet acteur
		GameControler.instance.getMapScreen().createVisualEffect("explosion-death", actor);
		
		// On prévient les listeners que le Character meurt
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

	/**
	 * Déplace le personnage jusqu'à ce qu'il atteigne les coordonnées indiquées.
	 */
	public boolean moveTo(int x, int y) {
		if (actor.getCurrentAction() != NONE
		// Détermine le chemin à suivre et le stocke
		|| !updatePath(x, y)) {
			return false;
		}
		// Suppression des actions en cours
		ai.clearActions();
		// Au prochain act, on va commencer à suivre ce chemin
		ai.addAction(new MoveActionData(x, y));
		return true;
	}
	

	protected boolean updatePath(int x, int y) {
		path = GameControler.instance.getMapScreen().getMap().findWalkPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		return path != null && !path.isEmpty();
	}
	
	/**
	 * Approche le personnage de la cible puis l'attaque.
	 */
	public boolean attack(WorldElementControler target) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (actor.getCurrentAction() != NONE
		// Si la cible n'est pas Damageable
		|| !(target instanceof Damageable)
		// Si la cible est morte
		|| ((Damageable)target).isDead()) {
			return false;
		}
		
		// Suppression des actions en cours
		ai.clearActions();

		// Si la cible est trop loin pour l'arme actuelle, on s'approche
		MapScreen mapScreen = GameControler.instance.getMapScreen();
		if (!mapScreen.isWithinRangeOf(actor, target.actor, characterData.weaponRange)) {
			if (!updatePath(target.actor.getWorldX(), target.actor.getWorldY())) {
				// Impossible d'atteindre la cible
				return false;
			}
			MoveActionData moveAction = new MoveActionData(target);
			moveAction.isMoveNearTarget = true;
			moveAction.isTracking = true;
			ai.addAction(moveAction);
		}
		// Attaque puis termine le tour
		ai.addAction(ATTACK, target);
		return true;
	}
	
	/**
	 * Arrête le déplacement en cours
	 */
	public void stopMove() {
		prepareThinking();
	}
	
	/**
	 * Arrête le tour courant
	 */
	public void endTurn() {
		// On arrête le tour courant si c'est à notre tour de jouer
		// et que la prochaine action n'est pas un END_TURN (sinon
		// ça va nous faire sauter 2 tours)
		// TODO Le check pour éviter 2 end turn est inutile car le end_turn fait un clearActions
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
	 * Annule toutes les actions en cours et prépare le think()
	 */
	protected void prepareThinking() {
		path = null;
		if (isShowDestination) {
			GameControler.instance.getMapScreen().clearPath();
		}
		ai.clearActions();
		ai.setNextActions(ACTION_THINK);
	}
	
	@Override
	public void act(float delta) {
		MapScreen mapScreen = GameControler.instance.getMapScreen();
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Une frappe a été prévue, on attaque
			case ATTACK:
				if (action.target != null && (action.target instanceof Damageable)
				&& mapScreen.isWithinRangeOf(actor, action.target.actor, characterData.weaponRange)) {
					// Lance l'animation de l'attaque
					actor.setCurrentAction(ATTACK, action.targetX);
					
					// Fait un bruit d'épée
					Assets.playSound(getAttackSound());
					
					// Retire des PV à la cible
					((Damageable)action.target).receiveDamage(characterData.attack);
					
					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
					ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_EAT_ACTION);
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					prepareThinking();
				}
				break;
			
			// Consomme un point d'action et arrête le tour si nécessaire
			case EAT_ACTION:
				characterData.actionsLeft--;
				if (characterData.actionsLeft <= 0) {
					ai.setNextAction(ACTION_END_TURN);
				} else {
					ai.nextAction();
				}
				break;
				
			// Le tour doit s'achever : toutes les actions encore en cours sont annulées
			case END_TURN:
				System.out.println("CharacterControler.act() END_TURN");
				GameControler.instance.endCurrentPlayerTurn();
				prepareThinking();
				break;
				
			// Un déplacement a été prévu, on se déplace
			case MOVE:
				if (isShowDestination) {
					mapScreen.clearPath();
				}
				// Détermine si on a finit le mouvement
				MoveActionData moveAction = (MoveActionData)action;
				boolean destinationReached = false;
				if (moveAction.isMoveNearTarget) {
					// S'il faut simplement s'approcher de la cible, la destination
					// est atteinte si on est autour de la cible
					destinationReached = isAround(actor, moveAction.target.actor);
				} else {
					// S'il faut vraiment aller jusqu'à la cible, la destination est
					// atteinte si on est sur ses coordonnées
					destinationReached = actor.getWorldX() == moveAction.targetX && actor.getWorldY() == moveAction.targetY;
				}
				// Si on est arrivés à la destination, c'est fini !
				if (destinationReached) {
					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
					path = null;
				} else {
					// On n'est toujours pas arrivé à destination : on continue à se déplacer.
					// Calcul du chemin à suivre
					if (moveAction.isTracking) {
						moveAction.targetX = moveAction.target.actor.getWorldX();
						moveAction.targetY = moveAction.target.actor.getWorldY();
						path = GameControler.instance.getMapScreen().getMap().findWalkPath(
							actor.getWorldX(), actor.getWorldY(), 
							moveAction.targetX, moveAction.targetY);
						if (path == null || path.isEmpty()) {
							// Pas de chemin possible.
							// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
							prepareThinking();
							break;
						}
					}
					// Déplacement vers la prochaine position
					if (path != null && !path.isEmpty()) {
						UnmutablePoint next = path.get(0);
						int nextX = next.getX();
						int nextY = next.getY();
						
						// On s'assure qu'on se dirige vers une case libre, donc ne contenant pas d'objet.
						// Même si un objet est traversable (ex : porte, chemin...) on veut s'arrêter
						// pour que le joueur ne se retrouve pas sur cet objet mais à côté.
						WorldElementActor onNextPos = mapScreen.getTopElementAt(nextX, nextY, LAYERS_OBSTACLES);
						if (onNextPos == null
						|| (moveAction.isStepOnTarget && onNextPos.equals(moveAction.target.actor))) {
							// Fait un bruit de pas
							Assets.playSound(getStepSound());
							
							// Déplace le personnage
							actor.moveTo(nextX, nextY, 1 / characterData.speed);
							
							// Affichage du chemin retenu
							if (isShowDestination) {
								mapScreen.showPath(path);
							}
							
							// Suppression de cette position du chemin
							path.remove(0);
							
							// On attend la fin du mouvement puis on termine le tour.
							// Le déplacement reprendra au tour suivant.
							ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_EAT_ACTION);
						} else {
							// Pas de chemin possible, on arrête le déplacement en cours et on
							// choisit une autre action
							prepareThinking();
						}
					} else {
						// Pas de chemin possible.
						// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
						prepareThinking();
					}
				}
				break;
					
			// Rien à faire. Ce n'est pas vraiment productif, donc on
			// va réfléchir à une meilleure action.
			case NONE:
				prepareThinking();
				break;
				
			// Détermination de la prochaine action.
			case THINK:
				ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (actor.getCurrentAction() == NONE) {
					// L'attente est finie, on exécute l'action suivante
					ai.nextAction();
				}
				break;
			
			// Une action non gérée a été demandée. On en choisit une autre.
			default:
				prepareThinking();
				break;
				
		}
		super.act(delta);
	}
	
	/**
	 * Retourne true si actor1 et actor2 sont sur des cases voisines
	 * @param actor
	 * @param target
	 * @return
	 */
	private boolean isAround(WorldElementActor actor1, WorldElementActor actor2) {
		// A côté s'ils sont sur le même X et avec 1 seule case d'écart en Y...
		return actor1.getWorldX() == actor2.getWorldX() && Math.abs(actor1.getWorldY() - actor2.getWorldY()) == 1
		// ... ou sur le même Y et avec une seule case d'écart en X
		|| actor1.getWorldY() == actor2.getWorldY() && Math.abs(actor1.getWorldX() - actor2.getWorldX()) == 1;
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
	 * Compte le nombre de points d'actions à attribuer à ce personnage et 
	 * met à jour la data en concordance.
	 */
	public void countActionPoints() {
		characterData.actionsLeft = 0;
	}
}
