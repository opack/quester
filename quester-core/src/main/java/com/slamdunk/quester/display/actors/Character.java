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
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;

public class Character extends Obstacle implements Damageable{
	// Nom
	private final String name;
	// Points de vie
	private int hp;
	// Points d'attaque
	private int attackPoints;
	// Distance � laquelle l'arme peut attaquer
	private int weaponRange;
	// Vitesse (en nombre de cases par seconde) � laquelle se d�place le personnage
	private float speed;
	
	/**
	 * Objets int�ress�s par ce qui arrive au Character
	 */
	private List<CharacterListener> listeners;
	
	/**
	 * IA du personnage
	 */
	private AI ai;
	
	protected Character(String name, AI ai, TextureRegion texture, int col, int row) {
		super(texture, col, row);
		this.ai = ai;
		ai.init();
		
		listeners = new ArrayList<CharacterListener>();
		this.name = name;
		
		weaponRange = 1;
		setHP(10);
		setSpeed(2);
		attackPoints = 1;
		
		// L'image du personnage est d�cal�e un peu vers le haut
		GameScreen screen = QuesterGame.instance.getMapScreen();
		float size = QuesterGame.instance.getMapScreen().getCellWidth() * 0.75f;
		getImage().setSize(size, size);
		float offsetX = (screen.getCellWidth() - size) / 2; // Au centre
		float offsetY = screen.getCellHeight() - size; // En haut
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
	 * Enregistrement d'une action demandant au personnage de se d�placer
	 * vers cette destination. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean moveTo(int x, int y) {
		WorldActor destination = QuesterGame.instance.getMapScreen().getTopElementAt(0, x, y);
		double distance = distanceTo(x, y);
		// Ignorer le d�placement dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size != 0
		// Si la destination est solide (non "traversable")
		|| (destination != null && destination.isSolid())
		// Si la distance � parcourir est diff�rente de 1 (c'est trop loin ou trop pr�s)
		|| distance != 1) {
			return false;
		}
		ai.setNextAction(MOVE);
		ai.setNextTargetPosition(x, y);
		return true;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'attaquer
	 * cette cible. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean attack(WorldActor target) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le personnage fait d�j� quelque chose
		if (getActions().size != 0
		// Si la cible n'est pas Damageable
		|| !(target instanceof Damageable)
		// Si la cible est morte
		|| ((Damageable)target).isDead()
		// Si la cible est trop loin pour l'arme actuelle
		|| !QuesterGame.instance.getMapScreen().isWithinRangeOf(this, target, weaponRange)
		) {
			return false;
		}
		ai.setNextAction(ATTACK);
		ai.setNextTarget(target);
		return true;
	}
	
	@Override
	public void act(float delta) {
		MapScreen mapScreen = QuesterGame.instance.getMapScreen();
		switch (ai.getNextAction()) {
			// Rien � faire;
			case NONE:
				break;
				
			// D�termination de la prochaine action.
			case THINK:
				ai.think();
				break;
				
			// Attente de la fin d'une Action en cours
			case WAIT_COMPLETION:
				if (getActions().size == 0) {
					// L'attente est finie, on ex�cute l'action suivante
					ai.nextAction();
				}
				break;
				
			// Un d�placement a �t� pr�vu, on se d�place
			case MOVE:
				Point destination = ai.getNextTargetPosition();
				WorldActor atDestination = mapScreen.getTopElementAt(0, destination.getX(), destination.getY());
				if (destination.getX() != -1 && destination.getY() != -1
				// On v�rifie une fois de plus que rien ne s'est plac� dans cette case
				// depuis l'appel � moveTo(), car �a a pu arriver
				&& (atDestination == null || !atDestination.isSolid())) {
					// D�place le personnage
					setPositionInWorld(destination.getX(), destination.getY());
					addAction(Actions.moveTo(
						destination.getX() * mapScreen.getCellWidth(),
						destination.getY() * mapScreen.getCellHeight(),
						1 / speed)
					);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Le cas �ch�ant, on repart en r�flexion pour trouver une nouvelle action
					ai.setNextAction(THINK);
					ai.setNextTarget(null);
				}
				break;
				
			// Une frappe a �t� pr�vue, on attaque
			case ATTACK:
				WorldActor target = ai.getNextTarget();
				if (target != null && (target instanceof Damageable)) {
					// Retire des PV � la cible
					((Damageable)target).receiveDamage(attackPoints);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// L'action n'est pas valide : on repart en r�flexion
					ai.setNextAction(THINK);
					ai.setNextTarget(null);
				}
				break;
		}
		super.act(delta);
	}
	
	@Override
	protected boolean shouldEndTurn() {
		return super.shouldEndTurn() && ai.getNextAction() == NONE;
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		ai.setNextAction(THINK);
		ai.setNextTarget(null);
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
		return QuesterGame.instance.getMapScreen().findPath(this, to);
	}

	public AI getIA() {
		return ai;
	}
}
