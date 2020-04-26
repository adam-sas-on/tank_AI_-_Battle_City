package com.company.view;

import com.company.model.Tank;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class GameView {
	private Canvas canvas;
	private GraphicsContext gContext;
	private static Image tiles;
	private Cell[] cells;
	private int[] positions;
	private final int rowColCells = 26;
	private final int sizePixels = 16;

	public GameView(){
		canvas = new Canvas(rowColCells*sizePixels, rowColCells*sizePixels);
		gContext = canvas.getGraphicsContext2D();

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);

		cells = new Cell[rowColCells*rowColCells];
		positions = new int[rowColCells];
		setCellsStructure();

		exampleCells();
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

	private void setCellsStructure(){
		int i;
		for(i = cells.length - 1; i >= 0; i--)
			cells[i] = new Cell();

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

	public Scene drawStart(){
		GridPane gridPane = new GridPane();
		BorderPane borderP = new BorderPane();

		borderP.setCenter(canvas);
		borderP.setRight(gridPane);

		//StackPane layout = new StackPane();
		Scene scene = new Scene(borderP);

		return scene;
	}

	public void drawMap(Tank tank){
		Cell cell = tank.getCell();
		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		for(int i = rowColCells*rowColCells-1; i >= 0; i--)
			cells[i].drawCell(gContext, tiles);

		cell.drawCell(gContext, tiles);
	}
}
