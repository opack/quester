package com.slamdunk.quester.display.hud.contextpad;

import static com.slamdunk.quester.ia.Action.ATTACK;
import static com.slamdunk.quester.ia.Action.CENTER_CAMERA;
import static com.slamdunk.quester.ia.Action.CROSS_DOOR;
import static com.slamdunk.quester.ia.Action.CROSS_PATH;
import static com.slamdunk.quester.ia.Action.ENTER_CASTLE;
import static com.slamdunk.quester.ia.Action.ENTER_VILLAGE;
import static com.slamdunk.quester.ia.Action.MOVE;
import static com.slamdunk.quester.ia.Action.NONE;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.actors.Castle;
import com.slamdunk.quester.display.actors.Damageable;
import com.slamdunk.quester.display.actors.Door;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.PathToRegion;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Village;
import com.slamdunk.quester.display.actors.WorldActor;

public class ContextPad extends Table {
	private Player player;
	private GameMap map;
	
	private final PadButton up;
	private final PadButton down;
	private final PadButton left;
	private final PadButton right;
	private final PadButton center;
	
	public ContextPad(int buttonSize, GameWorld world) {
		this.map = world.getMap();
		this.player = world.getPlayer();
		
		// Cr�ation des boutons
		Map<String, TextureRegion[]> assets = new HashMap<String, TextureRegion[]>();
		assets.put("cross", new TextureRegion[]{Assets.cross, Assets.cross});
		assets.put("arrow_up", new TextureRegion[]{Assets.arrowUp, Assets.arrowUp});
		assets.put("arrow_down", new TextureRegion[]{Assets.arrowDown, Assets.arrowDown});
		assets.put("arrow_left", new TextureRegion[]{Assets.arrowLeft, Assets.arrowLeft});
		assets.put("arrow_right", new TextureRegion[]{Assets.arrowRight, Assets.arrowRight});
		assets.put("sword", new TextureRegion[]{Assets.sword, Assets.sword});
		assets.put("commonDoor", new TextureRegion[]{Assets.commonDoor, Assets.commonDoor});
		assets.put("village", new TextureRegion[]{Assets.village, Assets.village});
		assets.put("castle", new TextureRegion[]{Assets.castle, Assets.castle});
		assets.put("pathToRegion_up", new TextureRegion[]{Assets.pathToRegionUp, Assets.pathToRegionUp});
		assets.put("pathToRegion_down", new TextureRegion[]{Assets.pathToRegionDown, Assets.pathToRegionDown});
		assets.put("pathToRegion_left", new TextureRegion[]{Assets.pathToRegionLeft, Assets.pathToRegionLeft});
		assets.put("pathToRegion_right", new TextureRegion[]{Assets.pathToRegionRight, Assets.pathToRegionRight});
		up = createButton(0, +1, "_up", assets);
		down = createButton(0, -1, "_down", assets);
		left = createButton(-1, 0, "_left", assets);
		right = createButton(+1, 0, "_right", assets);
		
		OnClickManager centerCameraActionManager = new OnClickManager(
			CENTER_CAMERA, 
			new CenterCameraOnClickListener(world, player),
			Assets.center, Assets.center);
		center = new PadButton(centerCameraActionManager);
		update();
		
		// Ajout � la table
		//debug();
		add();
		add(up).size(buttonSize, buttonSize);
		row();
		add(left).size(buttonSize, buttonSize);
		add(center).height((int)(buttonSize * 0.75)).width((int)(buttonSize * 0.75)).center();
		add(right).size(buttonSize, buttonSize);
		row();
		add();
		add(down).size(buttonSize, buttonSize);
		pack();
	}
	
	private PadButton createButton(int offsetX, int offsetY, String suffixAssetKey, Map<String, TextureRegion[]> assets) {
		
		TextureRegion[] textures = assets.get("cross");
		OnClickManager noActionActionManager = new OnClickManager(
			NONE,
			new NoActionOnClickListener(),
			textures[0], textures[1]);
		
		textures = assets.get("arrow" + suffixAssetKey);
		OnClickManager moveActionManager = new OnClickManager(
			MOVE, 
			new MoveOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		textures = assets.get("sword");
		OnClickManager attackActionManager = new OnClickManager(
			ATTACK, 
			new AttackOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		textures = assets.get("commonDoor");
		OnClickManager openDoorActionManager = new OnClickManager(
			CROSS_DOOR,
			new CrossPathOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		textures = assets.get("village");
		OnClickManager enterVillageActionManager = new OnClickManager(
			ENTER_VILLAGE, 
			new EnterVillageOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		textures = assets.get("castle");
		OnClickManager enterCastleActionManager = new OnClickManager(
			ENTER_CASTLE,
			new EnterCastleOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		textures = assets.get("pathToRegion" + suffixAssetKey);
		OnClickManager crossPathActionManager = new OnClickManager(
			CROSS_PATH, 
			new CrossPathOnClickListener(map, player, offsetX, offsetY),
			textures[0], textures[1]);
		
		return new PadButton(
			noActionActionManager, 
			moveActionManager, 
			attackActionManager, 
			openDoorActionManager,
			enterVillageActionManager,
			enterCastleActionManager,
			crossPathActionManager);
	}

	/**
	 * Met � jour les images du pad en fonction de l'environnement du joueur
	 */
	public void update() {
		int playerX = player.getWorldX();
		int playerY = player.getWorldY();
		
		updateButton(up, playerX, playerY + 1);
		updateButton(down, playerX, playerY - 1);
		updateButton(left, playerX - 1, playerY);
		updateButton(right, playerX + 1, playerY);
	}

	private void updateButton(PadButton button, int targetX, int targetY) {
		WorldActor target = map.getTopElementAt(targetX, targetY);
		if (target instanceof Damageable) {
			button.setCurrentManager(ATTACK);
		} else if (target instanceof Ground) {
			button.setCurrentManager(MOVE);
		} else if (target instanceof PathToRegion
		&& ((PathToRegion)target).isCrossable()) {
			if (target instanceof Door) {
				button.setCurrentManager(CROSS_DOOR);
			} else {
				button.setCurrentManager(CROSS_PATH);
			}
		} else if (target instanceof Village) {
			button.setCurrentManager(ENTER_VILLAGE);
		} else if (target instanceof Castle) {
			button.setCurrentManager(ENTER_CASTLE);
		}else {
			button.setCurrentManager(NONE);
		}
	}

}
