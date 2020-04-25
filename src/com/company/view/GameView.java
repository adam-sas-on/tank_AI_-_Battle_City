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
	private final int rowColCells = 26;
	private final int sizePixels = 16;

	public GameView(){
		canvas = new Canvas(rowColCells*sizePixels, rowColCells*sizePixels);
		gContext = canvas.getGraphicsContext2D();

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);

		cells = new Cell[rowColCells*rowColCells];
		setCellsStructure();

		exampleCells();
	}

	public Cell changeCellPositionToClosest(Cell cell){
		return null;
	}

	private void setUpperRowCells(){
		cells[0].linkNeighborCells(null, cells[1], cells[rowColCells], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = rowColCells - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+rowColCells], cells[i-1]);
			cells[i].setPos(i*sizePixels, 0);
		}

		cells[i].linkNeighborCells(null, null, cells[i+rowColCells], cells[i-1]);
		cells[i].setPos(i*sizePixels, 0);
	}

	private void setCellsStructure(){
		int i;
		for(i = cells.length - 1; i >= 0; i--)
			cells[i] = new Cell();

		setUpperRowCells();

		int rowIndex, colIndex,
				rowColLimit = rowColCells - 1;

		i = rowColCells;
		for(rowIndex = 1; rowIndex < rowColLimit; rowIndex++, i++){// loop through rows;
			cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], cells[i+rowColCells], null);// left side;
			cells[i].setPos(0, rowIndex*sizePixels);

			for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++){
				cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], cells[i+rowColCells], cells[i-1]);
				cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
			}

			cells[i].linkNeighborCells(cells[i-rowColCells], null, cells[i+rowColCells], cells[i-1]);// right side;
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i-rowColCells], cells[i+1], null, null);
		cells[i].setPos(0, rowIndex*sizePixels);

		for(colIndex = 1, i++; colIndex < rowColLimit; colIndex++, i++) {// connect lower row;
			cells[i].linkNeighborCells(cells[i - rowColCells], cells[i + 1], null, cells[i - 1]);
			cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
		}

		cells[i].linkNeighborCells(cells[i-rowColCells], null, null, cells[i-1]);
		cells[i].setPos(colIndex*sizePixels, rowIndex*sizePixels);
	}

	private void exampleCells(){
		cells[0].setMapCell(MapCell.EAGLE);
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
		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		for(int i = rowColCells*rowColCells-1; i >= 0; i--)
			cells[i].drawCell(gContext, tiles);

	}
}
