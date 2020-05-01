package com.company.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Cell {
	private MapCell mapCell;
	private int col;
	private int row;
	private int size;
	private boolean destructible;
	private Cell upCell, rightCell, downCell, leftCell;
	private int id;

	public Cell(){
		row = col = 0;
		mapCell = null;
		destructible = false;
		upCell = rightCell = downCell = leftCell = null;
		id = -1;
	}

	public int getId(){
		return id;
	}

	public MapCell getMapCell(){
		return mapCell;
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
		} else {
			size = mapCell.getSize();
			destructible = mapCell.isDestructible();
		}
	}

	public void setIndexId(int index){
		if(index >= 0)
			id = index;
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
		if(mapCell != null)
			return mapCell.isAccessible();

		return true;
	}

	public boolean isDestructible(){
		if(mapCell == MapCell.STEEL)
			return true;
		return destructible;
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
