package com.slamdunk.quester.display.hud.contextpad;

import static com.slamdunk.quester.ia.Action.ATTACK;
import static com.slamdunk.quester.ia.Action.CENTER_CAMERA;
import static com.slamdunk.quester.ia.Action.ENTER_VILLAGE;
import static com.slamdunk.quester.ia.Action.MOVE;
import static com.slamdunk.quester.ia.Action.NONE;
import static com.slamdunk.quester.ia.Action.OPEN_DOOR;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.Damageable;
import com.slamdunk.quester.display.actors.Door;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.Village;
import com.slamdunk.quester.display.actors.WorldActor;

public class ContextPad extends Table {
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
			Assets.commonDoor, Assets.commonDoor,
			Assets.village, Assets.village);
		down = createButton(
			0, -1,
			Assets.cross, Assets.cross,
			Assets.arrowDown, Assets.arrowDown,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor,
			Assets.village, Assets.village);
		left = createButton(
			-1, 0,
			Assets.cross, Assets.cross,
			Assets.arrowLeft, Assets.arrowLeft,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor,
			Assets.village, Assets.village);
		right = createButton(
			+1, 0,
			Assets.cross, Assets.cross,
			Assets.arrowRight, Assets.arrowRight,
			Assets.sword, Assets.sword,
			Assets.commonDoor, Assets.commonDoor,
			Assets.village, Assets.village);
		OnClickManager centerCameraActionManager = new OnClickManager(
			CENTER_CAMERA, 
			new CenterCameraOnClickListener(world, player),
			Assets.center, Assets.center);
		center = new PadButton(centerCameraActionManager);
		update();
		
		// Ajout à la table
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
	
	private PadButton createButton(
			int offsetX, int offsetY,
			TextureRegion imgNoActionUp, TextureRegion imgNoActionDown,
			TextureRegion imgMoveUp, TextureRegion imgMoveDown,
			TextureRegion imgAttackUp, TextureRegion imgAttackDown,
			TextureRegion imgOpenDoorUp, TextureRegion imgOpenDoorDown,
			TextureRegion imgEnterVillageUp, TextureRegion imgEnterVillageDown) {
		
		OnClickManager noActionActionManager = new OnClickManager(
			NONE,
			new NoActionOnClickListener(),
			imgNoActionUp, imgNoActionDown);
		OnClickManager moveActionManager = new OnClickManager(
			MOVE, 
			new MoveOnClickListener(map, player, offsetX, offsetY),
			imgMoveUp, imgMoveDown);
		OnClickManager attackActionManager = new OnClickManager(
			ATTACK, 
			new AttackOnClickListener(map, player, offsetX, offsetY),
			imgAttackUp, imgAttackDown);
		OnClickManager openDoorActionManager = new OnClickManager(
			OPEN_DOOR, 
			new OpenDoorOnClickListener(map, player, offsetX, offsetY),
			imgOpenDoorUp, imgOpenDoorDown);
		OnClickManager enterVillageActionManager = new OnClickManager(
			ENTER_VILLAGE, 
			new EnterVillageOnClickListener(map, player, offsetX, offsetY),
			imgEnterVillageUp, imgEnterVillageDown);
		
		return new PadButton(
			noActionActionManager, 
			moveActionManager, 
			attackActionManager, 
			openDoorActionManager,
			enterVillageActionManager);
	}

	/**
	 * Met à jour les images du pad en fonction de l'environnement du joueur
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
		} else if (target instanceof Door
				&& ((Door)target).isOpenable()) {
			button.setCurrentManager(OPEN_DOOR);
		} else if (target instanceof Village) {
			button.setCurrentManager(ENTER_VILLAGE);
		} else {
			button.setCurrentManager(NONE);
		}
	}

}
