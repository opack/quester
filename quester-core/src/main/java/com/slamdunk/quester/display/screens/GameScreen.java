package com.slamdunk.quester.display.screens;

import static com.slamdunk.quester.Quester.screenHeight;
import static com.slamdunk.quester.Quester.screenWidth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.display.hud.HUD;
import com.slamdunk.quester.display.messagebox.MessageBox;
import com.slamdunk.quester.display.messagebox.MessageBoxFactory;
import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.ContextMenuControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.model.map.MapArea;
import com.slamdunk.quester.model.map.MapBuilder;
import com.slamdunk.quester.model.points.Point;
import com.slamdunk.quester.model.points.UnmutablePoint;
import com.slamdunk.quester.utils.Assets;

public class GameScreen implements Screen {
	private static final FPSLogger fpsLogger = new FPSLogger();
	private final MapArea[][] areas;
	private final Point currentRoom;
	private MapRenderer mapRenderer;
	private HUD hud;
	private PlayerActor player;
	
	/**
	 * Musique à jouer sur cet écran
	 */
	private String backgroundMusic;
	
	
	private boolean isFirstDisplay;
	
	public GameScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		// Crée les pièces du donjon
		areas = builder.build();
		UnmutablePoint entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// Création du renderer
		mapRenderer = new MapRenderer(builder.getMapWidth(), builder.getMapHeight(), worldCellWidth, worldCellHeight);

		// DBG Affichage du donjon en texte
		builder.printMap();
		
		// DBG Rustine pour réussir à centrer sur le joueur lors de l'affichage
        // de la toute première pièce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute première fois (avant le passage dans le premier
        // render()).
        isFirstDisplay = true;
	}
	
	/**
	 * Largeur d'une cellule de la carte, en pixels.
	 * @return
	 */	
	public float getCellWidth() {
		return 0;
	}
	
	/**
	 * Hauteur d'une cellule de la carte, en pixels.
	 * @return
	 */
	public float getCellHeight() {
		return 0;
	}
	
	/**
	 * Centre la caméra sur le joueur
	 * @param element
	 */
	public void centerCameraOn(WorldElementActor actor) {
		mapRenderer.getCamera().position.set(
			actor.getX() + actor.getWidth() / 2, 
			actor.getY() + actor.getHeight() / 2, 
			0);
	}

	/**
	 * Affiche la pièce de donjon aux coordonnées indiquées, en placant
	 * le héro à l'entrée de la pièce aux coordonnées indiquées.
	 */
	public void displayWorld(DisplayData display) {
		Assets.playMusic(backgroundMusic);
		
		// La salle actuellement affichée a changé
		// Certains éléments (portes et chemins) ont besoin de connaître la position
		// de la salle courante. Il faut donc mettre à jour currentRoom avant de créer
		// les éléments.
        currentRoom.setXY(display.regionX, display.regionY);
		MapArea area = areas[display.regionX][display.regionY];
		mapRenderer.buildMap(area, currentRoom);

	 	// Placement du joueur puis création des autres personnages
	 	player.setPositionInWorld(display.playerX, display.playerY);
	 	CharacterControler playerControler = player.getControler();
	 	mapRenderer.addCharacter(playerControler);
        mapRenderer.createCharacters(area);
        
        // Mise à jour du pad et de la minimap
        hud.update(display.regionX, display.regionY);
        
        // Centrage de la caméra sur le joueur
        centerCameraOn(player);
	}
	
	@Override
	public void render(float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(player);
		}
		
		// Efface l'écran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Mise à jour du menu contextuel, qui doit faire une belle animation pour apparaître
		if (ContextMenuControler.openedMenu != null) {
			ContextMenuControler.openedMenu.act(delta);
		}
		
		// Le WorldElement dont le tour est en cours joue
		GameControler.instance.getCurrentCharacter().act(delta);
		
        // Dessine la scène et le hud
        mapRenderer.render();
        hud.draw();
        
        fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		mapRenderer.getStage().setViewport(screenWidth, screenHeight, true);
	}

	@Override
	public void show() {
		// Réactivation des listeners
		mapRenderer.enableInputListeners(true);
		
		// Centrage de la caméra sur le joueur
		// DBG Normalement le centerCameraOn() devrait être
		// suffisant pour centrer la caméra sur le joueur quand
		// on revient sur la carte du monde. Ca ne marche
		// malheureusement pas et on doit recourir encore
		// une fois à l'astuce du isFirstDisplay :(
		centerCameraOn(player);
		isFirstDisplay = true;
		
		// Lancement de la musique
		Assets.playMusic(backgroundMusic);
	}

	@Override
	public void hide() {
		// DBG L'écran n'est plus affiché. Il faut avoir sauvegardé avant !
		//mapRenderer.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
	
	/**
	 * Crée le HUD
	 */
	public void createHud(int miniMapWidth, int miniMapHeight) {
		hud = new HUD(player);
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			hud.setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
	}
	
	/**
	 * Crée une représentation physique (WorldActor) du joueur.
	 * @param hp
	 * @param att
	 */
	public void createPlayer(UnmutablePoint position) {
		player = new PlayerActor();
		player.setControler(GameControler.instance.getPlayer());
		player.setPositionInWorld(position.getX(), position.getY());
		
		GameControler.instance.getPlayer().setPathfinder(mapRenderer.getPathfinder());
	}
	
	public PlayerActor getPlayerActor() {
		return player;
	}

	public String getBackgroundMusic() {
		return backgroundMusic;
	}

	public void setBackgroundMusic(String backgroundMusic) {
		this.backgroundMusic = backgroundMusic;
	}
	
	/**
	 * Retourne la zone du monde aux coordonnées indiquées
	 */
	public MapArea getArea(Point currentArea) {
		return areas[currentArea.getX()][currentArea.getY()];
	}
	

	/**
	 * Retourne la zone du monde courante
	 */
	public MapArea getCurrentArea() {
		return areas[currentRoom.getX()][currentRoom.getY()];
	}

	/**
	 * Met à jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(Point currentArea) {
		updateHUD(currentArea.getX(), currentArea.getY());
	}
	
	/**
	 * Met à jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(int currentAreaX, int currentAreaY) {
		hud.update(currentAreaX, currentAreaY);
	}

	/**
	 * Affiche un message à l'utilisateur
	 * @param message
	 */
	public void showMessage(String message) {
		MessageBox msg = MessageBoxFactory.createSimpleMessage(message, hud);
		msg.show();
	}

	public MapRenderer getMap() {
		return mapRenderer;
	}
}
