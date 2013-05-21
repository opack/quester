package com.slamdunk.quester.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.CharacterListener;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameWorld;

public class HUD extends Stage implements CharacterListener {
	private final ContextPad pad;
	private final Label lblHp;
	private final Label lblAtt;
	
	public HUD(GameWorld world) {
		LabelStyle style = new LabelStyle();
		style.font = Assets.hudFont;
		lblHp = new Label("", style);
		lblAtt = new Label("", style);
		
		Table stats = new Table();
//		stats.debug();
		stats.add(new Image(Assets.heart)).height(16).width(16);
		stats.add(lblHp).width(50);//.top();
		stats.add().expandX();
		stats.row();
		stats.add(new Image(Assets.sword)).height(16).width(16);
		stats.add(lblAtt).width(50).top();
		stats.pack();
		
		pad = new ContextPad(64, world);
		
		Table table = new Table();
//		table.debug();
		//table.setBackground(new TextureRegionDrawable(Assets.hud));
		table.add(pad).padLeft(5);
		table.add(stats).padLeft(5).align(Align.bottom);
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
	
	public void updatePad() {
		pad.updatePad();
	}
	
//	@Override
//	public void draw() {
//		super.draw();
//		Table.drawDebug(this);
//	}
}
