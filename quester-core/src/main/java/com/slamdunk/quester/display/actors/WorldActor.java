package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.map.ScreenMap;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.model.map.ElementData;

/**
 * Contient l'ensemble des comportements communs � tous les
 * �l�ments du monde, sans aucune logique de jeu.
 * @author Didier
 *
 */
public class WorldActor extends Group implements Comparable<WorldActor>{
	private static int WORLD_ELEMENTS_COUNT = 0;
	
	private final int id;
	/**
	 * Position logique de l'�l�ment dans le monde
	 */
	private int worldX;
	private int worldY;
	
	/**
	 * Objet qui sert d'interm�diaire avec la map
	 */
	
	/**
	 * Indique l'ordre de jeu de cet �l�ment
	 */
	private int playRank;
	
	private Image image;
	
	private ElementData elementData;
	
	public WorldActor(TextureRegion texture) {
		this(texture, 0, 0);
	}
	
	public WorldActor(TextureRegion texture, int col, int row) {
		image = new Image(texture);
		addActor(image);
		
		id = WORLD_ELEMENTS_COUNT++;
		playRank = id;
		
		GameScreen screen = QuesterGame.instance.getMapScreen();
		image.setScaling(Scaling.stretch);
		image.setWidth(screen.getCellWidth());
		image.setHeight(screen.getCellHeight());
		
		setPositionInWorld(col, row);
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
		if (isSolid()) {
			QuesterGame.instance.getMapScreen().updateMapPosition(
				this,
				worldX, worldY,
				newX, newY);
		}
		setWorldX(newX);
		setWorldY(newY);
	}
	
	public long getId() {
		return id;
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
	
	public void setElementData(ElementData data) {
		this.elementData = data;
	}
	
	public ElementData getElementData() {
		return elementData;
	}

	/**
	 * Retourne true si l'�l�ment ne peut pas �tre travers�,
	 * false sinon.
	 * @return
	 */
	public boolean isSolid() {
		return false;
	}

	public double distanceTo(WorldActor destination) {
		return distanceTo(destination.getWorldX(), destination.getWorldY());
	}
	
	public double distanceTo(int x, int y) {
		return ScreenMap.distance(getWorldX(), getWorldY(), x, y);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (shouldEndTurn()) {
			endTurn();
		}
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

	/**
	 * Retourne true si le tour peut se terminer
	 * @return
	 */
	protected boolean shouldEndTurn() {
		// Si n'a plus d'action en cours, alors c'est que son tour peut s'achever.
		return getActions().size == 0;
	}

	/**
	 * Permet d'ordonner les �l�ments du monde entre eux pour savoir
	 * qui va jouer avant l'autre : le premier est celui ayant le rang
	 * le plus petit.
	 * Si plusieurs �l�ments ont le m�me rang, l'ordre est ind�termin�.
	 * Par d�faut, le rang correspond � l'ordre de cr�ation (donc l'id).
	 * @return
	 */
	public int getPlayRank() {
		return playRank;
	}
	
	public void setPlayRank(int rank) {
		this.playRank= rank;
	}

	@Override
	public int compareTo(WorldActor o) {
		return playRank - o.playRank;
	}

	public void endTurn() {
		QuesterGame.instance.endCurrentPlayerTurn();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WorldActor) {
			return id == ((WorldActor)obj).id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
