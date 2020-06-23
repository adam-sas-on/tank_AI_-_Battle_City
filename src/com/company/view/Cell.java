package com.company.view;

import com.company.model.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Cell {
	private MapCell mapCell;
	// unit coordinates on map for cell or local cell for movable cell; coordinates on screen when drawing;
	private int col;
	private int row;
	private int size;// size of icon from image-cells in pixels;
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

	public int checkModifyRow(Direction direction, int rowToCheck){
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

	public int checkModifyCol(Direction direction, int colToCheck){
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
		Cell stepper;
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
// todo: make one method;
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
		leftCell.blockUnblockMoveToRight(unsetBlockade);

		cellToSet = leftCell.getLeftCell();
		if(cellToSet != null){// left-left cell exists;
			cellToSet2 = cellToSet.getUpCell();
			if(cellToSet.getDownCell() != null)// left-left cell has down neighbour;
				cellToSet.blockUnblockMoveToRight(unsetBlockade);
		}
		if(cellToSet2 != null)
			cellToSet2.blockUnblockMoveToRight(unsetBlockade);
	}

	private boolean getMovementCase(Cell cell1, Cell cell2){
		if(cell1 == null || cell2 == null)
			return false;
		return cell1.isAccessible() && cell2.isAccessible();
	}

	private void checkToUnblockUpperCells(){
		if(upCell == null)
			return;

		Cell cellToSet, checkCell;
		boolean canMove;

		// - - - vertical;
		cellToSet = upCell.getUpCell();
		if(cellToSet != null){// UP-cell has UP-neighbour:
			canMove = getMovementCase(upCell, rightCell) && cellToSet.isAccessible();
			cellToSet.blockUnblockMoveToDown(canMove);

			cellToSet = cellToSet.getLeftCell();
			if(cellToSet != null){
				checkCell = upCell.getLeftCell();
				canMove = getMovementCase(checkCell, leftCell) && cellToSet.isAccessible();
				cellToSet.blockUnblockMoveToDown(canMove);
			}
		}

		if(downCell != null){// set/unset blockade for upper-cell;
			checkCell = downCell.getRightCell();
			canMove = getMovementCase(checkCell, downCell) && upCell.isAccessible();
			upCell.blockUnblockMoveToDown(canMove);
		}
	}

	private void checkToUnblockLeftCells(){
		if(leftCell == null)
			return;

		Cell cellToSet, checkCell;
		boolean canMove;

		// - - - horizontal;
		cellToSet = leftCell.getLeftCell();
		if(cellToSet != null){
			canMove = getMovementCase(leftCell, downCell) && cellToSet.isAccessible();
			cellToSet.blockUnblockMoveToRight(canMove);

			cellToSet = cellToSet.getUpCell();
			if(cellToSet != null){
				checkCell = leftCell.getUpCell();
				canMove = getMovementCase(checkCell, upCell) && cellToSet.isAccessible();
				cellToSet.blockUnblockMoveToRight(canMove);
			}
		}

		if(rightCell != null){
			checkCell = rightCell.getDownCell();
			canMove = getMovementCase(checkCell, rightCell) && leftCell.isAccessible();
			leftCell.blockUnblockMoveToRight(canMove);
		}
	}

	private void selfCheckToUnblockRight(){
		if(rightCell == null)
			return;

		if( !rightCell.isAccessible() )
			return;

		canMoveUp = true;

		Cell checkCell1 = rightCell.getRightCell();
		if(checkCell1 == null)
			return;

		Cell checkCell2 = checkCell1.getDownCell();
		canMoveRight = getMovementCase(checkCell1, checkCell2);
	}

	private void selfCheckToUnblockDown(){
		if(downCell == null)
			return;

		if( !downCell.isAccessible() )
			return;

		canMoveLeft = true;

		Cell checkCell1 = downCell.getDownCell();
		if(checkCell1 == null)
			return;

		Cell checkCell2 = checkCell1.getRightCell();
		canMoveDown = getMovementCase(checkCell1, checkCell2);
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

	public void unblockMovementsAround(){
		if(!accessible)
			return;

		if(upCell != null)
			upCell.blockUnblockMoveToLeft( upCell.isAccessible() );

		if(leftCell != null)
			leftCell.blockUnblockMoveToUp( leftCell.isAccessible() );

		checkToUnblockUpperCells();
		checkToUnblockLeftCells();

		selfCheckToUnblockRight();
		selfCheckToUnblockDown();
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

	// todo: what if mapToSet == null? change names cell1, cell2
	private Cell setSideWalls(MapCell mapToSet, Cell cell1, Cell cell2, int length){
		boolean isBlocking = !mapToSet.isAccessible();
		int i = 0;
		while(i < length && (cell1 != null || cell2 != null) ){
			if(cell1 != null){
				cell1.setMapCell(mapToSet);
				if(isBlocking)
					cell1.blockMovementsAround();
				cell1 = cell1.getDownCell();
			}
			if(cell2 != null){
				cell2.setMapCell(mapToSet);
				if(isBlocking)
					cell2.blockMovementsAround();
				cell2 = cell2.getDownCell();
			}
			i++;
		}
		return cell1;
	}

	public void encircleByMapCell(MapCell mapToSet){
		Cell cellToSet, rightCellToStart;
		int width = mapCell.getSize()/MapCell.getUnitSize() + 2, i;
		boolean isBlocking = !mapToSet.isAccessible();

		if(upCell != null){
			// - - - set  mapToSet  on upper wall;
			cellToSet = upCell.getLeftCell();
			if(cellToSet != null){
				cellToSet.setMapCell(mapToSet);
				if(isBlocking)
					cellToSet.blockMovementsAround();
			}

			cellToSet = upCell;
			for(i = 1; i < width && cellToSet != null; i++){
				cellToSet.setMapCell(mapToSet);
				if(isBlocking)
					cellToSet.blockMovementsAround();
				cellToSet = cellToSet.getRightCell();
			}
		}

		for(i = width - 1, rightCellToStart = rightCell; i < width && rightCellToStart != null; i++){
			rightCellToStart = rightCellToStart.getRightCell();
		}

		// - - - Set mapToSet  on side walls;
		cellToSet = leftCell;
		cellToSet = setSideWalls(mapToSet, cellToSet, rightCellToStart, width - 1);

		if(cellToSet != null)
			cellToSet = cellToSet.getRightCell();

		for(i = 1; i < width && cellToSet != null; i++){
			cellToSet.setMapCell(mapToSet);
			if(isBlocking)
				cellToSet.blockMovementsAround();
			cellToSet = cellToSet.getRightCell();
		}
	}

	public void setByOtherCell(Cell cell){
		if(cell == null)
			return;

		col = cell.getCol();
		row = cell.getRow();
		setMapCell( cell.getMapCell() );
	}

	public void setIndexId(int index){
		if(index >= 0)
			id = index;
	}

	private void setUpperRowCells(Cell[] cells, int cols, final int cellPrecisionUnitSize){
		cells[0].linkNeighborCells(null, cells[1], cells[cols], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = cols - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+cols], cells[i-1]);
			cells[i].setPos(i*cellPrecisionUnitSize, 0);
		}

		cells[i].linkNeighborCells(null, null, cells[i+cols], cells[i-1]);
		cells[i].setPos(i*cellPrecisionUnitSize, 0);
	}

	public void setCellStructure(Cell[] cells, int cols, int rows, final int cellPrecisionUnitSize){
		if(cols*rows > cells.length)
			throw new IndexOutOfBoundsException("Array of cells is too small to set the structure!");

		cells[0] = this;

		setUpperRowCells(cells, cols, cellPrecisionUnitSize);

		int rowIndex, colIndex, i;
		final int rowLimit = rows - 1, colLimit = cols - 1;

		i = cols;
		for(rowIndex = 1; rowIndex < rowLimit; rowIndex++, i++){// loop through rows;
			cells[i].linkNeighborCells(cells[i - cols],  cells[i + 1],  cells[i + cols], null);
			cells[i].setPos(0, rowIndex*cellPrecisionUnitSize);

			for(colIndex = 1, i++; colIndex < colLimit; colIndex++, i++){// loop through cols;
				cells[i].linkNeighborCells(cells[i - cols],  cells[i + 1],  cells[i + cols],  cells[i - 1]);
				cells[i].setPos(colIndex*cellPrecisionUnitSize, rowIndex*cellPrecisionUnitSize);
			}

			cells[i].linkNeighborCells(cells[i - cols],  null,  cells[i + cols],  cells[i - 1]);
			cells[i].setPos(colIndex*cellPrecisionUnitSize, rowIndex*cellPrecisionUnitSize);
		}

		// last row;
		cells[i].linkNeighborCells(cells[i - cols],  cells[i + 1],  null, null);
		cells[i].setPos(0, rowIndex*cellPrecisionUnitSize);

		for(colIndex = 1, i++; colIndex < colLimit; colIndex++, i++){// connect lowest row;
			cells[i].linkNeighborCells(cells[i - cols],  cells[i + 1], null, cells[i - 1]);
			cells[i].setPos(colIndex*cellPrecisionUnitSize, rowIndex*cellPrecisionUnitSize);
		}

		cells[i].linkNeighborCells(cells[i - cols], null, null, cells[i - 1]);
		cells[i].setPos(colIndex*cellPrecisionUnitSize, rowIndex*cellPrecisionUnitSize);
	}

	public boolean collide(Cell cell, final int unitSizeOfCells){
		int row2nd = cell.getRow(), col2nd = cell.getCol(),
			size2nd = (cell.getCellSize()*unitSizeOfCells )/MapCell.getUnitSize(),
			sizeOfThis = (size*unitSizeOfCells )/MapCell.getUnitSize();

		return row2nd + size2nd > row && col2nd + size2nd > col && row + sizeOfThis > row2nd && col + sizeOfThis > col2nd;
	}

	public int collisionArea(Cell cell, final int unitSizeOfCells){
		int overlapWidth, overlapHeight,
			size2nd = (cell.getCellSize()*unitSizeOfCells )/MapCell.getUnitSize(),
			sizeOfThis = (size*unitSizeOfCells )/MapCell.getUnitSize(), colRow;

		colRow = cell.getCol();
		overlapWidth = Math.min(col + sizeOfThis, colRow + size2nd) - Math.max(col, colRow);
		colRow = cell.getRow();
		overlapHeight = Math.min(row + sizeOfThis, colRow + size2nd) - Math.max(row, colRow);

		return (overlapWidth > 0 && overlapHeight > 0)?overlapWidth*overlapHeight:0;
	}

	public void linkNeighborCells(Cell up, Cell right, Cell down, Cell left){
		upCell = up;
		rightCell = right;
		downCell = down;
		leftCell = left;
	}

	public void drawCell(GraphicsContext context, Image tile, double zoomMultiplier){
		if(mapCell == null || mapCell == MapCell.NULL_BLOCKADE || mapCell == MapCell.NULL_UNIT_BLOCKADE)
			return;
		int mapCellSize = mapCell.getSize();
		double zoomedSize = zoomMultiplier*mapCellSize;
		context.drawImage(tile, mapCell.getRow(), mapCell.getCol(), mapCellSize, mapCellSize, col, row, zoomedSize, zoomedSize);
	}


	private boolean caseAppend(StringBuilder sb, boolean appendCase, String appendString, boolean previousAppend){
		if(appendCase){
			if(previousAppend)
				sb.append(", ");
			sb.append(appendString);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(23);
		sb.append("Cell (id:").append(id).append(") {").append(mapCell)
			.append("; position: (").append(col).append(", ").append(row)
			.append("); size = ").append(size);
		if(!accessible){
			sb.append("; access-denied; neighbors: {");
		} else
			sb.append("; neighbors: {");

		boolean hasElement;
		hasElement = caseAppend(sb, upCell != null, "UP", false);
		hasElement = caseAppend(sb, rightCell != null, "RIGHT", hasElement);
		hasElement = caseAppend(sb, downCell != null, "DOWN", hasElement);
		hasElement = caseAppend(sb, leftCell != null, "LEFT", hasElement);

		if(hasElement)
			sb.append("}; ");
		else
			sb.append("NONE}; ");

		sb.append("allowed movements: {");
		hasElement = caseAppend(sb, canMoveUp,"UP", false);
		hasElement = caseAppend(sb, canMoveRight, "RIGHT", hasElement);
		hasElement = caseAppend(sb, canMoveDown, "DOWN", hasElement);
		hasElement = caseAppend(sb, canMoveLeft, "LEFT", hasElement);

		if(hasElement)
			sb.append("};");
		else
			sb.append("NONE};");

		return sb.toString();
	}
}
