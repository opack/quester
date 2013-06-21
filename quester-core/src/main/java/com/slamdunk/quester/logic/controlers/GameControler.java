package com.slamdunk.quester.logic.controlers;

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

	private List<CharacterControler> characters;
	private int curCharacterPlaying;
	
	private Point currentArea;

	private boolean hasMoreEnemies;
	
	private PlayerControler player;
	
	private GameScreen screen;
	
	private GameControler() {
		currentArea = new Point(-1, -1);
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
	
	public void displayWorld(DisplayData data) {
		// Modification de la zone courante et affichage de la carte
		currentArea.setXY(data.regionX, data.regionY);
		screen.displayWorld(data);
		
		GameControler.instance.updateHasMoreEnemies();
		
		// Initialise l'IA de tous les personnages
		for (CharacterControler character : characters) {
			character.ai.init();
		}
		
		// Débute le jeu avec le premier joueur
		initCharacterOrder();
		screen.updateHUD(currentArea);
        characters.get(curCharacterPlaying).setPlaying(true);
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

	/**
	 * Sort du donjon courant pour retourner sur la carte du monde,
	 * ou quitte la carte du monde vers le menu
	 */
	public void exit() {
		Quester.getInstance().enterWorldMap();
	}
	
	public Point getCurrentArea() {
		return currentArea;
	}

	public CharacterControler getCurrentCharacter() {
		return characters.get(curCharacterPlaying);
	}

	public PlayerControler getPlayer() {
		return player;
	}

	/**
	 * Retourne la carte associée à ce monde
	 * @return
	 */
	public GameScreen getScreen() {
		return screen;
	}

	public boolean hasMoreEnemies() {
		return hasMoreEnemies;
	}
	
	public void initCharacterOrder() {
    	Collections.sort(characters);
    	curCharacterPlaying = 0;
	}

	public void nextPlayer() {
		endCurrentPlayerTurn();
		updateHUD();
	}

	@Override
	public void onActionPointsChanged(int oldValue, int newValue) {
		screen.updateHUD(currentArea);
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
		WorldElementActor removedActor = screen.getMap().removeElement(character.getActor());
		screen.getMap().getStage().getActors().removeValue(removedActor, true);
		// Met à jour le pathfinder. Si l'élément était solide,
		// alors sa disparition rend l'emplacement walkable.
		if (deadCharacterData.isSolid && removedActor != null) {
			screen.getMap().setWalkable(removedActor.getWorldX(), removedActor.getWorldY(), true);
		}
		characters.remove(character);
		MapArea area = screen.getCurrentArea();
		if (area.isPermKillCharacters()) {
			area.getCharacters().remove(deadCharacterData);
		}
		
		// Si c'est le joueur qui est mort, le jeu s'achève
		if (deadCharacterData.element == PLAYER) {
			screen.showMessage("Bouh ! T'es mort !");
		}
		
		// Détermine s'il reste des ennemis.
		updateHasMoreEnemies();
	}

	@Override
	public void onHealthPointsChanged(int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}
	
	public void setCurrentArea(int x, int y) {
		currentArea.setXY(x, y);
	}

	public void setScreen(GameScreen screen) {
		this.screen = screen;
		this.characters = screen.getMap().getCharacters();
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

	/**
	 * Mise à jour du pad et de la minimap
	 */
	public void updateHUD() {
 		screen.updateHUD(currentArea);
	}
}
