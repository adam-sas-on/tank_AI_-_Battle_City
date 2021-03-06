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
	private Enemy[] explodingTanks;
	private int tanksSpritesCount, explodingTanksCount;
	private EnemyPorts ports;

	private Bullet[] bullets;
	private int bulletsCount;
	private Bullet[] explosions;
	private int explosionsCount;
	private final double bulletsCountMultiplier = 1.4;
	private DamageClass damages;

	private Cell[] cells;
	private int rowCells;
	private int colCells;
	private final int maxCols;
	private int eagleIndex, bulletOnEagleIndex;
	private Cell collectibles;
	private int collectibleTimer;// timer how long tank can be suspended or how long player can be indestructible;
	private int cellUnitSize;
	private final int cellPrecisionUnitSize;
	//private final int intervalInMs;

	private BattleRandom rand;
	private int[] xyPos = new int[2];
	private int steps;
	private final int stepsPerSecond;


	public GameDynamics(MapLoader mapLoader, GameView view, BattleRandom random){
		rowCells = colCells = 26;// default Battle City map size;
		this.maxCols = mapLoader.getMaxCols();

		cellPrecisionUnitSize = view.getDefaultCellSize();
		stepsPerSecond = view.getFramesPerSecond();
		//intervalInMs = view.getIntervalInMilliseconds();
		steps = 0;

		int sizeBegin = 10;// any beginning size;

		tanksList = new LinkedList<>();
		activeTanks = new Enemy[sizeBegin];
		explodingTanks = new Enemy[sizeBegin];
		tanksSpritesCount = explodingTanksCount = 0;
		ports = new EnemyPorts(stepsPerSecond);

		bulletsCount = explosionsCount = 0;
		bullets = new Bullet[sizeBegin];
		explosions = new Bullet[sizeBegin];
		eagleIndex = bulletOnEagleIndex = -1;

		setCellsStructure(maxCols, mapLoader.getMaxRows() );
		collectibles = new Cell();

		damages = DamageClass.getInstance();

		rand = random;
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
		tanksSpritesCount = 0;

		boolean loaded = true;
		try {
			mapLoader.loadMap(cells[0], mapFileName, player1, player2, ports, tanksList, view);
		} catch(IOException | NullPointerException e){
			System.out.println("Loading map  " + mapFileName + "  failed!");
			loaded = false;
		}

		if(loaded) {
			rowCells = view.getRowCells();
			setEagleIndex();
			if(player1.getLifes() > 0)
				player1.revive();
			if(player2.getLifes() > 0)
				player2.revive();
			ports.setAmountOfTanks(tanksList);
			ports.levelUpPorts();
			ports.activatePort();
			steps = 0;
			collectibles.setMapCell(null);
		}
	}

	// - - - - - - - - - - - - - - - - Getters for players statistic- - - - - - - - - - - - - - - - -
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

	public int get1stPlayerPoints(){
		if(player1 == null)
			return 0;
		return player1.getPoints();
	}
	public int get2ndPlayerPoints(){
		if(player2 == null)
			return 0;
		return player2.getPoints();
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

			Bullet[] newExplodes = new Bullet[newLength];
			System.arraycopy(explosions, 0, newExplodes, 0, explosions.length);
			explosions = newExplodes;
		}
		bullets[bulletsCount] = bullet;
		bulletsCount++;
	}
	private <T> int removeFromArray(T[] array, int index, int count){
		if(index >= count || count < 1)
			return count;

		int i = index + 1;
		while(i < count){
			array[i - 1] = array[i];
			i++;
		}
		return count - 1;
	}

	private void addTank(Enemy tank){
		if(tanksSpritesCount + 1 > activeTanks.length){
			int newLength = (int)(activeTanks.length*bulletsCountMultiplier);
			Enemy[] newTanks = new Enemy[newLength];
			System.arraycopy(activeTanks,0, newTanks, 0, activeTanks.length);
			activeTanks = newTanks;

			Enemy[] newExplodingTanks = new Enemy[newLength];
			System.arraycopy(explodingTanks, 0, newExplodingTanks, 0, explodingTanks.length);
			explodingTanks = newExplodingTanks;
		}
		activeTanks[tanksSpritesCount] = tank;
		tanksSpritesCount++;
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

		int i;
		switch(collectibleType){
			case TIMER:
				for(i = 0; i < tanksSpritesCount; i++)
					activeTanks[i].makeFreezed();
				break;
			case BOMB:
				Cell bombCell = new Cell();
				bombCell.setMapCell(MapCell.BOMB);
				for(i = 0; i < tanksSpritesCount; i++){
					activeTanks[i].getHit(bombCell, null);
					explodingTanks[explodingTanksCount] = activeTanks[i];
					explodingTanksCount++;
					ports.removingTankFromMap();
				}
				tanksSpritesCount = 0;
				break;
			case STAR:
			case TANK_LIVE:
			case HELMET:
				if(collectibleType == MapCell.HELMET)
					collectibleTimer = stepsPerSecond*30;
				break;
			case SPADE:
				collectibleTimer = stepsPerSecond*20;
				encircleEagle(MapCell.STEEL);
				break;
		}
		player.useCollectible(collectibleType);
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


	private int addEnemyExplodesToList(List<Cell> explodesCell, int startIndex){
		Cell cell;
		int i, j = startIndex, count = explodesCell.size();

		for(i = 0; i < explodingTanksCount; i++){
			if(explodingTanks[i].exists() )
				continue;

			if(j < count){
				cell = explodesCell.get(j);
			} else
				cell = new Cell();

			explodingTanks[i].setUpCell(cell);
			cell.setUnsetDoubleSize(true);
			if(j >= count)
				explodesCell.add(cell);
			j++;
		}
		return j;
	}

	private int addPlayerExplodesToList(List<Cell> explodesCell, PlayerAITank player, int startIndex){
		Cell cell;
		int j = startIndex, count = explodesCell.size();

		if(player != null && !player.exists() && player.getLifes() >= 0){
			if(j < count)
				cell = explodesCell.get(j);
			else
				cell = new Cell();

			player.setUpCell(cell);
			cell.setUnsetDoubleSize(true);
			if(j >= count)
				explodesCell.add(cell);
			j++;
		}

		return j;
	}
	private int addPlayersExplodesToList(List<Cell> explodesCell, int startIndex){
		int j;

		j = addPlayerExplodesToList(explodesCell, player1, startIndex);
		j = addPlayerExplodesToList(explodesCell, player2, j);
		return j;
	}

	public int getExplodes(List<Cell> explodesCells){
		Cell cell;
		int i, count = explodesCells.size(), newSize;

		for(i = 0; i < explosionsCount; i++){
			if(i < count){
				cell = explodesCells.get(i);
			} else
				cell = new Cell();

			explosions[i].setUpCell( cell );
			cell.setUnsetDoubleSize(false);
			if(i >= count)
				explodesCells.add(cell);
		}

		newSize = addEnemyExplodesToList(explodesCells, explosionsCount);
		newSize = addPlayersExplodesToList(explodesCells, newSize);

		return newSize;
	}

	private void explodeBullet(int bulletIndex, Cell destroyedCell){
		explosions[explosionsCount] = bullets[bulletIndex];
		bulletsCount = removeFromArray(bullets, bulletIndex, bulletsCount);

		if (destroyedCell == null) {
			explosions[explosionsCount].setSmallExplode();
		} else
			explosions[explosionsCount].setExplode(destroyedCell);
		explosionsCount++;
	}

	private boolean performEnvironmentExplosion(int bulletIndex){
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
			explodeBullet(bulletIndex, null);

		return exploded;
	}

	private int tankHitByPlayer(Cell playersBulletCell, Cell tankBufferCell, boolean firstPlayersBullet){
		int i;
		int hitPoints;

		for(i = 0; i < tanksSpritesCount; i++){
			hitPoints = activeTanks[i].getHit(playersBulletCell, tankBufferCell);
			if(hitPoints > 0){
				if( activeTanks[i].clearPowerUp() )
					createCollectible();

				if( !activeTanks[i].exists() ){
					explodingTanks[explodingTanksCount] = activeTanks[i];
					tanksSpritesCount = removeFromArray(activeTanks, i, tanksSpritesCount);
					explodingTanksCount++;
					ports.removingTankFromMap();
				}

				if(firstPlayersBullet)
					player1.addPoints(hitPoints);
				else
					player2.addPoints(hitPoints);
				return i;
			}
		}
		return -1;
	}

	private boolean playerToPlayerBullet(Cell bulletCell, Cell tankBufferCell, boolean firstPlayersBullet){
		if(player1.getLifes() < 1 || player2.getLifes() < 1)
			return false;

		boolean isContact = false;

		if(firstPlayersBullet){
			player2.setUpCell(tankBufferCell);
			if(bulletCell.collide(tankBufferCell, cellPrecisionUnitSize) ){
				player2.makeFreezed();
				isContact = true;
			}
		} else {
			player1.setUpCell(tankBufferCell);
			if(bulletCell.collide(tankBufferCell, cellPrecisionUnitSize) ){
				player1.makeFreezed();
				isContact = true;
			}
		}

		return isContact;
	}

	private int bulletsContact(Cell bulletCell, int bulletIndex, boolean isPlayerBullet){
		Cell bulletCheckCell;
		int i;
		boolean collide, isCurrentPlayers;

		bulletCheckCell = new Cell();

		for(i = bulletIndex + 1; i < bulletsCount; i++){
			isCurrentPlayers = bullets[i].belongsToPlayer();
			if(!isPlayerBullet && !isCurrentPlayers)
				continue;

			bullets[i].setUpCell(bulletCheckCell);
			collide = bulletCheckCell.collide(bulletCell, cellPrecisionUnitSize);
			if(collide)
				return i;
		}
		return -1;
	}

	private boolean stepOfExplodes(){
		boolean eagleExists = true, keepSimulate;
		int i = 0;

		while(i < explosionsCount){
			keepSimulate = explosions[i].move();
			if(!keepSimulate){
				explosionsCount = removeFromArray(explosions, i, explosionsCount);
				if(i == bulletOnEagleIndex)
					eagleExists = false;
				continue;
			}
			i++;
		}

		i = 0;
		while(i < explodingTanksCount){
			if(explodingTanks[i].doRemove() ){
				explodingTanksCount = removeFromArray(explodingTanks, i, explodingTanksCount);
				continue;
			}
			i++;
		}

		return eagleExists;
	}

	private boolean moveBullets(){
		boolean isHitExplosion, eagleExists, isPlayers;

		final int colLimit = (colCells - 1)*cellPrecisionUnitSize;
		int i = 0, bulletOrTankIndex, tanksCount;
		Cell bulletCell = new Cell(), tankCell = new Cell();

		// simulate explodes;
		eagleExists = stepOfExplodes();

		while(i < bulletsCount){
			bullets[i].move();

			// - - - bullet touches border:
			bullets[i].getBulletPos(xyPos);
			if(xyPos[0] <= 0 || xyPos[1] <= 0 || xyPos[0] >= colLimit || xyPos[1] >= (rowCells-1)*cellPrecisionUnitSize){
				explodeBullet(i, null);
				continue;
			}

			bullets[i].setUpCell(bulletCell);
			if(cellCollideEagle(bulletCell) ){
				cells[eagleIndex].setMapCell(MapCell.EAGLE_DESTROYED);
				explodeBullet(i, cells[eagleIndex]);
				bulletOnEagleIndex = explosionsCount - 1;
			}

			isPlayers = bullets[i].belongsToPlayer();

			bullets[i].setUpCell(bulletCell);
			bulletOrTankIndex = bulletsContact(bulletCell, i, isPlayers);
			if(bulletOrTankIndex >= 0){
				explodeBullet(bulletOrTankIndex, null);
				bullets[i].resetBulletShooting();
				bulletsCount = removeFromArray(bullets, i, bulletsCount);
				continue;
			}

			if(isPlayers){
				boolean explodeContinue, firstPlayer = bullets[i].belongsToPlayer(player1);
				bullets[i].setUpCell(bulletCell);

				explodeContinue = playerToPlayerBullet(bulletCell, tankCell, firstPlayer);

				if(explodeContinue){
					explodeBullet(i, null);
					continue;
				}

				tanksCount = tanksSpritesCount;
				bulletOrTankIndex = tankHitByPlayer(bulletCell, tankCell, firstPlayer);
				if(bulletOrTankIndex >= 0){// one tank get hit ...
					if(tanksCount == tanksSpritesCount)// but tank is not destroyed yet;
						explodeBullet(i, null);
					else {
						bullets[i].resetBulletShooting();
						bulletsCount = removeFromArray(bullets, i, bulletsCount);
					}

					continue;
				}
			} else {
				isHitExplosion = player1.getHit(bulletCell, tankCell);
				if(!isHitExplosion)
					isHitExplosion = player2.getHit(bulletCell, tankCell);

				if(isHitExplosion){
					isHitExplosion = bulletCell.collide(tankCell, cellPrecisionUnitSize);
					if(isHitExplosion)
						explodeBullet(i, null);
					continue;
				}
			}

			isHitExplosion = performEnvironmentExplosion(i);// if true -> continue (no i++);
			if(isHitExplosion)
				continue;

			// if after all environment interactions bullet still runs;
			// check its interactions with ports  if it is the players one;
			if(isPlayers){
				bullets[i].setUpCell(bulletCell);
				ports.blockBlinking(bulletCell, cellPrecisionUnitSize);
			}

			i++;
		}
		return eagleExists;
	}

	// - - - - - - - - - - - - - - - - - - - Simulating tanks - - - - - - - - - - - - - - - - - - - -

	private void tanksExplodes(Cell bufferCell){
		for(int i = 0; i < explodingTanksCount; i++){
			explodingTanks[i].requestedPosition(xyPos);
			explodingTanks[i].setUpCell(bufferCell);
		}

		if(player1.getLifes() <= 0)
			player1.requestedPosition(xyPos);
		if(player2.getLifes() <= 0)
			player2.requestedPosition(xyPos);
	}

	private <T extends Tank> boolean tankCanMove(T tank, Cell currentPositionCell, Cell newPositionCell, int[] position){
		boolean moved = tank.requestedPosition(position), portsDoNotBlock;

		if( !tank.exists() )
			return false;

		tank.setUpCell(currentPositionCell);

		tank.setUpCell(newPositionCell);
		newPositionCell.setPos(position[0], position[1]);

		portsDoNotBlock = ports.canMove(currentPositionCell, newPositionCell, cellPrecisionUnitSize);

		return moved&&portsDoNotBlock;
	}

	private void playersAction(PlayerAITank player, Cell positionCell){
		player.setUpCell(positionCell);

		if(player.fireBullet() ){
			Bullet bullet = new Bullet(player, damages);
			addBullet(bullet);
		}

		player.setUpCell(positionCell);
		if(positionCell.collide(collectibles, cellPrecisionUnitSize) )
			collect(player);
	}


	private void tanksAction(Enemy tank, Cell positionCell){
		tank.setUpCell(positionCell);

		if(tank.fireBullet() ){
			Bullet bullet = new Bullet(tank, damages);
			addBullet(bullet);
		}
	}

	private boolean setTwoCells(int tankIndex, int xRequestedPos, int yRequestedPos, Cell current, Cell requested){
		if(xRequestedPos < 0 || yRequestedPos < 0 || tankIndex >= tanksSpritesCount + 2)// 2 players
			return false;

		boolean exploding = false;
		if(tankIndex > 1)
			exploding = !activeTanks[tankIndex - 2].exists();

		if(exploding)
			return false;

		switch(tankIndex){
			case 0:
				player1.setUpCell(current);
				player1.setUpCell(requested);
				break;
			case 1:
				player2.setUpCell(current);
				player2.setUpCell(requested);
				break;
			default:
				activeTanks[tankIndex - 2].setUpCell(current);
				activeTanks[tankIndex - 2].setUpCell(requested);
		}
		requested.setPos(xRequestedPos, yRequestedPos);

		return true;
	}

	private void tanksToTanksMovement(boolean[] acceptance, int[] xy2Dpoints, Cell current, Cell requested){
		final int count = 2 + tanksSpritesCount;

		if(count < acceptance.length || count*2 < xy2Dpoints.length)
			return;// throw new...

		Cell currentB = new Cell();
		int i, j, i2;
		boolean accepted;

		for(i = 0; i < count; i++){
			i2 = i*2;
			accepted = setTwoCells(i, xy2Dpoints[i2], xy2Dpoints[i2 + 1], current, requested);
			if( !accepted ){
				acceptance[i] = false;
				continue;
			}

			for(j = 0; j < count; j++){
				if(i == j)
					continue;
				i2 = j*2;
				accepted = setTwoCells(j, xy2Dpoints[i2], xy2Dpoints[i2 + 1], currentB, requested);
				if( !accepted ){
					acceptance[j] = false;
					continue;
				}

				accepted = currentB.newPositionAcceptance(requested, current, cellPrecisionUnitSize);
				if( !accepted)
					acceptance[j] = false;
			}
		}
	}

	private void moveTanks(){
		Cell tankNewPositionCell, tankCurrentCell, environmentCell;
		int[] xyPosAll;
		boolean[] movementAccepted;
		int allTanksCount = 2;// players
		boolean moved, player1playing = false, player2playing = false;

		if(player1 != null){
			player1playing = player1.getLifes() > 0;
		}
		if(player2 != null){
			player2playing = player2.getLifes() > 0;
		}

		allTanksCount += tanksSpritesCount;
		xyPosAll = new int[allTanksCount*2];// pairs of points for all tanks: {player1, player2, ...rest tanks};
		movementAccepted = new boolean[allTanksCount];
		tankNewPositionCell = new Cell();
		tankCurrentCell = new Cell();

		if(player1playing){
			movementAccepted[0] = tankCanMove(player1, tankCurrentCell, tankNewPositionCell, xyPos);
			if( !movementAccepted[0] ){
				player1.getPos(xyPos);
			}
			xyPosAll[0] = xyPos[0];
			xyPosAll[1] = xyPos[1];
		} else
			xyPosAll[0] = xyPosAll[1] = -1;

		if(player2playing) {
			movementAccepted[1] = tankCanMove(player2, tankCurrentCell, tankNewPositionCell, xyPos);
			if( !movementAccepted[1] ){
				player2.getPos(xyPos);
			}
			xyPosAll[2] = xyPos[0];
			xyPosAll[3] = xyPos[1];
		} else
			xyPosAll[2] = xyPosAll[3] = -1;


		int i = 0, posIndexBegin = 2, i2;
		while(i < tanksSpritesCount){
			if( activeTanks[i].doRemove() ){
				tanksSpritesCount = removeFromArray(activeTanks, i, tanksSpritesCount);
				allTanksCount--;
				continue;
			}

			moved = tankCanMove(activeTanks[i], tankNewPositionCell, tankCurrentCell, xyPos);
			movementAccepted[posIndexBegin + i] = moved;

			i2 = (posIndexBegin + i)*2;
			if( !moved ){
				activeTanks[i].getPos(xyPos);
			}
			xyPosAll[i2] = xyPos[0];
			xyPosAll[i2 + 1] = xyPos[1];
			i++;
		}

		tanksExplodes(tankCurrentCell);

		tanksToTanksMovement(movementAccepted, xyPosAll, tankCurrentCell, tankNewPositionCell);


		for(i = 0; i < allTanksCount; i++){
			if( (i == 0 && !player1playing) || (i == 1 && !player2playing) || (i > 1 && !activeTanks[i - 2].exists()) )
				continue;

			i2 = i*2;
			environmentCell = cellByPosition(xyPosAll[i2], xyPosAll[i2 + 1]);// environment cell for (x, y) position;
			switch(i){
				case 0:
					playersAction(player1, tankCurrentCell);
					if(movementAccepted[i])
						player1.moveOrBlock(environmentCell, xyPosAll[i2], xyPosAll[i2 + 1]);
					break;
				case 1:
					playersAction(player2, tankCurrentCell);
					if( movementAccepted[i] )
						player2.moveOrBlock(environmentCell, xyPosAll[i2], xyPosAll[i2 + 1]);
					break;
				default:
					tanksAction(activeTanks[i - 2], tankCurrentCell);
					if(movementAccepted[i])
						activeTanks[i - 2].moveOrBlock(environmentCell, xyPosAll[i2], xyPosAll[i2 + 1]);
			}
		}

	}

	public boolean isMapFinished(){
		int totalTanks = tanksSpritesCount + explodingTanksCount + tanksList.size();

		return totalTanks <= 0;
	}

	public boolean nextStep(){
		if( isMapFinished() )
			return true;

		moveTanks();

		boolean eagleExists;
		eagleExists = moveBullets();

		if(collectibleTimer > 0){
			collectibleTimer--;
			if(collectibleTimer == 0)
				encircleEagle(MapCell.BRICK);
		}


		boolean createNewTank;
		ports.activatePort();
		createNewTank = ports.nextStep(xyPos);

		if(createNewTank && !tanksList.isEmpty() ){
			Enemy tank = tanksList.poll();
			tank.setPos(xyPos[0], xyPos[1]);
			if(eagleIndex >= 0)
				tank.setEaglePosition(cells[eagleIndex]);
			addTank(tank);
			if( tank.hasPowerUp() )
				collectibles.setMapCell(null);
		}

		steps++;
		return eagleExists && (player1.getLifes() > 0 || player2.getLifes() > 0);
	}

	public void resetTheGame(){
		steps = 0;
		tanksSpritesCount = explodingTanksCount = 0;
		bulletsCount = explosionsCount = 0;
		collectibles.setMapCell(null);
		player1.reset();
		player2.reset();
	}

	// - - - - - - - - - - - - - - Methods for iterator and for drawing - - - - - - - - - - - - - -

	public void setFromCollectible(Cell cellToSet){
		if(cellToSet == null)
			return;
		cellToSet.setByOtherCell(collectibles);
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
			private boolean iterateEnvironment = true;
			private boolean iterateTanks = tanksSpritesCount > 0;
			private boolean iterateBullets = bulletsCount > 0;

			private boolean iteratePlayer1 = player1 != null && player1.exists();
			private boolean player1Immortality = iteratePlayer1 && player1.getImmortalityCell() != null;
			private boolean iteratePlayer2 = player2 != null && player2.exists();
			private boolean player2Immortality = iteratePlayer2 && player2.getImmortalityCell() != null;

			private boolean iteratePorts = ports.size() > 0;
			private int iterateIndex = 0;

			@Override
			public boolean hasNext(){
				return iterateEnvironment || iterateTanks || iterateBullets ||
						iteratePorts;
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
					activeTanks[iterateIndex++].setUpCell(iterCell);
					doRound = true;

					if(iterateIndex >= tanksSpritesCount){
						iterateTanks = false;
						iterateIndex = 0;
					}
				} else if(iterateBullets){
					bullets[iterateIndex++].setUpCell(iterCell);
					doRound = true;
					if(iterateIndex >= bulletsCount){
						iterateBullets = false;
						iterateIndex = 0;
					}
				} else if(iteratePorts){
					ports.setNextCell(iterCell);
					iterateIndex++;
					doRound = true;

					iteratePorts = iterateIndex < ports.size();
					if(!iteratePorts)
						iterateIndex = 0;
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
