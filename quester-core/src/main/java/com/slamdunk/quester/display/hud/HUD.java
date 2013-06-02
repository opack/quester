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
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.hud.contextpad.ContextPad;
import com.slamdunk.quester.display.hud.minimap.DungeonMiniMap;
import com.slamdunk.quester.display.hud.minimap.MiniMap;
import com.slamdunk.quester.map.logical.MapArea;

public class HUD extends Stage implements CharacterListener {
	private final ContextPad pad;
	private MiniMap minimap;
	private final Label lblHp;
	private final Label lblAtt;
	
	/**
	 * 
	 * @param world
	 * @param areas Si != null, la minimap est activée
	 */
	public HUD(GameWorld world) {
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
		
		pad = new ContextPad(64, world);
		
		Table table = new Table();
//		table.debug();
		//table.setBackground(new TextureRegionDrawable(Assets.hud));
		table.add(pad).padLeft(5);
		table.add(stats).padLeft(5).align(Align.bottom);
		table.pack();

		addActor(table);
	}
	
	public void setMiniMap(int worldWidth, int worldHeight, int miniMapWidth, int miniMapHeight) {
		minimap = new MiniMap(worldWidth, worldHeight);
		minimap.init(miniMapWidth, miniMapHeight);
		minimap.setX(SCREEN_WIDTH - minimap.getWidth());
		minimap.setY(SCREEN_HEIGHT - minimap.getHeight());
		
		addActor(minimap);
	}
	
	public void setMiniMap(MapArea[][] rooms, int miniMapWidth, int miniMapHeight) {
		DungeonMiniMap dungeonminimap = new DungeonMiniMap(rooms.length, rooms[0].length);
		dungeonminimap.init(miniMapWidth, miniMapHeight);
		dungeonminimap.setX(SCREEN_WIDTH - dungeonminimap.getWidth());
		dungeonminimap.setY(SCREEN_HEIGHT - dungeonminimap.getHeight());
		
		minimap = dungeonminimap;
		addActor(minimap);
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
	
	public void update(int currentRoomX, int currentRoomY) {
		pad.update();
		if (minimap != null
		&& currentRoomX != -1 
		&& currentRoomY != -1) {
			minimap.setPlayerRoom(currentRoomX, currentRoomY);
		}
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
