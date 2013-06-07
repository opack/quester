package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.model.data.PathData;

public class DungeonDoorControler extends PathToAreaControler {
	public DungeonDoorControler(PathData data, PathToAreaActor actor) {
		super(data, actor);
	}
	
	/**
	 * Ouvre la porte et effectue l'action ad�quate en fonction
	 * de ce qui se trouve derri�re (une autre pi�ce, sortie du
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
