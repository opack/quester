package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.model.map.MapElements.PLAYER;

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
	
	private boolean isInAttackPhase;
	
	private GameControler() {
		currentArea = new Point(-1, -1);
		isInAttackPhase = false;
	}

	/**
	 * Cr�e le contr�leur du joueur, qui sera utilis� dans chaque �cran de jeu
	 */
	public void createPlayerControler(int hp, int att) {
		PlayerData data = new PlayerData(hp, att);
		data.speed = 2;
		
		player = new PlayerControler(data, null);
		player.addListener(this);
	}
	
	/**
	 * Retourne la carte associ�e � ce monde
	 * @return
	 */
	public MapScreen getMapScreen() {
		return mapScreen;
	}
	
	public void setMapScreen(MapScreen mapScreen) {
		this.mapScreen = mapScreen;
		this.characters = mapScreen.getCharacters();
	}

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}

	/**
	 * Ach�ve le tour du joueur courant et d�marre le tour du joueur suivant.
	 */
	public void endCurrentPlayerTurn() {
		// Mise � jour du pad et de la minimap
		mapScreen.updateHUD(currentArea);
     	
		// Changement de phase
		isInAttackPhase = !isInAttackPhase;
		
		// S'il ne reste plus d'ennemis, on reste en phase d'�clairage
		boolean noMoreEnemies = true;
		for (CharacterControler character : characters) {
			if (character.isHostile()) {
				noMoreEnemies = false;
				break;
			}
		}
		if (noMoreEnemies) {
			isInAttackPhase = false;
		}
		if (isInAttackPhase)
			System.out.println("GameControler.endCurrentPlayerTurn() ATTAQUE");
		else 
			System.out.println("GameControler.endCurrentPlayerTurn() ECLAIRAGE");
		
		// Si une nouvelle phase d'attaque d�bute, alors c'est au prochain joueur de jouer
		if (isInAttackPhase) {
	        // Le tour du joueur courant s'ach�ve
			if (curCharacterPlaying > 0 && curCharacterPlaying < characters.size()) {
				characters.get(curCharacterPlaying).setPlaying(false);
			}
			
			// Au tour du prochain de jouer !
	        curCharacterPlaying++;
	        
	        // Quand tout le monde a jou� son tour, on recalcule
	        // l'ordre de jeu pour le prochain tour car il se peut que �a ait chang�.
	        initCharacterOrder();
		}
		
		
        // On active le prochain joueur
        characters.get(curCharacterPlaying).countActionPoints();
        characters.get(curCharacterPlaying).setPlaying(true);
	}

	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		mapScreen.displayWorld(data);
		
		// R�ordonne la liste d'ordre de jeu
        initCharacterOrder();
        isInAttackPhase = false;
        endCurrentPlayerTurn();
	}

	public WorldElementControler getCurrentCharacter() {
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
		// On recherche l'indice du personnage � supprimer dans la liste
		int index = characters.indexOf(character);
		// Si le perso supprim� devait jouer apr�s le joueur actuel (index > curCharacterPlaying),
		// alors l'ordre de jeu n'est pas impact�.
		// Si le perso supprim� devait jouer avant (index < curCharacterPlaying), alors l'ordre de
		// jeu est impact� car les indices changent. Si on ne fait rien, un joueur risque de passer
		// son tour.
		// Si le perso supprim� est le joueur actuel (index = curCharacterPlaying), alors le
		// raisonnement est le m�me
		if (index <= curCharacterPlaying) {
			curCharacterPlaying --;
		}
		
		// Suppression du character dans la liste et de la pi�ce
		WorldElementData deadCharacterData = character.getData();
		mapScreen.removeElement(character.getActor());
		characters.remove(character);
		MapArea area = mapScreen.getArea(currentArea);
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'ach�ve
		if (deadCharacterData.element == PLAYER) {
			mapScreen.showMessage("Bouh ! T'es mort !");
		}
	}

	public void initCharacterOrder() {
		if (curCharacterPlaying >= characters.size()) {
        	Collections.sort(characters);
        	curCharacterPlaying = 0;
        }
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

	public boolean isInAttackPhase() {
		return isInAttackPhase;
	}
}
