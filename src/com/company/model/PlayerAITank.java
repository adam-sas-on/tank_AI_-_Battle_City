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
	private double pixelSpeed;
	private double bulletSpeed;
	private double x_pos, y_pos;
	private double x_limit, y_limit;
	private Cell cell;
	private int level;
	private int currentDirection;
	private Map<Integer, MapCell[]> icons;
	private MapCell[] currentIcons;
	private int currentIconInd;
	private int bulletSteps;
	private final int nextBulletSteps;

	public PlayerAITank(int msInterval, int cellSize, SpriteEventController driver) {
		tankDriver = driver;

		pixelSpeed = (12*cellSize*msInterval*2)/5000.0;// speed: 12 cells / 5000 ms;
		if(pixelSpeed < 1.0)
			pixelSpeed = 1;

		nextBulletSteps = 1000/(msInterval*2);
		bulletSteps = 0;
		bulletSpeed = (6*cellSize*msInterval*2)/1000.0;// bullet speed: 6 cells / second;
		if(bulletSpeed <= pixelSpeed)
			bulletSpeed = pixelSpeed + 1.0;

		x_limit = 12.0*cellSize;
		y_limit = x_limit;
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

	public Bullet fireBullet(int cellSize, DamageClass damages){
		int row = tankDriver.takeTheShootPower();
		if(row < 1 || bulletSteps > 0)
			return null;

		bulletSteps = nextBulletSteps;
		int col = (int)Math.round(x_pos);
		row = (int)Math.round(y_pos);
		KeyCode directionCode = tankDriver.getKeyCode();

		Bullet bullet = new Bullet(bulletSpeed, cellSize, directionCode, col, row, damages);
		bullet.assignToPlayer();
		if(level > 1)
			bullet.setDoubleSpeed();
		bullet.setDestructivePower(level);

		return bullet;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	public void addIcons(int direction, MapCell[] cells){
		if(icons.isEmpty() )
			cell.setMapCell(cells[0]);
		icons.put(direction, cells);
	}

	@Override
	public void setPos(double x, double y) {
		int cellSize = MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();
		x_pos = x*cellSize;
		y_pos = y*cellSize;
		int col = (int) x, row = (int)y;
		cell.setPos(col*cellSize, row*cellSize);
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
					yPosNew -= pixelSpeed;
					if (yPosNew < 0)
						yPosNew = 0.0;
					break;
				case RIGHT:
					xPosNew += pixelSpeed;
					if (xPosNew > x_limit)
						xPosNew = x_limit;
					break;
				case DOWN:
					yPosNew += pixelSpeed;
					if (yPosNew > y_limit)
						yPosNew = y_limit;
					break;
				case LEFT:
					xPosNew -= pixelSpeed;
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
