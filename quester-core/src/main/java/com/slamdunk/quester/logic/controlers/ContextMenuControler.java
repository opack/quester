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
		menuItemsActors = new ArrayList<ContextMenuActor>();
		menuItemsActors.add(new ContextMenuActor(Assets.menu_torch, PLACE_TORCH));
		if (darknessControler.getData().torchCount > 0) {
			menuItemsActors.add(new ContextMenuActor(Assets.menu_move, MOVE));
		} else {
			// S'il y a un ennemi, on permet d'attaquer
			ContextMenuData contextMenuData = (ContextMenuData)data;
			MapLayer charactersLayer = GameControler.instance.getMapScreen().getLayer(LAYER_CHARACTERS);
			MapCell cell = charactersLayer.getCell(contextMenuData.sourceX, contextMenuData.sourceY);
			if (cell != null) {
				WorldElementActor target = (WorldElementActor)cell.getActor();
				WorldElementControler targetControler = target.getControler();
				if (targetControler instanceof Damageable) {
					menuItemsActors.add(new ContextMenuActor(Assets.sword, ATTACK));
					this.targetControler = targetControler;
				}
			}
		}
	}

	public void layoutItems() {
		openedMenu = this;
		
		MapScreen mapScreen = GameControler.instance.getMapScreen();
		
		// Calcul du centre du menu contextuel
		ContextMenuData contextMenuData = (ContextMenuData)data;
		float centerX = contextMenuData.sourceX * mapScreen.getCellWidth();
		float centerY = contextMenuData.sourceY * mapScreen.getCellHeight();
		
		// Chaque acteur sera espacé également
		double marginAngle = 2 * Math.PI / menuItemsActors.size();
		
		// Détermine la position de chaque acteur
		overlay = mapScreen.getLayer(LAYER_OVERLAY);
		for (int index = 0; index < menuItemsActors.size(); index++) {
			// Ajoute ce contrôleur pour recevoir les clicks
			WorldElementActor curActor = menuItemsActors.get(index);
			curActor.setControler(this);

			double curAngle = marginAngle * index;
			float itemCenterX = (float)(centerX + contextMenuData.radius * Math.cos(curAngle));
			float itemCenterY = (float)(centerY + contextMenuData.radius * Math.sin(curAngle));
			
			curActor.setX(itemCenterX - curActor.getWidth() / 2);
			curActor.setY(itemCenterY - curActor.getHeight() / 2);
			
			// Ajoute cet acteur à l'écran
			overlay.addActor(curActor);
		}
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
