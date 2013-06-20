package com.slamdunk.quester.logic.ai;

import com.slamdunk.quester.logic.controlers.PathToAreaControler;
import com.slamdunk.quester.logic.controlers.PlayerControler;

/**
 * Traverse le chemin indiqué.
 */
public class CrossPathAction implements AIAction {
	private PlayerControler player;
	private PathToAreaControler path;
	
	public CrossPathAction(PlayerControler player, PathToAreaControler path) {
		this.player = player;
		this.path = path;
	}
	
	@Override
	public void act() {
		// Ouverture de la porte
		path.open();

		// L'action est consommée : réalisation de la prochaine action
		player.getAI().nextAction();
	}

	@Override
	public QuesterActions getAction() {
		return QuesterActions.CROSS_PATH;
	}

}
