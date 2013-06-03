package com.slamdunk.quester.core;

import java.util.Collections;
import java.util.List;

import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;

public class QuesterGame implements GameWorld, CharacterListener {
	
	public static final QuesterGame instance = new QuesterGame();

	private MapScreen mapScreen;
	private Point currentArea;
	
	private Player player;
	private int curCharacterPlaying;
	private List<WorldActor> characters;

	@Override
	public MapScreen getMapScreen() {
		return mapScreen;
	}
	
	public void setMapScreen(MapScreen mapScreen) {
		this.mapScreen = mapScreen;
		this.characters = mapScreen.getCharacters();
	}

	@Override
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
		// Mise à jour du pad et de la minimap
		mapScreen.updateHUD(currentArea);
     	
        // Au tour du prochain de jouer !
        curCharacterPlaying++;
        
        // Quand tout le monde a joué son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que ça ait changé.
        if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
	}

	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		mapScreen.displayWorld(data);
		
		// Réordonne la liste d'ordre de jeu
        initCharacterOrder();
	}

	public WorldActor getCurrentCharacter() {
		return characters.get(curCharacterPlaying);
	}
	
	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAttackPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterDeath(Character character) {
		// On recherche l'indice du personnage à supprimer dans la liste
		int index = characters.indexOf(character);
		// Si le perso supprimé devait jouer après le joueur actuel (index > curCharacterPlaying),
		// alors l'ordre de jeu n'est pas impacté.
		// Si le perso supprimé devait jouer avant (index < curCharacterPlaying), alors l'ordre de
		// jeu est impacté car les indices changent. Si on ne fait rien, un joueur risque de passer
		// son tour.
		// Si le perso supprimé est le joueur actuel (index = curCharacterPlaying), alors le
		// raisonnement est le même
		if (index <= curCharacterPlaying) {
			curCharacterPlaying --;
		}
		
		// Suppression du character dans la liste et de la pièce
		mapScreen.removeElement(character);
		characters.remove(character);
		MapArea area = mapScreen.getArea(currentArea);
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(character.getElementData());
		}
		
		// Si c'est le joueur qui est mort, le jeu s'achève
		if (character.equals(player)) {
			mapScreen.showMessage("Bouh ! T'es mort !");
		}
	}

	public void initCharacterOrder() {
		curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}
}
