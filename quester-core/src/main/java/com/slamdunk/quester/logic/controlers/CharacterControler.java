package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYERS_OBSTACLES;
import static com.slamdunk.quester.logic.ai.AI.ACTION_END_TURN;
import static com.slamdunk.quester.logic.ai.AI.ACTION_THINK;
import static com.slamdunk.quester.logic.ai.AI.ACTION_WAIT_COMPLETION;
import static com.slamdunk.quester.logic.ai.Actions.ATTACK;
import static com.slamdunk.quester.logic.ai.Actions.END_TURN;
import static com.slamdunk.quester.logic.ai.Actions.NONE;
import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.logic.ai.MoveActionData;
import com.slamdunk.quester.model.data.CharacterData;
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
	 * Son à jouer lorsque le personnage marche
	 */
	protected Sound stepsSound;
	
	public CharacterControler(CharacterData data, CharacterActor body, AI ai) {
		super(data, body);
		characterData = (CharacterData)data;
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
			for (CharacterListener listener : listeners) {
				listener.onCharacterDeath(this);
			}
		}
	}
	
	@Override
	public void receiveDamage(int damage) {
		// TODO Retirer la valeur d'armure éventuellement
		characterData.health -= damage;
		
		// Si un déplacement était en cours, il est interrompu
		stopMove();
		
		if (isDead()) {
			for (CharacterListener listener : listeners) {
				listener.onCharacterDeath(this);
			}
		}
	}

	@Override
	public boolean isDead() {
		return characterData.health <= 0;
	}
	
	public Sound getStepsSound() {
		return stepsSound;
	}

	public void setStepsSound(Sound stepsSound) {
		this.stepsSound = stepsSound;
	}

	/**
	 * Déplace le personnage jusqu'à ce qu'il atteigne les coordonnées indiquées.
	 */
	public boolean moveTo(int x, int y) {
		if (actor.getActions().size != 0
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
		path = GameControler.instance.getMapScreen().findPath(
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
		if (actor.getActions().size != 0
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
		ai.addAction(ACTION_END_TURN);
		return true;
	}
	
	/**
	 * Arrête le déplacement en cours
	 */
	public void stopMove() {
		prepareThinking();
	}
	

	/**
	 * Annule toutes les actions en cours et prépare le think()
	 */
	protected void prepareThinking() {
		path = null;
		if (data.element == PLAYER) {
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
				System.out.println("CharacterControler.act() " + getClass());
				if (action.target != null && (action.target instanceof Damageable)) {
					// Fait un bruit d'épée
					Sound swordSound = Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
					Assets.playSound(swordSound);
					
					// Retire des PV à la cible
					((Damageable)action.target).receiveDamage(characterData.attack);
					
					// L'action est consommée : réalisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					prepareThinking();
				}
				break;
				
			// Un déplacement a été prévu, on se déplace
			case MOVE:
				if (data.element == PLAYER) {
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
						path = GameControler.instance.getMapScreen().findPath(
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
							// Fait un bruit de pas pour le joueur seulement
							if (stepsSound != null) {
								Assets.playSound(stepsSound);
							}
							
							// Déplace le personnage
							actor.moveTo(nextX, nextY, 1 / characterData.speed);
							
							// Affichage du chemin retenu
							if (data.element == PLAYER) {
								mapScreen.showPath(path);
							}
							
							// Suppression de cette position du chemin
							path.remove(0);
							
							// On attend la fin du mouvement puis on termine le tour.
							// Le déplacement reprendra au tour suivant.
							ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
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
				if (actor.getActions().size == 0) {
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

	@Override
	protected boolean shouldEndTurn() {
		if (super.shouldEndTurn()) {
			if (ai.getNextAction().action == END_TURN) {
				// Si toutes les autres actions sont finies et qu'on doit
				// finir le tour, on supprime cette action et on fini le tour
				ai.nextAction();
				return true;
			} else {
				// Toutes les actions sont finies, on arrête le tour
				// si aucune autre action ne doit être effectuée
				return ai.getNextAction().action == NONE;
			}
		}
		return false;
	}
	
//	@Override
//	public void endTurn() {
//		super.endTurn();
//		// S'il n'y a aucune action à effectuer au prochain tour, 
//		// alors il va falloir réfléchir pour en trouver une !
//		if (ai.getNextAction().action == NONE) {
//			ai.addAction(ACTION_THINK);
//		}
//	}
	
	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	public AI getAI() {
		return ai;
	}

	public List<UnmutablePoint> getPath() {
		return path;
	}
}
