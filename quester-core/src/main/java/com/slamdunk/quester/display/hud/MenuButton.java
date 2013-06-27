package com.slamdunk.quester.display.hud;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;

/**
 * Gère l'affichage d'un menu déroulant de boutons
 */
public class MenuButton {
	private int itemsBeingAnimated;
	private Button menuBtn;
	private final ButtonStyle openMenuBtnStyle;
	private final ButtonStyle closeMenuBtnStyle;
	
	private List<Button> menuItems;
	private boolean isMenuVisible;
	
	public MenuButton() {
		// Création du bouton principal
		closeMenuBtnStyle = new ButtonStyle();
		closeMenuBtnStyle.up = new TextureRegionDrawable(Assets.menu_close);
		closeMenuBtnStyle.down = new TextureRegionDrawable(Assets.menu_close);
		closeMenuBtnStyle.pressedOffsetY = 1.0f;
		openMenuBtnStyle = new ButtonStyle();
		openMenuBtnStyle.up = new TextureRegionDrawable(Assets.menu_open);
		openMenuBtnStyle.down = new TextureRegionDrawable(Assets.menu_open);
		openMenuBtnStyle.pressedOffsetY = 1.0f;
		menuBtn = new Button(openMenuBtnStyle);
		menuBtn.addListener(new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				isMenuVisible = !isMenuVisible;
				if (isMenuVisible) {
					showMenu();
				} else {
					hideMenu();
				}
			};
		});
		
		// Création du bouton d'affichage de la minimap
		ButtonStyle mapBtnStyle = new ButtonStyle();
		mapBtnStyle.up = new TextureRegionDrawable(Assets.map);
		mapBtnStyle.down = new TextureRegionDrawable(Assets.map);
		mapBtnStyle.pressedOffsetY = 1.0f;
		Button displayMap = new Button(mapBtnStyle);
		displayMap.addListener(new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getScreen().getHUDRenderer().getToggleMinimapVisibility();
				hideMenu();
			};
		});
		
		// Création du bouton de centrage de la caméra sur le joueur
		ButtonStyle centerBtnStyle = new ButtonStyle();
		centerBtnStyle.up = new TextureRegionDrawable(Assets.center);
		centerBtnStyle.down = new TextureRegionDrawable(Assets.center);
		centerBtnStyle.pressedOffsetY = 1.0f;
		Button centerCamera = new Button(centerBtnStyle);
		centerCamera.addListener(new ClickListener(){
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				GameControler.instance.getPlayer().getActor().centerCameraOnSelf();
				hideMenu();
			};
		});
		
		menuItems = new ArrayList<Button>();
		menuItems.add(displayMap);
		menuItems.add(centerCamera);
	}

	public void act(float delta) {
		if (itemsBeingAnimated != 0) {
			for (Button menuItem : menuItems) {
				menuItem.act(delta);
			}
		}		
	}

	public Button getMenuBtn() {
		return menuBtn;
	}
	
	/**
	 * Cache tous les boutons et les aligne sur le menu
	 */
	public void prepareMenu() {
		final float x = menuBtn.getX();
		final float y = menuBtn.getY();
		final float size = Config.asFloat("menu.itemSize", 48f);
		final Group parent = menuBtn.getParent();
		for (Button menuItem : menuItems) {
			menuItem.setVisible(false);
			menuItem.setPosition(x, y);
			menuItem.setSize(size, size);
			parent.addActor(menuItem);
			
		}
	}

	public void setMenuBtn(Button menuBtn) {
		this.menuBtn = menuBtn;
	}
	
	public void showMenu() {
		if (itemsBeingAnimated != 0) {
			return;
		}
		itemsBeingAnimated = menuItems.size();
		
		menuBtn.setStyle(closeMenuBtnStyle);
		
		final float menuAnimationSpeed = Config.asFloat("menu.speed", 0.3f);
		final float padding = Config.asFloat("menu.itemPadding", 1.0f);
		float curItemX = menuBtn.getX();
		float curItemY = menuBtn.getY();
		for (Button menuItem : menuItems) {
			curItemY = curItemY - padding - menuItem.getHeight();
			
			menuItem.setVisible(true);
			menuItem.addAction(Actions.sequence(
				Actions.moveTo(
					curItemX,
					curItemY,
					menuAnimationSpeed),
				new Action() {
					@Override
					public boolean act(float arg0) {
						itemsBeingAnimated--;
						return true;
					}
				}
			));
		}
	}
	
	public void hideMenu() {
		if (itemsBeingAnimated != 0) {
			return;
		}
		itemsBeingAnimated = menuItems.size();
		
		menuBtn.setStyle(openMenuBtnStyle);
		
		final float menuAnimationSpeed = Config.asFloat("menu.speed", 1.0f);
		final float menuBtnX = menuBtn.getX();
		final float menuBtnY = menuBtn.getY();
		for (Button menuItem : menuItems) {
			menuItem.addAction(Actions.sequence(
				Actions.moveTo(
					menuBtnX,
					menuBtnY,
					menuAnimationSpeed),
				new Action() {
					@Override
					public boolean act(float arg0) {
						getActor().setVisible(false);
						itemsBeingAnimated--;
						return true;
					}
				}
			));
		}
	}
}
