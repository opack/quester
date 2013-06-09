package com.slamdunk.quester.display.actors;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class CharacterActor extends WorldElementActor{
	protected CharacterControler characterControler;
	
	protected CharacterActor(TextureRegion texture) {
		super(texture);
		
		// L'image du personnage est décalée un peu vers le haut
		GameScreen screen = GameControler.instance.getMapScreen();
		float size = GameControler.instance.getMapScreen().getCellWidth() * 0.75f;
		getImage().setSize(size, size);
		float offsetX = (screen.getCellWidth() - size) / 2; // Au centre
		float offsetY = screen.getCellHeight() - size; // En haut
		getImage().setPosition(offsetX, offsetY);
	}
	
	@Override
	public void setControler(WorldElementControler controler) {
		super.setControler(controler);
		characterControler = (CharacterControler)controler;
	}
	
	@Override
	public CharacterControler getControler() {
		return characterControler;
	}
	
	@Override
	public void drawSpecifics(SpriteBatch batch) {
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


	public List<UnmutablePoint> findPathTo(WorldElementActor to) {
		return GameControler.instance.getMapScreen().findPath(this, to);
	}
}
