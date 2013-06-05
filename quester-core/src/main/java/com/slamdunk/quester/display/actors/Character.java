package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.model.ai.AI.ACTION_END_TURN;
import static com.slamdunk.quester.model.ai.AI.ACTION_WAIT_COMPLETION;
import static com.slamdunk.quester.model.ai.Actions.ATTACK;
import static com.slamdunk.quester.model.ai.Actions.END_TURN;
import static com.slamdunk.quester.model.ai.Actions.MOVE;
import static com.slamdunk.quester.model.ai.Actions.NONE;
import static com.slamdunk.quester.model.ai.Actions.THINK;
import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.screens.AbstractMapScreen;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.ai.AI;
import com.slamdunk.quester.model.ai.ActionData;
import com.slamdunk.quester.model.map.CharacterData;
import com.slamdunk.quester.model.map.ElementData;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class Character extends WorldActor implements Damageable{
	protected CharacterData data;
	
	/**
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * Chemin que va suivre le personnage
	 */
	private List<UnmutablePoint> path;
	
	protected Character(CharacterData data, TextureRegion texture, int col, int row) {
		super(data, texture, col, row);
		listeners = new ArrayList<CharacterListener>();
		
		data.ai.setBody(this);
		data.ai.init();
		
		// L'image du personnage est décalée un peu vers le haut
		GameScreen screen = QuesterGame.instance.getMapScreen();
		float size = QuesterGame.instance.getMapScreen().getCellWidth() * 0.75f;
		getImage().setSize(size, size);
		float offsetX = (screen.getCellWidth() - size) / 2; // Au centre
		float offsetY = screen.getCellHeight() - size; // En haut
		getImage().setPosition(offsetX, offsetY);
	}
	
	@Override
	public void setElementData(ElementData data) {
		super.setElementData(data);
		this.data = (CharacterData)data;
	}
	
	@Override
	public CharacterData getElementData() {
		return data;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage de se déplacer
	 * vers cette destination. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean moveTo(int x, int y) {
//		WorldActor destination = QuesterGame.instance.getMapScreen().getTopElementAt(0, x, y);
//		double distance = distanceTo(x, y);
//		// Ignorer le déplacement dans les conditions suivantes :
//		// Si le personnage fait déjà quelque chose
//		if (getActions().size != 0
//		// Si la destination est solide (non "traversable")
//		|| (destination != null && destination.isSolid())
//		// Si la distance à parcourir est différente de 1 (c'est trop loin ou trop près)
//		|| distance != 1) {
//			return false;
//		}
//		data.ai.addAction(MOVE, x, y);
		if (getActions().size != 0) {
			return false;
		}
		// Détermine le chemin à suivre et le stocke
		path = QuesterGame.instance.getMapScreen().findPath(
				getWorldX(), getWorldY(), 
				x, y);
		// Au prochain act, on va commencer à suivre ce chemin
		data.ai.addAction(MOVE, x, y);
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'attaquer
	 * cette cible. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean attack(WorldActor target) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (getActions().size != 0
		// Si la cible n'est pas Damageable
		|| !(target instanceof Damageable)
		// Si la cible est morte
		|| ((Damageable)target).isDead()
		// Si la cible est trop loin pour l'arme actuelle
		|| !QuesterGame.instance.getMapScreen().isWithinRangeOf(this, target, data.weaponRange)
		) {
			return false;
		}
		data.ai.addAction(ATTACK, target);
		return true;
	}
	
	@Override
	public void act(float delta) {
		MapScreen mapScreen = QuesterGame.instance.getMapScreen();
		ActionData action = data.ai.getNextAction();
		switch (action.action) {
			// Rien à faire;
			case NONE:
				break;
				
			// Détermination de la prochaine action.
			case THINK:
				data.ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (getActions().size == 0) {
					// L'attente est finie, on exécute l'action suivante
					data.ai.nextAction();
				}
				break;
				
			// Un déplacement a été prévu, on se déplace
			case MOVE:
				if (data.element == PLAYER) {
					mapScreen.clearPath();
				}
				// Si on est arrivés à la destination, c'est fini !
				if (getWorldX() == action.targetX && getWorldY() == action.targetY) {
					// L'action est consommée : réalisation de la prochaine action
					data.ai.nextAction();
					path = null;
				} else {
					// On n'est toujours pas arrivé à destination : on continue à se déplacer.
					// Calcul du chemin à suivre
					if (path != null && !path.isEmpty()) {
						UnmutablePoint next = path.remove(0);
						int nextX = next.getX();
						int nextY = next.getY();
						
						// On s'assure qu'on se dirige vers une case libre
						WorldActor onNextPos = mapScreen.getTopElementBetween(0, AbstractMapScreen.LEVEL_FOG, nextX, nextY);
						if (onNextPos == null || !onNextPos.isSolid()) {
							// Affichage du chemin retenu
							if (data.element == PLAYER) {
								mapScreen.showPath(path);
							}
							
							// Déplace le personnage
							setPositionInWorld(nextX, nextY);
							addAction(Actions.moveTo(
								nextX * mapScreen.getCellWidth(),
								nextY * mapScreen.getCellHeight(),
								1 / data.speed)
							);
							
							// On attend la fin avant de s'approcher encore de la cible.
							data.ai.setNextActions(ACTION_WAIT_COMPLETION, ACTION_END_TURN);
						} else {
							// Pas de chemin possible, on arrête le déplacement en cours...
							data.ai.nextAction();
							path = null;
							// ... et on décide de faire autre chose
							data.ai.clearActions();
							data.ai.addAction(THINK, null);
						}
					} else {
						// Pas de chemin possible.
						// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
						data.ai.clearActions();
						data.ai.addAction(THINK, null);
					}
				}
				break;
				
			// Une frappe a été prévue, on attaque
			case ATTACK:
				if (action.target != null && (action.target instanceof Damageable)) {
					// Retire des PV à la cible
					((Damageable)action.target).receiveDamage(data.attack);
					
					// L'action est consommée : réalisation de la prochaine action
					data.ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui était prévu et on réfléchit de nouveau.
					data.ai.clearActions();
					data.ai.addAction(THINK, null);
				}
				break;
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		if (super.shouldEndTurn()) {
			if (data.ai.getNextAction().action == END_TURN) {
				// Si toutes les autres actions sont finies et qu'on doit
				// finir le tour, on supprime cette action et on fini le tour
				data.ai.nextAction();
			} else {
				// Toutes les actions sont finies, on arrête le tour
				// si aucune autre action ne doit être effectuée
				return data.ai.getNextAction().action == NONE;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		data.ai.addAction(THINK, null);
	}
	
	@Override
	public int getHealth() {
		return data.health;
	}
	
	@Override
	public void setHealth(int value) {
		int oldValue = data.health;
		data.health = value;
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
		data.health -= damage;
		if (isDead()) {
			for (CharacterListener listener : listeners) {
				listener.onCharacterDeath(this);
			}
		}
	}

	@Override
	public boolean isDead() {
		return data.health <= 0;
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// Mesures
		int picSize = Assets.heart.getTexture().getWidth();
		
		String att = String.valueOf(data.attack);
		TextBounds textBoundsAtt = Assets.characterFont.getBounds(att);
		float offsetAttX =  getX() + (getWidth() - (picSize + 1 + textBoundsAtt.width)) / 2;
		float offsetAttTextY = getY() + 1 + picSize - (picSize - textBoundsAtt.height) / 2;
		
		String hp = String.valueOf(data.health);
		TextBounds textBoundsHp = Assets.characterFont.getBounds(hp);
		float offsetHpX = getX() + (getWidth() - (picSize + 1 + textBoundsHp.width)) / 2;
		float offsetHpTextY = offsetAttTextY + 1 + picSize;
		
		float backgroundWidth = Math.max(picSize + 1 + textBoundsAtt.width, picSize + 1 + textBoundsHp.width) + 4;
		
	// Dessin
		// Dessin du rectangle de fond
		CharacterStatsNinePatch nine = CharacterStatsNinePatch.getInstance();
		nine.draw(batch, getX() + (getWidth() - backgroundWidth) / 2, getY(), backgroundWidth, 2 * picSize + 2);
		
		// Affiche le nombre de PV
		batch.draw(
			Assets.heart,
			offsetHpX,
			getY() + picSize,
			picSize, picSize);
		Assets.characterFont.draw(
			batch,
			hp,
			offsetHpX + picSize + 1,
			offsetHpTextY);
		
		// Affiche le nombre de points d'attaque
		picSize = Assets.sword.getTexture().getWidth();
		batch.draw(
			Assets.sword,
			offsetAttX,
			getY() + 1,
			picSize, picSize);
		Assets.characterFont.draw(
			batch,
			att,
			offsetAttX + picSize + 1,
			offsetAttTextY);
	}

	public void addListener(CharacterListener listener) {
		listeners.add(listener);
	}

	public List<UnmutablePoint> findPathTo(WorldActor to) {
		return QuesterGame.instance.getMapScreen().findPath(this, to);
	}

	public AI getIA() {
		return data.ai;
	}
}
