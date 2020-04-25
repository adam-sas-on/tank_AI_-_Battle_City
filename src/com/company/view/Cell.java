package com.company.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

public class Cell {
	MapCell mapCell;
	private int col;
	private int row;
	private Cell upCell, rightCell, downCell, leftCell;

	public Cell(){
		row = col = 0;
		mapCell = null;
		upCell = rightCell = downCell = leftCell = null;
	}

	public void setPos(int col, int row){
		this.col = col;
		this.row = row;
	}

	public void setMapCell(MapCell newCell){
		mapCell = newCell;
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

	public void linkLeftCell(Cell cell){
		leftCell = cell;
	}

	public void linkNeighborCells(Cell up, Cell right, Cell down, Cell left){
		upCell = up;
		rightCell = right;
		downCell = down;
		leftCell = left;
	}
	/*public boolean collide(Cell cell){

		return false;
	}*/

	public void drawCell(GraphicsContext context, Image tile){
		if(mapCell == null)
			return;
		int size = mapCell.getSize();
		context.drawImage(tile, mapCell.getRow(), mapCell.getCol(), size, size, col, row, size, size);
	}
}
