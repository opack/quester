package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.controlers.GamePhases.ATTACK;
import static com.slamdunk.quester.logic.controlers.GamePhases.LIGHT;
import static com.slamdunk.quester.logic.controlers.GamePhases.MOVE;
import static com.slamdunk.quester.model.map.MapElements.PLAYER;

import java.util.Collections;
import java.util.List;

import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.screens.DisplayData;
import com.slamdunk.quester.display.screens.GameScreen;
import com.slamdunk.quester.model.data.PlayerData;
import com.slamdunk.quester.model.data.WorldElementData;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.points.Point;

public class GameControler implements CharacterListener {
	
	public static final GameControler instance = new GameControler();

	private GameScreen screen;
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
	public GameScreen getScreen() {
		return screen;
	}
	
	public void setScreen(GameScreen screen) {
		this.screen = screen;
		this.characters = screen.getMap().getCharacters();
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
	 * Passe � la phase suivante
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
				// Rien � faire : on reste en MOVE
				break;
		}
		characters.get(curCharacterPlaying).updateActionPoints();
	}

	/**
	 * Ach�ve le tour du joueur courant et d�marre le tour du joueur suivant.
	 */
	public void endCurrentPlayerTurn() {
		// C'est au prochain joueur de jouer. Le tour du joueur courant s'ach�ve
		characters.get(curCharacterPlaying).setPlaying(false);
		
		// Au tour du prochain de jouer !
		curCharacterPlaying++;
		
		// Quand tout le monde a jou� son tour, on recalcule
        // l'ordre de jeu pour le prochain tour car il se peut que �a ait chang�.
        if (curCharacterPlaying >= characters.size()) {
        	initCharacterOrder();
        }
		
        // On active le prochain joueur
        characters.get(curCharacterPlaying).setPlaying(true);
	}

	public void nextPlayer() {
		endCurrentPlayerTurn();
		// Quelle que soit la phase pr�c�dente, quand un tour finit
		// on repart sur la phase d'attaque
		setGamePhase(GamePhases.ATTACK);
		characters.get(curCharacterPlaying).updateActionPoints();
		updateHUD();
	}
	
	/**
	 * Mise � jour du pad et de la minimap
	 */
	public void updateHUD() {
 		screen.updateHUD(currentArea);
	}

	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		screen.displayWorld(data);
		
		GameControler.instance.updateHasMoreEnemies();
		
		// Initialise l'IA de tous les personnages
		for (CharacterControler character : characters) {
			character.ai.init();
		}
		
		// D�bute le jeu avec le premier joueur
		initCharacterOrder();
		screen.updateHUD(currentArea);
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
		WorldElementActor removedActor = screen.getMap().removeElement(character.getActor());
		screen.getMapRenderer().getStage().getActors().removeValue(removedActor, true);
		// Met � jour le pathfinder. Si l'�l�ment �tait solide,
		// alors sa disparition rend l'emplacement walkable.
		if (deadCharacterData.isSolid) {
			screen.getMap().setWalkable(removedActor.getWorldX(), removedActor.getWorldY(), true);
		}
		characters.remove(character);
		MapArea area = screen.getCurrentArea();
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'ach�ve
		if (deadCharacterData.element == PLAYER) {
			screen.showMessage("Bouh ! T'es mort !");
		}
		
		// D�termine s'il reste des ennemis.
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
		screen.updateHUD(currentArea);
	}
}
