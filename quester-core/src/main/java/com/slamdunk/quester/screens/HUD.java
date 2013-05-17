package com.slamdunk.quester.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.CharacterListener;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;

public class HUD extends Stage implements CharacterListener {
	private final GameWorld world;
	
	private final Label lblHp;
	private final Label lblAtt;
	
	public HUD(GameWorld world) {
		this.world = world;
		
		LabelStyle style = new LabelStyle();
		style.font = Assets.hudFont;
		lblHp = new Label("", style);
		lblAtt = new Label("", style);
		
		Table table = new Table();
//		table.debug();
		table.setBackground(new TextureRegionDrawable(Assets.hud));
		
		table.add(new Image(Assets.heart)).height(16).width(16).padLeft(10);
		table.add(lblHp).width(50);//.top();
		table.add().expandX();
		
		table.row();
		table.add(new Image(Assets.sword)).height(16).width(16).padLeft(10);
		table.add(lblAtt).width(50).top();
		
		table.pack();

		addActor(table);
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// Les HP du joueur ont été modifiés
		lblHp.setText(String.valueOf(newValue));
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// Les ATT du joueur ont été modifiés
		lblAtt.setText(String.valueOf(newValue));
	}

	@Override
	public void onCharacterDeath(Character character) {
		// TODO Auto-generated method stub
		
	}
	
//	@Override
//	public void draw() {
//		super.draw();
//		Table.drawDebug(this);
//	}
}
