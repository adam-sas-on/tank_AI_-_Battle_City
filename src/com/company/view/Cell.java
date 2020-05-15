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

	/*public boolean canMove(KeyCode direction){
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
	}*/

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

	public void blockMoveToUp(MapCell mapCell1, MapCell mapCell2){
		boolean accessible1 = (mapCell1 == null) || mapCell1.isAccessible(),
				accessible2 = (mapCell2 == null) || mapCell2.isAccessible();
		canMoveUp = accessible1 && accessible2;
	}

	public void blockMoveToRight(MapCell mapCell1, MapCell mapCell2){
		boolean accessible1 = (mapCell1 == null) || mapCell1.isAccessible(),
				accessible2 = (mapCell2 == null) || mapCell2.isAccessible();
		canMoveRight = accessible1 && accessible2;
	}

	public void blockMoveToDown(MapCell mapCell1, MapCell mapCell2){
		boolean accessible1 = (mapCell1 == null) || mapCell1.isAccessible(),
				accessible2 = (mapCell2 == null) || mapCell2.isAccessible();
		canMoveDown = accessible1 && accessible2;
	}

	public void blockMoveToLeft(MapCell mapCell1, MapCell mapCell2){
		boolean accessible1 = (mapCell1 == null) || mapCell1.isAccessible(),
				accessible2 = (mapCell2 == null) || mapCell2.isAccessible();
		canMoveLeft = accessible1 && accessible2;
	}

	private void canMoveFromUp(MapCell leftMapCell, MapCell rightMapCell){
		Cell cellToSet, cellToSet2 = null;
		upCell.blockMoveToDown(mapCell, rightMapCell);

		cellToSet = upCell.getUpCell();
		if(cellToSet != null){// up-up cell exists;
			cellToSet2 = cellToSet.getLeftCell();
			if(cellToSet.getRightCell() != null)// up-up cell has right neighbour;
				cellToSet.blockMoveToDown(mapCell, rightMapCell);
		}
		if(cellToSet2 != null)
			cellToSet2.blockMoveToDown(leftMapCell, mapCell);
	}

	private void canMoveFromLeft(MapCell upperMapCell, MapCell lowerMapCell){
		Cell cellToSet, cellToSet2 = null;

		cellToSet = leftCell.getLeftCell();
		if(cellToSet != null){// left-left cell exists;
			cellToSet2 = cellToSet.getUpCell();
			if(cellToSet.getDownCell() != null)// left-left cell has down neighbour;
				cellToSet.blockMoveToRight(mapCell, lowerMapCell);
		}
		if(cellToSet2 != null)
			cellToSet2.blockMoveToRight(upperMapCell, mapCell);
	}

	public void blockMovementsAround(){
		if(accessible)
			return;

		MapCell sideMapCell1 = null, sideMapCell2 = null;
		Cell cellToSet, cellToSet2;
		// - - - vertical;
		if(upCell != null || downCell != null){
			if(leftCell != null)
				sideMapCell1 = leftCell.getMapCell();
			if(rightCell != null)
				sideMapCell2 = rightCell.getMapCell();
		}

		if(upCell != null){// can move from UP;
			canMoveFromUp(sideMapCell1, sideMapCell2);
		}

		if(downCell != null && leftCell != null){// can move from down
			leftCell.blockMoveToUp(sideMapCell1, mapCell);
		}

		// - - - horizontal;
		sideMapCell1 = sideMapCell2 = null;
		if(leftCell != null || rightCell != null){
			if(upCell != null)
				sideMapCell1 = upCell.getMapCell();
			if(downCell != null)
				sideMapCell2 = downCell.getMapCell();
		}

		if(leftCell != null){// can move from left?
			canMoveFromLeft(sideMapCell1, sideMapCell2);
		}

		if(rightCell != null && upCell != null){// can move from right;
			upCell.blockMoveToLeft(sideMapCell1, mapCell);
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
