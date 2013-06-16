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
	 * Musique � jouer sur cet �cran
	 */
	private String backgroundMusic;
	
	
	private boolean isFirstDisplay;
	
	public GameScreen(MapBuilder builder, int worldCellWidth, int worldCellHeight) {
		// Cr�e les pi�ces du donjon
		areas = builder.build();
		UnmutablePoint entrance = builder.getEntranceRoom();
		currentRoom = new Point(entrance.getX(), entrance.getY());
		
		// Cr�ation du renderer
		mapRenderer = new MapRenderer(builder.getMapWidth(), builder.getMapHeight(), worldCellWidth, worldCellHeight);

		// DBG Affichage du donjon en texte
		builder.printMap();
		
		// DBG Rustine pour r�ussir � centrer sur le joueur lors de l'affichage
        // de la toute premi�re pi�ce. Etrangement le centerCameraOn(player) ne
        // fonctionne pas la toute premi�re fois (avant le passage dans le premier
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
	 * Centre la cam�ra sur le joueur
	 * @param element
	 */
	public void centerCameraOn(WorldElementActor actor) {
		mapRenderer.getCamera().position.set(
			actor.getX() + actor.getWidth() / 2, 
			actor.getY() + actor.getHeight() / 2, 
			0);
	}

	/**
	 * Affiche la pi�ce de donjon aux coordonn�es indiqu�es, en placant
	 * le h�ro � l'entr�e de la pi�ce aux coordonn�es indiqu�es.
	 */
	public void displayWorld(DisplayData display) {
		Assets.playMusic(backgroundMusic);
		
		// La salle actuellement affich�e a chang�
		// Certains �l�ments (portes et chemins) ont besoin de conna�tre la position
		// de la salle courante. Il faut donc mettre � jour currentRoom avant de cr�er
		// les �l�ments.
        currentRoom.setXY(display.regionX, display.regionY);
		MapArea area = areas[display.regionX][display.regionY];
		mapRenderer.buildMap(area, currentRoom);

	 	// Placement du joueur puis cr�ation des autres personnages
	 	player.setPositionInWorld(display.playerX, display.playerY);
	 	CharacterControler playerControler = player.getControler();
	 	mapRenderer.addCharacter(playerControler);
        mapRenderer.createCharacters(area);
        
        // Mise � jour du pad et de la minimap
        hud.update(display.regionX, display.regionY);
        
        // Centrage de la cam�ra sur le joueur
        centerCameraOn(player);
	}
	
	@Override
	public void render(float delta) {
		if (isFirstDisplay) {
			isFirstDisplay = false;
			centerCameraOn(player);
		}
		
		// Efface l'�cran
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Mise � jour du menu contextuel, qui doit faire une belle animation pour appara�tre
		if (ContextMenuControler.openedMenu != null) {
			ContextMenuControler.openedMenu.act(delta);
		}
		
		// Le WorldElement dont le tour est en cours joue
		GameControler.instance.getCurrentCharacter().act(delta);
		
        // Dessine la sc�ne et le hud
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
		// R�activation des listeners
		mapRenderer.enableInputListeners(true);
		
		// Centrage de la cam�ra sur le joueur
		// DBG Normalement le centerCameraOn() devrait �tre
		// suffisant pour centrer la cam�ra sur le joueur quand
		// on revient sur la carte du monde. Ca ne marche
		// malheureusement pas et on doit recourir encore
		// une fois � l'astuce du isFirstDisplay :(
		centerCameraOn(player);
		isFirstDisplay = true;
		
		// Lancement de la musique
		Assets.playMusic(backgroundMusic);
	}

	@Override
	public void hide() {
		// DBG L'�cran n'est plus affich�. Il faut avoir sauvegard� avant !
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
	 * Cr�e le HUD
	 */
	public void createHud(int miniMapWidth, int miniMapHeight) {
		hud = new HUD(player);
		if (miniMapWidth > 0 && miniMapHeight > 0) {
			hud.setMiniMap(areas, miniMapWidth, miniMapHeight);
		}
	}
	
	/**
	 * Cr�e une repr�sentation physique (WorldActor) du joueur.
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
	 * Retourne la zone du monde aux coordonn�es indiqu�es
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
	 * Met � jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(Point currentArea) {
		updateHUD(currentArea.getX(), currentArea.getY());
	}
	
	/**
	 * Met � jour le HUD.
	 * @param currentArea
	 */
	public void updateHUD(int currentAreaX, int currentAreaY) {
		hud.update(currentAreaX, currentAreaY);
	}

	/**
	 * Affiche un message � l'utilisateur
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
