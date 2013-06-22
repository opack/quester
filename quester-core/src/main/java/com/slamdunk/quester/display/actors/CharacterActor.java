package com.slamdunk.quester.display.actors;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.display.Clip;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.utils.Assets;

public class CharacterActor extends WorldElementActor{
	protected CharacterControler characterControler;
	
	protected CharacterActor(TextureRegion texture) {
		super(texture);
		
		// L'image du personnage est décalée un peu vers le haut
		if (getImage() != null) {
			ActorMap map = GameControler.instance.getScreen().getMap();
			float size = map.getCellWidth() * 0.75f;
			getImage().setSize(size, size);
			float offsetX = (map.getCellWidth() - size) / 2; // Au centre
			float offsetY = map.getCellHeight() - size; // En haut
			getImage().setPosition(offsetX, offsetY);
		}
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
		// Met à jour l'animation du personnage
		drawClip(batch);
		
		// Mesures
		int picSize = Assets.heart.getTexture().getWidth();
		
		String att = String.valueOf(characterControler.getData().attack);
		TextBounds textBoundsAtt = Assets.characterFont.getBounds(att);
		float offsetAttX =  getX() + (getWidth() - (picSize + 1 + textBoundsAtt.width)) / 2;
		float offsetAttTextY = getY() + 1 + picSize - (picSize - textBoundsAtt.height) / 2;
		
		String hp = String.valueOf(characterControler.getData().health);
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
	
	public List<Point> findPathTo(WorldElementActor to) {
		return GameControler.instance.getScreen().getMap().findPath(getWorldX(), getWorldY(), to.getWorldX(), to.getWorldY());
	}
	
	@Override
	public CharacterControler getControler() {
		return characterControler;
	}

	@Override
	public void setControler(WorldElementControler controler) {
		super.setControler(controler);
		characterControler = (CharacterControler)controler;
	}

	protected Clip initClip(Clip clip) {
		// La taille de la zone de dessin est la taille du WorldElementActor
		ActorMap map = GameControler.instance.getScreen().getMap();
		clip.drawArea.width = map.getCellWidth();
		clip.drawArea.height = map.getCellHeight();
		
		// La frame est agrandie en X et en Y d'un facteur permettant d'occuper toute la largeur
		TextureRegion aFrame = clip.getKeyFrame(0);
		clip.scaleX = clip.drawArea.width / aFrame.getRegionWidth();
		clip.scaleY = clip.scaleX;
		
		// Les frames doivent être dessinées au centre horizontal et à 25% du bas
		clip.alignX = 0.5f;
		clip.offsetY = 0.25f;
		return clip;
	}
}
