package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYER_CHARACTERS;
import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYER_OVERLAY;
import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;
import static com.slamdunk.quester.logic.ai.QuesterActions.MOVE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.display.actors.ContextMenuActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.MapCell;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.ContextMenuData;
import com.slamdunk.quester.utils.Assets;

public class ContextMenuControler extends WorldElementControler {
	public static ContextMenuControler openedMenu;
	
	private List<ContextMenuActor> menuItemsActors;
	private DarknessControler darknessControler;
	private WorldElementControler targetControler;
	private MapLayer overlay;

	public ContextMenuControler(ContextMenuData data, DarknessControler darknessControler) {
		super(data);
		this.darknessControler = darknessControler;
		createMenuItems();
	}
	
	private void createMenuItems() {
		// Détermine la présence d'un ennemi
		boolean containsDamageable = false;
		ContextMenuData contextMenuData = (ContextMenuData)data;
		MapLayer charactersLayer = GameControler.instance.getMapScreen().getLayer(LAYER_CHARACTERS);
		MapCell cell = charactersLayer.getCell(contextMenuData.sourceX, contextMenuData.sourceY);
		WorldElementControler targetControler = null;
		if (cell != null) {
			WorldElementActor target = (WorldElementActor)cell.getActor();
			targetControler = target.getControler();
			containsDamageable = targetControler instanceof Damageable;
		}
		
		menuItemsActors = new ArrayList<ContextMenuActor>();
		// On peut mettre une torche s'il n'y a pas d'ennemi
		if (!containsDamageable) {
			menuItemsActors.add(new ContextMenuActor(Assets.menu_torch, PLACE_TORCH));
		}
		// On peut se déplacer si la zone est éclairée
		if (darknessControler.getData().torchCount > 0) {
			menuItemsActors.add(new ContextMenuActor(Assets.menu_move, MOVE));
		}
		// On peut attaquer s'il y a un truc qu'on peut détruire
		if (containsDamageable) {
			menuItemsActors.add(new ContextMenuActor(Assets.sword, ATTACK));
			this.targetControler = targetControler;
		}
	}

	public void layoutItems() {
		openedMenu = this;
		
		MapScreen mapScreen = GameControler.instance.getMapScreen();
		overlay = mapScreen.getLayer(LAYER_OVERLAY);
		
		// Calcul du centre du menu contextuel
		ContextMenuData contextMenuData = (ContextMenuData)data;
		float centerX = contextMenuData.sourceX * mapScreen.getCellWidth();
		float centerY = contextMenuData.sourceY * mapScreen.getCellHeight();
		
		final int menuItemCount = menuItemsActors.size();
		if (menuItemCount == 1) {
			layoutItem(menuItemsActors.get(0), centerX, centerY);
		} else {
			// Chaque acteur sera espacé également
			double marginAngle = 2 * Math.PI / menuItemCount;
			
			// Détermine la position de chaque acteur
			for (int index = 0; index < menuItemsActors.size(); index++) {
				// Ajoute ce contrôleur pour recevoir les clicks
				WorldElementActor curActor = menuItemsActors.get(index);
				curActor.setControler(this);
	
				double curAngle = marginAngle * index;
				float itemCenterX = (float)(centerX + contextMenuData.radius * Math.cos(curAngle));
				float itemCenterY = (float)(centerY + contextMenuData.radius * Math.sin(curAngle));
				
				layoutItem(curActor, itemCenterX, itemCenterY);
			}
		}
	}

	private void layoutItem(WorldElementActor itemActor, float itemCenterX, float itemCenterY) {
		itemActor.setControler(this);
		itemActor.setX(itemCenterX - itemActor.getWidth() / 2);
		itemActor.setY(itemCenterY - itemActor.getHeight() / 2);
		overlay.addActor(itemActor);
	}

	public void onMenuItemClicked(QuesterActions action) {
		ContextMenuData contextMenuData = (ContextMenuData)data;
		
		// Effectue l'action demandée
    	PlayerControler player = GameControler.instance.getPlayer();
    	switch (action) {
	    	case ATTACK:
				player.attack(targetControler);
				break;
    		case MOVE:
    			player.moveTo(contextMenuData.sourceX, contextMenuData.sourceY);
    			break;
    		case PLACE_TORCH:
    			player.placeTorch(darknessControler);
    			break;
    		default:
    			// Rien à faire : fermeture du menu
    	}
    	
    	// Ferme le menu
    	closeMenu();
	}
	
	public void closeMenu() {
		openedMenu = null;
		for (WorldElementActor actor : menuItemsActors) {
    		overlay.removeActor(actor);
    	}
	}
}
