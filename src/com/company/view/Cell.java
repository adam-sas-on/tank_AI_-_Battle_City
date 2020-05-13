package com.company.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;


public class Cell {
	private MapCell mapCell;
	private int col;
	private int row;
	private int size;
	private boolean destructible, accessible;
	private Cell upCell, rightCell, downCell, leftCell;
	private boolean canMoveUp, canMoveRight, canMoveDown, canMoveLeft;
	private int id;

	public Cell(){
		row = col = 0;
		mapCell = null;
		destructible = false;
		upCell = rightCell = downCell = leftCell = null;
		canMoveUp = canMoveRight = canMoveDown = canMoveLeft = false;
		id = -1;
	}

	public int getId(){
		return id;
	}

	public MapCell getMapCell(){
		return mapCell;
	}

	public Cell getUpCell(){
		return upCell;
	}

	public Cell getRightCell(){
		return rightCell;
	}

	public Cell getDownCell(){
		return downCell;
	}

	public Cell getLeftCell(){
		return leftCell;
	}

	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}

	public int getCellSize(){
		return size;
	}

	public boolean isAccessible(){
		return accessible;
	}

	public boolean isDestructible(){
		if(mapCell == MapCell.STEEL)
			return true;
		return destructible;
	}

	public boolean canMove(KeyCode direction){
		switch(direction){
			case UP:
				return canMoveUp;
			case RIGHT:
				return canMoveRight;
			case DOWN:
				return canMoveDown;
			case LEFT:
				return canMoveLeft;
			default:
				return false;
		}
	}

	public void resetMovement(final int col, final int row, final int maxCols, final int maxRows){
		canMoveUp = canMoveRight = canMoveDown = canMoveLeft = true;
		Cell stepper = leftCell;
		//* rounding down position case makes it is not necessary to set movement to upCell and leftCell;

		stepper = rightCell;
		if(stepper == null || col >= maxCols - 2)
			canMoveRight = false;
		else {
			stepper = stepper.getRightCell();
			if(stepper == null)
				canMoveRight = false;
		}

		stepper = downCell;
		if(stepper == null || row >= maxRows - 2)
			canMoveDown = false;
		else {
			stepper = stepper.getRightCell();
			if(stepper == null)
				canMoveDown = false;
		}
	}

	public void setPos(int col, int row){
		this.col = col;
		this.row = row;
	}

	public void setMapCell(MapCell newCell) {
		mapCell = newCell;
		if (mapCell == null){
			size = 0;
			destructible = false;
			accessible = true;
		} else {
			size = mapCell.getSize();
			destructible = mapCell.isDestructible();
			accessible = mapCell.isAccessible();
		}
	}

	public void setIndexId(int index){
		if(index >= 0)
			id = index;
	}


	public boolean collide(Cell cell){
		int row2nd = cell.getRow(), col2nd = cell.getCol(),
				size2nd = cell.getCellSize();

		return row2nd + size2nd > row && col2nd + size2nd > col && row + size > row2nd && col + size > col2nd;
	}

	public void linkNeighborCells(Cell up, Cell right, Cell down, Cell left){
		upCell = up;
		rightCell = right;
		downCell = down;
		leftCell = left;
	}

	public void drawCell(GraphicsContext context, Image tile){
		if(mapCell == null)
			return;
		int size = mapCell.getSize();
		context.drawImage(tile, mapCell.getRow(), mapCell.getCol(), size, size, col, row, size, size);
	}
}
