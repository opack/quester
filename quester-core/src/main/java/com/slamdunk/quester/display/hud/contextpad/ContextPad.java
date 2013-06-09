package com.slamdunk.quester.display.hud.contextpad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class ContextPad extends Table {
	public ContextPad(int buttonSize, PlayerActor player) {
		// Cr�ation des boutons
		Button centerCamera = createButton(Assets.center, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getMapScreen().centerCameraOnPlayer();
			};
		});
		Button stopAction = createButton(Assets.cross, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().stopMove();
				GameControler.instance.getPlayer().endTurn();
				
			};
		});
		
		// Ajout � la table
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
