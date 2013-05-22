package com.slamdunk.quester.hud;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.Damageable;
import com.slamdunk.quester.actors.Door;
import com.slamdunk.quester.actors.Ground;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.core.GameWorld;

public class ContextPad extends Table {
	private static final int ACTION_NONE = 0;
	private static final int ACTION_MOVE = 1;
	private static final int ACTION_ATTACK = 2;
	private static final int ACTION_OPEN_DOOR = 3;
	private static final int ACTION_CENTER_CAMERA = 4;
	
	private Character player;
	private GameMap map;
	
	private final PadButton up;
	private final PadButton down;
	private final PadButton left;
	private final PadButton right;
	private final PadButton center;
	
	public ContextPad(int buttonSize, GameWorld world) {
		this.map = world.getMap();
		this.player = world.getPlayer();
		
		// Création des boutons
		up = createButton(
			0, +1,
			Assets.cross, Assets.cross,
			Assets.arrowUp, Assets.arrowUp,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor);
		down = createButton(
			0, -1,
			Assets.cross, Assets.cross,
			Assets.arrowDown, Assets.arrowDown,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor);
		left = createButton(
			-1, 0,
			Assets.cross, Assets.cross,
			Assets.arrowLeft, Assets.arrowLeft,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor);
		right = createButton(
			+1, 0,
			Assets.cross, Assets.cross,
			Assets.arrowRight, Assets.arrowRight,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor);
		OnClickManager centerCameraActionManager = new OnClickManager(
			ACTION_CENTER_CAMERA, 
			new CenterCameraOnClickListener(world, player),
			Assets.center, Assets.center);
		center = new PadButton(centerCameraActionManager);
		updatePad();
		
		// Ajout à la table
		//debug();
		add();
		add(up).height(buttonSize).width(buttonSize);
		row();
		add(left).height(buttonSize).width(buttonSize);
		add(center).height((int)(buttonSize * 0.75)).width((int)(buttonSize * 0.75)).center();
		add(right).height(buttonSize).width(buttonSize);
		row();
		add();
		add(down).height(buttonSize).width(buttonSize);
		pack();
	}
	
	private PadButton createButton(
			int offsetX, int offsetY,
			TextureRegion imgNoActionUp, TextureRegion imgNoActionDown,
			TextureRegion imgMoveUp, TextureRegion imgMoveDown,
			TextureRegion imgAttackUp, TextureRegion imgAttackDown,
			TextureRegion imgOpenDoorUp, TextureRegion imgOpenDoorDown) {
		
		OnClickManager noActionActionManager = new OnClickManager(
			ACTION_NONE,
			new NoActionOnClickListener(),
			imgNoActionUp, imgNoActionDown);
		OnClickManager moveActionManager = new OnClickManager(
			ACTION_MOVE, 
			new MoveOnClickListener(map, player, offsetX, offsetY),
			imgMoveUp, imgMoveDown);
		OnClickManager attackActionManager = new OnClickManager(
			ACTION_ATTACK, 
			new AttackOnClickListener(map, player, offsetX, offsetY),
			imgAttackUp, imgAttackDown);
		OnClickManager openDoorActionManager = new OnClickManager(
			ACTION_OPEN_DOOR, 
			new OpenDoorOnClickListener(map, player, offsetX, offsetY),
			imgOpenDoorUp, imgOpenDoorDown);
		
		return new PadButton(
			noActionActionManager, 
			moveActionManager, 
			attackActionManager, 
			openDoorActionManager);
	}

	/**
	 * Met à jour les images du pad en fonction de l'environnement du joueur
	 */
	public void updatePad() {
		int playerX = player.getWorldX();
		int playerY = player.getWorldY();
		
		updateButton(up, playerX, playerY + 1);
		updateButton(down, playerX, playerY - 1);
		updateButton(left, playerX - 1, playerY);
		updateButton(right, playerX + 1, playerY);
	}

	private void updateButton(PadButton button, int targetX, int targetY) {
		WorldElement target = map.getTopElementAt(targetX, targetY);
		if (target instanceof Damageable) {
			button.setCurrentManager(ACTION_ATTACK);
		} else if (target instanceof Ground) {
			button.setCurrentManager(ACTION_MOVE);
		} else if (target instanceof Door
				&& ((Door)target).isOpenable()) {
			button.setCurrentManager(ACTION_OPEN_DOOR);
		} else {
			button.setCurrentManager(ACTION_NONE);
		}
	}

}
