package com.slamdunk.quester.display.hud.contextpad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.PlayerActor;

public class ContextPad extends Table {
	public ContextPad(int buttonSize, PlayerActor player) {
		// Création des boutons
		Button centerCamera = createButton(Assets.center, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				QuesterGame.instance.getMapScreen().centerCameraOnPlayer();
			};
		});
		Button stopAction = createButton(Assets.cross, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				QuesterGame.instance.getPlayer().stopActions();
			};
		});
		
		// Ajout à la table
		add(centerCamera).size(buttonSize, buttonSize);
		add(stopAction).size(buttonSize, buttonSize);		
		pack();
	}
	
	private static Button createButton(TextureRegion texture, ClickListener listener) {
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(texture);
		style.up = new TextureRegionDrawable(texture);
		style.pressedOffsetY = 1f;
		Button button = new Button(style);
		button.addListener(listener);
		return button;
	}
}
