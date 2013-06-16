package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.AI.ACTION_EAT_ACTION;
import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.ENTER_CASTLE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.screens.MapRenderer;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.PlayerAI;
import com.slamdunk.quester.model.data.CastleData;
import com.slamdunk.quester.model.data.PlayerData;
import com.slamdunk.quester.utils.Assets;
public class PlayerControler extends CharacterControler {

	public PlayerControler(PlayerData data, PlayerActor body) {
		super(data, body, new PlayerAI());
		setShowDestination(true);
	}
	
	@Override
	public Sound getStepSound() {
		return Assets.stepsSound;
	}
	
	@Override
	public Sound getAttackSound() {
		return Assets.swordSounds[MathUtils.random(Assets.swordSounds.length - 1)];
	}

	public boolean enterCastle(CastleControler castle) {
		// On se déplace sur le château
		if (!moveTo(castle.getActor().getWorldX(), castle.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le donjon une fois que le déplacement est fini
		ai.addAction(ENTER_CASTLE, castle);
		return true;		
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera préparée pendant le prochain
	 * appel à think() et effectuée pendant la méthode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// On se déplace sur le chemin
		if (!moveTo(path.getActor().getWorldX(), path.getActor().getWorldY())) {
			return false;
		}
		
		// On entre dans le une fois que le déplacement est fini
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	/**
	 * Tente de placer une torche à la place de la zone d'ombre indiquée
	 * @param darknessActor
	 */
	public boolean placeTorch(DarknessControler darknessControler) {
		// Approche de la cible
		if (!moveNear(darknessControler.getActor().getWorldX(),darknessControler.getActor().getWorldY())) {
			return false;
		}
		
		// Retrait de la zone d'ombre et création d'une torche
		ai.addAction(PLACE_TORCH, darknessControler);
		return true;
	}
	
	@Override
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Entrée dans un donjon
			case ENTER_CASTLE:
				CastleData castleData = ((CastleControler)action.target).getData();
				Quester.getInstance().enterDungeon(
					castleData.dungeonWidth, castleData.dungeonHeight,
					castleData.roomWidth, castleData.roomHeight,
					castleData.difficulty);
				
				// L'action est consommée : réalisation de la prochaine action
				ai.nextAction();
				break;
				
			// Ouverture de porte/région a été prévue
			case CROSS_PATH:
				// Ouverture de la porte
				((PathToAreaControler)action.target).open();

				// L'action est consommée : réalisation de la prochaine action
				ai.nextAction();
				break;
			
			// S'il ne reste aucun ennemi, les actions sont gratuites
			case EAT_ACTION:
				if (!GameControler.instance.hasMoreEnemies()) {
					ai.nextAction();
				}
				break;
			
			// Positionnement d'une torche
			case PLACE_TORCH:
				// Ajout d'une torche à la zone
				((DarknessControler)action.target).addTorch();
					
				// L'action est consommée : réalisation de la prochaine action
				ai.nextAction();
				ai.setNextActions(ACTION_EAT_ACTION);
				break;
		}
		super.act(delta);
	}
	
	@Override
	public int countActionPoints() {
		final MapRenderer map = GameControler.instance.getScreen().getMap();
		final boolean[][] litCells = map.getPathfinder().getWalkables();
		final int playerX = actor.getWorldX();
		final int playerY = actor.getWorldY();
		final int width = map.getMapWidth();
		final int height = map.getMapHeight();
		// Par défaut, toutes ces valeurs seront initialisées à false, indiquant qu'aucune cellule
		// n'a encore été traitée.
		final boolean[][] processedCells = new boolean[width][height];
		
		// Le joueur obtient 1 PA par case de lumière ayant une case d'ombre comme voisine
		// et uniquement pour la zone dans laquelle il se trouve
		int actionPoints = countLightCells(
			litCells, processedCells,
			playerX, playerY, 
			width, height,
			0);
		
//		final ScreenMap map = GameControler.instance.getMapScreen().getMap();
//		// On retire 1 case car on ne souhaite pas analyser les murs, qui sont toujours éclairés
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
//		// Le joueur a autant de points d'action que le plus long chemin de lumière partant du héros
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
	
	private int[][] neightbors = new int[][]{
		new int[]{0, +1},
		new int[]{0, -1},
		new int[]{-1, 0},
		new int[]{+1, 0}
	};
		
	private int countLightCells(boolean[][] litCells, boolean[][] processedCells, int x, int y, int width, int height, int count) {
		// Marque la cellule comme traitée
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
				// Si ce voisin est éclairé et n'a pas encore été traité,
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
	 * Ajoute 1 point par cellule éclairée à la position actuelle et autour de cette position
	 */
	private void addActionPoints(
			boolean[][] litCells, boolean[][] countedCells,
			int width, int height,
			int x, int y,
			List<Integer> lightPathsLengths, int pathIndex) {
		
		if (!litCells[x][y]) {
			// Si la cellule n'est pas éclairée, aucun point à compter
			return;
		}
		
		// Ajoute 1 PA si la cellule est éclairée et n'a pas encore été comptabilisée
		int currentLength = lightPathsLengths.get(pathIndex);
		if (!countedCells[x][y]) {
			currentLength++;
			lightPathsLengths.set(pathIndex, currentLength);
			countedCells[x][y] = true;
		}
		
		// Procède de la même façon pour les cellules voisines
		boolean isCurrentPath = true;
		int col;
		int row;
		for (int[]neighbor : neightbors) {
			col = x + neighbor[0];
			row = y + neighbor[1];
			// Les cellules à compter sont celles qui sont éclairées, sans compter les murs.
			if (col > 0 && col < width - 1
			&& row > 0 && row < height - 1
			// Si la cellule est éclairée...
			&& litCells[col][row] 
			// mais n'a pas encore été comptabilisée alors on compte 
			// les PA qu'elle et ses voisines rapportent
			&& !countedCells[col][row]) {
				if (isCurrentPath) {
					addActionPoints(litCells, countedCells, width, height, col, row, lightPathsLengths, pathIndex);
				} else {
					// On va débuter un nouveau chemin de lumière partant du point existant.
					// On recopie donc la longueur obtenue jusque là
					lightPathsLengths.add(currentLength);
					addActionPoints(litCells, countedCells, width, height, col, row, lightPathsLengths, lightPathsLengths.size() - 1);
				}
				// Dès le prochain voisin, s'il respecte les règles alors on devra
				// commencer un nouveau chemin.
				isCurrentPath = false;
			}
		}
	}
}
