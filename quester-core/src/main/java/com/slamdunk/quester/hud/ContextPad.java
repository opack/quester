package com.slamdunk.quester.hud;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.Damageable;
import com.slamdunk.quester.actors.Ground;
import com.slamdunk.quester.actors.WorldElement;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.core.GameWorld;

public class ContextPad extends Table {
	private GameWorld world;
	private Character player;
	private GameMap map;
	
	private final Button up;
	private final Button down;
	private final Button left;
	private final Button right;
	
	private final SwitchActionOnClickListener upListener;
	private final SwitchActionOnClickListener downListener;
	private final SwitchActionOnClickListener leftListener;
	private final SwitchActionOnClickListener rightListener;

	public ContextPad(int buttonSize, GameWorld world) {
		this.world = world;
		this.map = world.getMap();
		this.player = world.getPlayer();
		
		// Création des listeners
		upListener = new SwitchActionOnClickListener();
		downListener = new SwitchActionOnClickListener();
		leftListener = new SwitchActionOnClickListener();
		rightListener = new SwitchActionOnClickListener();
		
		// Création des boutons
		up = createButton(
			upListener,
			0, +1,
			Assets.arrowUp, Assets.arrowUp,
			Assets.sword, Assets.sword,
			Assets.arrowUp, Assets.arrowUp);
		down = createButton(
			downListener,
			0, -1,
			Assets.arrowDown, Assets.arrowDown,
			Assets.sword, Assets.sword,
			Assets.arrowDown, Assets.arrowDown);
		left = createButton(
			leftListener,
			-1, 0,
			Assets.arrowLeft, Assets.arrowLeft,
			Assets.sword, Assets.sword,
			Assets.arrowLeft, Assets.arrowLeft);
		right = createButton(
			rightListener,
			+1, 0,
			Assets.arrowRight, Assets.arrowRight,
			Assets.sword, Assets.sword,
			Assets.arrowRight, Assets.arrowRight);
		updatePad();
		
		// Ajout à la table
		//debug();
		add();
		add(up).height(buttonSize).width(buttonSize);
		row();
		add(left).height(buttonSize).width(buttonSize);
		add();
		add(right).height(buttonSize).width(buttonSize);
		row();
		add();
		add(down).height(buttonSize).width(buttonSize);
		pack();
	}

	/**
	 * Met à jour les images du pad en fonction de l'environnement du joueur
	 */
	protected void updatePad() {
		int playerX = player.getWorldX();
		int playerY = player.getWorldY();
		
		updateButton(up, upListener, playerX, playerY + 1);
		updateButton(down, downListener, playerX, playerY - 1);
		updateButton(left, leftListener, playerX - 1, playerY);
		updateButton(right, rightListener, playerX - 1, playerY);
	}

	private void updateButton(Button button, SwitchActionOnClickListener listener, int targetX, int targetY) {
		WorldElement target = map.getTopElementAt(targetX, targetY);
		if (target instanceof Damageable) {
			listener.setCurrentListener(AttackOnClickListener.class);
		} else if (target instanceof Ground) {
			listener.setCurrentListener(MoveOnClickListener.class);
		} else {
			listener.setCurrentListener(NoActionOnClickListener.class);
		}
		button.setStyle(listener.getCurrentListener().getStyle());
	}

	private Button createButton(
			SwitchActionOnClickListener switchListener,
			int offsetX, int offsetY,
			TextureRegion imgMoveUp, TextureRegion imgMoveDown,
			TextureRegion imgAttackUp, TextureRegion imgAttackDown,
			TextureRegion imgNoActionUp, TextureRegion imgNoActionDown) {
		OnClickManager attackActionManager = new OnClickManager(new AttackOnClickListener(map, player, offsetX, offsetY));
		OnClickManager moveActionManager = new OnClickManager(new MoveOnClickListener(map, player, offsetX, offsetY));
		OnClickManager noActionActionManager = new OnClickManager(new NoActionOnClickListener(map, player, offsetX, offsetY));
		
		Button button = new Button(moveBtnManager.getStyle());
		button.addListener(switchListener);
		return button;
	}
}
