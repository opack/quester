package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.map.world.WorldElements.VILLAGE;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.display.actors.Character;
import com.slamdunk.quester.display.actors.CharacterListener;
import com.slamdunk.quester.display.actors.Ground;
import com.slamdunk.quester.display.actors.Player;
import com.slamdunk.quester.display.actors.Village;
import com.slamdunk.quester.display.actors.WorldActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.ia.CharacterIA;
import com.slamdunk.quester.ia.IA;
import com.slamdunk.quester.map.MapCell;
import com.slamdunk.quester.map.MapLayer;
import com.slamdunk.quester.map.world.WorldBuilder;
import com.slamdunk.quester.map.world.WorldRegion;

public class WorldMapScreen extends AbstractMapScreen implements CharacterListener  {
	private HUD hud;
	private static final FPSLogger fpsLogger = new FPSLogger();
	
	private Character player;
	
	private WorldRegion region;
	
	public WorldMapScreen(
			Game game,
			int mapWidth, int mapHeight,
			int worldCellWidth, int worldCellHeight) {
		super(game, mapWidth, mapHeight, worldCellWidth, worldCellHeight);
		// Crée le mooooooonde !
		region = new WorldBuilder(mapWidth, mapHeight).build();
		
		// Crée le joueur : A FAIRE IMPERATIVEMENT AVANT LE HUD !
		createPlayer();
		
		// Crée le hud
		createHud();
		
		// Affiche le monde
		WorldDisplayData data = new WorldDisplayData();
        data.playerX = region.getStartVillagePosition().getX();
        data.playerY = region.getStartVillagePosition().getY();
        displayWorld(data);
	}
	
	private void createPlayer() {
		IA ia = new CharacterIA();
		player = new Player("Player", ia, this, 0, 0);
        player.setHP(150);
        player.setAttackPoints(3);
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
		// Mise à jour du hud et en particulier du pad
		hud.update();
		
		// Rien d'autre à faire : seul le joueur joue. Une fois son tour fini, c'est encore à lui de jouer :)
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void displayWorld(Object data) {
		WorldDisplayData display = (WorldDisplayData)data;
		
		MapLayer backgroundLayer = screenMap.getLayer(LAYER_GROUND);
        MapLayer obstaclesLayer = screenMap.getLayer(LAYER_OBSTACLES);
        MapLayer charactersLayer = screenMap.getLayer(LAYER_CHARACTERS);
        
        // Nettoyage de la pièce actuelle
		clearMap();

		// Création du fond
		WorldActor element = null;
		for (int col = 0; col < region.getWidth(); col++) {
			for (int row = 0; row < region.getHeight(); row++) {
				// On met toujours de l'herbe
				element = new Ground(Assets.grass, col, row, this);
				backgroundLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
				
				// Et on ajoute éventuellement des choses dessus
				if (region.get(col, row) == VILLAGE) {
					element = new Village(Assets.village, col, row, this);
					obstaclesLayer.setCell(new MapCell(String.valueOf(element.getId()), col, row, element));
					screenMap.setWalkable(col, row, false);
				}
			}
		}

		// Création de la liste des personnages
		player.setPositionInWorld(display.playerX, display.playerY);
		characters.add(player);
		charactersLayer.setCell(new MapCell(String.valueOf(player.getId()), display.playerX, display.playerY, player));

		// Mise à jour du pad et de la minimap
		hud.update();

		// Centrage de la caméra sur le joueur
		centerCameraOn(player);
	}

	@Override
	public void onCharacterDeath(Character character) {
		// TODO Auto-generated method stub
	}
}
