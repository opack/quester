package com.slamdunk.quester.logic.controlers;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.ai.CrossPathAction;
import com.slamdunk.quester.logic.ai.EnterCastleAction;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.PlayerData;
import com.slamdunk.quester.utils.Assets;
public class PlayerControler extends CharacterControler {

	private int[][] neightbors = new int[][]{
		new int[]{0, +1},
		new int[]{0, -1},
		new int[]{-1, 0},
		new int[]{+1, 0}
	};
	
	public PlayerControler(PlayerData data, PlayerActor body) {
		super(data, body, new PlayerAI());
		setShowDestination(true);
	}
	
	@Override
	public int countActionPoints() {
		final ActorMap map = GameControler.instance.getScreen().getMap();
		final boolean[][] litCells = map.getPathfinder().getWalkables();
		final int playerX = actor.getWorldX();
		final int playerY = actor.getWorldY();
		final int width = map.getMapWidth();
		final int height = map.getMapHeight();
		// Par d�faut, toutes ces valeurs seront initialis�es � false, indiquant qu'aucune cellule
		// n'a encore �t� trait�e.
		final boolean[][] processedCells = new boolean[width][height];
		
		// Le joueur obtient 1 PA par case de lumi�re ayant une case d'ombre comme voisine
		// et uniquement pour la zone dans laquelle il se trouve
		int actionPoints = countLightCells(
			litCells, processedCells,
			playerX, playerY, 
			width, height,
			0);
		System.out.println("PlayerControler.countActionPoints()actionPoints="+actionPoints);
		return actionPoints;
	}

	private int countLightCells(boolean[][] litCells, boolean[][] processedCells, int x, int y, int width, int height, int count) {
		// Marque la cellule comme trait�e
		processedCells[x][y] = true;
		
		boolean hasDarkNeighor = false;
		int col;
		int row;
		for (int[]neighbor : neightbors) {
			col = x + neighbor[0];
			row = y + neighbor[1];
			// On ne compte pas les murs
			if (col > 0 && col < width - 1
			&& row > 0 && row < height - 1) {
				// Si ce voisin est �clair� et n'a pas encore �t� trait�,
				// on regarde s'il ne faudrait pas ajouter un PA
				if (litCells[col][row]) {
					if (!processedCells[col][row]) {
						count = countLightCells(litCells, processedCells, col, row, width, height, count);
					}
				} else {
					// On active le flag indiquant que cette cellule a un voisin dark.
					hasDarkNeighor = true;
				}
			}
		}
		// Ajout d'1PA si la cellule a au moins un voisin sombre
		if (hasDarkNeighor) {
			count++;
		}
		return count;
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// On se d�place sur le chemin
		if (!moveTo(path.getActor().getWorldX(), path.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le une fois que le d�placement est fini
		ai.addAction(new CrossPathAction(this, path));
		return true;
	}
	
	public boolean enterCastle(CastleControler castle) {
		// On se d�place sur le ch�teau
		if (!moveTo(castle.getActor().getWorldX(), castle.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le donjon une fois que le d�placement est fini
		ai.addAction(new EnterCastleAction(this, castle));
		return true;		
	}
		
	@Override
	public Sound getAttackSound() {
		return Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
	}

	@Override
	public Sound getStepSound() {
		return Assets.stepsSound;
	}
}
