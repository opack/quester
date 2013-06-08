package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.utils.Assets;

public class DungeonDoorControler extends PathToAreaControler {
	public DungeonDoorControler(PathData data, PathToAreaActor actor) {
		super(data, actor);
		// Choix d'un son au hasard pour cette porte
		setOpenSound(Assets.doorOpenSounds[MathUtils.random(Assets.doorOpenSounds.length - 1)]);
	}
	
	/**
	 * Ouvre la porte et effectue l'action adéquate en fonction
	 * de ce qui se trouve derrière (une autre pièce, sortie du
	 * donjon...).
	 */
	public boolean open() {
		PathData data = getData();
		switch (data.element) {
			case DUNGEON_EXIT_DOOR:
				GameControler.instance.exit();
				return true;
			default:
				return super.open();
		}
	}
}
