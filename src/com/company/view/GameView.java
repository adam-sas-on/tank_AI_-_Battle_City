package com.company.view;

import com.company.model.Tank;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

public class GameView {
	private Canvas canvas;
	private GraphicsContext gContext;
	private static Image tiles;
	private MapLoader mapLoader;
	private Cell[] cells;
	private int[] positions;
	private final int rowColCells = 26;
	private final int sizePixels = 16;
	List<Integer> trees;
	List<Cell> tanks;

	public GameView(){
		canvas = new Canvas(rowColCells*sizePixels, rowColCells*sizePixels);
		gContext = canvas.getGraphicsContext2D();

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);

		mapLoader = MapLoader.getInstance();

		cells = new Cell[rowColCells*rowColCells];
		positions = new int[rowColCells];
		setCellsStructure();

		exampleCells();
		trees = new ArrayList<>();
		tanks = new ArrayList<>();
	}

	public int getRowColCells(){
		return rowColCells;
	}
	public int getSizePixels(){
		return sizePixels;
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
		cells[0].linkNeighborCells(null, cells[1], cells[rowColCells], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = rowColCells - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+rowColCells], cells[i-1]);
			cells[i].setPos(i*sizePixels, 0);
			positions[i] = i*sizePixels;
		}

		cells[i].linkNeighborCells(null, null, cells[i+rowColCells], cells[i-1]);
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
				rowColLimit = rowColCells - 1;

		i = rowColCells;
		for(rowIndex = 1; rowIndex < rowColLimit; rowIndex++, i++){// loop through rows;
			cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], cells[i+rowColCells], null);// left side;
			cells[i].setPos(0, rowIndex*sizePixels);
			positions[rowIndex] = rowIndex*sizePixels;

			for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++){
				cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], cells[i+rowColCells], cells[i-1]);
				cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
			}

			cells[i].linkNeighborCells(cells[i-rowColCells], null, cells[i+rowColCells], cells[i-1]);// right side;
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], null, null);
		cells[i].setPos(0, rowIndex*sizePixels);
		positions[rowIndex] = rowIndex*sizePixels;

		for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++) {// connect lower row;
			cells[i].linkNeighborCells(cells[i - rowColCells], cells[i + 1], null, cells[i - 1]);
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i-rowColCells], null, null, cells[i-1]);
		cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
	}

	private void exampleCells(){
		cells[0].setMapCell(MapCell.EAGLE);

		Cell cell = cells[0].getDownCell();
		cell = cell.getDownCell();
		cell.setMapCell(MapCell.TANK_2_LVL_3_STATE_1_RIGHT);
	}

	public void addCell(Cell cell){
		tanks.add(cell);
	}

	private boolean cellNotCollideWithOthers(Cell cell){
		for(Cell sprite : tanks){
			if(sprite.equals(cell) )
				continue;

			if(cell.collide(sprite) )
				return false;
		}

		/*Iterator<Cell> iter = tanks.iterator();
		Cell sprite;

		while(iter.hasNext() ){
			sprite = iter.next();
			if(sprite.equals(cell) )
				continue;

			if(cell.collide(sprite) )
				return false;
		}*/
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
	}

	public void loadMapSetPlayers(String fileName, Tank player1, Tank player2){
		mapLoader.loadMap(cells[0], fileName, player1, player2, trees);
	}

	public Scene drawStart(){
		GridPane gridPane = new GridPane();
		BorderPane borderP = new BorderPane();

		borderP.setCenter(canvas);
		borderP.setRight(gridPane);

		//StackPane layout = new StackPane();
		Scene scene = new Scene(borderP);

		return scene;
	}

	public void drawMap(){
		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		int i;
		for(i = rowColCells*rowColCells-1; i >= 0; i--) {
			cells[i].drawCell(gContext, tiles);
		}

		for (Cell tank : tanks){
			tank.drawCell(gContext, tiles);
		}

		int ind;
		i = trees.size() - 1;
		for(; i >= 0; i--){
			ind = trees.get(i);
			cells[ind].drawCell(gContext, tiles);
		}

	}
}
