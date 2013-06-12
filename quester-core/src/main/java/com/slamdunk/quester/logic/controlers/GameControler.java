package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;
import static com.slamdunk.quester.logic.controlers.GamePhases.ATTACK;
import static com.slamdunk.quester.logic.controlers.GamePhases.LIGHT;
import static com.slamdunk.quester.logic.controlers.GamePhases.MOVE;

import java.util.Collections;
import java.util.List;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.MapScreen;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.data.PlayerData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;

public class GameControler implements CharacterListener {
	
	public static final GameControler instance = new GameControler();

	private MapScreen mapScreen;
	private Point currentArea;
	
	private int curCharacterPlaying;
	private List<CharacterControler> characters;

	private PlayerControler player;
	
	private GamePhases gamePhase;
	
	private boolean hasMoreEnemies;
	
	private GameControler() {
		currentArea = new Point(-1, -1);
		gamePhase = MOVE;
	}

	/**
	 * Crée le contrôleur du joueur, qui sera utilisé dans chaque écran de jeu
	 */
	public void createPlayerControler(int hp, int att) {
		PlayerData data = new PlayerData(hp, att);
		data.speed = 2;
		
		player = new PlayerControler(data, null);
		player.addListener(this);
	}
	
	/**
	 * Retourne la carte associée à ce monde
	 * @return
	 */
	public MapScreen getMapScreen() {
		return mapScreen;
	}
	
	public void setMapScreen(MapScreen mapScreen) {
		this.mapScreen = mapScreen;
		this.characters = mapScreen.getCharacters();
		updateHasMoreEnemies();
	}

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}
	
	/**
	 * Passe à la phase suivante
	 */
	public void nextPhase() {
		switch (gamePhase) {
			case ATTACK :
				gamePhase = LIGHT;
				break;
			case LIGHT :
				if (hasMoreEnemies) {
					gamePhase = ATTACK;
					endCurrentPlayerTurn();
				}
				break;
			case MOVE:
				// Rien à faire : on reste en MOVE
				break;
		}
		characters.get(curCharacterPlaying).updateActionPoints();
	}

	/**
	 * Achève le tour du joueur courant et démarre le tour du joueur suivant.
	 */
	public void endCurrentPlayerTurn() {
		// C'est au prochain joueur de jouer. Le tour du joueur courant s'achève
		characters.get(curCharacterPlaying).setPlaying(false);
		
		// Au tour du prochain de jouer !
		curCharacterPlaying++;
		
		// Quand tout le monde a joué son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que ça ait changé.
        if (curCharacterPlaying >= characters.size()) {
        	initCharacterOrder();
        }
		
        // On active le prochain joueur
        characters.get(curCharacterPlaying).setPlaying(true);
	}

	public void nextPlayer() {
		endCurrentPlayerTurn();
		// Quelle que soit la phase précédente, quand un tour finit
		// on repart sur la phase d'attaque
		setGamePhase(GamePhases.ATTACK);
		characters.get(curCharacterPlaying).updateActionPoints();
		updateHUD();
	}
	
	/**
	 * Mise à jour du pad et de la minimap
	 */
	public void updateHUD() {
 		mapScreen.updateHUD(currentArea);
	}

	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		mapScreen.displayWorld(data);
		
		GameControler.instance.updateHasMoreEnemies();
		
		// Initialise l'IA de tous les personnages
		for (CharacterControler character : characters) {
			character.ai.init();
		}
		
		// Débute le jeu avec le premier joueur
		initCharacterOrder();
		mapScreen.updateHUD(currentArea);
        characters.get(curCharacterPlaying).updateActionPoints();
        characters.get(curCharacterPlaying).setPlaying(true);
	}

	public CharacterControler getCurrentCharacter() {
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
	public void onCharacterDeath(CharacterControler character) {
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
		WorldElementData deadCharacterData = character.getData();
		mapScreen.removeElement(character.getActor());
		characters.remove(character);
		MapArea area = mapScreen.getArea(currentArea);
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'achève
		if (deadCharacterData.element == PLAYER) {
			mapScreen.showMessage("Bouh ! T'es mort !");
		}
		
		// Détermine s'il reste des ennemis.
		updateHasMoreEnemies();
	}

	public void updateHasMoreEnemies() {
		hasMoreEnemies = false;
		for (CharacterControler character : characters) {
			if (character.isHostile()) {
				hasMoreEnemies = true;
				break;
			}
		}
	}
	
	public boolean hasMoreEnemies() {
		return hasMoreEnemies;
	}

	public void initCharacterOrder() {
    	Collections.sort(characters);
    	curCharacterPlaying = 0;
	}

	public PlayerControler getPlayer() {
		return player;
	}
	
	public Point getCurrentArea() {
		return currentArea;
	}

	public void setCurrentArea(int x, int y) {
		currentArea.setXY(x, y);
	}

	public GamePhases getGamePhase() {
		return gamePhase;
	}
	
	public void setGamePhase(GamePhases gamePhase) {
		this.gamePhase = gamePhase;
	}

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		mapScreen.updateHUD(currentArea);
	}
}
