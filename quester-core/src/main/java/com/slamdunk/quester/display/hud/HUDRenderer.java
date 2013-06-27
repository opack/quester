package com.slamdunk.quester.display.hud;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.quester.display.actors.PlayerActor;
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
	private ActionSlots actionSlots;
	private MenuButton menu;
	
	/**
	 * 
	 * @param areas Si != null, la minimap est activée
	 */
	public HUDRenderer(PlayerActor player) {
		actionSlots = new ActionSlots();
		
		Table table = new Table();
//		table.debug();
		table.add(createUpTable()).align(Align.left);
		table.row();
		table.add();
		table.add(createRightTable()).expand().align(Align.bottom | Align.right);
		table.row();
		table.add(createBottomTable()).colspan(2).align(Align.center);
		table.pack();
		
		table.setFillParent(true);
		
		addActor(table);
	}
	
	private Table createRightTable() {
		// Création des images qui pourront être dnd
		Image arrivingSlot1 = new Image(Assets.emptySlot);
		Image arrivingSlot2 = new Image(Assets.emptySlot);
		
		// Ajout au gestionnaire de dnd
		actionSlots.addSource(arrivingSlot1);
		actionSlots.addSource(arrivingSlot2);
		
		// Ajout à la table pour les organiser joliment
		arrivingActionSlots = new Table();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(new Image(Assets.emptySlot)).size(32, 32).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(arrivingSlot1).size(64, 64).padBottom(5);
		arrivingActionSlots.row();
		arrivingActionSlots.add(arrivingSlot2).size(64, 64).padBottom(5);
		arrivingActionSlots.row();
		return arrivingActionSlots;
	}

	private Table createBottomTable() {
	// Création des emplacements de stockage d'action
		// Création des images qui pourront être dnd
		Image stockSlot1 = new Image(Assets.emptySlot);
		Image stockSlot2 = new Image(Assets.emptySlot);
		Image stockSlot3 = new Image(Assets.emptySlot);
		Image stockSlot4 = new Image(Assets.emptySlot);
		Image stockSlot5 = new Image(Assets.emptySlot);
		// Ajout au gestionnaire de dnd
		actionSlots.addSource(stockSlot1);
		actionSlots.addSource(stockSlot2);
		actionSlots.addSource(stockSlot3);
		actionSlots.addSource(stockSlot4);
		actionSlots.addSource(stockSlot5);
		// Ajout à la table pour les organiser joliment
		stockedActionSlots = new Table();
		stockedActionSlots.add(stockSlot1).size(64, 64).padRight(5);
		stockedActionSlots.add(stockSlot2).size(64, 64).padRight(5);
		stockedActionSlots.add(stockSlot3).size(64, 64).padRight(5);
		stockedActionSlots.add(stockSlot4).size(64, 64).padRight(5);
		stockedActionSlots.add(stockSlot5).size(64, 64).padRight(5);
		stockedActionSlots.pack();
		
		// Création de la table englobante
		Table bottom = new Table();
		bottom.add(stockedActionSlots);
		bottom.pack();
		return bottom;
	}

	private Table createUpTable() {
		// Création du bouton d'affichage de la minimap
		menu = new MenuButton();
		
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
		up.add(menu.getMenuBtn()).size(64, 64);
		up.add(stats).align(Align.bottom | Align.left);
		up.pack();
		
		// Préparation du menu
		menu.prepareMenu();
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

	public void getToggleMinimapVisibility() {
		if (minimap != null) {
			minimap.setVisible(!minimap.isVisible());
		}
	}
	
	public void render(float delta) {
		// Mise à jour éventuelle du menu
		menu.act(delta);
		
		// Dessin du HUD
		draw();
		
//		Table.drawDebug(this);
	}
}
