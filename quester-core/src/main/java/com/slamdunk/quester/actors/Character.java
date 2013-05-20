package com.slamdunk.quester.actors;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
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
	protected static final int ACTION_GOTOROOM = 4;
	
	// Nom
	private final String name;
	// Points de vie
	private int hp;
	// Points d'attaque
	private int attackPoints;
	// Cible de la prochaine attaque
	private WorldElement nextTarget;
	// Destination du prochain d�placement
	private int nextDestinationX;
	private int nextDestinationY;
	// Prochaine action � r�aliser
	private int nextAction;
	// Distance � laquelle l'arme peut attaquer
	private int weaponRange;
	// Vitesse (en nombre de cases par seconde) � laquelle se d�place le personnage
	private float speed;
	
	/**
	 * Objets int�ress�s par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	protected Character(String name, TextureRegion texture, GameWorld world, int col, int row) {
		super(texture, col, row, world);
		listeners = new ArrayList<CharacterListener>();
		this.name = name;
		
		weaponRange = 1;
		setHP(10);
		setSpeed(2);
		attackPoints = 1;
		
		nextAction = ACTION_THINK;
		nextTarget = null;
		nextDestinationX = -1;
		nextDestinationY = -1;
		
		// L'image du personnage est d�cal�e un peu vers le haut
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
	 * Enregistrement d'une action demandant au personnage de se d�placer
	 * vers cette destination. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean moveTo(int x, int y) {
		// Ignorer le d�placement dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size != 0
		// Si la destination est solide (non "traversable")
		|| map.getTopElementAt(0, x, y) != null
		// Si la distance � parcourir est diff�rente de 1 (c'est trop loin ou trop pr�s)
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
	 * cette cible. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean attack(WorldElement target) {
		// Ignorer le d�placement dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size != 0
		// Si la cible est trop loin pour l'arme actuelle
		|| !map.isWithinRangeOf(this, target, weaponRange)
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
				// D�termination de la prochaine action.
				case ACTION_THINK:
					think();
					break;
					
				// Un d�placement a �t� pr�vu, on se d�place
				case ACTION_MOVE:
					if (nextDestinationX != -1 && nextDestinationY != -1
					// On v�rifie une fois de plus que rien ne s'est plac� dans cette case
					// depuis l'appel � moveTo(), car �a a pu arriver
					&& map.getTopElementAt(0, nextDestinationX, nextDestinationY) == null) {
						// D�place le personnage
						setPositionInWorld(nextDestinationX, nextDestinationY);
						addAction(Actions.moveTo(
							nextDestinationX * map.getCellWidth(),
							nextDestinationY * map.getCellHeight(),
							1 / speed)
						);
						// L'actin est consomm�e : r�initialisation de la prochaine action
						nextAction = ACTION_NONE;
					} else {
						// Le cas �ch�ant, on repart en r�flexion pour trouver une nouvelle action
						nextAction = ACTION_THINK;
					}
					nextDestinationX = -1;
					nextDestinationY = -1;
					break;
					
				// Une frappe a �t� pr�vue, on attaque
				case ACTION_ATTACK:
					if (nextTarget != null && (nextTarget instanceof Damageable)) {
						// Retire des PV � la cible
						((Damageable)nextTarget).receiveDamage(attackPoints);
						// L'actin est consomm�e : r�initialisation de la prochaine action
						nextAction = ACTION_NONE;
					} else {
						// L'action n'est pas valide : on repart en r�flexion
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
		// Retirer la valeur d'armure �ventuellement
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

	/**
	 * M�thode charg�e de d�cider ce que fera l'�l�ment lorsque ce
	 * sera � son tour de jouer. Par d�faut, il ne fait rien et
	 * termine son tour.
	 */
	public void think() {
		nextAction = ACTION_NONE;
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
		MenuNinePatch nine = MenuNinePatch.getInstance();
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
}
