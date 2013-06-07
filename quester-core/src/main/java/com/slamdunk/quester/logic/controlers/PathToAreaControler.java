package com.slamdunk.quester.logic.controlers;

import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.model.map.GameMap;

public class PathToAreaControler extends WorldElementControler {

	public PathToAreaControler(PathData data, PathToAreaActor actor) {
		super(data, actor);
	}

	@Override
	public PathData getData() {
		return (PathData)data;
	}
	
	/**
	 * Franchit le chemin
	 */
	public boolean open() {
		PathData pathData = getData();
		if (!pathData.isCrossable) {
			return false;
		}
		
		DisplayData data = new DisplayData();
		data.regionX = pathData.toX;
		data.regionY = pathData.toY;
		
		GameMap map = GameControler.instance.getMapScreen();
		
		// La porte est sur le mur du haut, le perso appara�tra donc dans la prochaine pi�ce en bas
		if (actor.getWorldY() == map.getMapHeight() - 1) {
			data.playerX = actor.getWorldX();
			data.playerY = 0;
		}
		// La porte est sur le mur du bas, le perso appara�tra donc dans la prochaine pi�ce en haut
		else if (actor.getWorldY() == 0) {
			data.playerX = actor.getWorldX();
			data.playerY = map.getMapHeight() - 1;
		}
		// La porte est sur le mur de gauche, le perso appara�tra donc dans la prochaine pi�ce � droite
		else if (actor.getWorldX() == 0) {
			data.playerX =  map.getMapWidth() - 1;
			data.playerY = actor.getWorldY();
		}
		// La porte est sur le mur de droite, le perso appara�tra donc dans la prochaine pi�ce � gauche
		else if (actor.getWorldX() == map.getMapWidth() - 1) {
			data.playerX =  0;
			data.playerY = actor.getWorldY();
		}
		GameControler.instance.displayWorld(data);
		return true;
	}
}
