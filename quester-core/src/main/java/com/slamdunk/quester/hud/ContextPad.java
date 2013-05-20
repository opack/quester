package com.slamdunk.quester.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.core.Assets;

public class ContextPad extends Table {

	public ContextPad(int buttonSize) {
		// Création des boutons
		Button btnUp = createButton(Assets.arrowUp, Assets.arrowUp, new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				System.out.println("HUD.HUD(...).new ClickListener() {...}.clicked() UP");
			}
		});
		Button btnDown = createButton(Assets.arrowDown, Assets.arrowDown, new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				System.out.println("HUD.HUD(...).new ClickListener() {...}.clicked() DOWN");
			}
		});
		Button btnLeft = createButton(Assets.arrowLeft, Assets.arrowLeft, new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				System.out.println("HUD.HUD(...).new ClickListener() {...}.clicked() LEFT");
			}
		});
		Button btnRight = createButton(Assets.arrowRight, Assets.arrowRight, new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				System.out.println("HUD.HUD(...).new ClickListener() {...}.clicked() RIGHT");
			}
		});
		
		// Ajout à la table
		//debug();
		add();
		add(btnUp).height(buttonSize).width(buttonSize);
		row();
		add(btnLeft).height(buttonSize).width(buttonSize);
		add();
		add(btnRight).height(buttonSize).width(buttonSize);
		row();
		add();
		add(btnDown).height(buttonSize).width(buttonSize);
		pack();
	}

	private Button createButton(TextureRegion imgUp, TextureRegion imgDown, ClickListener clickListener) {
		Button button = new Button(new TextureRegionDrawable(imgUp), new TextureRegionDrawable(imgDown));
		button.addListener(clickListener);
		return button;
	}
}
