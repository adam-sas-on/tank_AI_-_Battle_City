package com.company.view;

import com.company.GameDynamics;
import com.company.model.Bullet;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameView {
	private Canvas canvas;
	private GraphicsContext gContext;
	private static Image tiles;
	private Cell[] cells;
	private int[] positions;
	private int rowCells = 26, colCells = 26;
	private int sizePixels = 16;
	List<Integer> trees;
	List<Cell> tanks;
	List<Bullet> bullets;

	public GameView(){
		canvas = new Canvas(colCells *sizePixels, rowCells *sizePixels);
		gContext = canvas.getGraphicsContext2D();

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);


		cells = new Cell[rowCells * rowCells];
		positions = new int[rowCells];
		setCellsStructure();

		exampleCells();
		trees = new ArrayList<>();
		tanks = new ArrayList<>();
		bullets = new LinkedList<>();
	}

	public int getRowColCells(){
		return rowCells;
	}
	public int getSizePixels(){
		return sizePixels;
	}

	public void setColsRows(int newCols, int newRows){
		if(newCols > 1)
			colCells = newCols;
		if(newRows > 1)
			rowCells = newRows;
	}

	public void modifyCellSize(int stageWidth, int stageHeight){
		int widthSizePixels = stageWidth/colCells;
		sizePixels = stageHeight/rowCells;
		if(sizePixels > widthSizePixels)
			sizePixels = widthSizePixels;

		canvas.setWidth(colCells*sizePixels);
		canvas.setHeight(rowCells*sizePixels);
	}

	private int binarySearchPos(int pos){
		int j = positions.length - 1;

		if(pos > positions[j] )
			return positions[j];

		int i = 0, mid;
		while(i <= j){
			mid = (i + j)/2;

			if(pos < positions[mid])
				j = mid - 1;
			else if(pos > positions[mid])
				i = mid + 1;
			else
				return positions[mid];
		}

		return ( (positions[i] - pos) <= (pos - positions[j]) )? positions[i] : positions[j];
	}

	public void changeCellPositionToClosest(Cell cell){
		int row, col;

		col = binarySearchPos(cell.getCol());
		row = binarySearchPos(cell.getRow());

		cell.setPos(col, row);
	}

	private void setUpperRowCells(){
		cells[0].linkNeighborCells(null, cells[1], cells[rowCells], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = rowCells - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+ rowCells], cells[i-1]);
			cells[i].setPos(i*sizePixels, 0);
			positions[i] = i*sizePixels;
		}

		cells[i].linkNeighborCells(null, null, cells[i+ rowCells], cells[i-1]);
		cells[i].setPos(i*sizePixels, 0);
		positions[i] = i*sizePixels;
	}

	private void setCellsStructure() {
		int i;
		for (i = cells.length - 1; i >= 0; i--){
			cells[i] = new Cell();
			cells[i].setIndexId(i);
		}

		positions[0] = 0;
		setUpperRowCells();

		int rowIndex, colIndex,
				rowColLimit = rowCells - 1;

		i = rowCells;
		for(rowIndex = 1; rowIndex < rowColLimit; rowIndex++, i++){// loop through rows;
			cells[i].linkNeighborCells(cells[i- rowCells], cells[i+1], cells[i+ rowCells], null);// left side;
			cells[i].setPos(0, rowIndex*sizePixels);
			positions[rowIndex] = rowIndex*sizePixels;

			for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++){
				cells[i].linkNeighborCells(cells[i- rowCells], cells[i+1], cells[i+ rowCells], cells[i-1]);
				cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
			}

			cells[i].linkNeighborCells(cells[i- rowCells], null, cells[i+ rowCells], cells[i-1]);// right side;
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i- rowCells], cells[i+1], null, null);
		cells[i].setPos(0, rowIndex*sizePixels);
		positions[rowIndex] = rowIndex*sizePixels;

		for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++) {// connect lower row;
			cells[i].linkNeighborCells(cells[i - rowCells], cells[i + 1], null, cells[i - 1]);
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i- rowCells], null, null, cells[i-1]);
		cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
	}

	private void exampleCells(){
		cells[0].setMapCell(MapCell.EAGLE);

		Cell cell = cells[0].getDownCell();
		cell = cell.getDownCell();
		cell.setMapCell(MapCell.TANK_2_LVL_3_STATE_1_RIGHT);
	}

	public void addBullet(Bullet bullet){
		bullets.add(bullet);
	}

	public void addCell(Cell cell){
		tanks.add(cell);
	}

	/*private boolean cellNotCollideWithOthers(Cell cell){
		for(Cell sprite : tanks){
			if(sprite.equals(cell) )
				continue;

			if(cell.collide(sprite) )
				return false;
		}

		return true;
	}

	public boolean setPosIfAccessible(Cell cell, int col, int row, KeyCode direction){
		int size = cell.getCellSize(),
			colCell = col/sizePixels, rowCell = row/sizePixels,
			cellIndex = rowCell*rowColCells + colCell;
		if(cellIndex >= cells.length)
			return false;

		boolean accessible = cells[cellIndex].isAccessible();
		if(size < sizePixels || !accessible)
			return accessible;

		// - - - case when cell covers also neighbour cells:
		Cell cellLeft = null, cellRight = null;
		switch (direction) {
			case UP:
				cellLeft = cells[cellIndex];
				cellRight = cellLeft.getRightCell();
				break;
			case RIGHT:
				cellLeft = cells[cellIndex].getRightCell();
				if(cellLeft != null) {
					cellRight = cellLeft.getDownCell();
					cellLeft = cellLeft.getRightCell();
				}
				if(cellRight != null)
					cellRight = cellRight.getRightCell();
				break;
			case DOWN:
				cellRight = cells[cellIndex].getDownCell();
				if(cellRight != null){
					cellLeft = cellRight.getRightCell();
					cellRight = cellRight.getDownCell();
				}
				if(cellLeft != null)
					cellLeft = cellLeft.getDownCell();
				break;
			case LEFT:
				cellRight = cells[cellIndex];
				cellLeft = cellRight.getDownCell();
				break;
		}

		if(cellLeft == null || cellRight == null)
			return false;

		accessible = cellLeft.isAccessible() && cellRight.isAccessible();
		if(accessible)
			accessible = cellNotCollideWithOthers(cell);

		if(accessible)
			cell.setPos(col, row);

		return accessible;
	}*/


	public Scene drawStart(){
		GridPane gridPane = new GridPane();
		BorderPane borderP = new BorderPane();

		borderP.setCenter(canvas);
		borderP.setRight(gridPane);

		//StackPane layout = new StackPane();
		Scene scene = new Scene(borderP);

		return scene;
	}

	public void drawMap(GameDynamics dynamics){
		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		dynamics.setCellSize(sizePixels);

		Cell cell;

		Iterator<Cell> iter = dynamics.iterator();
		while(iter.hasNext() ){
			cell = iter.next();
			cell.drawCell(gContext, tiles);
		}

	}
}
