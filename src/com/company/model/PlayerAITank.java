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
	private int x_pos, y_pos;
	private boolean canKeepMoving;
	private int level;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private Cell currentCell;
	private int currentIconInd;
	private int bulletSteps;
	private final int nextBulletSteps;
	private final int size;
	private final int cellPrecisionSize;
	private final int playerNumber;
	private static int numberStepper;

	public PlayerAITank(SpriteEventController driver, int msInterval, int cellUnitSize) {
		tankDriver = driver;

		cellPrecisionSize = cellUnitSize;
		size = (cellUnitSize*MapCell.TANK_1_LVL_1_STATE_1_UP.getSize())/(MapCell.getUnitSize() );
		cellSpeed = (12*msInterval*cellUnitSize*2)/5000;// speed: 12 cells / 5000 ms;

		nextBulletSteps = (1000*3)/(msInterval*2*2);
		bulletSteps = 0;
		bulletSpeed = (6*msInterval*cellUnitSize*2)/1000;// bullet speed: 6 cells / second;

		currentDirection = driver.directionByKeyCodeOrUp(KeyCode.UP);

		level = 1;
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

	public Bullet fireBullet(DamageClass damages){
		int bulletPower = tankDriver.takeTheShootPower();
		if(bulletPower < 1 || bulletSteps > 0)
			return null;

		bulletSteps = nextBulletSteps;
		KeyCode directionCode = tankDriver.getKeyCode();

		Bullet bullet = new Bullet(bulletSpeed, size, directionCode, x_pos, y_pos, damages);
		bullet.assignToPlayer();
		if(level > 1)
			bullet.setDoubleSpeed();
		if(bulletPower > 1)
			bullet.setDestructivePower(level);

		return bullet;
	}

	@Override
	public Cell getCell() {
		return null;
	}

	public void getPos(int[] xyPos){
		xyPos[0] = x_pos;
		xyPos[1] = y_pos;
	}

	@Override
	public void setUpCell(Cell cell, int newCellUnitSize) {
		cell.setMapCell(currentIcons[currentIconInd]);
		cell.setPos(x_pos, y_pos);
		cell.roundPos(cellPrecisionSize, newCellUnitSize);
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
			if(level <= 0)
				canKeepMoving = false;
		}

		if(level < 5)// don't change icons for every higher level then max = 4;
			setIcons();
	}

	@Override
	public void setPos(int x, int y){
		x_pos = x;
		y_pos = y;
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
	public boolean move(int[] newXY){
		int newDirection = tankDriver.move();
		bulletSteps--;

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
			//cell.setMapCell(currentIcons[currentIconInd]);
		}

		newXY[0] = xPosNew;
		newXY[1] = yPosNew;

		return true;
	}


}
