package com.company.model;

import com.company.SpriteEventController;
import com.company.logic.BattleRandom;
import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public abstract class Enemy implements Tank {
	private BattleRandom randomEngine;
	private int cellSpeed;
	private int bulletSpeed;
	protected int x_pos, y_pos;
	private int level;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private int currentIconInd;
	private int bulletSteps, bulletsInRange;
	private int freezeStepper;
	private final int stepsFor5Sec;
	private final int size;
	private final int cellPrecisionSize;
	protected final int nextBulletSteps, nextBulletMinimumSteps;

	public Enemy(BattleRandom rand, int msInterval, int cellUnitSize){
		randomEngine = rand;

		cellPrecisionSize = cellUnitSize;
		size = (cellUnitSize*MapCell.TANK_LIGHT_STATE_1_UP.getSize() )/MapCell.getUnitSize();
		cellSpeed = (10*msInterval*cellUnitSize*2)/5000;// speed: 10 cells / 5000 ms;

		nextBulletSteps = 1000/msInterval;
		bulletSteps = 0;
		bulletsInRange = 0;
		bulletSpeed = (6*msInterval*cellUnitSize*2)/1000;// bullet speed: 6 cells / second;

		// steps after which bullets move 3 times their size:
		int bulletUnitSize = (cellUnitSize*MapCell.BULLET_UP.getSize())/(MapCell.getUnitSize() );
		nextBulletMinimumSteps = Math.max( ( 3*bulletUnitSize )/bulletSpeed, 2);

		stepsFor5Sec = 5000/msInterval;
		freezeStepper = 0;

		currentDirection = Direction.DOWN.getDirection();

		level = 1;
		icons = new HashMap<>();
		currentIconInd = 0;
		setIcons(false);
	}
	public Enemy(BattleRandom rand, int msInterval, int cellUnitSize, boolean powerApp){
		this(rand, msInterval, cellUnitSize);
		setIcons(powerApp);
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

	protected abstract void setIcons(boolean containsPowerUp);

	@Override
	public void setUpCell(Cell cell){
		cell.setMapCell(currentIcons[currentIconInd]);
		cell.setPos(x_pos, y_pos);
	}

	public void setPos(int x, int y){
		if(x >= 0)
			x_pos = x;
		if(y >= 0)
			y_pos = y;
	}

	public void moveOrBlock(Cell cell, int x, int y){
		if(cell == null)
			return;

		Direction direction = Direction.directionByAngle(currentDirection);
		x_pos = cell.checkModifyCol(direction, x);
		y_pos = cell.checkModifyRow(direction, y);
	}

	@Override
	public boolean requestedPosition(int[] newXY){
		return false;
	}

	/*public boolean fireBullet(){
		int bulletPower = 1/ *randomEngine.??* /;
		if(bulletPower < 1 || bulletSteps > 0 || bulletsInRange > 0 || freezeStepper > 0){
			return false;
		}
		bulletSteps = nextBulletSteps;//  (level > 1)?nextBulletSteps/2:nextBulletSteps;
		bulletsInRange = 1;

		return true;
	}*/

}
