package com.slamdunk.quester.display.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.slamdunk.quester.core.QuesterGame;

public class Castle extends WorldActor {
	private int dungeonWidth;
	private int dungeonHeight;
	private int roomWidth;
	private int roomHeight;

	public Castle(TextureRegion texture, int col, int row) {
		super(texture, col, row);
		
		addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	// Demande au joueur d'entrer dans le château
	        	QuesterGame.instance.getPlayer().enterCastle(Castle.this);
	        }
		});
	}
	
	@Override
	public boolean isSolid() {
		// On autorise le joueur a marcher sur le château
		return false;
	}

	public int getDungeonWidth() {
		return dungeonWidth;
	}
	
	public int getDungeonHeight() {
		return dungeonHeight;
	}
	
	public int getRoomWidth() {
		return roomWidth;
	}
	
	public int getRoomHeight() {
		return roomHeight;
	}

	public void setDungeonWidth(int dungeonWidth) {
		this.dungeonWidth = dungeonWidth;
	}

	public void setDungeonHeight(int dungeonHeight) {
		this.dungeonHeight = dungeonHeight;
	}

	public void setRoomWidth(int roomWidth) {
		this.roomWidth = roomWidth;
	}

	public void setRoomHeight(int roomHeight) {
		this.roomHeight = roomHeight;
	}
}
