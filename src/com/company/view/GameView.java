package com.company.view;

import com.company.GameDynamics;
import com.company.model.Bullet;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameView {
	private Canvas canvas;
	private GraphicsContext gContext;
	private static Image tiles;
	private Button startPause;
	private Label[] playersLives;
	private Cell[] cells;
	private int[] positions;
	private int rowCells = 26, colCells = 26;
	private int sizePixels;
	private int rightMenuWidth;
	private int framesPerSecond;
	private boolean pause;
	private final int unitSize = MapCell.getUnitSize();// any icon because unit size is the same for all;
	private final int cellDefaultSize;
	List<Integer> trees;
	List<Cell> tanks;
	List<Bullet> bullets;

	public GameView(int cellPrecisionUnitSize){
		sizePixels = unitSize;
		framesPerSecond = 2;

		canvas = new Canvas(colCells *sizePixels, rowCells *sizePixels);
		gContext = canvas.getGraphicsContext2D();
		startPause = new Button("Pause");
		playersLives = new Label[2];
		playersLives[0] = new Label();
		playersLives[1] = new Label();
		rightMenuWidth = 150;

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);

		cellDefaultSize = Math.max(MapCell.getUnitSize(), cellPrecisionUnitSize);

		pause = false;
		cells = new Cell[rowCells * rowCells];
		positions = new int[rowCells];

		trees = new ArrayList<>();
		tanks = new ArrayList<>();
		bullets = new LinkedList<>();
	}

	public int getDefaultCellSize(){
		return cellDefaultSize;
	}

	public int getRowCells(){
		return rowCells;
	}
	public int getSizePixels(){
		return sizePixels;
	}

	public int getFramesPerSecond(){
		return framesPerSecond;
	}

	public Button getStartPauseButton(){
		return startPause;
	}

	public void setFramesPerSeconds(int timeFrameInMilliseconds){
		framesPerSecond = 1000/timeFrameInMilliseconds;
		if(framesPerSecond < 2)
			framesPerSecond = 2;
	}

	public void setColsRows(int newCols, int newRows){
		if(newCols > 1)
			colCells = newCols;
		if(newRows > 1)
			rowCells = newRows;
	}

	public void pauseDrawing(){
		pause = true;
		startPause.setText("Play");
	}
	public void keepDrawing(){
		pause = false;
		startPause.setText("Pause");
	}

	public void modifyCellSize(int stageWidth, int stageHeight){
		int widthSizePixels = (stageWidth - rightMenuWidth)/colCells;
		sizePixels = (stageHeight - MapCell.getUnitSize() - 1)/rowCells;
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


	private void exampleCells(){
		cells[0].setMapCell(MapCell.EAGLE);

		Cell cell = cells[0].getDownCell();
		cell = cell.getDownCell();
		cell.setMapCell(MapCell.TANK_2_LVL_3_STATE_1_RIGHT);
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


	private void setRightMenu(GridPane ui){
		ui.setPadding(new Insets(10));

		ui.add(startPause, 0, 1);
		ui.setHgap(10);
		ui.add(playersLives[0], 0, 3);
		ui.setHgap(10);
		ui.add(playersLives[1], 0, 50);
	}

	public Scene drawStart(){
		GridPane gridPane = new GridPane();
		BorderPane borderP = new BorderPane();

		gridPane.setPrefWidth(rightMenuWidth);
		setRightMenu(gridPane);

		borderP.setLeft(canvas);
		borderP.setRight(gridPane);

		//StackPane layout = new StackPane();
		Scene scene = new Scene(borderP);

		return scene;
	}

	public void typeText(String text){
		gContext.setFill(Color.AZURE);
		double width = canvas.getWidth(), charWidth = width/(text.length() + 2);
		Font font = new Font("", 120);

		gContext.setFont(font);
		gContext.fillText(text, charWidth, canvas.getHeight()/2, width - charWidth*2);

		pause = true;
	}

	public void drawMap(GameDynamics dynamics){
		if(pause)
			return;

		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		int lifes = dynamics.get1stPlayerLifes();
		if(lifes > 0)
			playersLives[0].setText("Player 1: " + lifes + " lifes");

		lifes = dynamics.get2ndPlayerLifes();
		if(lifes > 0)
			playersLives[1].setText("Player 2: " + lifes + " lifes");

		dynamics.setCellSize(sizePixels);

		Cell cell;
		final double multiplier = ( (double)sizePixels)/unitSize;

		Iterator<Cell> iter = dynamics.iterator();
		while(iter.hasNext() ){
			cell = iter.next();
			//cell.roundPos(cellDefaultSize, sizePixels);

			cell.drawCell(gContext, tiles, multiplier);
		}

	}
}
