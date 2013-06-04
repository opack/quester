package com.slamdunk.quester.core;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.Collections;
import java.util.List;

import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.map.ElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.PlayerData;
import com.slamdunk.quester.model.points.Point;

public class QuesterGame implements GameWorld, CharacterListener {
	
	public static final QuesterGame instance = new QuesterGame();

	private MapScreen mapScreen;
	private Point currentArea;
	
	private PlayerData playerData;
	private int curCharacterPlaying;
	private List<WorldActor> characters;

	// DBG En attendant de créer les objets de logic, on conserve une référence vers l'Actor
	private Player player;
	
	private QuesterGame() {
		currentArea = new Point(-1, -1);
	}

	@Override
	public void createPlayerData(int hp, int att) {
		playerData = new PlayerData(hp, att);
		playerData.speed = 2;
	}
	
	
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
	public PlayerData getPlayerData() {
		return playerData;
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
		ElementData deadCharacterData = character.getElementData();
		mapScreen.removeElement(character);
		characters.remove(character);
		MapArea area = mapScreen.getArea(currentArea);
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'achève
		if (deadCharacterData.element == PLAYER) {
			mapScreen.showMessage("Bouh ! T'es mort !");
		}
	}

	public void initCharacterOrder() {
		curCharacterPlaying = characters.size();
        endCurrentPlayerTurn();
	}

	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
}
