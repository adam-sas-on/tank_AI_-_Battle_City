package com.company.model;

import com.company.SpriteEventController;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class PlayerAITank implements Tank {
	private SpriteEventController tankDriver;
	private double cellSpeed;
	private double bulletSpeed;
	private double x_pos, y_pos;
	private Cell cell;
	private int level;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private int currentIconInd;
	private int bulletSteps;
	private final int nextBulletSteps;
	private final double size = 2.0*MapCell.TANK_1_LVL_1_STATE_1_UP.getUnitSize();

	public PlayerAITank(int msInterval, SpriteEventController driver) {
		tankDriver = driver;

		cellSpeed = (12*size*msInterval*2)/5000.0;// speed: 12 cells / 5000 ms;
		if(cellSpeed < 1.0)
			cellSpeed = 1;

		nextBulletSteps = (1000*3)/(msInterval*2*2);
		bulletSteps = 0;
		bulletSpeed = (6*size*msInterval*2)/1000.0;// bullet speed: 6 cells / second;
		if(bulletSpeed <= cellSpeed)
			bulletSpeed = cellSpeed + 1.0;

		currentDirection = driver.directionForUp();

		level = 1;
		icons = new HashMap<>();
		currentIconInd = 0;
		cell = new Cell();

		setPos(12, 4);
	}

	public void setPosOnPlayer1(){
		setPos(4, 12);
	}

	public void setPosOnPlayer2(){
		setPos(8, 12);
	}

	public Bullet fireBullet(DamageClass damages){
		int row = tankDriver.takeTheShootPower();
		if(row < 1 || bulletSteps > 0)
			return null;

		bulletSteps = nextBulletSteps;
		int col = (int)Math.round(x_pos);
		row = (int)Math.round(y_pos);
		KeyCode directionCode = tankDriver.getKeyCode();

		Bullet bullet = new Bullet(bulletSpeed, size, directionCode, col, row, damages);
		bullet.assignToPlayer();
		if(level > 1)
			bullet.setDoubleSpeed();
		bullet.setDestructivePower(level);

		return bullet;
	}

	@Override
	public Cell getCell() {
		return null;
	}

	@Override
	public void setUpCell(Cell cell, int cellUnitSize) {
		cell.setMapCell(currentIcons[currentIconInd]);
		int col = (int) Math.round(x_pos*cellUnitSize), row = (int) Math.round(y_pos*cellUnitSize);
		cell.setPos(col, row);
	}

	public void addIcons(int direction, MapCell[] cells){
		if(icons.isEmpty() ){
			currentIcons = cells;
		}
		icons.put(direction, cells);
	}

	@Override
	public void setPos(double x, double y) {
		x_pos = x;
		y_pos = y;
	}


	@Override
	public void move(GameView view){
		int newDirection = tankDriver.move();
		bulletSteps--;

		if(newDirection < 0)
			return;

		double xPosNew = x_pos, yPosNew = y_pos;
		KeyCode directionCode = tankDriver.getKeyCode();

		if(newDirection != currentDirection){
			view.changeCellPositionToClosest(cell);
			yPosNew = cell.getRow();
			xPosNew = cell.getCol();

			currentIcons = icons.get(newDirection);
			currentDirection = newDirection;
		} else {
			switch (directionCode){
				case UP:
					yPosNew -= cellSpeed;
					if (yPosNew < 0)
						yPosNew = 0.0;
					break;
				case RIGHT:
					xPosNew += cellSpeed;
					/*if (xPosNew > x_limit)
						xPosNew = x_limit;*/
					break;
				case DOWN:
					yPosNew += cellSpeed;
					/*if (yPosNew > y_limit)
						yPosNew = y_limit;*/
					break;
				case LEFT:
					xPosNew -= cellSpeed;
					if (xPosNew < 0.0)
						xPosNew = 0.0;
					break;
			}
		}

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd %currentIcons.length;
			cell.setMapCell(currentIcons[currentIconInd]);
		}

		int col = (int)Math.round(xPosNew), row = (int)Math.round(yPosNew);
		boolean accessible = view.setPosIfAccessible(cell, col, row, directionCode);
		if(accessible){
			x_pos = col;
			y_pos = row;
		}
	}


}
