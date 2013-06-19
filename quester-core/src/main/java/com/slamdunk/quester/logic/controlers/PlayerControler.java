package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.ENTER_CASTLE;

import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.map.ActorMap;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.CastleData;
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
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Entr�e dans un donjon
			case ENTER_CASTLE:
				CastleData castleData = ((CastleControler)action.target).getData();
				Quester.getInstance().enterDungeon(
					castleData.dungeonWidth, castleData.dungeonHeight,
					castleData.roomWidth, castleData.roomHeight,
					castleData.difficulty);
				
				// L'action est consomm�e : r�alisation de la prochaine action
				ai.nextAction();
				break;
				
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_PATH:
				// Ouverture de la porte
				((PathToAreaControler)action.target).open();

				// L'action est consomm�e : r�alisation de la prochaine action
				ai.nextAction();
				break;
			
			// S'il ne reste aucun ennemi, les actions sont gratuites
			case EAT_ACTION:
				if (!GameControler.instance.hasMoreEnemies()) {
					ai.nextAction();
				}
				break;
		}
		super.act(delta);
	}

	/**
	 * Ajoute 1 point par cellule �clair�e � la position actuelle et autour de cette position
	 */
	private void addActionPoints(
			boolean[][] litCells, boolean[][] countedCells,
			int width, int height,
			int x, int y,
			List<Integer> lightPathsLengths, int pathIndex) {
		
		if (!litCells[x][y]) {
			// Si la cellule n'est pas �clair�e, aucun point � compter
			return;
		}
		
		// Ajoute 1 PA si la cellule est �clair�e et n'a pas encore �t� comptabilis�e
		int currentLength = lightPathsLengths.get(pathIndex);
		if (!countedCells[x][y]) {
			currentLength++;
			lightPathsLengths.set(pathIndex, currentLength);
			countedCells[x][y] = true;
		}
		
		// Proc�de de la m�me fa�on pour les cellules voisines
		boolean isCurrentPath = true;
		int col;
		int row;
		for (int[]neighbor : neightbors) {
			col = x + neighbor[0];
			row = y + neighbor[1];
			// Les cellules � compter sont celles qui sont �clair�es, sans compter les murs.
			if (col > 0 && col < width - 1
			&& row > 0 && row < height - 1
			// Si la cellule est �clair�e...
			&& litCells[col][row] 
			// mais n'a pas encore �t� comptabilis�e alors on compte 
			// les PA qu'elle et ses voisines rapportent
			&& !countedCells[col][row]) {
				if (isCurrentPath) {
					addActionPoints(litCells, countedCells, width, height, col, row, lightPathsLengths, pathIndex);
				} else {
					// On va d�buter un nouveau chemin de lumi�re partant du point existant.
					// On recopie donc la longueur obtenue jusque l�
					lightPathsLengths.add(currentLength);
					addActionPoints(litCells, countedCells, width, height, col, row, lightPathsLengths, lightPathsLengths.size() - 1);
				}
				// D�s le prochain voisin, s'il respecte les r�gles alors on devra
				// commencer un nouveau chemin.
				isCurrentPath = false;
			}
		}
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
		
//		final ScreenMap map = GameControler.instance.getMapScreen().getMap();
//		// On retire 1 case car on ne souhaite pas analyser les murs, qui sont toujours �clair�s
//		final int width = map.getMapWidth();
//		final int height = map.getMapHeight();
//		final boolean[][] litCells = map.getLightPathfinder().getWalkables();
//		final int playerX = actor.getWorldX();
//		final int playerY = actor.getWorldY();
//		
//		final boolean[][] countedCells = new boolean[width][height];
//		for (int x = 1; x < width - 1; x++) {
//			for (int y = 1; y < height - 1; y++) {
//				countedCells[x - 1][y - 1] = false;
//			}
//		}
//		
//		// Le joueur a autant de points d'action que le plus long chemin de lumi�re partant du h�ros
//		List<Integer> lightPathsLengths = new ArrayList<Integer>();
//		lightPathsLengths.add(0);
//		addActionPoints(litCells, countedCells, width, height, playerX, playerY, lightPathsLengths, 0);
//		int actionPoints = 1;
//		for (int length : lightPathsLengths) {
//			if (length > characterData.actionsLeft) {
//				actionPoints = length;
//			}
//		}
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
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	public boolean enterCastle(CastleControler castle) {
		// On se d�place sur le ch�teau
		if (!moveTo(castle.getActor().getWorldX(), castle.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le donjon une fois que le d�placement est fini
		ai.addAction(ENTER_CASTLE, castle);
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
