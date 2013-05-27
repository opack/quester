package com.slamdunk.quester.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.slamdunk.quester.actors.Character;
import com.slamdunk.quester.actors.CharacterListener;
import com.slamdunk.quester.actors.Player;
import com.slamdunk.quester.hud.HUD;

public class WorldMapScreen extends AbstractMapScreen implements CharacterListener  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private final int mapWidth;
	private final int mapHeight;
	
	private Character player;
	
	public WorldMapScreen(
			Game game,
			int mapWidth, int mapHeight,
			int worldCellWidth, int worldCellHeight) {
		super(game, mapWidth, mapHeight, worldCellWidth, worldCellHeight);
		// Crée les pièces du donjon
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		// Crée le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
		
		// Crée le hud
		createHud();
	}
	
	private void createPlayer() {
		player = new Player("Player", this, 0, 0);
        player.setHP(1500);
        player.setAttackPoints(30);
        player.setPlayRank(0); // On veut s'assurer que le joueur sera le premier à jouer
        player.addListener(this);
	}

	/**
	 * Crée le HUD
	 */
	private void createHud() {
		hud = new HUD(this);
		// Ajout du HUD à la liste des Stages, pour qu'il puisse recevoir les clics.
		// On l'ajoute même en premier pour qu'il gère les clics avant le reste du donjon.
		getStages().add(0, hud);
		player.addListener(hud);
		// Comme le Character a déjà été créé, on initialise l'HUD
		hud.onHealthPointsChanged(0, player.getHP());
		hud.onAttackPointsChanged(0, player.getAttackPoints());
	}

	@Override
	public void render (float delta) {
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Le WorldElement dont le tour est en cours joue
        //stage.act(Gdx.graphics.getDeltaTime());
		player.act(delta);
		
        // Dessine la scène et le hud
        mainStage.draw();
        hud.draw();
        
        fpsLogger.log();
	}

	@Override
	public void pause () {
		// TODO Sauvegarde de l'état courant
	}

	@Override
	public void resume () {
		// TODO Restauration de l'état précédent
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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
	public Character getPlayer() {
		return player;
	}

	@Override
	public void endCurrentPlayerTurn() {
		// Rien à faire : seul le joueur joue. Une fois son tour fini, c'est encore à lui de jouer :)
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void displayWorld(Object data) {
		// Affiche la totalité de la carte du monde
		// TODO
	}

	@Override
	public void onCharacterDeath(Character character) {
		// TODO Auto-generated method stub
	}
}
