package com.company.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;


public class Cell {
	private MapCell mapCell;
	// unit coordinates on map for cell or local cell for movable cell; coordinates on screen when drawing;
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

	public int checkModifyRow(KeyCode direction, int rowToCheck){
		int modifiedRow = rowToCheck;
		switch(direction){
			case UP:
				if(!canMoveUp || !accessible)
					modifiedRow = downCell.getRow();
				break;
			case RIGHT:
				if(!canMoveRight || !accessible)
					modifiedRow = row;
				break;
			case DOWN:
				if(!canMoveDown || !accessible)
					modifiedRow = row;
				break;
			case LEFT:
				if(!canMoveLeft || !accessible)
					modifiedRow = rightCell.getRow();
				break;
		}
		return modifiedRow;
	}

	public int checkModifyCol(KeyCode direction, int colToCheck){
		int modifiedCol = colToCheck;
		switch(direction){
			case UP:
				if(!canMoveUp || !accessible)
					modifiedCol = downCell.getCol();
				break;
			case RIGHT:
				if(!canMoveRight || !accessible)
					modifiedCol = col;
				break;
			case DOWN:
				if(!canMoveDown || !accessible)
					modifiedCol = col;
				break;
			case LEFT:
				if(!canMoveLeft || !accessible)
					modifiedCol = rightCell.getCol();
				break;
		}
		return modifiedCol;
	}

	public void roundPos(final int oldUnitSize, final int newUnitSize){
		col = (col*newUnitSize)/oldUnitSize;// round down (floor);
		row = (row*newUnitSize)/oldUnitSize;
	}

	/*private boolean inaccessibleAndBigger(Cell cell, final int basicSize){
		if(cell.getMapCell() == null)
			return false;
		int mapCellSize = cell.getMapCell().getSize();
		return mapCellSize > basicSize && !cell.isAccessible();
	}

	/**
	 * Checks all cells up-left around;
	 * @return true is any of cells around is not accessible
	 * /
	private boolean isOnBiggerBlockingCell(){
		int basicCellSize = MapCell.BRICK.getUnitSize();
		Cell checkCell;
		boolean isBlocking = false;

		checkCell = upCell;
		if(checkCell != null){
			isBlocking = inaccessibleAndBigger(checkCell, basicCellSize);
			if(isBlocking)
				return true;
		}

		checkCell = leftCell;
		if(checkCell != null){
			isBlocking = inaccessibleAndBigger(checkCell, basicCellSize);
			if(isBlocking)
				return true;

			checkCell = checkCell.getUpCell();
			if(checkCell != null)
				isBlocking = inaccessibleAndBigger(checkCell, basicCellSize);
		}

		// (!accessible1 && isBigger1) || (!accessible2  && isBigger2) || (!accessible3  && isBigger3);
		return isBlocking;
	}*/

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

	public void blockUnblockMoveToUp(boolean unsetBlockade){
		canMoveUp = unsetBlockade;
	}

	public void blockUnblockMoveToRight(boolean unsetBlockade){
		canMoveRight = unsetBlockade;
	}

	public void blockUnblockMoveToDown(boolean unsetBlockade){
		canMoveDown = unsetBlockade;
	}

	public void blockUnblockMoveToLeft(boolean unsetBlockade){
		canMoveLeft = unsetBlockade;
	}

	private void canMoveFromUp(boolean unsetBlockade){
		Cell cellToSet, cellToSet2 = null;
		upCell.blockUnblockMoveToDown(unsetBlockade);

		cellToSet = upCell.getUpCell();
		if(cellToSet != null){// up-up cell exists;
			cellToSet2 = cellToSet.getLeftCell();
			if(cellToSet.getRightCell() != null)// up-up cell has right neighbour;
				cellToSet.blockUnblockMoveToDown(unsetBlockade);
		}
		if(cellToSet2 != null)
			cellToSet2.blockUnblockMoveToDown(unsetBlockade);
	}

	private void canMoveFromLeft(boolean unsetBlockade){
		Cell cellToSet, cellToSet2 = null;

		cellToSet = leftCell.getLeftCell();
		if(cellToSet != null){// left-left cell exists;
			cellToSet2 = cellToSet.getUpCell();
			if(cellToSet.getDownCell() != null)// left-left cell has down neighbour;
				cellToSet.blockUnblockMoveToRight(unsetBlockade);
		}
		if(cellToSet2 != null)
			cellToSet2.blockUnblockMoveToRight(unsetBlockade);
	}

	public void blockMovementsAround(){
		canMoveUp = canMoveRight = canMoveDown = canMoveLeft = false;

		// - - - vertical;
		if(upCell != null){// can move from UP;
			canMoveFromUp(false);
		}

		if(downCell != null && leftCell != null){// can move from down
			leftCell.blockUnblockMoveToUp(false);
		}

		// - - - horizontal;
		if(leftCell != null){// can move from left?
			canMoveFromLeft(false);
		}

		if(rightCell != null && upCell != null){// can move from right;
			upCell.blockUnblockMoveToLeft(false);
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

	public void drawCell(GraphicsContext context, Image tile, double zoomMultiplier){
		if(mapCell == null)
			return;
		int mapCellSize = mapCell.getSize();
		double zoomedSize = zoomMultiplier*mapCellSize;
		context.drawImage(tile, mapCell.getRow(), mapCell.getCol(), mapCellSize, mapCellSize, col, row, zoomedSize, zoomedSize);
	}
}
