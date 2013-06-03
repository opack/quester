package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.model.ai.Action.ATTACK;
import static com.slamdunk.quester.model.ai.Action.MOVE;
import static com.slamdunk.quester.model.ai.Action.NONE;
import static com.slamdunk.quester.model.ai.Action.THINK;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.ai.AI;
import com.slamdunk.quester.model.map.CharacterData;
import com.slamdunk.quester.model.map.ElementData;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class Character extends Obstacle implements Damageable{
	protected CharacterData data;
	
	/**
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
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
	
	@Override
	public boolean isSolid() {
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage de se déplacer
	 * vers cette destination. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean moveTo(int x, int y) {
		WorldActor destination = QuesterGame.instance.getMapScreen().getTopElementAt(0, x, y);
		double distance = distanceTo(x, y);
		// Ignorer le déplacement dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (getActions().size != 0
		// Si la destination est solide (non "traversable")
		|| (destination != null && destination.isSolid())
		// Si la distance à parcourir est différente de 1 (c'est trop loin ou trop près)
		|| distance != 1) {
			return false;
		}
		data.ai.setNextAction(MOVE);
		data.ai.setNextTargetPosition(x, y);
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
		data.ai.setNextAction(ATTACK);
		data.ai.setNextTarget(target);
		return true;
	}
	
	@Override
	public void act(float delta) {
		MapScreen mapScreen = QuesterGame.instance.getMapScreen();
		switch (data.ai.getNextAction()) {
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
				Point destination = data.ai.getNextTargetPosition();
				WorldActor atDestination = mapScreen.getTopElementAt(0, destination.getX(), destination.getY());
				if (destination.getX() != -1 && destination.getY() != -1
				// On vérifie une fois de plus que rien ne s'est placé dans cette case
				// depuis l'appel à moveTo(), car ça a pu arriver
				&& (atDestination == null || !atDestination.isSolid())) {
					// Déplace le personnage
					setPositionInWorld(destination.getX(), destination.getY());
					addAction(Actions.moveTo(
						destination.getX() * mapScreen.getCellWidth(),
						destination.getY() * mapScreen.getCellHeight(),
						1 / data.speed)
					);
					
					// L'action est consommée : réalisation de la prochaine action
					data.ai.nextAction();
				} else {
					// Le cas échéant, on repart en réflexion pour trouver une nouvelle action
					data.ai.setNextAction(THINK);
					data.ai.setNextTarget(null);
				}
				break;
				
			// Une frappe a été prévue, on attaque
			case ATTACK:
				WorldActor target = data.ai.getNextTarget();
				if (target != null && (target instanceof Damageable)) {
					// Retire des PV à la cible
					((Damageable)target).receiveDamage(data.attack);
					
					// L'action est consommée : réalisation de la prochaine action
					data.ai.nextAction();
				} else {
					// L'action n'est pas valide : on repart en réflexion
					data.ai.setNextAction(THINK);
					data.ai.setNextTarget(null);
				}
				break;
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		return super.shouldEndTurn() && data.ai.getNextAction() == NONE;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		data.ai.setNextAction(THINK);
		data.ai.setNextTarget(null);
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
