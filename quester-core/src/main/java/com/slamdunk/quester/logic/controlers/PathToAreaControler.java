package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.audio.Sound;
import com.slamdunk.quester.display.actors.PathToAreaActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.model.data.PathData;
import com.slamdunk.quester.utils.Assets;

public class PathToAreaControler extends WorldElementControler {
	private Sound openSound;

	public PathToAreaControler(PathData data, PathToAreaActor actor) {
		super(data, actor);
	}

	@Override
	public PathData getData() {
		return (PathData)data;
	}
	
	public Sound getOpenSound() {
		return openSound;
	}

	/**
	 * Franchit le chemin
	 */
	public boolean open() {
		PathData pathData = getData();
		if (!pathData.isCrossable) {
			return false;
		}
		
		// Si un son a été définit, on le joue
		if (openSound != null) {
			Assets.playSound(openSound);
		}
		
		// A présent, on affiche la nouvelle carte
		DisplayData data = new DisplayData();
		data.regionX = pathData.toX;
		data.regionY = pathData.toY;
		
		ActorMap map = GameControler.instance.getScreen().getMap();
		
		// La porte est sur le mur du haut, le perso apparaîtra donc dans la prochaine pièce en bas
		if (actor.getWorldY() == map.getMapHeight() - 1) {
			data.playerX = actor.getWorldX();
			data.playerY = 0;
		}
		// La porte est sur le mur du bas, le perso apparaîtra donc dans la prochaine pièce en haut
		else if (actor.getWorldY() == 0) {
			data.playerX = actor.getWorldX();
			data.playerY = map.getMapHeight() - 1;
		}
		// La porte est sur le mur de gauche, le perso apparaîtra donc dans la prochaine pièce à droite
		else if (actor.getWorldX() == 0) {
			data.playerX =  map.getMapWidth() - 1;
			data.playerY = actor.getWorldY();
		}
		// La porte est sur le mur de droite, le perso apparaîtra donc dans la prochaine pièce à gauche
		else if (actor.getWorldX() == map.getMapWidth() - 1) {
			data.playerX =  0;
			data.playerY = actor.getWorldY();
		}
		GameControler.instance.displayWorld(data);
		return true;
	}

	public void setOpenSound(Sound openSound) {
		this.openSound = openSound;
	}
}
