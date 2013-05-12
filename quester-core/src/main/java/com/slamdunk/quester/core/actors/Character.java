package com.slamdunk.quester.core.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;

public class Character extends Obstacle implements Damageable{
	protected static final int ACTION_NONE = 0;
	protected static final int ACTION_THINK = 1;
	protected static final int ACTION_MOVE = 2;
	protected static final int ACTION_ATTACK = 3;
	
	// Nom
	private final String name;
	// Points de vie
	private int hp;
	// Points d'attaque
	private int attackPoints;
	// Cible de la prochaine attaque
	private WorldElement nextTarget;
	// Destination du prochain déplacement
	private int nextDestinationX;
	private int nextDestinationY;
	// Prochaine action à réaliser
	private int nextAction;
	// Distance à laquelle l'arme peut attaquer
	private int weaponRange;
	// Vitesse (en nombre de cases par seconde) à laquelle se déplace le personnage
	private float speed;
	
	protected Character(String name, TextureRegion texture, GameWorld gameWorld, int col, int row) {
		super(texture, col, row, gameWorld);
		this.name = name;
		
		weaponRange = 1;
		setHP(10);
		setSpeed(2);
		attackPoints = 1;
		
		nextAction = ACTION_THINK;
		nextTarget = null;
		nextDestinationX = -1;
		nextDestinationY = -1;
		
		// L'image du personnage est décalée un peu vers le haut
		float size = gameWorld.getWorldCellSize() * 0.75f;
		getImage().setSize(size, size);
		float offset = gameWorld.getWorldCellSize() - size;
		getImage().setPosition(offset / 2, offset);
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

	public void setAttackPoints(int attackPoints) {
		this.attackPoints = attackPoints;
	}

	@Override
	public boolean isSolid() {
		return true;
	}
	
	public void setNextTarget(WorldElement nextTarget) {
		this.nextTarget = nextTarget;
	}

	public void setNextAction(int nextAction) {
		this.nextAction = nextAction;
	}
	
	public WorldElement getNextTarget() {
		return nextTarget;
	}

	public int getNextAction() {
		return nextAction;
	}

	/**
	 * Enregistrement d'une action demandant au personnage de se déplacer
	 * vers cette destination. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean moveTo(int x, int y) {
		// Ignorer le déplacement dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (getActions().size != 0
		// Si la destination est solide (non "traversable")
		|| world.getObstacleAt(x, y) != null
		// Si la distance à parcourir est différente de 1 (c'est trop loin ou trop près)
		|| distanceTo(x, y) != 1
		) {
			return false;
		}
		nextAction = ACTION_MOVE;
		nextDestinationX = x;
		nextDestinationY = y;
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'attaquer
	 * cette cible. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean attack(WorldElement target) {
		// Ignorer le déplacement dans les conditions suivantes :
		// Si le personnage fait déjà quelque chose
		if (getActions().size != 0
		// Si la cible est trop loin pour l'arme actuelle
		|| !world.isReachable(this, target, weaponRange)
		) {
			return false;
		}
		nextAction = ACTION_ATTACK;
		nextTarget = target;
		return true;
	}
	
	@Override
	public void act(float delta) {
		if (nextAction != ACTION_NONE) {
			switch (nextAction) {
				// Détermination de la prochaine action.
				case ACTION_THINK:
					think();
					break;
					
				// Un déplacement a été prévu, on se déplace
				case ACTION_MOVE:
					if (nextDestinationX != -1 && nextDestinationY != -1
					// On vérifie une fois de plus que rien ne s'est placé dans cette case
					// depuis l'appel à moveTo(), car ça a pu arriver
					&& world.getObstacleAt(nextDestinationX, nextDestinationY) == null) {
						// Déplace le personnage
						setPositionInWorld(nextDestinationX, nextDestinationY);
						final float cellSize = world.getWorldCellSize();
						addAction(Actions.moveTo(
							nextDestinationX * cellSize, nextDestinationY * cellSize, 1 / speed)
						);
						// L'actin est consommée : réinitialisation de la prochaine action
						nextAction = ACTION_NONE;
					} else {
						// Le cas échéant, on repart en réflexion pour trouver une nouvelle action
						nextAction = ACTION_THINK;
					}
					nextDestinationX = -1;
					nextDestinationY = -1;
					break;
					
				// Une frappe a été prévue, on attaque
				case ACTION_ATTACK:
					if (nextTarget != null && (nextTarget instanceof Damageable)) {
						// Retire des PV à la cible
						((Damageable)nextTarget).receiveDamage(attackPoints);
						// L'actin est consommée : réinitialisation de la prochaine action
						nextAction = ACTION_NONE;
					} else {
						// L'action n'est pas valide : on repart en réflexion
						nextAction = ACTION_THINK;
					}
					nextTarget = null;
					break;
			}
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		return super.shouldEndTurn() && nextAction == ACTION_NONE;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		nextAction = ACTION_THINK;
	}
	
	@Override
	public void receiveDamage(int damage) {
		// Retirer la valeur d'armure éventuellement
		hp -= damage;
		if (isDead()) {
			onDeath();
		}
	}

	@Override
	public int getHP() {
		return hp;
	}

	@Override
	public void setHP(int value) {
		hp = value;
	}

	@Override
	public boolean isDead() {
		return hp <= 0;
	}

	@Override
	public void onDeath() {
		world.removeElement(this);
	}
	
	/**
	 * Méthode chargée de décider ce que fera l'élément lorsque ce
	 * sera à son tour de jouer. Par défaut, il ne fait rien et
	 * termine son tour.
	 */
	public void think() {
		nextAction = ACTION_NONE;
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// Affiche le nom du personnage
//		String name = getName();
//		Assets.characterFont.draw(
//			batch,
//			name,
//			getX(), getY() + Assets.characterFont.getBounds(name).height);
		
		// Affiche le nombre de PV
		batch.draw(Assets.heart, getX(), getY());
		String hp = String.valueOf(getHP());
		Assets.characterFont.draw(
			batch,
			hp,
			getX() + Assets.heart.getTexture().getWidth(), getY() + Assets.characterFont.getBounds(hp).height);
		
		// Affiche le nombre de points d'attaque
		batch.draw(Assets.sword, getX() + getWidth() / 2, getY());
		String att = String.valueOf(getAttackPoints());
		Assets.characterFont.draw(
			batch,
			att,
			getX() + getWidth() / 2 + Assets.heart.getTexture().getWidth(), getY() + Assets.characterFont.getBounds(att).height);
	}
}
