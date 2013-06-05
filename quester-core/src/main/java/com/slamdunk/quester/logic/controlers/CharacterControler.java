package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYERS_OBSTACLES;
import static com.slamdunk.quester.logic.ai.AI.ACTION_END_TURN;
import static com.slamdunk.quester.logic.ai.AI.ACTION_THINK;
import static com.slamdunk.quester.logic.ai.AI.ACTION_WAIT_COMPLETION;
import static com.slamdunk.quester.logic.ai.Actions.ATTACK;
import static com.slamdunk.quester.logic.ai.Actions.END_TURN;
import static com.slamdunk.quester.logic.ai.Actions.MOVE;
import static com.slamdunk.quester.logic.ai.Actions.NONE;
import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.points.UnmutablePoint;

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
	
	public CharacterControler(CharacterData data, CharacterActor body) {
		super(data, body);
		characterData = (CharacterData)data;
		listeners = new ArrayList<CharacterListener>();
		
		data.ai.init();
	}

	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le château
		return false;
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
		stopActions();
		
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
	

	/**
	 * Enregistrement d'une action demandant au personnage de se déplacer
	 * vers cette destination. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean moveTo(int x, int y) {
		if (actor.getActions().size != 0) {
			return false;
		}
		// Détermine le chemin à suivre et le stocke
		path = QuesterGame.instance.getMapScreen().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		if (path == null || path.isEmpty()) {
			return false;
		}
		// Au prochain act, on va commencer à suivre ce chemin
		characterData.ai.addAction(MOVE, x, y);
		return true;
	}
	
	public boolean moveNear(int x, int y) {
		if (actor.getActions().size != 0) {
			return false;
		}
		// Détermine le chemin à suivre et le stocke
		path = QuesterGame.instance.getMapScreen().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		if (path == null || path.isEmpty()) {
			return false;
		}
		
		// On veut s'arrêter avant la destination
		path.remove(path.size() - 1);
		
		// Au prochain act, on va commencer à suivre ce chemin
		// jusqu'à la destination
		if (!path.isEmpty()) {
			// Si on n'est pas déjà à côté de la destination,
			// on demande un déplacement là-bas
			UnmutablePoint destination = path.get(path.size() - 1);
			characterData.ai.addAction(MOVE, destination.getX(), destination.getY());
		}
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'attaquer
	 * cette cible. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean attack(WorldElementActor target) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (actor.getActions().size != 0
		// Si la cible n'est pas Damageable
		|| !(target instanceof Damageable)
		// Si la cible est morte
		|| ((Damageable)target).isDead()
		// Si la cible est trop loin pour l'arme actuelle, on s'approche
		|| !QuesterGame.instance.getMapScreen().isWithinRangeOf(actor, target, characterData.weaponRange)) {
			return false;
		}
		
		characterData.ai.addAction(ATTACK, target);
		return true;
	}
	
	public void stopActions() {
		// Suppression du chemin en cours
		QuesterGame.instance.getMapScreen().clearPath();
		path = null;
		
		// Suppression des actions en cours
		characterData.ai.clearActions();
		
		// La prochaine action à effectuer sera de réfléchir
		characterData.ai.addAction(ACTION_THINK);		
	}
	
	@Override
	public void act() {
		MapScreen mapScreen = QuesterGame.instance.getMapScreen();
		ActionData action = characterData.ai.getNextAction();
		switch (action.action) {
			// Rien à faire;
			case NONE:
				break;
				
			// Détermination de la prochaine action.
			case THINK:
				characterData.ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (actor.getActions().size == 0) {
					// L'attente est finie, on exécute l'action suivante
					characterData.ai.nextAction();
				}
				break;
				
			// Déplace le joueur vers une position qui peut éventuellement contenir un obstacle.
			// C'est essentiellement pour l'effet visuel.
			case STEP_ON:
				// Déplace le personnage
				actor.setPositionInWorld(action.targetX, action.targetY);
				actor.addAction(Actions.moveTo(
					action.targetX * mapScreen.getCellWidth(),
					action.targetY * mapScreen.getCellHeight(),
					1 / characterData.speed)
				);
				
				// L'action est consommée : réalisation de la prochaine action
				characterData.ai.nextAction();
				
				// On attend la fin avant de s'approcher encore de la cible.
				characterData.ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
				break;
				
			// Un déplacement a été prévu, on se déplace
			case MOVE:
				if (data.element == PLAYER) {
					mapScreen.clearPath();
				}
				// Si on est arrivés à la destination, c'est fini !
				if (actor.getWorldX() == action.targetX && actor.getWorldY() == action.targetY) {
					// L'action est consommée : réalisation de la prochaine action
					characterData.ai.nextAction();
					path = null;
				} else {
					// On n'est toujours pas arrivé à destination : on continue à se déplacer.
					// Calcul du chemin à suivre
					if (path != null && !path.isEmpty()) {
						UnmutablePoint next = path.remove(0);
						int nextX = next.getX();
						int nextY = next.getY();
						
						// On s'assure qu'on se dirige vers une case libre, donc ne contenant pas d'objet.
						// Même si un objet est traversable (ex : porte, chemin...) on veut s'arrêter
						// pour que le joueur ne se retrouve pas sur cet objet mais à côté.
						WorldElementActor onNextPos = mapScreen.getTopElementAt(nextX, nextY, LAYERS_OBSTACLES);
						if (onNextPos == null) {
							// Affichage du chemin retenu
							if (data.element == PLAYER) {
								mapScreen.showPath(path);
							}
							
							// Déplace le personnage
							actor.setPositionInWorld(nextX, nextY);
							actor.addAction(Actions.moveTo(
								nextX * mapScreen.getCellWidth(),
								nextY * mapScreen.getCellHeight(),
								1 / characterData.speed)
							);
							
							// On attend la fin avant de s'approcher encore de la cible.
							characterData.ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
						} else {
							// Pas de chemin possible, on arrête le déplacement en cours...
							characterData.ai.nextAction();
							path = null;
							// ... et on décide de faire autre chose
							characterData.ai.clearActions();
							characterData.ai.addAction(ACTION_THINK);
						}
					} else {
						// Pas de chemin possible.
						// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
						characterData.ai.clearActions();
						characterData.ai.addAction(ACTION_THINK);
					}
				}
				break;
				
			// Une frappe a été prévue, on attaque
			case ATTACK:
				if (action.target != null && (action.target instanceof Damageable)) {
					// Retire des PV à la cible
					((Damageable)action.target).receiveDamage(characterData.attack);
					
					// L'action est consommée : réalisation de la prochaine action
					characterData.ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					characterData.ai.clearActions();
					characterData.ai.addAction(ACTION_THINK);
				}
				break;
		}
		super.act();
	}
	
	@Override
	protected boolean shouldEndTurn() {
		if (super.shouldEndTurn()) {
			if (characterData.ai.getNextAction().action == END_TURN) {
				// Si toutes les autres actions sont finies et qu'on doit
				// finir le tour, on supprime cette action et on fini le tour
				characterData.ai.nextAction();
			} else {
				// Toutes les actions sont finies, on arrête le tour
				// si aucune autre action ne doit être effectuée
				return characterData.ai.getNextAction().action == NONE;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		characterData.ai.addAction(ACTION_THINK);
	}
	
	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	public AI getAI() {
		return characterData.ai;
	}
}
