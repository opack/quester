package com.slamdunk.quester.display.hud.contextpad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class ContextPad extends Table {
	private static Button createButton(TextureRegion texture, ClickListener listener) {
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(texture);
		style.down = new TextureRegionDrawable(texture);
		style.pressedOffsetY = 1f;
		Button button = new Button(style);
		button.addListener(listener);
		return button;
	}
	
	public ContextPad(int buttonSize) {
		// Création des boutons
		Button centerCamera = createButton(Assets.center, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getScreen().centerCameraOn(GameControler.instance.getPlayer().getActor());
			};
		});
		Button stopMoveButton = createButton(Assets.cross, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().stopMove();
			};
		});
		
		// Ajout à la table
		add(centerCamera).size(buttonSize, buttonSize);
		add(stopMoveButton).size(buttonSize, buttonSize);		
		pack();
	}
}
