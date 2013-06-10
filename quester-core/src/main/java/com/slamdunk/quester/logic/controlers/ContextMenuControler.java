package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.display.screens.AbstractMapScreen.LAYER_OVERLAY;
import static com.slamdunk.quester.logic.ai.QuesterActions.MOVE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.quester.display.actors.ContextMenuActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.map.MapLayer;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.model.data.ContextMenuData;
import com.slamdunk.quester.utils.Assets;

public class ContextMenuControler extends WorldElementControler {
	private List<ContextMenuActor> menuItemsActors;
	private DarknessControler darknessControler;
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
		}
	}

	public void layoutItems() {
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
    		case PLACE_TORCH:
    			player.placeTorch(darknessControler);
    			break;
    		case MOVE:
    			player.moveTo(contextMenuData.sourceX, contextMenuData.sourceY);
    			break;
    	}
    	
    	// Supprime le menu
    	for (WorldElementActor actor : menuItemsActors) {
    		overlay.removeActor(actor);
    	}
	}
}
