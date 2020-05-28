package com.company;

import com.company.logic.BattleRandom;
import com.company.model.*;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;
import com.company.view.MapLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameDynamics implements Iterable<Cell> {
	private PlayerAITank player1;
	private PlayerAITank player2;

	private List<Enemy> tanks;
	private List<EnemyPort> enemyPorts;
	private int newTankSteps, currentStepsForNewTank;
	private final int minimumStepsForNewTank;// game-steps after which new tank can appear;

	private Bullet[] bullets;
	private int bulletsCount;
	private final double bulletsCountMultiplier = 1.4;
	private DamageClass damages;

	private Cell[] cells;
	private int rowCells;
	private int colCells;
	private final int maxCols;
	private Cell collectibles;
	private int collectibleTimer;// timer how long tank can be suspended or how long player can be indestructible;
	private List<Integer> treesIds;
	private int cellUnitSize;
	private final int cellPrecisionUnitSize;

	private BattleRandom rand;
	private int[] xyPos = new int[2];
	private int steps;
	private final int stepsPerSecond;


	public GameDynamics(MapLoader mapLoader, GameView view){
		rowCells = colCells = 26;// default Battle City map size;
		this.maxCols = mapLoader.getMaxCols();

		cellPrecisionUnitSize = view.getDefaultCellSize();
		stepsPerSecond = view.getFramesPerSecond();
		minimumStepsForNewTank = stepsPerSecond*2;// assumption that new tank appears after minimum 2s;
		currentStepsForNewTank = stepsPerSecond*10;
		steps = newTankSteps = 0;

		tanks = new LinkedList<>();
		enemyPorts = new ArrayList<>();
		treesIds = new ArrayList<>();

		bulletsCount = 0;
		bullets = new Bullet[10];// any beginning size;

		setCellsStructure(maxCols, mapLoader.getMaxRows() );
		collectibles = new Cell();

		damages = DamageClass.getInstance();

		rand = new BattleRandom();
	}

	/*private void setUpperRowCells(int maxCols){
		cells[0].linkNeighborCells(null, cells[1], cells[maxCols], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = maxCols - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+maxCols], cells[i-1]);
			cells[i].setPos(i*cellPrecisionUnitSize, 0);
		}

		cells[i].linkNeighborCells(null, null, cells[i+maxCols], cells[i-1]);
		cells[i].setPos(i*cellPrecisionUnitSize, 0);
	}*/

	private Cell cellByPosition(int newCol, int newRow){
		int col = newCol/cellPrecisionUnitSize, row = newRow/cellPrecisionUnitSize;
		if(col < 0 || row < 0 || row >= rowCells || col >= colCells)
			return null;

		int cellIndex = row*maxCols + col;
		return cells[cellIndex];
	}

	private void setCellsStructure(int maxCols, int maxRows){
		int i, cellsCount = maxCols*maxRows;

		cells = new Cell[cellsCount];
		for (i = 0; i < cellsCount; i++){
			cells[i] = new Cell();
			cells[i].setIndexId(i);
		}

		cells[0].setCellStructure(cells, maxCols, maxRows, cellPrecisionUnitSize);
	}

	private boolean isPosAccessible(double newCol, double newRow){
		int col = (int) Math.floor(newCol), row = (int) Math.floor(newRow);
		if(col < 0 || row < 0)
			return false;

		int cellIndex = row*maxCols + col;
		boolean accessible = cells[cellIndex].isAccessible();
		if(!accessible)
			return false;

		double dx = newCol - col, dy = newRow - row;
		// dy > dx: vertical movement;
		// dy < dx: horizontal movement;
		if(dy > dx){
			accessible = cells[cellIndex + 1].isAccessible();
			if(!accessible)
				return false;

			row += 2;
			if(row >= rowCells)
				return false;

			cellIndex = row*maxCols + col;
			accessible = cells[cellIndex].isAccessible();
			return accessible && cells[cellIndex + 1].isAccessible();
		} else {
			cellIndex = (row + 1)*maxCols + col;
			accessible = cells[cellIndex].isAccessible();
			if(!accessible)
				return false;

			col += 2;
			if(col >= colCells)
				return false;

			cellIndex += 2;
			int cellIndex2 = row*maxCols + col;
			return cells[cellIndex].isAccessible() && cells[cellIndex2].isAccessible();
		}
	}

	private void changeCellPositionToClosest(Cell cell, double x, double y){
		int col = (int) Math.round(x), row = (int) Math.round(y);
		cell.setPos(col, row);
	}

	public void loadMap(String mapFileName, MapLoader mapLoader, GameView view){
		bulletsCount = 0;
		if(player1 == null || player2 == null)
			throw new NullPointerException("Can not load map: players are not set");

		player1.setDefaultPlayerPosition();
		player2.setDefaultPlayerPosition();

		mapLoader.loadMap(cells[0], mapFileName, player1, player2, enemyPorts, treesIds, view);
		currentStepsForNewTank--;
		if(currentStepsForNewTank < minimumStepsForNewTank)
			currentStepsForNewTank = minimumStepsForNewTank;
	}

	public void setFirstPlayer(PlayerAITank player){
		player1 = player;
	}
	public void setSecondPlayer(PlayerAITank player){
		player2 = player;
	}

	private void addBullet(Bullet bullet){
		if(bulletsCount + 1 > bullets.length){
			int newLength = (int)(bullets.length*bulletsCountMultiplier);
			Bullet[] newBullets = new Bullet[newLength];
			System.arraycopy(bullets,0, newBullets, 0, bullets.length);
			bullets = newBullets;
		}
		bullets[bulletsCount] = bullet;
		bulletsCount++;
	}
	private void removeBullet(int index){
		if(index >= bulletsCount)
			return;

		int i = index + 1;
		while(i < bulletsCount){
			bullets[i - 1] = bullets[i];
			i++;
		}
		bulletsCount--;
	}

	private void createCollectible(){
		Cell player1Cell, player2Cell;

		player1Cell = new Cell();
		player2Cell = new Cell();
		player1.setUpCell(player1Cell);
		player2.setUpCell(player2Cell);

		int randomRow;
		boolean collide = true;
		while(collide){
			randomRow = rand.randomOdd(rowCells - 1)*cellPrecisionUnitSize;
			collectibles.setPos(rand.randomOdd(colCells - 1)*cellPrecisionUnitSize, randomRow);

			collide = player1Cell.collide(collectibles, cellPrecisionUnitSize);
			if(!collide)
				collide = player2Cell.collide(collectibles, cellPrecisionUnitSize);
		}

		MapCell[] mapCells = new MapCell[]{MapCell.TIMER, MapCell.BOMB, MapCell.STAR, MapCell.TANK_LIVE, MapCell.HELMET, MapCell.SPADE};
		randomRow = rand.randRange(0, mapCells.length);
		collectibles.setMapCell(mapCells[randomRow]);
	}

	private void collect(PlayerAITank player){
		switch(collectibles.getMapCell() ){
			case TIMER:
				collectibleTimer = stepsPerSecond*30;
				break;
			case BOMB:
				// todo: perform explosions for all enemy tanks;
				break;
			case STAR:
				player.promoteDegrade(true);
				break;
			case TANK_LIVE:
				// todo add live for player;
				break;
			case HELMET:
				collectibleTimer = stepsPerSecond*30;
				// todo: make player with blinking cell indestructible;
				break;
			case SPADE:
				collectibleTimer = stepsPerSecond*30;
				// todo: create steel around eagle;
				break;
		}
		collectibles.setMapCell(null);
	}

	private void performEnvironmentExplosion(int bulletIndex){
		Cell cellRight, cellLeft;
		boolean exploded = false;

		bullets[bulletIndex].getRightCornerPos(xyPos);
		cellRight = cellByPosition(xyPos[0], xyPos[1]);
		bullets[bulletIndex].getLeftCornerPos(xyPos);
		cellLeft = cellByPosition(xyPos[0], xyPos[1]);

		if(cellRight != null && cellRight.isDestructible() ){
			exploded = bullets[bulletIndex].setRightDamageCell(cellRight);
			cellRight.unblockMovementsAround();
		}

		if(cellLeft != null && cellLeft.isDestructible() ){
			exploded |= bullets[bulletIndex].setLeftDamageCell(cellLeft);
			cellLeft.unblockMovementsAround();
		}

		if(exploded)
			bullets[bulletIndex].setSmallExplode();
	}

	private int bulletsContact(int bulletIndex){
		Cell bulletCell, bulletCheckCell;
		int i;
		boolean collide;

		bulletCell = new Cell();
		bulletCheckCell = new Cell();
		bullets[bulletIndex].setUpCell(bulletCell);

		for(i = bulletIndex + 1; i < bulletsCount; i++){
			if(bullets[i].isExploding() )
				continue;

			bullets[i].setUpCell(bulletCheckCell);
			collide = bulletCheckCell.collide(bulletCell, cellPrecisionUnitSize);
			if(collide)
				return i;
		}
		return -1;
	}

	private void moveBullets(){
		boolean keepMoving;

		final int colLimit = (colCells - 1)*cellPrecisionUnitSize;
		int i = 0, bulletIndex;

		while(i < bulletsCount){
			keepMoving = bullets[i].move();
			if(!keepMoving){// explosion sprite finished;
				removeBullet(i);
				continue;
			}

			// - - - bullet touches border:
			bullets[i].getBulletPos(xyPos);
			if(xyPos[0] <= 0 || xyPos[1] <= 0 || xyPos[0] >= colLimit || xyPos[1] >= (rowCells-1)*cellPrecisionUnitSize){
				bullets[i].setSmallExplode();
				i++;
				continue;
			}

			if(bullets[i].belongsToPlayer() ){
				bulletIndex = bulletsContact(i);
				if(bulletIndex >= 0){
					bullets[i].setSmallExplode();
					removeBullet(bulletIndex);
					continue;
				}
			}

			performEnvironmentExplosion(i);
			i++;
		}

	}

	private void movePlayer(PlayerAITank player){
		boolean moved = player.move(xyPos);
		Cell checkCell = cellByPosition(xyPos[0], xyPos[1]);
		if(moved){
			player.blockMovement(checkCell, xyPos[0], xyPos[1]);
		}

		if(player.fireBullet(damages) ){
			Bullet bullet = new Bullet(player, damages);
			addBullet(bullet);
		}

		checkCell = new Cell();
		player.setUpCell(checkCell);
		if(checkCell.collide(collectibles, cellPrecisionUnitSize) )
			collect(player);
	}

	public void nextStep(){

		movePlayer(player1);

		movePlayer(player2);

		moveBullets();

		// temporary creating collectible for testing;
		int tensSeconds = steps/(stepsPerSecond*10);
		if(tensSeconds%2 == 1 && collectibles.getMapCell() == null){
			createCollectible();
		} else if(tensSeconds%2 == 0)
			collectibles.setMapCell(null);

		steps++;
	}


	public void setCellSize(int cellSize){
		cellUnitSize = 16;
		if(cellSize > 0)
			cellUnitSize = cellSize;
	}

	@Override
	public Iterator<Cell> iterator(){
		Iterator<Cell> iter = new Iterator<>() {
			Cell iterCell = new Cell();
			private boolean iterateEnvironment = true, iterateTanks = true, iterateBullets = bulletsCount > 0;
			private boolean player1NotIterated = true, player2NotIterated = player2 != null;
			private boolean iterateTrees = treesIds.size() > 0, drawCollectible = collectibles.getMapCell() != null;
			private boolean iteratePorts = enemyPorts.size() > 0;
			private int iterateIndex = 0;
			private Iterator<Enemy> tankIter = tanks.iterator();
			private final int treesCount = treesIds.size();

			@Override
			public boolean hasNext(){
				return iterateEnvironment || iterateTanks || iterateBullets ||
						iterateTrees || iteratePorts || drawCollectible;
			}

			@Override
			public Cell next(){
				Cell cell;
				boolean doRound = false;

				if(iterateEnvironment){
					int index = iterateIndex/colCells*(maxCols - colCells) + iterateIndex;// index + remaining cols;
					cell = cells[index];
					iterCell.setPos(cell.getCol(), cell.getRow() );
					iterCell.setMapCell(cell.getMapCell());
					doRound = true;

					iterateIndex++;
					if(iterateIndex >= rowCells*colCells){
						iterateIndex = 0;
						iterateEnvironment = false;
					}
				} else if(iterateTanks){
					if(player1NotIterated){
						player1.setUpCell(iterCell);
						doRound = true;

						player1NotIterated = false;
					} else if(player2NotIterated){
						player2.setUpCell(iterCell);
						doRound = true;

						player2NotIterated = false;
					} else if(tankIter.hasNext() ){
						cell = tankIter.next().getCell();
					} else {
						iterateTanks = false;
					}
				} else if(iterateBullets){
					bullets[iterateIndex++].setUpCell(iterCell);
					doRound = true;
					if(iterateIndex >= bulletsCount){
						iterateBullets = false;
						iterateIndex = 0;
					}
				} else if(iterateTrees) {
					int treeInd = treesIds.get(iterateIndex);
					iterateIndex++;
					cell = cells[treeInd];// be sure this is properly implemented;
					iterCell.setPos(cell.getCol(), cell.getRow());
					iterCell.setMapCell(cell.getMapCell());
					doRound = true;

					iterateTrees = iterateIndex < treesCount;
					if(!iterateTrees)
						iterateIndex = 0;
				} else if(iteratePorts){
					enemyPorts.get(iterateIndex++).setUpCell(iterCell);
					doRound = true;

					iteratePorts = iterateIndex < enemyPorts.size();
					if(!iteratePorts)
						iterateIndex = 0;
				} else if(drawCollectible){
					iterCell.setMapCell( collectibles.getMapCell() );
					iterCell.setPos( collectibles.getCol(), collectibles.getRow() );
					doRound = true;
					drawCollectible = false;
				}

				if(doRound)
					iterCell.roundPos(cellPrecisionUnitSize, cellUnitSize);

				return iterCell;
			}

			@Override
			public void remove(){

			}
		};
		return iter;
	}
}
