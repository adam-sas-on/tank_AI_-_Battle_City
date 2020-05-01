package com.company.model;

import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public abstract class Tank {
	private double pixelSpeed;
	private double bulletSpeed;
	protected double x_pos, y_pos;
	private double x_limit, y_limit;
	protected Cell cell;
	protected int level;
	protected KeyCode currentDirection;
	private Map<KeyCode, MapCell[]> icons;
	private MapCell[] currentIcons;
	private boolean ride;
	private int currentIconInd;

	public Tank(int msInterval, int cellSize){
		pixelSpeed = (12*cellSize*msInterval*2)/5000.0;// speed: 12 cells / 5000 ms;
		if(pixelSpeed < 1.0)
			pixelSpeed = 1;

		bulletSpeed = (6*cellSize*msInterval*2)/1000.0;// bullet speed: 6 cells / second;
		if(bulletSpeed <= pixelSpeed)
			bulletSpeed = pixelSpeed + 1.0;

		x_limit = 12.0*cellSize;
		y_limit = x_limit;
		currentDirection = KeyCode.UP;
		ride = false;

		level = 1;
		icons = new HashMap<>();
		currentIconInd = 0;
		cell = new Cell();
	}

	public int getXpos(){
		return (int)Math.round(x_pos);
	}
	public int getYpos(){
		return (int)Math.round(y_pos);
	}

	public Cell getCell(){
		return cell;
	}

	public void setPos(int row, int col, int cellSize){
		x_pos = (double)col*cellSize;
		y_pos = (double)row*cellSize;
		cell.setPos(col*cellSize, row*cellSize);
	}

	public void addIcons(KeyCode code, MapCell[] cells){
		if(icons.isEmpty() )
			cell.setMapCell(cells[0]);
		icons.put(code, cells);
	}

	public void move(GameView view){
		if(!ride)
			return;

		double xPosNew = x_pos, yPosNew = y_pos;
		switch(currentDirection){
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

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd %currentIcons.length;
			cell.setMapCell(currentIcons[currentIconInd]);
		}

		int col = (int)Math.round(xPosNew), row = (int)Math.round(yPosNew);
		boolean accessible = view.setPosIfAccessible(cell, col, row, currentDirection);
		if(accessible){
			x_pos = col;
			y_pos = row;
		}
	}

	public void turn(KeyCode newDirection, GameView view){
		double xPosNew = x_pos, yPosNew = y_pos;
		if(newDirection != currentDirection){
			view.changeCellPositionToClosest(cell);
			yPosNew = cell.getRow();
			xPosNew = cell.getCol();

			currentIcons = icons.get(newDirection);
			int col = (int)Math.round(xPosNew), row = (int)Math.round(yPosNew);
			boolean accessible = view.setPosIfAccessible(cell, col, row, newDirection);
			if(accessible){
				x_pos = col;
				y_pos = row;
			}
		}

		if(currentIcons != null){
			currentIconInd++;
			currentIconInd = currentIconInd %currentIcons.length;
			cell.setMapCell(currentIcons[currentIconInd]);
		}

		ride = true;
		currentDirection = newDirection;
	}

	public void stop(){
		ride = false;
	}

	public abstract Bullet fireBullet(int msInterval, int cellSize, DamageClass damages);

}
