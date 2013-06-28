package com.slamdunk.quester.display.hud;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.hud.actionslots.ActionSlots;
import com.slamdunk.quester.display.hud.actionslots.ActionSlotsHelper;
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
	private ActionSlots actionSlots;
	private MenuButton menu;
	private Stage mapStage;
	/**
	 * Vecteur utilisé pour accélérer le traitement de la méthode hit
	 */
	private Vector2 hitTestPos;
	
	public HUDRenderer() {
		hitTestPos = new Vector2();
	}
	
	public void init(Stage mapStage) {
		this.mapStage = mapStage;
		
		actionSlots = new ActionSlots();
		actionSlots.addTarget(GameControler.instance.getScreen().getPlayerActor());
		
		Table table = new Table();
//		table.debug();
		table.add(createUpTable()).align(Align.left);
		table.row();
		table.add().expand();
//DBG		table.add(createRightTable()).expand().align(Align.bottom | Align.right);
		table.row();
		table.add(createBottomTable()).colspan(2).align(Align.center);
		table.pack();
		table.setFillParent(true);
		addActor(table);
		
		actionSlots.fillActionSlots();
	}
	
//	private Table createRightTable() {
//		// Création des images qui pourront être dnd
//		ActionSlotActor upcomingSlot1 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor upcomingSlot2 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor upcomingSlot3 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor upcomingSlot4 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor arrivalSlot1 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor arrivalSlot2 = ActionSlotsHelper.createEmptySlot();
//		
//		// Ajout au gestionnaire de dnd
//		actionSlots.addUpcomingSlots(upcomingSlot1, upcomingSlot2, upcomingSlot3, upcomingSlot4);
//		actionSlots.addArrivalSlots(arrivalSlot1, arrivalSlot2);
//		
//		// Ajout à la table pour les organiser joliment
//		Table right = new Table();
//		right.add(upcomingSlot1).size(32, 32).padBottom(5);
//		right.row();
//		right.add(upcomingSlot2).size(32, 32).padBottom(5);
//		right.row();
//		right.add(upcomingSlot3).size(32, 32).padBottom(5);
//		right.row();
//		right.add(upcomingSlot4).size(32, 32).padBottom(5);
//		right.row();
//		right.add(arrivalSlot1).size(64, 64).padBottom(5);
//		right.row();
//		right.add(arrivalSlot2).size(64, 64).padBottom(5);
//		right.row();
//		return right;
//	}

	private Table createBottomTable() {
	// Création des images qui pourront être dnd
		ActionSlotActor upcomingSlot1 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor upcomingSlot2 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor upcomingSlot3 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor upcomingSlot4 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor upcomingSlot5 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor arrivalSlot1 = ActionSlotsHelper.createEmptySlot();
//		ActionSlotActor arrivalSlot2 = ActionSlotsHelper.createEmptySlot();
		
	// Création des emplacements de stockage d'action
		// Création des images qui pourront être dnd
		ActionSlotActor stockSlot1 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor stockSlot2 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor stockSlot3 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor stockSlot4 = ActionSlotsHelper.createEmptySlot();
		ActionSlotActor stockSlot5 = ActionSlotsHelper.createEmptySlot();
		// Ajout au gestionnaire de dnd
		actionSlots.addUpcomingSlots(upcomingSlot1, upcomingSlot2, upcomingSlot3, upcomingSlot4, upcomingSlot5);
		actionSlots.addStockSlots(stockSlot1, stockSlot2, stockSlot3, stockSlot4, stockSlot5);
		actionSlots.addArrivalSlots(stockSlot1, stockSlot2, stockSlot3, stockSlot4, stockSlot5);
//		actionSlots.addArrivalSlots(arrivalSlot1, arrivalSlot2);
				
		// Création de la table englobante
		Table bottom = new Table();
		bottom.align(Align.center);
		bottom.add(stockSlot1).size(64, 64).padRight(5);
		bottom.add(stockSlot2).size(64, 64).padRight(5);
		bottom.add(stockSlot3).size(64, 64).padRight(5);
		bottom.add(stockSlot4).size(64, 64).padRight(5);
		bottom.add(stockSlot5).size(64, 64).padRight(5);
		bottom.row().padBottom(5).padTop(5);
		bottom.add(upcomingSlot1).size(32, 32).padRight(5);
		bottom.add(upcomingSlot2).size(32, 32).padRight(5);
		bottom.add(upcomingSlot3).size(32, 32).padRight(5);
		bottom.add(upcomingSlot4).size(32, 32).padRight(5);
		bottom.add(upcomingSlot5).size(32, 32).padRight(5);
//		bottom.add(arrivalSlot1).size(64, 64).padRight(5);
//		bottom.add(arrivalSlot2).size(64, 64).padRight(5);
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
		actionSlots.act(delta);
		
		// Dessin du HUD
		draw();
		
//		Table.drawDebug(this);
	}
	
	@Override
	public Actor hit(float stageX, float stageY, boolean touchable) {
		Actor hit = super.hit(stageX, stageY, touchable);
		
		// Si on est en plein drag'n'drop, et qu'aucun acteur n'est aux coordonnées indiquées,
		// on récupère l'acteur à ces coordonnées sur le Stage de la map. Ainsi on peut faire
		// un drag'n'drop depuis le Stage du hud vers le Stage de la map.
		if (hit == null && actionSlots.isDragging()) {
			hitTestPos.x = stageX;
			hitTestPos.y = stageY;
			Vector2 screenCoords = stageToScreenCoordinates(hitTestPos);
			Vector2 stageCoords = mapStage.screenToStageCoordinates(screenCoords);
			hit = mapStage.hit(stageCoords.x, stageCoords.y, touchable);
		}
		return hit;
	}
}
