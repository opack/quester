package com.slamdunk.quester.display.actors;

import static com.slamdunk.quester.ia.Action.ATTACK;
import static com.slamdunk.quester.ia.Action.MOVE;
import static com.slamdunk.quester.ia.Action.NONE;
import static com.slamdunk.quester.ia.Action.OPEN_DOOR;
import static com.slamdunk.quester.ia.Action.THINK;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.ia.IA;
import com.slamdunk.quester.map.points.Point;
import com.slamdunk.quester.map.points.UnmutablePoint;

public class Character extends Obstacle implements Damageable{
	// Nom
	private final String name;
	// Points de vie
	private int hp;
	// Points d'attaque
	private int attackPoints;
	// Distance à laquelle l'arme peut attaquer
	private int weaponRange;
	// Vitesse (en nombre de cases par seconde) à laquelle se déplace le personnage
	private float speed;
	
	/**
	 * Objets intéressés par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * IA du personnage
	 */
	private IA ia;
	
	protected Character(String name, IA ia, TextureRegion texture, GameWorld world, int col, int row) {
		super(texture, col, row, world);
		this.ia = ia;
		ia.init();
		
		listeners = new ArrayList<CharacterListener>();
		this.name = name;
		
		weaponRange = 1;
		setHP(10);
		setSpeed(2);
		attackPoints = 1;
		
		// L'image du personnage est décalée un peu vers le haut
		float size = world.getMap().getCellWidth() * 0.75f;
		getImage().setSize(size, size);
		float offsetX = (map.getCellWidth() - size) / 2; // Au centre
		float offsetY = map.getCellHeight() - size; // En haut
		getImage().setPosition(offsetX, offsetY);
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public String getName() {
		return name;
	}
	
	public int getAttackPoints() {
		return attackPoints;
	}

	public void setAttackPoints(int value) {
		int oldValue = attackPoints;
		attackPoints = value;
		for (CharacterListener listener : listeners) {
			listener.onAttackPointsChanged(oldValue, value);
		}
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
		WorldActor destination = map.getTopElementAt(0, x, y);
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
		ia.setNextAction(MOVE);
		ia.setNextTargetPosition(x, y);
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
		|| !map.isWithinRangeOf(this, target, weaponRange)
		) {
			return false;
		}
		ia.setNextAction(ATTACK);
		ia.setNextTarget(target);
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean openDoor(Door door) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (getActions().size != 0) {
			return false;
		}
		ia.setNextAction(OPEN_DOOR);
		ia.setNextTarget(door);
		return true;
	}
	
	@Override
	public void act(float delta) {
		switch (ia.getNextAction()) {
			// Rien à faire;
			case NONE:
				break;
				
			// Détermination de la prochaine action.
			case THINK:
				ia.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (getActions().size == 0) {
					// L'attente est finie, on exécute l'action suivante
					ia.nextAction();
				}
				break;
				
			// Un déplacement a été prévu, on se déplace
			case MOVE:
				Point destination = ia.getNextTargetPosition();
				WorldActor atDestination = map.getTopElementAt(0, destination.getX(), destination.getY());
				if (destination.getX() != -1 && destination.getY() != -1
				// On vérifie une fois de plus que rien ne s'est placé dans cette case
				// depuis l'appel à moveTo(), car ça a pu arriver
				&& (atDestination == null || !atDestination.isSolid())) {
					// Déplace le personnage
					setPositionInWorld(destination.getX(), destination.getY());
					addAction(Actions.moveTo(
						destination.getX() * map.getCellWidth(),
						destination.getY() * map.getCellHeight(),
						1 / speed)
					);
					
					// L'action est consommée : réalisation de la prochaine action
					ia.nextAction();
				} else {
					// Le cas échéant, on repart en réflexion pour trouver une nouvelle action
					ia.setNextAction(THINK);
					ia.setNextTarget(null);
				}
				break;
				
			// Une frappe a été prévue, on attaque
			case ATTACK:
				WorldActor target = ia.getNextTarget();
				if (target != null && (target instanceof Damageable)) {
					// Retire des PV à la cible
					((Damageable)target).receiveDamage(attackPoints);
					
					// L'action est consommée : réalisation de la prochaine action
					ia.nextAction();
				} else {
					// L'action n'est pas valide : on repart en réflexion
					ia.setNextAction(THINK);
					ia.setNextTarget(null);
				}
				break;
				
			// Une ouverture de porte a été prévue
			case OPEN_DOOR:
				WorldActor door = ia.getNextTarget();
				if (door != null && (door instanceof Door)) {
					// Ouverture de la porte
					((Door)door).openDoor();

					// L'action est consommée : réalisation de la prochaine action
					ia.nextAction();
				} else {
					// L'action n'est pas valide : on repart en réflexion
					ia.setNextAction(THINK);
					ia.setNextTarget(null);
				}
				break;
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		return super.shouldEndTurn() && ia.getNextAction() == NONE;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		ia.setNextAction(THINK);
		ia.setNextTarget(null);
	}
	
	@Override
	public void receiveDamage(int damage) {
		// Retirer la valeur d'armure éventuellement
		setHP (hp - damage);
		if (isDead()) {
			for (CharacterListener listener : listeners) {
				listener.onCharacterDeath(this);
			}
		}
	}

	@Override
	public int getHP() {
		return hp;
	}

	@Override
	public void setHP(int value) {
		int oldValue = hp;
		hp = value;
		for (CharacterListener listener : listeners) {
			listener.onHealthPointsChanged(oldValue, value);
		}
	}

	@Override
	public boolean isDead() {
		return hp <= 0;
	}

	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// Mesures
		int picSize = Assets.heart.getTexture().getWidth();
		
		String att = String.valueOf(getAttackPoints());
		TextBounds textBoundsAtt = Assets.characterFont.getBounds(att);
		float offsetAttX =  getX() + (getWidth() - (picSize + 1 + textBoundsAtt.width)) / 2;
		float offsetAttTextY = getY() + 1 + picSize - (picSize - textBoundsAtt.height) / 2;
		
		String hp = String.valueOf(getHP());
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
		return map.findPath(this, to);
	}

	public IA getIA() {
		return ia;
	}
}
