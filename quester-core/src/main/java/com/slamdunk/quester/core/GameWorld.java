package com.slamdunk.quester.core;

import java.util.List;

import com.slamdunk.quester.core.actors.Character;
import com.slamdunk.quester.core.actors.WorldElement;
import com.slamdunk.quester.core.pathfinding.UnmutablePoint;

public interface GameWorld {
	Character getPlayer();

	float getWorldCellSize();

	WorldElement getTopElementAt(int col, int row);

	void updateMapPosition(WorldElement element, int oldCol, int oldRow, int newCol, int newRow);

	WorldElement getObstacleAt(int curCol, int curRow);

	void removeElement(WorldElement element);

	boolean isReachable(WorldElement pointOfView, WorldElement target, int weaponRange);

	void endCurrentPlayerTurn();

	List<UnmutablePoint> findPath(WorldElement from, WorldElement to);
	
	List<UnmutablePoint> findPath(int fromX, int fromY, int toX, int toY);
}
