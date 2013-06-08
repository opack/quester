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

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.actors.CharacterActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.AI;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.CharacterAI;
import com.slamdunk.quester.model.data.CharacterData;
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
	 * Son � jouer lorsque le personnage marche
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
		// TODO Retirer la valeur d'armure �ventuellement
		characterData.health -= damage;
		
		// Si un d�placement �tait en cours, il est interrompu
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
	 * Enregistrement d'une action demandant au personnage de se d�placer
	 * vers cette destination. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean moveTo(int x, int y) {
		if (actor.getActions().size != 0) {
			return false;
		}
		// D�termine le chemin � suivre et le stocke
		path = GameControler.instance.getMapScreen().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		if (path == null || path.isEmpty()) {
			return false;
		}
		// Au prochain act, on va commencer � suivre ce chemin
		ai.addAction(MOVE, x, y);
		return true;
	}
	
	public boolean moveNear(int x, int y) {
		if (actor.getActions().size != 0) {
			return false;
		}
		// D�termine le chemin � suivre et le stocke
		path = GameControler.instance.getMapScreen().findPath(
				actor.getWorldX(), actor.getWorldY(), 
				x, y);
		if (path == null || path.isEmpty()) {
			return false;
		}
		
		// On veut s'arr�ter avant la destination
		path.remove(path.size() - 1);
		
		// Au prochain act, on va commencer � suivre ce chemin
		// jusqu'� la destination
		if (!path.isEmpty()) {
			// Si on n'est pas d�j� � c�t� de la destination,
			// on demande un d�placement l�-bas
			UnmutablePoint destination = path.get(path.size() - 1);
			ai.addAction(MOVE, destination.getX(), destination.getY());
		}
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'attaquer
	 * cette cible. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean attack(WorldElementControler target) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (actor.getActions().size != 0
		// Si la cible n'est pas Damageable
		|| !(target instanceof Damageable)
		// Si la cible est morte
		|| ((Damageable)target).isDead()
		// Si la cible est trop loin pour l'arme actuelle, on s'approche
		|| !GameControler.instance.getMapScreen().isWithinRangeOf(actor, target.actor, characterData.weaponRange)) {
			return false;
		}
		
		ai.addAction(ATTACK, target);
		return true;
	}
	
	/**
	 * Arr�te le d�placement en cours
	 */
	public void stopMove() {
		path = null;
		if (data.element == PLAYER) {
			GameControler.instance.getMapScreen().clearPath();
		}
	}
	
	@Override
	public void act(float delta) {
		MapScreen mapScreen = GameControler.instance.getMapScreen();
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Une frappe a �t� pr�vue, on attaque
			case ATTACK:
				if (action.target != null && (action.target instanceof Damageable)) {
					// Fait un bruit d'�p�e
					Sound swordSound = Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
					swordSound.play();
					
					// Retire des PV � la cible
					((Damageable)action.target).receiveDamage(characterData.attack);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					ai.clearActions();
					ai.addAction(ACTION_THINK);
				}
				break;
				
			// Un d�placement a �t� pr�vu, on se d�place
			case MOVE:
				if (data.element == PLAYER) {
					mapScreen.clearPath();
				}
				// Si on est arriv�s � la destination, c'est fini !
				if (actor.getWorldX() == action.targetX && actor.getWorldY() == action.targetY) {
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
					path = null;
				} else {
					// On n'est toujours pas arriv� � destination : on continue � se d�placer.
					// Calcul du chemin � suivre
					if (path != null && !path.isEmpty()) {
						UnmutablePoint next = path.get(0);
						int nextX = next.getX();
						int nextY = next.getY();
						
						// On s'assure qu'on se dirige vers une case libre, donc ne contenant pas d'objet.
						// M�me si un objet est traversable (ex : porte, chemin...) on veut s'arr�ter
						// pour que le joueur ne se retrouve pas sur cet objet mais � c�t�.
						WorldElementActor onNextPos = mapScreen.getTopElementAt(nextX, nextY, LAYERS_OBSTACLES);
						if (onNextPos == null) {
							// Fait un bruit de pas pour le joueur seulement
							if (stepsSound != null) {
								stepsSound.play();
							}
							
							// D�place le personnage
							actor.moveTo(nextX, nextY, 1 / characterData.speed);
							
							// Affichage du chemin retenu
							if (data.element == PLAYER) {
								mapScreen.showPath(path);
							}
							
							// Suppression de cette position du chemin
							path.remove(0);
							
							// On attend la fin avant de s'approcher encore de la cible.
							ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
						} else {
							// Pas de chemin possible, on arr�te le d�placement en cours...
							ai.nextAction();
							path = null;
							// ... et on d�cide de faire autre chose
							ai.clearActions();
							ai.addAction(ACTION_THINK);
						}
					} else {
						// Pas de chemin possible.
						// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
						ai.clearActions();
						ai.addAction(ACTION_THINK);
					}
				}
				break;
					
			// Rien � faire;
			case NONE:
				break;
				
			// D�place le joueur vers une position qui peut �ventuellement contenir un obstacle.
			// C'est essentiellement pour l'effet visuel.
			case STEP_ON:
				// D�place le personnage
				actor.moveTo(action.targetX, action.targetY, 1 / characterData.speed);
				
				// L'action est consomm�e : r�alisation de la prochaine action
				ai.nextAction();
				
				// On attend la fin avant de finir le tour.
				ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
				break;
			
			// D�termination de la prochaine action.
			case THINK:
				ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (actor.getActions().size == 0) {
					// L'attente est finie, on ex�cute l'action suivante
					ai.nextAction();
				}
				break;
				
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		if (super.shouldEndTurn()) {
			if (ai.getNextAction().action == END_TURN) {
				// Si toutes les autres actions sont finies et qu'on doit
				// finir le tour, on supprime cette action et on fini le tour
				ai.nextAction();
			} else {
				// Toutes les actions sont finies, on arr�te le tour
				// si aucune autre action ne doit �tre effectu�e
				return ai.getNextAction().action == NONE;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		ai.addAction(ACTION_THINK);
	}
	
	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}
	
	public AI getAI() {
		return ai;
	}
}
