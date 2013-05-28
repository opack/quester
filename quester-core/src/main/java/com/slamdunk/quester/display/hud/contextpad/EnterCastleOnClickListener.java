package com.slamdunk.quester.display.hud.contextpad;

import com.slamdunk.quester.core.GameMap;
import com.slamdunk.quester.display.actors.Castle;
import com.slamdunk.quester.display.actors.Player;

public class EnterCastleOnClickListener extends ActionOnClickListener {
	public EnterCastleOnClickListener(GameMap map, Player player, int offsetX, int offsetY) {
		super(map, player, offsetX, offsetY);
	}

	@Override
	public void onClick() {
		// Calcul de la position du château
		int castleX = player.getWorldX() + offsetX;
		int castleY = player.getWorldY() + offsetY;
		
		// On déplace le joueur sur cette position (pour qu'il se retrouve là lorsqu'il ressortira)
		// puis il entre dans le donjon
		Castle castle = (Castle)map.getTopElementAt(castleX, castleY);
		player.enterCastle(castle);
	}
}
