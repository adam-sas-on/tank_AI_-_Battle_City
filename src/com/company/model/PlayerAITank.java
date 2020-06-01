package com.company.model;

import com.company.SpriteEventController;
import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class PlayerAITank implements Tank {
	private SpriteEventController tankDriver;
	private int cellSpeed;
	private int bulletSpeed;
	private int lastBulletPower;
	private int x_pos, y_pos, xStart, yStart;
	private boolean canKeepMoving;
	private int level, lifes;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private int currentIconInd;
	private int bulletSteps, bulletSteps2nd;
	private int bulletsInRange;
	private int freezeStepper, immortalStepper;
	private final int stepsFor5Sec;
	private final int nextBulletSteps, nextBulletMinimumSteps;
	private final int size;
	private final int cellPrecisionSize;
	private final int playerNumber;
	private static int numberStepper;

	public PlayerAITank(SpriteEventController driver, int msInterval, int cellUnitSize) {
		tankDriver = driver;

		cellPrecisionSize = cellUnitSize;
		size = (cellUnitSize*MapCell.TANK_1_LVL_1_STATE_1_UP.getSize())/(MapCell.getUnitSize() );
		cellSpeed = (12*msInterval*cellUnitSize*2)/5000;// speed: 12 full-cells / 5000 ms;

		nextBulletSteps = (1000)/(msInterval);
		bulletSteps = bulletSteps2nd = bulletsInRange = 0;
		bulletSpeed = (6*msInterval*cellUnitSize*2)/1000;// bullet speed: 6 full-cells / second;

		// steps after which bullets move 3 times their size:
		int bulletUnitSize = (cellUnitSize*MapCell.BULLET_UP.getSize())/(MapCell.getUnitSize() );
		nextBulletMinimumSteps = Math.max( ( 3*bulletUnitSize )/bulletSpeed, 2);

		stepsFor5Sec = 5000/msInterval;
		freezeStepper = immortalStepper = 0;

		currentDirection = driver.directionByKeyCodeOrUp(KeyCode.UP);

		level = 1;
		lifes = 3;
		icons = new HashMap<>();
		currentIconInd = 0;
		canKeepMoving = true;

		setPos(4*cellUnitSize, 12*cellUnitSize);
		numberStepper++;
		playerNumber = numberStepper;
	}

	private int roundInRange(final int value, final int rangeSize){
		int roundDown = (value/rangeSize)*rangeSize, diff;
		diff = ( 2*(value - roundDown) > rangeSize)?rangeSize:0;
		return diff + roundDown;
	}

	public void setDefaultPlayerPosition(){
		if(playerNumber == 1){
			setPos(4 * cellPrecisionSize, 12 * cellPrecisionSize);
		} else {
			setPos(8 * cellPrecisionSize, 12 * cellPrecisionSize);
		}
	}

	public boolean fireBullet(){
		int bulletPower = tankDriver.takeTheShootPower();
		if (bulletPower < 1 || /*bulletSteps > 0 ||*/ bulletsInRange > 0 || freezeStepper > 0){
			return false;
		}

		bulletSteps = (level > 1)?nextBulletSteps/2:nextBulletSteps;

		lastBulletPower = bulletPower;
		if(level < 4)
			lastBulletPower = 1;
		bulletsInRange++;

		return true;
	}

	@Override
	public Cell getCell() {
		return null;
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

	public KeyCode getDirectionCode(){
		return tankDriver.getKeyCode();
	}

	public int getBulletSpeed(){
		if(level < 1)
			return 0;

		return (level > 1)?bulletSpeed*2:bulletSpeed;
	}

	public boolean lastBulletCanDestroySteel(){
		return lastBulletPower > 1;
	}

	public void resetBulletShots(int bulletsStepsDistance){
		int currentNextBulletSteps = (level > 1)?nextBulletSteps/2:nextBulletSteps;
		if(bulletsStepsDistance < currentNextBulletSteps && currentNextBulletSteps > nextBulletMinimumSteps){
			bulletSteps = nextBulletMinimumSteps;
		}
	}

	@Override
	public void setUpCell(Cell cell){
		cell.setMapCell(currentIcons[currentIconInd]);
		cell.setPos(x_pos, y_pos);
	}

	public void setIcons(){
		MapCell[] cells;
		int direction = tankDriver.directionByKeyCodeOrUp(KeyCode.UP);

		if(playerNumber < 2)
			cells = MapCell.player1UpState(level);
		else
			cells = MapCell.player2UpState(level);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction, cells);

		direction = tankDriver.directionByKeyCodeOrUp(KeyCode.RIGHT);
		if(playerNumber < 2){
			cells = MapCell.player1RightState(level);
		} else
			cells = MapCell.player2RightState(level);
		icons.put(direction, cells);

		if(playerNumber < 2){
			direction = tankDriver.directionByKeyCodeOrUp(KeyCode.DOWN);
			icons.put(direction, MapCell.player1DownState(level) );

			direction = tankDriver.directionByKeyCodeOrUp(KeyCode.LEFT);
			icons.put(direction, MapCell.player1LeftState(level) );
		} else {
			direction = tankDriver.directionByKeyCodeOrUp(KeyCode.DOWN);
			cells = MapCell.player2DownState(level);
			icons.put(direction, cells);

			direction = tankDriver.directionByKeyCodeOrUp(KeyCode.LEFT);
			cells = MapCell.player2LeftState(level);
			icons.put(direction, cells);
		}
	}

	public void promoteDegrade(boolean doPromote){
		if(doPromote){
			level++;
		} else {
			level--;
			if(level <= 0){
				canKeepMoving = false;
				lifes--;
				if(lifes > 0){
					level = 1;
					revive();
				}
			}
		}

		if(level < 5){// don't change icons for every higher level then max = 4;
			setIcons();
			currentIcons = icons.get(currentDirection);
		}
	}

	public void useCollectible(MapCell collectibleType){
		switch(collectibleType){
			case HELMET:
				immortalStepper = stepsFor5Sec*2;// 10 seconds of immortality like it is in original game;
				break;
			case TANK_LIVE:
				lifes++;
				break;
			case STAR:
				promoteDegrade(true);
				break;
		}
	}

	public MapCell getImmortalityCell(){
		if(immortalStepper < 1)
			return null;

		int step = immortalStepper/2;
		if( (step&2) != 0 )
			return MapCell.IMMORTALITY_1;
		else
			return MapCell.IMMORTALITY_2;
	}

	public void makeFreezed(){
		if(immortalStepper > 0)
			return;

		MapCell[] newIcons = new MapCell[currentIcons.length + 2];
		int i;
		newIcons[0] = currentIcons[0];
		for(i = newIcons.length - 1; i > 0; i--)
			newIcons[i] = null;

		i = newIcons.length - 2;// last one will be empty;
		int j = currentIcons.length - 1;
		for(; j > 0; j--, i--){
			newIcons[i] = currentIcons[j];
		}
		currentIcons = newIcons;

		freezeStepper = stepsFor5Sec;
		canKeepMoving = false;
		tankDriver.blockUnblockController(true);
	}

	public void getShot(Bullet bullet){

	}

	@Override
	public void setPos(int x, int y){
		x_pos = x;
		y_pos = y;
	}

	public void setStartingPos(int x, int y){
		xStart = x_pos = x;
		yStart = y_pos = y;
	}

	public void revive(){
		x_pos = xStart;
		y_pos = yStart;
		canKeepMoving = true;
		currentDirection = tankDriver.directionByKeyCodeOrUp(KeyCode.UP);
		currentIconInd = 0;
		currentIcons = icons.get(currentDirection);
		immortalStepper = stepsFor5Sec;
	}

	public void blockMovement(Cell cell, int x, int y){
		if(cell == null)
			return;

		KeyCode directionCode = tankDriver.getKeyCode(currentDirection);
		x_pos = cell.checkModifyCol(directionCode, x);
		y_pos = cell.checkModifyRow(directionCode, y);
		//canKeepMoving = cell.canMove(directionCode);
	}

	@Override
	public boolean move(int[] newXY) {
		bulletSteps--;
		//bulletSteps2nd--;
		if (bulletSteps == 0)
			bulletsInRange = (bulletsInRange - 1) & 3;

		if (freezeStepper > 0){
			freezeStepper--;
			if(freezeStepper == 0){
				canKeepMoving = true;
				currentIcons = icons.get(currentDirection);
				currentIconInd = 0;
				tankDriver.blockUnblockController(false);
			} else {
				currentIconInd++;
				currentIconInd = currentIconInd %currentIcons.length;
			}
			return false;
		}

		if(immortalStepper > 0)
			immortalStepper--;

		int newDirection = tankDriver.move();
		if(newDirection < 0)
			return false;

		int xPosNew = x_pos, yPosNew = y_pos;
		KeyCode directionCode = tankDriver.getKeyCode();

		if(newDirection != currentDirection){
			xPosNew = roundInRange(x_pos, cellPrecisionSize);
			yPosNew = roundInRange(y_pos, cellPrecisionSize);
			x_pos = xPosNew;
			y_pos = yPosNew;

			currentIcons = icons.get(newDirection);
			currentDirection = newDirection;
		} else if(canKeepMoving){
			switch (directionCode){
				case UP:
					yPosNew -= cellSpeed;
					if (yPosNew < 0)
						yPosNew = 0;
					break;
				case RIGHT:
					xPosNew += cellSpeed;
					break;
				case DOWN:
					yPosNew += cellSpeed;
					break;
				case LEFT:
					xPosNew -= cellSpeed;
					if (xPosNew < 0)
						xPosNew = 0;
					break;
			}
		}

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd %currentIcons.length;
		}

		newXY[0] = xPosNew;
		newXY[1] = yPosNew;

		return true;
	}


}
