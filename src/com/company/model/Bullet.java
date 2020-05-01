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
	private int leftColDiff, leftRowDiff;
	private int rightColDiff, rightRowDiff;

	private boolean isPlayers;
	private boolean canDestroySteel;
	private Cell cell;
	private int explodeIndex;
	private MapCell[] explodes;

	private Map<MapCell,MapCell> rightSideDestruction;
	private Map<MapCell,MapCell> leftSideDestruction;

	public Bullet(int msInterval, int cellSize, KeyCode direction, int col, int row, DamageClass damages){
		pixelSpeed = (6*cellSize*msInterval*2)/1000.0;// speed: 6 cells / 1000 ms;
		if(pixelSpeed < 1.5)
			pixelSpeed = 1.5;
		isPlayers = canDestroySteel = false;// most bullets;
		xDirection = (float) 0.0;
		yDirection = (float) 0.0;

		cell = new Cell();
		rightSideDestruction = new HashMap<>(14);
		leftSideDestruction = new HashMap<>(14);

		setMapCellAndPosition(cellSize, direction, col, row, damages);

		explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
				MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
		explodeIndex = -1;
	}

	private void setMapCellAndPosition(int cellSize, KeyCode direction, int col, int row, DamageClass damages){
		x_pos = col;
		y_pos = row;
		int colLoc = col, rowLoc = row, bulletSize = MapCell.BULLET_UP.getSize();

		leftColDiff = leftRowDiff = rightColDiff = rightRowDiff = 0;

		switch(direction){
			case UP:
				cell.setMapCell(MapCell.BULLET_UP);
				yDirection = (float) -1.0;
				x_pos += (cellSize - bulletSize)/2.0;
				colLoc += (cellSize - bulletSize)/2;

				rightColDiff = MapCell.BULLET_UP.getSize();
				damages.upRightDamagesSet(rightSideDestruction);
				damages.upLeftDamageSet(leftSideDestruction);
				break;
			case RIGHT:
				cell.setMapCell(MapCell.BULLET_RIGHT);
				xDirection = (float) 1.0;
				x_pos += cellSize - bulletSize;
				colLoc += cellSize - bulletSize;
				y_pos += (cellSize - bulletSize)/2.0;
				rowLoc += (cellSize - bulletSize)/2;

				leftColDiff = MapCell.BULLET_RIGHT.getSize();
				rightColDiff = rightRowDiff = MapCell.BULLET_RIGHT.getSize();
				damages.rightRightDamagesSet(rightSideDestruction);
				damages.rightLeftDamageSet(leftSideDestruction);
				break;
			case LEFT:
				cell.setMapCell(MapCell.BULLET_LEFT);
				xDirection = (float) -1.0;
				y_pos += (cellSize - bulletSize)/2.0;
				rowLoc += (cellSize - bulletSize)/2;
				colLoc -= bulletSize;

				leftRowDiff = MapCell.BULLET_LEFT.getSize();
				damages.leftRightDamagesSet(rightSideDestruction);
				damages.leftLeftDamageSet(leftSideDestruction);
				break;
			default:
				cell.setMapCell(MapCell.BULLET_DOWN);
				yDirection = (float) 1.0;
				x_pos += (cellSize - bulletSize)/2.0;
				colLoc += (cellSize - bulletSize)/2;
				y_pos += cellSize - bulletSize;
				rowLoc += cellSize - bulletSize;

				rightRowDiff = MapCell.BULLET_DOWN.getSize();
				leftColDiff = leftRowDiff = MapCell.BULLET_DOWN.getSize();
				damages.downRightDamagesSet(rightSideDestruction);
				damages.downLeftDamageSet(leftSideDestruction);
		}
		cell.setPos(colLoc, rowLoc);
	}


	public Cell getCell(){
		return cell;
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
		try {
			colRowPos[0] = cell.getCol() + leftColDiff;
			colRowPos[1] = cell.getRow() + leftRowDiff;
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
		try {
			colRowPos[0] = cell.getCol() + rightColDiff;
			colRowPos[1] = cell.getRow() + rightRowDiff;
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

	public boolean move(){
		if(explodeIndex >=0){
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
			int posDiff = (MapCell.EXPLODE_1.getSize() - cell.getCellSize()) / 2, col = cell.getCol();
			cell.setPos(col - posDiff, cell.getRow() - posDiff);
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
