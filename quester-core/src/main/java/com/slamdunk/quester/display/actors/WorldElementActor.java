package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.map.MapElements;

/**
 * Contient l'ensemble des comportements communs à tous les
 * éléments du monde, sans aucune logique de jeu.
 * @author Didier
 *
 */
public class WorldElementActor extends Group{
	/**
	 * Contrôleur (cerveau) de l'acteur
	 */
	protected WorldElementControler controler;
	
	/**
	 * Indique ce que fait l'acteur, pour choisir l'animation à dessiner
	 */
	protected QuesterActions currentAction;
	/**
	 * Objet qui sert d'intermédiaire avec la map
	 */
	private Image image;
	
	/**
	 * Indique que l'acteur est déplacement vers la gauche
	 */
	protected boolean isLookingLeft;
	
	/**
	 * Le monde dans lequel évolue l'Actor
	 */
	private ActorMap map;
	
	/**
	 * Compteur utilisé pour cadencer les animations
	 */
	protected float stateTime;
	
	/**
	 * Position logique de l'élément dans le monde
	 */
	private int worldX;
	
	private int worldY;
	
	public WorldElementActor(TextureRegion texture) {
		map = GameControler.instance.getScreen().getMap();
		
		if (texture != null) {
			image = new Image(texture);
			image.setScaling(Scaling.stretch);
			image.setWidth(map.getCellWidth());
			image.setHeight(map.getCellHeight());
			addActor(image);
		}
		
		currentAction = QuesterActions.NONE;
	}
	
	public void centerCameraOnSelf() {
		addAction(new CameraMoveToAction(
				getX() + map.getCellWidth() / 2,
				getY() + map.getCellHeight() / 2,
				1.0f));
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		drawSpecifics(batch);
	}

	/**
	 * Dessine le clip adéquat en fonction de l'action courante
	 * @param batch
	 */
	protected void drawClip(SpriteBatch batch) {
		Clip clip = getClip(currentAction);
		if (clip == null) {
			return;
		}
		stateTime += Gdx.graphics.getDeltaTime();
		clip.drawArea.x = getX();
		clip.drawArea.y = getY();
		clip.flipH = isLookingLeft;
		clip.play(stateTime, batch);
	}
	
	/**
	 * Appelée pendant le draw pour dessiner les particularités
	 * de ce WorldElement.
	 * @param batch
	 */
	protected void drawSpecifics(SpriteBatch batch) {
	}

	/**
	 * Retourne le clip à jouer lors de l'action spécifiée
	 * Cette fonction doit être redéfinie.
	 * @return
	 */
	public Clip getClip(QuesterActions action) {
		return null;
	}

	public WorldElementControler getControler() {
		return controler;
	}

	public QuesterActions getCurrentAction() {
		return currentAction;
	}

	public Image getImage() {
		return image;
	}

	/**
	 * Retourne le X exprimé en unité de la map et pas en pixels
	 * @return
	 */
	public int getWorldX() {
		return worldX;
	}

	/**
	 * Retourne le Y exprimé en unité de la map et pas en pixels
	 * @return
	 */
	public int getWorldY() {
		return worldY;
	}
	
	public void moveTo(int destinationX, int destinationY, float duration) {
		currentAction = QuesterActions.MOVE;
		isLookingLeft = destinationX <= worldX;
			
		setPositionInWorld(destinationX, destinationY);
		if (controler.getData().element == MapElements.PLAYER) {
				addAction(Actions.sequence(
						// On déplace le joueur et la caméra
						Actions.parallel(
							Actions.moveTo(
								destinationX * map.getCellWidth(),
								destinationY * map.getCellHeight(),
								duration),
							new CameraMoveToAction(
								destinationX * map.getCellWidth() + map.getCellWidth() / 2,
								destinationY * map.getCellHeight() + map.getCellHeight() / 2,
								duration)),
						new Action() {
							@Override
							public boolean act(float delta) {
								WorldElementActor.this.currentAction = QuesterActions.NONE;
								return true;
							}
						}
					)
				);
//			}
		} else {
			addAction(Actions.sequence(
					Actions.moveTo(
						destinationX * map.getCellWidth(),
						destinationY * map.getCellHeight(),
						duration),
					new Action() {
						@Override
						public boolean act(float delta) {
							WorldElementActor.this.currentAction = QuesterActions.NONE;
							return true;
						}
					}
				)
			);
		}
	}
	
	public void setControler(WorldElementControler controler) {
		this.controler = controler;
	}
	
	public void setCurrentAction(QuesterActions action, int targetX) {
		// Si l'action change, on RAZ le compteur pour les animations
		if (action != currentAction) {
			stateTime = 0f;
		}
		this.currentAction = action;
		isLookingLeft = targetX <= worldX;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Place l'acteur dans la case spécifiée par la colonne
	 * et la ligne indiquées. Cette méthode se charge simplement
	 * de convertir une unité logiques (col/row) en unité réelle
	 * (x/y en pixels) et de mettre à jour le monde.
	 * @param worldX
	 * @param worldY
	 */
	public void setPositionInWorld(int newX, int newY) {
		map.updateMapPosition(
			this,
			worldX, worldY,
			newX, newY);
		setWorldX(newX);
		setWorldY(newY);
	}

	private void setWorldX(int worldX) {
		this.worldX = worldX;
	}

	private void setWorldY(int worldY) {
		this.worldY = worldY;
	}
}
