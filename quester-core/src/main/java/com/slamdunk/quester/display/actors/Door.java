package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.GameWorld;
import com.slamdunk.quester.display.screens.DungeonDisplayData;
import com.slamdunk.quester.map.dungeon.RoomElements;
import com.slamdunk.quester.map.dungeon.RoomWalls;

public class Door extends Obstacle {
	private final RoomWalls wall;
	private final RoomElements type;
	
	private final int destinationRoomX;
	private final int destinationRoomY;
	
	private boolean isOpenable;

	public Door(
		TextureRegion texture,
		int col, int row,
		GameWorld gameWorldListener,
		int destinationRoomX, int destinationRoomY,
		RoomWalls wall, RoomElements type) {
		super(texture, col, row, gameWorldListener);

		this.wall = wall;
		this.type = type;
		
		this.destinationRoomX = destinationRoomX;
		this.destinationRoomY = destinationRoomY;
		
		isOpenable = true;
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur de changer de salle
	        	Door.this.openDoor();
	        }
		});
	}
	
	public boolean isOpenable() {
		return isOpenable;
	}

	public void setOpenable(boolean isOpenable) {
		this.isOpenable = isOpenable;
	}

	/**
	 * Ouvre la porte et effectue l'action adéquate en fonction
	 * de ce qui se trouve derrière (une autre pièce, sortie du
	 * donjon...).
	 */
	public void openDoor() {
		switch (type) {
			case DUNGEON_EXIT_DOOR:
				world.exit();
				break;
			case COMMON_DOOR:
				moveToRoom();
				break;
			default:
				break;
		}
	}

	/**
	 * Déplace le personnage dans une autre pièce
	 */
	private void moveToRoom() {
		DungeonDisplayData data = new DungeonDisplayData();
		data.roomX = destinationRoomX;
		data.roomY = destinationRoomY;
		switch (wall) {
			case TOP:
				// La porte est sur le mur du haut, le perso apparaîtra donc dans la prochaine pièce en bas
				data.entranceX = getWorldX();
				data.entranceY = 0;
				break;
			case BOTTOM:
				// La porte est sur le mur du bas, le perso apparaîtra donc dans la prochaine pièce en haut
				data.entranceX = getWorldX();
				data.entranceY = map.getMapHeight() - 1;
				break;
			case LEFT:
				// La porte est sur le mur de gauche, le perso apparaîtra donc dans la prochaine pièce à droite
				data.entranceX =  map.getMapWidth() - 1;
				data.entranceY = getWorldY();
				break;
			case RIGHT:
				// La porte est sur le mur de droite, le perso apparaîtra donc dans la prochaine pièce à gauche
				data.entranceX =  0;
				data.entranceY = getWorldY();
				break;
		}
		world.displayWorld(data);
	}

	public int getDestinationRoomX() {
		return destinationRoomX;
	}

	public int getDestinationRoomY() {
		return destinationRoomY;
	}

	public RoomElements getType() {
		return type;
	}
}
