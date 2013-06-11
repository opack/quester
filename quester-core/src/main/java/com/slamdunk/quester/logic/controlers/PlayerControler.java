package com.slamdunk.quester.logic.controlers;

import static com.slamdunk.quester.logic.ai.AI.ACTION_EAT_ACTION;
import static com.slamdunk.quester.logic.ai.QuesterActions.CROSS_PATH;
import static com.slamdunk.quester.logic.ai.QuesterActions.ENTER_CASTLE;
import static com.slamdunk.quester.logic.ai.QuesterActions.PLACE_TORCH;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.quester.Quester;
import com.slamdunk.quester.display.actors.PlayerActor;
import com.slamdunk.quester.display.map.ScreenMap;
import com.slamdunk.quester.logic.ai.ActionData;
import com.slamdunk.quester.logic.ai.MoveActionData;
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
		// Ignorer l'action dans les conditions suivantes :
		// S'il n'est pas possible de se rendre � ce chemin
		if (!updatePath(castle.actor.getWorldX(), castle.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le ch�teau
		MoveActionData moveAction = new MoveActionData(castle);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
		// On entre dans le donjon une fois que le d�placement est fini
		ai.addAction(ENTER_CASTLE, castle);
		return true;		
	}
	
	/**
	 * Enregistrement d'une action demandant au personnage d'ouvrir
	 * cette porte. L'action sera pr�par�e pendant le prochain
	 * appel � think() et effectu�e pendant la m�thode act().
	 */
	public boolean crossPath(PathToAreaControler path) {
		// Ignorer l'action dans les conditions suivantes :
		// Si le chemin n'est pas traversable
		if (!path.getData().isCrossable
		// S'il n'est pas possible de se rendre � ce chemin
		|| !updatePath(path.actor.getWorldX(), path.actor.getWorldY())) {
			return false;
		}
		// D�place le joueur SUR le chemin
		MoveActionData moveAction = new MoveActionData(path);
		moveAction.isStepOnTarget = true;
		ai.addAction(moveAction);
		// On entre dans le une fois que le d�placement est fini
		ai.addAction(CROSS_PATH, path);
		return true;
	}
	
	/**
	 * Tente de placer une torche � la place de la zone d'ombre indiqu�e
	 * @param darknessActor
	 */
	public boolean placeTorch(DarknessControler darknessControler) {
		// Ignorer l'action dans les conditions suivantes :
		// Si la zone d'ombre est d�j� �clair�e par 3 torches
		if (darknessControler.darknessData.torchCount == 3
		// Si la zone d'ombre n'est pas accessible depuis le h�ros
		|| GameControler.instance.getMapScreen().getMap().findLightPath(
				actor.getWorldX(), actor.getWorldY(), 
				darknessControler.actor.getWorldX(), darknessControler.actor.getWorldY()) == null) {
			return false;
		}
		
		// Retrait de la zone d'ombre et cr�ation d'une torche
		ai.addAction(PLACE_TORCH, darknessControler);
		return true;
	}
	
	@Override
	public void act(float delta) {
		ActionData action = ai.getNextAction();
		switch (action.action) {
			// Entr�e dans un donjon
			case ENTER_CASTLE:
				WorldElementControler target = action.target;
				if (target != null && (target instanceof CastleControler)) {
					CastleData castleData = ((CastleControler)target).getData();
					Quester.getInstance().enterDungeon(
						castleData.dungeonWidth, castleData.dungeonHeight,
						castleData.roomWidth, castleData.roomHeight,
						castleData.difficulty);
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
			// Ouverture de porte/r�gion a �t� pr�vue
			case CROSS_PATH:
				WorldElementControler path = action.target;
				if (path != null && (path instanceof PathToAreaControler)) {
					// Ouverture de la porte
					((PathToAreaControler)path).open();

					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
			// Positionnement d'une torche
			case PLACE_TORCH:
				DarknessControler darknessControler = (DarknessControler)action.target;
				if (GameControler.instance.getMapScreen().getMap().findLightPath(
						actor.getWorldX(), actor.getWorldY(), 
						darknessControler.actor.getWorldX(), darknessControler.actor.getWorldY()) != null) {
					// Ajout d'une torche � la zone
					darknessControler.addTorch();
					
					// L'action est consomm�e : r�alisation de la prochaine action
					ai.nextAction();
					ai.setNextActions(ACTION_EAT_ACTION);
				} else {
					// Cette action est impossible. On annule tout ce qui �tait pr�vu et on r�fl�chit de nouveau.
					prepareThinking();
				}
				break;
		}
		super.act(delta);
	}
	
	@Override
	public void countActionPoints() {
		final ScreenMap map = GameControler.instance.getMapScreen().getMap();
		// On retire 1 case car on ne souhaite pas analyser les murs, qui sont toujours �clair�s
		final int width = map.getMapWidth();
		final int height = map.getMapHeight();
		final boolean[][] litCells = map.getLightfinder().getWalkables();
		final int playerX = actor.getWorldX();
		final int playerY = actor.getWorldY();
		
		final boolean[][] countedCells = new boolean[width][height];
		for (int x = 1; x < width - 1; x++) {
			for (int y = 1; y < height - 1; y++) {
				countedCells[x - 1][y - 1] = false;
			}
		}
		
		// Le joueur a autant de points d'action que le plus long chemin de lumi�re partant du h�ros
		List<Integer> lightPathsLengths = new ArrayList<Integer>();
		lightPathsLengths.add(0);
		addActionPoints(litCells, countedCells, width, height, playerX, playerY, lightPathsLengths, 0);
		characterData.actionsLeft = 0;
		for (int length : lightPathsLengths) {
			if (length > characterData.actionsLeft) {
				characterData.actionsLeft = length;
			}
		}
		System.out.println("PlayerControler.countActionPoints()characterData.actionsLeft="+characterData.actionsLeft);
	}

	private int[][] neightbors = new int[][]{
		new int[]{0, +1},
		new int[]{0, -1},
		new int[]{-1, 0},
		new int[]{+1, 0}
	};
	
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
			System.out.printf("PlayerControler.addActionPoints() %d = %d\n", pathIndex, currentLength);
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
}
