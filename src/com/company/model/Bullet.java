package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class Bullet {
	private double pixelSpeed;
	private float xDirection, yDirection;

	private double x_pos, y_pos;
	private double leftColDiff, leftRowDiff;
	private double rightColDiff, rightRowDiff;
	private final double bulletSize;

	private boolean isPlayers;
	private boolean canDestroySteel;
	private Cell cell;
	private int explodeIndex;
	private MapCell bulletMapCell;
	private MapCell[] explodes;

	private Map<MapCell,MapCell> rightSideDestruction;
	private Map<MapCell,MapCell> leftSideDestruction;

	public Bullet(double bulletSpeed, double tankSize, KeyCode direction, double tankX, double tankY, DamageClass damages){
		pixelSpeed = bulletSpeed;// speed: 6 cells / 1000 ms;
		if(pixelSpeed < 1.5)
			pixelSpeed = 1.5;
		isPlayers = canDestroySteel = false;// most bullets;
		xDirection = (float) 0.0;
		yDirection = (float) 0.0;
		bulletSize = MapCell.BULLET_UP.getSize() / ((double)MapCell.BULLET_UP.getUnitSize());

		cell = new Cell();
		rightSideDestruction = new HashMap<>(14);
		leftSideDestruction = new HashMap<>(14);

		setMapCellAndPosition(tankSize, direction, tankX, tankY);

		explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
				MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
		explodeIndex = -1;

		damages.setDamages(rightSideDestruction, leftSideDestruction, direction);
	}

	private void setMapCellAndPosition(double tankSize, KeyCode direction, double tankX, double tankY){
		x_pos = tankX;
		y_pos = tankY;

		leftColDiff = leftRowDiff = rightColDiff = rightRowDiff = 0;

		switch(direction){
			case UP:
				bulletMapCell = MapCell.BULLET_UP;
				yDirection = (float) -1.0;
				x_pos += (tankSize - bulletSize)/2.0;

				rightColDiff = bulletSize;
				break;
			case RIGHT:
				bulletMapCell = MapCell.BULLET_RIGHT;
				xDirection = (float) 1.0;
				x_pos += tankSize - bulletSize;
				y_pos += (tankSize - bulletSize)/2.0;

				leftColDiff = bulletSize;
				rightColDiff = rightRowDiff = bulletSize;
				break;
			case LEFT:
				bulletMapCell = MapCell.BULLET_LEFT;
				xDirection = (float) -1.0;
				y_pos += (tankSize - bulletSize)/2.0;

				leftRowDiff = bulletSize;
				break;
			default:
				bulletMapCell = MapCell.BULLET_DOWN;
				yDirection = (float) 1.0;
				x_pos += (tankSize - bulletSize)/2.0;
				y_pos += tankSize - bulletSize;

				rightRowDiff = bulletSize;
				leftColDiff = leftRowDiff = bulletSize;
		}
	}


	public Cell getCell(){
		return cell;
	}

	public void setUpCell(Cell cell, int cellUnitSize){
		cell.setMapCell(bulletMapCell);
		int col = (int) Math.round(x_pos*cellUnitSize), row = (int) Math.round(y_pos*cellUnitSize);
		cell.setPos(col, row);
	}

	/**
	 * 	leftPosCol
	 * UP: cell.getCol()
	 * RIGHT: cell.getCol() + cell.getCellSize()
	 * DOWN: cell.getCol() + cell.getCellSize()
	 * LEFT: cell.getCol()
	 * 	leftPosRow
	 * UP: cell.getRow()
	 * RIGHT: cell.getRow()
	 * DOWN: cell.getRow() + cell.getCellSize()
	 * LEFT: cell.getRow() + cell.getCellSize()
	 * @param colRowPos: array to assign values {col, row};
	 */
	public void getLeftCornerPos(int[] colRowPos){
		int cellSize = bulletMapCell.getUnitSize();
		try {
			colRowPos[0] = (int) ( (x_pos + leftColDiff)*cellSize);
			colRowPos[1] = (int) ( (y_pos + leftRowDiff)*cellSize);
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	/**
	 * 	rightPosCol
	 * UP: cell.getCol() + cell.getCellSize()
	 * RIGHT: cell.getCol() + cell.getCellSize()
	 * DOWN: cell.getCol()
	 * LEFT: cell.getCol()
	 * 	rightPosRow
	 * UP: cell.getRow()
	 * RIGHT: cell.getRow() + cell.getCellSize()
	 * DOWN: cell.getRow() + cell.getCellSize()
	 * LEFT: cell.getRow()
	 * @param colRowPos: array to assign values {col, row};
	 */
	public void getRightCornerPos(int[] colRowPos){
		int cellSize = bulletMapCell.getUnitSize();
		try {
			colRowPos[0] = (int) ( (x_pos + rightColDiff)*cellSize);
			colRowPos[1] = (int) ( (y_pos + rightRowDiff)*cellSize);
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	public boolean canDestroySteel(){
		return canDestroySteel;
	}

	public boolean belongsToPlayer(){
		return isPlayers;
	}

	public void assignToPlayer(){
		isPlayers = true;
	}

	public void setDoubleSpeed(){
		pixelSpeed *= 2.0;
	}

	public void setDestructivePower(int tankLevel){
		if(tankLevel > 3)
			canDestroySteel = true;
	}
	public void makeWeak(){
		canDestroySteel = false;
	}

	public boolean move(){
		if(explodeIndex >=0){
			bulletMapCell = explodes[explodeIndex];
			cell.setMapCell(explodes[explodeIndex]);
			explodeIndex++;
			return explodeIndex < explodes.length;
		}

		y_pos += pixelSpeed* yDirection;
		x_pos += pixelSpeed* xDirection;

		int col = (int)Math.round(x_pos), row = (int)Math.round(y_pos);
		cell.setPos(col, row);
		return true;
	}

	public void setExplode(){
		if(explodeIndex < 0) {
			explodeIndex = 0;
			explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
					MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
			int posDiff = (MapCell.EXPLODE_1.getSize() - cell.getCellSize()) / 2, col = cell.getCol();
			cell.setPos(col - posDiff, cell.getRow() - posDiff);
		}
	}

	public void setSmallExplode(){
		if(explodeIndex < 0) {
			explodeIndex = 0;
			explodes = new MapCell[]{MapCell.EXPLODE_1};
			double explodeSize = MapCell.EXPLODE_1.getSize()/((double)bulletMapCell.getUnitSize() ), posDiff;
			posDiff = (explodeSize - bulletSize)/2.0;
			x_pos -= posDiff;
			y_pos -= posDiff;
		}
	}

	public void setRightDamageCell(Cell cell){
		MapCell cellType = cell.getMapCell();
		cellType = rightSideDestruction.get(cellType);
		cell.setMapCell(cellType);
	}

	public void setLeftDamageCell(Cell cell){
		MapCell cellType = cell.getMapCell();
		cellType = leftSideDestruction.get(cellType);
		cell.setMapCell(cellType);
	}
}
