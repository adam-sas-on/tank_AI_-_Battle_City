package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;

import java.util.HashMap;
import java.util.Map;

public abstract class Enemy implements Tank {
	private BattleRandom randomEngine;
	protected int cellSpeed;
	protected int bulletSpeed;
	protected int x_pos, y_pos;
	private int eagleX, eagleY;
	protected int level;
	protected boolean isExploding;
	protected int points;
	protected boolean hasPowerUp;
	protected int currentDirection;
	protected Map<Integer, MapCell[]> icons;
	protected MapCell[] currentIcons;
	protected int currentIconInd;
	private int bulletSteps, bulletsInRange;
	private int freezeStepper;
	protected int nextBulletSteps;
	private boolean evenMove;
	private final int stepsFor5Sec;
	private final int size;
	private final int cellPrecisionSize;

	public Enemy(BattleRandom rand, GameView view){
		randomEngine = rand;
		int msInterval = view.getIntervalInMilliseconds(), cellUnitSize = view.getDefaultCellSize();

		cellPrecisionSize = cellUnitSize;
		size = (cellUnitSize*MapCell.TANK_LIGHT_STATE_1_UP.getSize() )/MapCell.getUnitSize();
		cellSpeed = (10*msInterval*cellUnitSize*2)/5000;// speed: 10 cells / 5000 ms;

		nextBulletSteps = 1000/msInterval;
		bulletSteps = 0;
		bulletsInRange = 0;
		bulletSpeed = (6*msInterval*cellUnitSize*2)/1000;// bullet speed: 6 cells / second;

		eagleX = eagleY = -1;
		stepsFor5Sec = 5000/msInterval;
		freezeStepper = 0;
		evenMove = false;
		isExploding = false;
		hasPowerUp = false;

		currentDirection = Direction.DOWN.getDirection();

		level = 1;
		icons = new HashMap<>();
		currentIconInd = 0;

	}

	private int roundInRange(final int value, final int rangeSize){
		int roundDown = (value/rangeSize)*rangeSize, diff;
		diff = ( 2*(value - roundDown) > rangeSize)?rangeSize:0;
		return diff + roundDown;
	}

	public void getPos(int[] xyPos){
		try {
			xyPos[0] = x_pos;
			xyPos[1] = y_pos;
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	public int getTankSize(){
		return size;
	}

	public int getBulletSpeed(){
		return bulletSpeed;
	}

	public int getBulletSteps(){
		return bulletSteps;
	}

	public void resetBulletShots(){
		bulletSteps = nextBulletSteps/2;
		bulletsInRange = 0;
	}

	public Direction getDirectionCode(){
		return Direction.directionByAngle(currentDirection);
	}

	@Override
	public void setUpCell(Cell cell){
		if(isExploding && (currentIconInd >= currentIcons.length || currentIconInd < 0) ){
			cell.setMapCell(null);
			currentIconInd = -1;
		} else {
			cell.setMapCell(currentIcons[currentIconInd]);
			cell.setPos(x_pos, y_pos);
		}
	}

	protected abstract void setIcons(boolean containsPowerUp);

	public void makeFreezed(){
		freezeStepper = stepsFor5Sec*2;
	}


	protected boolean isHit(Cell bulletCell, Cell tankBufferCell){
		if(bulletCell == null || tankBufferCell == null || isExploding)
			return false;

		tankBufferCell.setMapCell(currentIcons[currentIconInd]);
		tankBufferCell.setPos(x_pos, y_pos);
		return bulletCell.collide(tankBufferCell, cellPrecisionSize);
	}

	protected void setExplosion(){
		isExploding = true;
		currentIcons = MapCell.bigExplosionMapCells();
		currentIconInd = 0;
		x_pos -= cellPrecisionSize;
		y_pos -= cellPrecisionSize;
	}

	public int getHit(Cell bulletCell, Cell tankBufferCell){
		boolean hit = isHit(bulletCell, tankBufferCell);

		if(hit) {
			level--;
			if(level < 1)
				setExplosion();
		}

		return isExploding?points:0;
	}

	@Override
	public boolean exists(){
		return !isExploding;
	}
	public boolean doRemove(){
		return isExploding && currentIconInd < 0;
	}

	public void setEaglePosition(Cell eagleCell){
		if(eagleCell == null)
			return;
		if(eagleCell.getMapCell() != MapCell.EAGLE)
			return;

		eagleX = eagleCell.getCol();
		eagleY = eagleCell.getRow();
	}

	public void setPos(int x, int y){
		if(x >= 0)
			x_pos = x;
		if(y >= 0)
			y_pos = y;
	}

	public void moveOrBlock(Cell cell, int x, int y){
		if(cell == null || isExploding)
			return;

		Direction direction = Direction.directionByAngle(currentDirection);
		x_pos = cell.checkModifyCol(direction, x);
		y_pos = cell.checkModifyRow(direction, y);
	}

	@Override
	public boolean requestedPosition(int[] newXY){
		bulletSteps--;

		if(bulletSteps == 0 && bulletsInRange > 0)
			bulletsInRange--;

		if(isExploding){
			currentIconInd++;
			return false;
		}

		if(freezeStepper > 0){
			freezeStepper--;
			return false;
		}

		int newDirection = currentDirection, eagleDx = eagleX - x_pos;
		if(evenMove)
			newDirection = randomEngine.randomDirectionAngleOrStop(eagleDx, eagleY - y_pos, currentDirection);
		evenMove = !evenMove;
		if(newDirection < 0)
			return false;

		int xPosNew = x_pos, yPosNew = y_pos;

		if(newDirection != currentDirection){
			xPosNew = roundInRange(x_pos, cellPrecisionSize);
			yPosNew = roundInRange(y_pos, cellPrecisionSize);
			newXY[0] = x_pos = xPosNew;
			newXY[1] = y_pos = yPosNew;

			currentIcons = icons.get(newDirection);
			currentDirection = newDirection;
		} else {
			Direction direction = Direction.directionByAngle(currentDirection);
			newXY[0] = xPosNew;
			newXY[1] = yPosNew;
			direction.changePositionBySteps(newXY, cellSpeed);
		}

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd % currentIcons.length;
		}

		return true;
	}

	public boolean fireBullet(){
		boolean shootTheBullet = randomEngine.performTask(bulletSteps, nextBulletSteps);
		if(!shootTheBullet || bulletSteps > 0 || bulletsInRange > 0 || freezeStepper > 0){
			return false;
		}

		bulletSteps = nextBulletSteps;
		bulletsInRange = 1;

		return true;
	}

}
