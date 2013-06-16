package com.slamdunk.quester.display.hud.contextpad;

import static com.slamdunk.quester.logic.controlers.GamePhases.MOVE;
import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;

public class ContextPad extends Table {
	private Button endPhaseButton;
	private Label actionsLeftLabel;
	
	public ContextPad(int buttonSize, final PlayerActor player) {
		// Création des boutons
		Button centerCamera = createButton(Assets.center, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getScreen().centerCameraOn(player);
			};
		});
		endPhaseButton = createButton(Assets.menu_attack, new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().stopMove();
				GameControler.instance.nextPhase();
				
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
		CharacterControler currentCharacter = GameControler.instance.getCurrentCharacter();
		
		// Met à jour l'image de la phase
		switch (GameControler.instance.getGamePhase()) {
			case ATTACK:
				if (currentCharacter.getData().element == PLAYER) {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_attack);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_attack);
					endPhaseButton.setDisabled(false);
				} else {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_attack_disabled);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_attack_disabled);
					endPhaseButton.setDisabled(true);
				}
				break;
			
			case LIGHT:
				if (currentCharacter.getData().element == PLAYER) {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_torch);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_torch);
					endPhaseButton.setDisabled(false);
				} else {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_torch_disabled);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_torch_disabled);
					endPhaseButton.setDisabled(true);
				}
				break;
				
			case MOVE:
				if (currentCharacter.getData().element == PLAYER) {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_move);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_move);
					endPhaseButton.setDisabled(false);
				} else {
					endPhaseButton.getStyle().up = new TextureRegionDrawable(Assets.menu_move_disabled);
					endPhaseButton.getStyle().down = new TextureRegionDrawable(Assets.menu_move_disabled);
					endPhaseButton.setDisabled(true);
				}
				break;
		}
		
		// Met à jour le nombre d'actions restantes
		int actionsLeft = currentCharacter.getData().actionsLeft;
		actionsLeftLabel.setText(String.valueOf(actionsLeft));
		actionsLeftLabel.setPosition(endPhaseButton.getWidth() - actionsLeftLabel.getWidth(), 0);
		actionsLeftLabel.setVisible(GameControler.instance.getGamePhase() != MOVE && GameControler.instance.hasMoreEnemies());
	}
}
