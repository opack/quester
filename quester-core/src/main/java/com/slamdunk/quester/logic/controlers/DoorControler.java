package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.model.data.PathData;

public class DoorControler extends PathToAreaControler {
	public DoorControler(PathData data, PathToAreaActor actor) {
		super(data, actor);
	}
	
	/**
	 * Ouvre la porte et effectue l'action ad�quate en fonction
	 * de ce qui se trouve derri�re (une autre pi�ce, sortie du
	 * donjon...).
	 */
	public void open() {
		PathData data = getData();
		switch (data.element) {
			case DUNGEON_EXIT_DOOR:
				GameControler.instance.exit();
				break;
			case COMMON_DOOR:
				super.open();
				break;
			default:
				break;
		}
	}
}
