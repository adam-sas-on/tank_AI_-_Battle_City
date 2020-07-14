package com.company.model;

import com.company.SpriteEventController;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;

import java.util.HashMap;
import java.util.Map;

public class PlayerAITank implements Tank {
	private SpriteEventController tankDriver;
	private int cellSpeed;
	private final int bulletSpeed;
	private int lastBulletPower;
	private int x_pos, y_pos, xStart, yStart;
	private boolean isExploding;
	private int level, lifes;
	private int points, actionPoints;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private int currentIconInd;
	private int bulletSteps, bulletSteps2nd, bulletsInRange;
	private int freezeStepper, immortalStepper;
	private final int stepsFor5Sec;
	private final int nextBulletSteps, nextBulletMinimumSteps;
	private final int size;
	private final int cellPrecisionSize;
	private final int playerNumber;
	private static int numberStepper;

	public PlayerAITank(SpriteEventController driver, GameView view){
		tankDriver = driver;

		int msInterval = view.getIntervalInMilliseconds(), cellUnitSize = view.getDefaultCellSize();

		cellPrecisionSize = cellUnitSize;
		size = (cellUnitSize*MapCell.TANK_1_LVL_1_STATE_1_UP.getSize())/(MapCell.getUnitSize() );
		cellSpeed = (12*msInterval*cellUnitSize*2)/5000;// speed: 12 full-cells / 5000 ms;

		nextBulletSteps = (1000)/(msInterval);
		bulletSteps = bulletSteps2nd = 0;
		bulletsInRange = 0;
		bulletSpeed = (6*msInterval*cellUnitSize*2)/1000;// bullet speed: 6 full-cells / second;

		// steps after which bullets move 3 times their size:
		int bulletUnitSize = (cellUnitSize*MapCell.BULLET_UP.getSize())/(MapCell.getUnitSize() );
		nextBulletMinimumSteps = Math.max( ( 3*bulletUnitSize )/bulletSpeed, 100/msInterval);

		stepsFor5Sec = 5000/msInterval;
		freezeStepper = immortalStepper = 0;

		currentDirection = Direction.UP.getDirection();

		level = 1;
		lifes = 3;
		points = actionPoints = 0;
		icons = new HashMap<>();
		currentIconInd = 0;
		isExploding = false;

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

	public void getPos(int[] xyPos){
		try {
			xyPos[0] = x_pos;
			xyPos[1] = y_pos;
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	public int getTankSize(){
		return size;
	}

	public Direction getDirectionCode(){
		return tankDriver.getDirection();
	}

	public int getBulletSpeed(){
		if(level < 1)
			return 0;

		return (level > 1)?bulletSpeed*2:bulletSpeed;
	}

	public int getBulletSteps(){
		return bulletSteps;
	}

	public boolean lastBulletCanDestroySteel(){
		return lastBulletPower > 1;
	}

	public int getLifes(){
		return lifes;
	}

	public int getPoints(){
		return points;
	}

	public boolean isImmortal(){
		return immortalStepper > 0;
	}

	public int addPoints(int pointsToAdd){
		points += pointsToAdd;
		actionPoints += pointsToAdd;
		return points;
	}

	public void resetBulletShots(int bulletsStepsDistance){
		int currentNextBulletSteps = (level > 1)?nextBulletSteps/2:nextBulletSteps;
		if(bulletsStepsDistance <= currentNextBulletSteps && currentNextBulletSteps > nextBulletMinimumSteps){
			bulletSteps = 0;

			bulletsInRange--;
			bulletSteps2nd = (bulletsInRange > 1)?nextBulletMinimumSteps:currentNextBulletSteps - nextBulletMinimumSteps;
		}
	}

	@Override
	public void setUpCell(Cell cell){
		try {
			cell.setMapCell(currentIcons[currentIconInd]);
			cell.setPos(x_pos, y_pos);
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Players icon index out of range ("+ currentIconInd + ")! \n\t" + e);
		}
	}

	public void setIcons(){
		MapCell[] cells;
		int direction = Direction.UP.getDirection();

		if(playerNumber < 2)
			cells = MapCell.player1UpState(level);
		else
			cells = MapCell.player2UpState(level);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction, cells);

		direction = Direction.RIGHT.getDirection();
		if(playerNumber < 2){
			cells = MapCell.player1RightState(level);
		} else
			cells = MapCell.player2RightState(level);
		icons.put(direction, cells);

		if(playerNumber < 2){
			direction = Direction.DOWN.getDirection();
			icons.put(direction, MapCell.player1DownState(level) );

			direction = Direction.LEFT.getDirection();
			icons.put(direction, MapCell.player1LeftState(level) );
		} else {
			direction = Direction.DOWN.getDirection();
			cells = MapCell.player2DownState(level);
			icons.put(direction, cells);

			direction = Direction.LEFT.getDirection();
			cells = MapCell.player2LeftState(level);
			icons.put(direction, cells);
		}
	}

	private void promoteDegrade(boolean doPromote){
		if(doPromote){
			level++;
		} else {
			level--;
			if(level <= 0){
				lifes--;
				if(lifes > 0){
					level = 1;
				}
				isExploding = true;
				actionPoints -= 4000;// arbitrary values for now;
			}
			actionPoints -= 1000;
		}

		if(isExploding){
			currentIcons = MapCell.bigExplosionMapCells();
			currentIconInd = 0;
			x_pos -= cellPrecisionSize;
			y_pos -= cellPrecisionSize;
		} else if(level < 5){// don't change icons for every higher level then max = 4;
			setIcons();
			currentIcons = icons.get(currentDirection);
		}
	}

	public void useCollectible(MapCell collectibleType){
		int addPoints = 0;
		switch(collectibleType){
			case HELMET:
				immortalStepper = stepsFor5Sec*2;// 10 seconds of immortality like it is in original game;
				addPoints = 500;
				break;
			case TANK_LIVE:
				lifes++;
				addPoints = 500;
				break;
			case STAR:
				promoteDegrade(true);
				addPoints = 500;
				break;
			case BOMB:
			case SPADE:
			case TIMER:
				addPoints = 500;
				break;
		}
		points += addPoints;
		actionPoints += addPoints;
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

		if(freezeStepper < 2){
			MapCell[] newIcons = new MapCell[6];
			int i;
			for(i = newIcons.length - 1; i >= 0; i--)
				newIcons[i] = MapCell.NULL_BLOCKADE;

			newIcons[0] = currentIcons[currentIconInd];
			newIcons[1] = currentIcons[currentIconInd];
			newIcons[2] = currentIcons[currentIconInd];
			currentIcons = newIcons;
		}
		currentIconInd = 0;

		freezeStepper = stepsFor5Sec;
		tankDriver.blockUnblockController(true);
	}

	public boolean getHit(Cell bulletCell, Cell tankBufferCell){
		if(bulletCell == null || tankBufferCell == null || isExploding)
			return false;

		tankBufferCell.setMapCell(currentIcons[currentIconInd]);
		tankBufferCell.setPos(x_pos, y_pos);

		boolean hit = false;
		if(bulletCell.collide(tankBufferCell, cellPrecisionSize) ){
			if(immortalStepper < 1)
				promoteDegrade(false);
			hit = true;
		}

		return hit;
	}

	@Override
	public boolean exists(){
		return !isExploding;
	}

	private void setPos(int x, int y){
		x_pos = x;
		y_pos = y;
	}

	public void setStartingPos(int x, int y){
		xStart = x_pos = x;
		yStart = y_pos = y;
	}

	private void reviveInAction(){
		x_pos = xStart;
		y_pos = yStart;
		//currentDirection = Direction.UP.getDirection();
		currentIconInd = 0;
		isExploding = false;
		currentIcons = icons.get(currentDirection);
		immortalStepper = stepsFor5Sec;
		freezeStepper = 0;
		tankDriver.blockUnblockController(false);
	}
	public void revive(){
		reviveInAction();

		tankDriver.upDateActionPoints(actionPoints);
		actionPoints = 0;
	}

	public void reset(){
		bulletSteps = bulletSteps2nd = 0;
		bulletsInRange = 0;
		freezeStepper = immortalStepper = 0;
		currentDirection = Direction.UP.getDirection();

		level = 1;
		lifes = 3;
		points = actionPoints = 0;
		currentIconInd = 0;
		isExploding = false;
		setIcons();
		currentIcons = icons.get(currentDirection);
	}

	public void moveOrBlock(Cell cell, int x, int y){
		if(cell == null)
			return;

		Direction direction = Direction.directionByAngle(currentDirection);
		x_pos = cell.checkModifyCol(direction, x);
		y_pos = cell.checkModifyRow(direction, y);
	}

	@Override
	public boolean requestedPosition(int[] newXY) {
		bulletSteps--;

		if(isExploding){
			currentIconInd++;
			if(currentIconInd == currentIcons.length){
				if(lifes > 0)
					reviveInAction();
				else {
					lifes--;
					currentIconInd = 0;
					currentIcons = new MapCell[]{null, null};
				}
			}

			return false;
		}

		if(bulletSteps == 0 && bulletsInRange > 0)
			bulletsInRange--;
		else if(bulletSteps < -2*nextBulletSteps){
			bulletSteps = bulletSteps2nd = 0;
			bulletsInRange = 0;
		}

		actionPoints--;

		if (freezeStepper > 0){
			freezeStepper--;
			if(freezeStepper == 0){
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

		if(newDirection != currentDirection){
			xPosNew = roundInRange(x_pos, cellPrecisionSize);
			yPosNew = roundInRange(y_pos, cellPrecisionSize);
			newXY[0] = x_pos = xPosNew;
			newXY[1] = y_pos = yPosNew;

			currentIcons = icons.get(newDirection);
			currentDirection = newDirection;
		} else {
			Direction direction = tankDriver.getDirection();
			newXY[0] = xPosNew;
			newXY[1] = yPosNew;
			direction.changePositionBySteps(newXY, cellSpeed);
		}

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd %currentIcons.length;
		}

		return true;
	}

	public boolean fireBullet(){
		int bulletPower = tankDriver.takeTheShootPower(), bulletLimit = (level > 2)?1:0;
		if (bulletPower < 1 || bulletSteps > bulletSteps2nd || bulletsInRange > bulletLimit || freezeStepper > 0){
			return false;
		}

		bulletSteps = (level > 1)?nextBulletSteps/2:nextBulletSteps;

		bulletsInRange++;
		bulletSteps2nd = (bulletsInRange > 1)?0:bulletSteps - nextBulletMinimumSteps;

		lastBulletPower = bulletPower;
		if(level < 4)
			lastBulletPower = 1;

		return true;
	}

	// - - - - - - - - - - - - - Methods for points of players behaviour- - - - - - - - - - - - - - -
	public void friendlyFire(){
		actionPoints -= 1000;// arbitrary values for now;
	}
	public void eagleDestroyed(){
		actionPoints -= 20000;
	}

	public void setMaxColsOfMaps(int maxCols){
		tankDriver.setMaxColsOfMap(maxCols);
	}

}
