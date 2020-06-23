package com.company;

import com.company.logic.BattleRandom;
import com.company.model.*;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;
import com.company.view.MapLoader;

import java.io.IOException;
import java.util.*;

public class GameDynamics implements Iterable<Cell> {
	private PlayerAITank player1;
	private PlayerAITank player2;

	private Queue<Enemy> tanksList;
	private Enemy[] activeTanks;
	private EnemyPorts ports;

	private Bullet[] bullets;
	private int bulletsCount;
	private final double bulletsCountMultiplier = 1.4;
	private DamageClass damages;

	private Cell[] cells;
	private int rowCells;
	private int colCells;
	private final int maxCols;
	private int eagleIndex, bulletOnEagleIndex;
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
		steps = 0;

		int sizeBegin = 10;// any beginning size;

		tanksList = new LinkedList<>();
		activeTanks = new Enemy[sizeBegin];
		ports = new EnemyPorts(stepsPerSecond);
		treesIds = new ArrayList<>();

		bulletsCount = 0;
		bullets = new Bullet[sizeBegin];
		eagleIndex = bulletOnEagleIndex = -1;

		setCellsStructure(maxCols, mapLoader.getMaxRows() );
		collectibles = new Cell();

		damages = DamageClass.getInstance();

		rand = new BattleRandom();
	}

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

	/*private boolean isPosAccessible(double newCol, double newRow){
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
	}*/

	public void loadMap(String mapFileName, MapLoader mapLoader, GameView view){
		bulletsCount = 0;
		if(player1 == null || player2 == null)
			throw new NullPointerException("Can not load map: players are not set");

		player1.setDefaultPlayerPosition();
		player2.setDefaultPlayerPosition();

		boolean loaded = true;
		try {
			mapLoader.loadMap(cells[0], mapFileName, player1, player2, ports, tanksList, treesIds, view);
		} catch(IOException | NullPointerException e){
			System.out.println("Loading map  " + mapFileName + "  failed!");
			loaded = false;
		}

		if(loaded) {
			rowCells = view.getRowCells();
			setEagleIndex();
			player1.revive();
			player2.revive();
			ports.levelUpPorts();
			ports.activatePort();
		}
	}

	public int get1stPlayerLifes(){
		if(player1 == null)
			return 0;
		return player1.getLifes();
	}
	public int get2ndPlayerLifes(){
		if(player2 == null)
			return 0;
		return player2.getLifes();
	}

	private void setEagleIndex(){
		bulletOnEagleIndex = -1;

		int i = 0, indexLimit = rowCells*colCells, mapIndex;
		for(eagleIndex = -1; i < indexLimit; i++){
			mapIndex = i/colCells*(maxCols - colCells) + i;// i + remaining cols;
			if(cells[i].getMapCell() == MapCell.EAGLE){
				eagleIndex = mapIndex;
				return;
			}
		}
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
		MapCell[] mapCells = new MapCell[]{MapCell.TIMER, MapCell.BOMB, MapCell.STAR, MapCell.TANK_LIVE, MapCell.HELMET, MapCell.SPADE};
		randomRow = rand.randRange(0, mapCells.length);
		collectibles.setMapCell(mapCells[randomRow]);

		boolean collide = true;
		while(collide){
			randomRow = rand.randomOdd(rowCells - 1)*cellPrecisionUnitSize;
			collectibles.setPos(rand.randomOdd(colCells - 1)*cellPrecisionUnitSize, randomRow);

			collide = player1Cell.collide(collectibles, cellPrecisionUnitSize);
			if(!collide)
				collide = player2Cell.collide(collectibles, cellPrecisionUnitSize);
			if(!collide && eagleIndex >= 0)
				collide = cellCollideEagle(collectibles);
		}
	}

	private void collect(PlayerAITank player){
		MapCell collectibleType = collectibles.getMapCell();
		if(collectibleType == null)
			return;

		switch(collectibleType){
			case TIMER:
				collectibleTimer = stepsPerSecond*10;
				break;
			case BOMB:
				// todo: perform explosions for all enemy tanks;
				break;
			case STAR:
			case TANK_LIVE:
			case HELMET:
				player.useCollectible(collectibleType);
				if(collectibleType == MapCell.HELMET)
					collectibleTimer = stepsPerSecond*30;
				break;
			case SPADE:
				collectibleTimer = stepsPerSecond*20;
				encircleEagle(MapCell.STEEL);
				break;
		}
		collectibles.setMapCell(null);
	}

	private void encircleEagle(MapCell cellType){
		if(eagleIndex < 0)
			return;

		cells[eagleIndex].encircleByMapCell(cellType);
	}

	private boolean cellCollideEagle(Cell cell){
		if(eagleIndex < 0 || cell == null)
			return false;

		return cell.collide(cells[eagleIndex], cellPrecisionUnitSize);
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

	private int bulletsContact(Cell bulletCell, int bulletIndex){
		Cell bulletCheckCell;
		int i;
		boolean collide;

		bulletCheckCell = new Cell();
		//bullets[bulletIndex].setUpCell(bulletCell);

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

	private boolean moveBullets(){
		boolean keepMoving, eagleExists = true, isPlayers;

		final int colLimit = (colCells - 1)*cellPrecisionUnitSize;
		int i = 0, bulletIndex;
		Cell bulletCell = new Cell(), tankCell = new Cell();

		while(i < bulletsCount && eagleExists){
			keepMoving = bullets[i].move();
			if(!keepMoving){// explosion sprite finished;
				removeBullet(i);
				if(i == bulletOnEagleIndex || cells[eagleIndex].getMapCell() == MapCell.EAGLE_DESTROYED)
					eagleExists = false;
				continue;
			}

			if(bullets[i].isExploding() ) {
				i++;
				continue;
			}

			// - - - bullet touches border:
			bullets[i].getBulletPos(xyPos);
			if(xyPos[0] <= 0 || xyPos[1] <= 0 || xyPos[0] >= colLimit || xyPos[1] >= (rowCells-1)*cellPrecisionUnitSize){
				bullets[i].setSmallExplode();
				i++;
				continue;
			}

			bullets[i].setUpCell(bulletCell);
			if(cellCollideEagle(bulletCell) ){
				cells[eagleIndex].setMapCell(MapCell.EAGLE_DESTROYED);
				bullets[i].setExplode(cells[eagleIndex]);
				bulletOnEagleIndex = i;
			}

			isPlayers = bullets[i].belongsToPlayer();
			if(isPlayers){
				boolean explodeContinue = false;
				bullets[i].setUpCell(bulletCell);

				bulletIndex = bulletsContact(bulletCell, i);
				if(bulletIndex >= 0){
					explodeContinue = true;
					bullets[bulletIndex].resetBulletShooting();
					removeBullet(bulletIndex);
				}

				if(bullets[i].belongsToPlayer(player1) && !explodeContinue){
					player2.setUpCell(tankCell);
					if(bulletCell.collide(tankCell, cellPrecisionUnitSize) ){
						player2.makeFreezed();
						explodeContinue = true;
					}
				} else if(!explodeContinue){
					player1.setUpCell(tankCell);
					if(bulletCell.collide(tankCell, cellPrecisionUnitSize) ){
						player1.makeFreezed();
						explodeContinue = true;
					}
				}

				if(explodeContinue){
					bullets[i].setSmallExplode();
					continue;
				}
			}

			performEnvironmentExplosion(i);

			// if after all environment interactions bullet still runs;
			// check its interactions with ports if it is the players one;
			if(isPlayers && !bullets[i].isExploding() ){
				bullets[i].setUpCell(bulletCell);
				ports.blockBlinking(bulletCell, cellPrecisionUnitSize);
			}

			i++;
		}
		return eagleExists;
	}

	private void movePlayer(PlayerAITank player){
		Cell tankNewPositionCell, tankCurrentCell, environmentCell;
		boolean moved = player.requestedPosition(xyPos);

		environmentCell = cellByPosition(xyPos[0], xyPos[1]);// environment cell for (x, y) position;
		tankNewPositionCell = new Cell();
		tankCurrentCell = new Cell();

		if(moved){
			player.setUpCell(tankCurrentCell);
			player.setUpCell(tankNewPositionCell);// players tank cell before movement;
			tankNewPositionCell.setPos(xyPos[0], xyPos[1]);// cell on new position of tank;
			// todo: check conditions between tanks; use direction as a hint where to search other tanks;

			moved = ports.canMove(tankCurrentCell, tankNewPositionCell, cellPrecisionUnitSize);
		}

		if(moved){
			player.moveOrBlock(environmentCell, xyPos[0], xyPos[1]);
		}

		if(player.fireBullet() ){
			Bullet bullet = new Bullet(player, damages);
			addBullet(bullet);
		}

		player.setUpCell(tankNewPositionCell);
		if(tankNewPositionCell.collide(collectibles, cellPrecisionUnitSize) )
			collect(player);
	}

	public boolean nextStep(){
		int tensSeconds;

		movePlayer(player1);

		movePlayer(player2);

		boolean eagleExists = true;
		eagleExists = moveBullets();

		if(collectibleTimer > 0){
			collectibleTimer--;
			if(collectibleTimer == 0)
				encircleEagle(MapCell.BRICK);
		}

		// temporary creating collectible for testing;
		tensSeconds = steps/(stepsPerSecond*10);
		if(tensSeconds%2 == 1 && collectibles.getMapCell() == null){
			createCollectible();
			ports.activatePort();// testing ports;
		} else if(tensSeconds%2 == 0)
			collectibles.setMapCell(null);

		ports.nextStep();

		steps++;
		return eagleExists;
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
			private boolean iteratePlayer1 = player1 != null;
			private boolean player1Immortality = iteratePlayer1 && player1.getImmortalityCell() != null;
			private boolean iteratePlayer2 = player2 != null;
			private boolean player2Immortality = iteratePlayer2 && player2.getImmortalityCell() != null;
			private boolean iterateTrees = treesIds.size() > 0, drawCollectible = collectibles.getMapCell() != null;
			private boolean iteratePorts = ports.size() > 0;
			private int iterateIndex = 0;
			private Iterator<Enemy> tankIter = tanksList.iterator();
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
					iterCell.setByOtherCell(cell);
					doRound = true;

					iterateIndex++;
					if(iterateIndex >= rowCells*colCells){
						iterateIndex = 0;
						iterateEnvironment = false;
					}
				} else if(iteratePlayer1){
					player1.setUpCell(iterCell);
					if(player1Immortality){
						iterCell.setMapCell( player1.getImmortalityCell() );
						player1Immortality = false;
					} else
						iteratePlayer1 = false;
					doRound = true;
				} else if(iteratePlayer2){
					player2.setUpCell(iterCell);
					if(player2Immortality){
						iterCell.setMapCell( player2.getImmortalityCell() );
						player2Immortality = false;
					} else
						iteratePlayer2 = false;
					doRound = true;

				} else if(iterateTanks){
					if(tankIter.hasNext() ){
						cell = tankIter.next().getCell();
					} else
						iterateTanks = false;

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
					iterCell.setByOtherCell(cell);
					doRound = true;

					iterateTrees = iterateIndex < treesCount;
					if(!iterateTrees)
						iterateIndex = 0;
				} else if(iteratePorts){
					ports.setNextCell(iterCell);
					iterateIndex++;
					doRound = true;

					iteratePorts = iterateIndex < ports.size();
					if(!iteratePorts)
						iterateIndex = 0;
				} else if(drawCollectible){
					iterCell.setByOtherCell(collectibles);
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
