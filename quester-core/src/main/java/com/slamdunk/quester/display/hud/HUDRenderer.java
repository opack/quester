package com.slamdunk.quester.display.hud;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.hud.minimap.DungeonMiniMap;
import com.slamdunk.quester.display.hud.minimap.MiniMap;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.CharacterData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.utils.Assets;

public class HUDRenderer extends Stage {
	private Label lblAtt;
	private Label lblHp;
	private MiniMap minimap;
	private ActionSlots actionSlots;
	private MenuButton menu;
	
	public void init() {
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
		WorldElementActor upcomingSlot1 = createEmptySlot();
		WorldElementActor upcomingSlot2 = createEmptySlot();
		WorldElementActor upcomingSlot3 = createEmptySlot();
		WorldElementActor upcomingSlot4 = createEmptySlot();
		WorldElementActor arrivalSlot1 = createEmptySlot();
		WorldElementActor arrivalSlot2 = createEmptySlot();
		
		// Ajout au gestionnaire de dnd
		actionSlots.addUpcomingSlots(upcomingSlot1, upcomingSlot2, upcomingSlot3, upcomingSlot4);
		actionSlots.addArrivalSlots(arrivalSlot1, arrivalSlot2);
		
		// Ajout à la table pour les organiser joliment
		Table right = new Table();
		right.add(upcomingSlot1).size(32, 32).padBottom(5);
		right.row();
		right.add(upcomingSlot2).size(32, 32).padBottom(5);
		right.row();
		right.add(upcomingSlot3).size(32, 32).padBottom(5);
		right.row();
		right.add(upcomingSlot4).size(32, 32).padBottom(5);
		right.row();
		right.add(arrivalSlot1).size(64, 64).padBottom(5);
		right.row();
		right.add(arrivalSlot2).size(64, 64).padBottom(5);
		right.row();
		return right;
	}

	private Table createBottomTable() {
	// Création des emplacements de stockage d'action
		// Création des images qui pourront être dnd
		WorldElementActor stockSlot1 = createEmptySlot();
		WorldElementActor stockSlot2 = createEmptySlot();
		WorldElementActor stockSlot3 = createEmptySlot();
		WorldElementActor stockSlot4 = createEmptySlot();
		WorldElementActor stockSlot5 = createEmptySlot();
		// Ajout au gestionnaire de dnd
		actionSlots.addStockSlots(stockSlot1, stockSlot2, stockSlot3, stockSlot4, stockSlot5);
		
		// Création de la table englobante
		Table bottom = new Table();
		bottom.add(stockSlot1).size(64, 64).padRight(5);
		bottom.add(stockSlot2).size(64, 64).padRight(5);
		bottom.add(stockSlot3).size(64, 64).padRight(5);
		bottom.add(stockSlot4).size(64, 64).padRight(5);
		bottom.add(stockSlot5).size(64, 64).padRight(5);
		bottom.pack();
		return bottom;
	}

	private WorldElementActor createEmptySlot() {
		WorldElementData data = new WorldElementData();
		WorldElementControler slotControler = new WorldElementControler(data);
		
		WorldElementActor slotActor = new WorldElementActor(Assets.emptySlot);
		slotActor.setControler(slotControler);
		
		return slotActor;
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
