package com.slamdunk.quester.display.hud;

import static com.slamdunk.quester.core.Quester.SCREEN_HEIGHT;
import static com.slamdunk.quester.core.Quester.SCREEN_WIDTH;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.QuesterGame;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.hud.contextpad.ContextPad;
import com.slamdunk.quester.display.hud.minimap.DungeonMiniMap;
import com.slamdunk.quester.display.hud.minimap.MiniMap;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.PlayerData;

public class HUD extends Stage {
	private final ContextPad pad;
	private MiniMap minimap;
	private final Label lblHp;
	private final Label lblAtt;
	
	/**
	 * 
	 * @param areas Si != null, la minimap est activée
	 */
	public HUD(Player player) {
		LabelStyle style = new LabelStyle();
		style.font = Assets.hudFont;
		lblHp = new Label("", style);
		lblAtt = new Label("", style);
		
		Table stats = new Table();
//		stats.debug();
		stats.add(new Image(Assets.heart)).size(32, 32);
		stats.add(lblHp).width(50);//.top();
		stats.add().expandX();
		stats.row();
		stats.add(new Image(Assets.sword)).size(32, 32);
		stats.add(lblAtt).width(50).top();
		stats.pack();
		
		pad = new ContextPad(64, player);
		
		Table table = new Table();
//		table.debug();
		//table.setBackground(new TextureRegionDrawable(Assets.hud));
		table.add(pad).padLeft(5);
		table.add(stats).padLeft(5).align(Align.bottom);
		table.pack();

		addActor(table);
	}
	
	public void setMiniMap(int worldWidth, int worldHeight, int miniMapImageWidth, int miniMapImageHeight) {
		minimap = new MiniMap(worldWidth, worldHeight);
		minimap.init(miniMapImageWidth, miniMapImageHeight);
		minimap.setX(SCREEN_WIDTH - minimap.getWidth());
		minimap.setY(SCREEN_HEIGHT - minimap.getHeight());
		
		addActor(minimap);
	}
	
	public void setMiniMap(MapArea[][] rooms, int miniMapImageWidth, int miniMapImageHeight) {
		DungeonMiniMap dungeonminimap = new DungeonMiniMap(rooms.length, rooms[0].length);
		dungeonminimap.init(miniMapImageWidth, miniMapImageHeight, rooms);
		dungeonminimap.setX(SCREEN_WIDTH - dungeonminimap.getWidth());
		dungeonminimap.setY(SCREEN_HEIGHT - dungeonminimap.getHeight());
		
		minimap = dungeonminimap;
		addActor(minimap);
	}

	public void update(int currentAreaX, int currentAreaY) {
		// Mise à jour du pad
		pad.update();
		
		// Mise à jour de la minimap
		if (minimap != null
		&& currentAreaX != -1 
		&& currentAreaY != -1) {
			minimap.setPlayerRoom(currentAreaX, currentAreaY);
		}
		
		// Mise à jour des stats
		PlayerData playerData = QuesterGame.instance.getPlayerData();
		lblHp.setText(String.valueOf(playerData.health));
		lblAtt.setText(String.valueOf(playerData.attack));
	}
	
	public void update() {
		update(-1, -1);
	}
	
//	@Override
//	public void draw() {
//		super.draw();
//		Table.drawDebug(this);
//	}
}
