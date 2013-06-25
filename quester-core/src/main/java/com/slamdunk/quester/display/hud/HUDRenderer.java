package com.slamdunk.quester.display.hud;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.hud.contextpad.ContextPad;
import com.slamdunk.quester.display.hud.minimap.DungeonMiniMap;
import com.slamdunk.quester.display.hud.minimap.MiniMap;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.utils.Assets;

public class HUDRenderer extends Stage {
	private Label lblAtt;
	private Label lblHp;
	private MiniMap minimap;
	private Table arrivingActionSlots;
	private Table stockedActionSlots;
	
	/**
	 * 
	 * @param areas Si != null, la minimap est activée
	 */
	public HUDRenderer(PlayerActor player) {
		Table table = new Table();
//		table.debug();
		table.add(createUpTable()).align(Align.left);
		table.row();
		table.add();
		table.add(createRightTable()).expand().align(Align.bottom | Align.right);
		table.row();
		table.add(createBottomTable());
		table.pack();
		
		table.setFillParent(true);
		
		addActor(table);
	}
	
	private Table createRightTable() {
		arrivingActionSlots = new Table();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padBottom(5);
		arrivingActionSlots.row();
		return arrivingActionSlots;
	}

	private Table createBottomTable() {
		// Création des boutons généraux
		ContextPad pad = new ContextPad(64);
		
		// Création des emplacements de stockage d'action
		stockedActionSlots = new Table();
		stockedActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padRight(5);
		stockedActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padRight(5);
		stockedActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padRight(5);
		stockedActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padRight(5);
		stockedActionSlots.add(new Image(Assets.emptySlot)).size(64, 64).padRight(5);
		stockedActionSlots.pack();
		
		// Création de la table englobante
		Table bottom = new Table();
		bottom.add(pad);
		bottom.add(stockedActionSlots);
		bottom.pack();
		return bottom;
	}

	private Table createUpTable() {
		// Création du bouton d'affichage de la minimap
		ButtonStyle btnStyle = new ButtonStyle();
		btnStyle.up = new TextureRegionDrawable(Assets.map);
		btnStyle.down = new TextureRegionDrawable(Assets.map);
		btnStyle.pressedOffsetY = 1.0f;
		Button displayMap = new Button(btnStyle);
		displayMap.addListener(new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				minimap.setVisible(!minimap.isVisible());
			};
		});
		
		// Création des statistiques
		LabelStyle style = new LabelStyle();
		style.font = Assets.hudFont;
		lblHp = new Label("", style);
		lblAtt = new Label("", style);
		
		Table stats = new Table();
		stats.add(new Image(Assets.heart)).size(32, 32);
		stats.add(lblHp).width(50).top();
		stats.add().expandX();
		stats.row();
		stats.add(new Image(Assets.sword)).size(32, 32);
		stats.add(lblAtt).width(50).top();
		stats.pack();
		
		// Création de la table englobante
		Table up = new Table();
		up.add(displayMap);
		up.add(stats).align(Align.bottom | Align.left);
		up.pack();
		return up;
	}

	public void setMiniMap(int worldWidth, int worldHeight, int miniMapImageWidth, int miniMapImageHeight) {
		minimap = new MiniMap(worldWidth, worldHeight);
		minimap.init(miniMapImageWidth, miniMapImageHeight);
		minimap.setX((screenWidth - minimap.getWidth()) / 2);
		minimap.setY((screenHeight - minimap.getHeight()) / 2);
		minimap.setVisible(false);
		
		addActor(minimap);
	}
	
	public void setMiniMap(MapArea[][] rooms, int miniMapImageWidth, int miniMapImageHeight) {
		DungeonMiniMap dungeonminimap = new DungeonMiniMap(rooms.length, rooms[0].length);
		dungeonminimap.init(miniMapImageWidth, miniMapImageHeight, rooms);
		dungeonminimap.setX((screenWidth - dungeonminimap.getWidth()) / 2);
		dungeonminimap.setY((screenHeight - dungeonminimap.getHeight()) / 2);
		dungeonminimap.setVisible(false);
		
		minimap = dungeonminimap;
		addActor(minimap);
	}

	public void update() {
		update(-1, -1);
	}
	
	public void update(int currentAreaX, int currentAreaY) {
		// Mise à jour de la minimap
		if (minimap != null
		&& currentAreaX != -1 
		&& currentAreaY != -1) {
			minimap.setPlayerRoom(currentAreaX, currentAreaY);
		}
		
		// Mise à jour des stats
		CharacterData playerData = GameControler.instance.getPlayer().getData();
		lblHp.setText(String.valueOf(playerData.health));
		lblAtt.setText(String.valueOf(playerData.attack));
	}
	
//	@Override
//	public void draw() {
//		super.draw();
//		Table.drawDebug(this);
//	}
}
