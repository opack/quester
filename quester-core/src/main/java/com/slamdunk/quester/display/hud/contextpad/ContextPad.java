package com.slamdunk.quester.display.hud.contextpad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class ContextPad extends Table {
	private Button endPhaseButton;
	private Label actionsLeftLabel;
	
	public ContextPad(int buttonSize, PlayerActor player) {
		// Création des boutons
		Button centerCamera = createButton(Assets.center, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getMapScreen().centerCameraOnPlayer();
			};
		});
		endPhaseButton = createButton(Assets.menu_attack, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().stopMove();
				GameControler.instance.getPlayer().endTurn();
				
			};
		});
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = Assets.characterFont;
		actionsLeftLabel = new Label("0", labelStyle);
		endPhaseButton.add(actionsLeftLabel);
		
		// Ajout à la table
		add(centerCamera).size(buttonSize, buttonSize);
		add(endPhaseButton).size(buttonSize, buttonSize);		
		pack();
	}
	
	private static Button createButton(TextureRegion texture, ClickListener listener) {
		ButtonStyle style = new ButtonStyle();
		style.up = new TextureRegionDrawable(texture);
		style.down = new TextureRegionDrawable(texture);
		style.pressedOffsetY = 1f;
		Button button = new Button(style);
		button.addListener(listener);
		return button;
	}

	public void update() {
		// Met à jour l'image de la phase
		if (GameControler.instance.isInAttackPhase()) {
			endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_attack);
			endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_attack);
		} else {
			endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_torch);
			endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_torch);
		}
		
		// Met à jour le nombre d'actions restantes
		int actionsLeft = GameControler.instance.getCurrentCharacter().getData().actionsLeft;
		actionsLeftLabel.setText(String.valueOf(actionsLeft));
	}
}
