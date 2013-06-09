package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

/**
 * Contient l'ensemble des comportements communs � tous les
 * �l�ments du monde, sans aucune logique de jeu.
 * @author Didier
 *
 */
public class WorldElementActor extends Group{
	/**
	 * Le monde dans lequel �volue l'Actor
	 */
	private MapScreen mapScreen;
	
	/**
	 * Position logique de l'�l�ment dans le monde
	 */
	private int worldX;
	private int worldY;
	
	/**
	 * Objet qui sert d'interm�diaire avec la map
	 */
	private Image image;
	
	/**
	 * Indique ce que fait l'acteur, pour choisir l'animation � dessiner
	 */
	protected QuesterActions currentAction;
	
	/**
	 * Indique que l'acteur est d�placement vers la gauche
	 */
	protected boolean isLookingLeft;
	
	protected WorldElementControler controler;
	
	/**
	 * Compteur utilis� pour cadencer les animations
	 */
	protected float stateTime;
	
	public WorldElementActor(TextureRegion texture) {
		mapScreen = GameControler.instance.getMapScreen();
		
		image = new Image(texture);
		addActor(image);
		
		GameScreen screen = GameControler.instance.getMapScreen();
		image.setScaling(Scaling.stretch);
		image.setWidth(screen.getCellWidth());
		image.setHeight(screen.getCellHeight());
		
		currentAction = QuesterActions.NONE;
	}
	
	public WorldElementControler getControler() {
		return controler;
	}

	public void setControler(WorldElementControler controler) {
		this.controler = controler;
	}

	/**
	 * Place l'acteur dans la case sp�cifi�e par la colonne
	 * et la ligne indiqu�es. Cette m�thode se charge simplement
	 * de convertir une unit� logiques (col/row) en unit� r�elle
	 * (x/y en pixels) et de mettre � jour le monde.
	 * @param worldX
	 * @param worldY
	 */
	public void setPositionInWorld(int newX, int newY) {
		GameControler.instance.getMapScreen().updateMapPosition(
			this,
			worldX, worldY,
			newX, newY);
		setWorldX(newX);
		setWorldY(newY);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Retourne le X exprim� en unit� de la map et pas en pixels
	 * @return
	 */
	public int getWorldX() {
		return worldX;
	}

	private void setWorldX(int worldX) {
		this.worldX = worldX;
	}

	/**
	 * Retourne le Y exprim� en unit� de la map et pas en pixels
	 * @return
	 */
	public int getWorldY() {
		return worldY;
	}

	private void setWorldY(int worldY) {
		this.worldY = worldY;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		drawSpecifics(batch);
	}
	
	/**
	 * Appel�e pendant le draw pour dessiner les particularit�s
	 * de ce WorldElement.
	 * @param batch
	 */
	protected void drawSpecifics(SpriteBatch batch) {
	}
	
	public void moveTo(int destinationX, int destinationY, float duration) {
		currentAction = QuesterActions.MOVE;
		isLookingLeft = destinationX <= worldX;
			
		setPositionInWorld(destinationX, destinationY);
		addAction(Actions.sequence(
				Actions.moveTo(
					destinationX * mapScreen.getCellWidth(),
					destinationY * mapScreen.getCellHeight(),
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

	public QuesterActions getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(QuesterActions action, int targetX) {
		// Si l'action change, on RAZ le compteur pour les animations
		if (action != currentAction) {
			stateTime = 0f;
		}
		this.currentAction = action;
		isLookingLeft = targetX <= worldX;
	}
}
