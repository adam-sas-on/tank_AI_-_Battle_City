package com.company.view;

import com.company.GameDynamics;
import com.company.model.Bullet;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
	private Button mapSelectButton;
	private Label[] playersLives, playersPoints;
	private ListView<String> mapList;
	private int rowCells = 26, colCells = 26;
	private int sizePixels;
	private int rightMenuWidth;
	private int framesPerSecond, timeFrameInMilliseconds;
	private boolean pause;
	private final int unitSize = MapCell.getUnitSize();// any icon because unit size is the same for all;
	private final int cellDefaultSize;

	private Cell powerUps;
	private List<Cell> trees;
	private int treesCount;
	private List<Cell> explodes;
	private int explodesCount;

	public GameView(int cellPrecisionUnitSize){
		sizePixels = unitSize;
		framesPerSecond = 2;
		timeFrameInMilliseconds = 500;

		canvas = new Canvas(colCells *sizePixels, rowCells *sizePixels);
		gContext = canvas.getGraphicsContext2D();
		setRightMenu();
		rightMenuWidth = 150;

		InputStream is = Cell.class.getResourceAsStream("/battle_city_tiles.png");
		tiles = new Image(is);

		cellDefaultSize = Math.max(MapCell.getUnitSize(), cellPrecisionUnitSize);

		pause = false;

		powerUps = new Cell();
		trees = new ArrayList<>();
		explodes = new ArrayList<>();
		treesCount = explodesCount = 0;
	}

	private void setRightMenu(){
		startPause = new Button("Pause");
		playersLives = new Label[2];
		playersLives[0] = new Label();
		playersLives[1] = new Label();

		playersPoints = new Label[2];
		playersPoints[0] = new Label();
		playersPoints[1] = new Label();

		mapList = new ListView<>();
		mapSelectButton = new Button("Load map");
	}

	public int getDefaultCellSize(){
		return cellDefaultSize;
	}

	public int getRowCells(){
		return rowCells;
	}

	public int getFramesPerSecond(){
		return framesPerSecond;
	}

	public int getIntervalInMilliseconds(){
		return timeFrameInMilliseconds;
	}

	public Button getStartPauseButton(){
		return startPause;
	}

	public Button getLoadingMapButton(){
		return mapSelectButton;
	}

	public String getSelectedMap(){
		String map;
		map = mapList.getSelectionModel().getSelectedItem();
		return map;
	}

	public void addMaps(List<String> maps){
		if(maps.size() > 0){
			mapList.getItems().clear();
			mapList.getItems().addAll(maps);
		}
	}

	public void selectMap(String map){
		if(mapList.getItems().size() == 0)
			return;

		int mapIndex = mapList.getItems().indexOf(map);
		if(mapIndex >= 0)
			mapList.getSelectionModel().clearAndSelect(mapIndex);
	}

	public void selectNextMap(){
		int selectedIndex = mapList.getSelectionModel().getSelectedIndex();
		if(selectedIndex + 1 == mapList.getItems().size() )
			mapList.getSelectionModel().selectFirst();
		else
			mapList.getSelectionModel().selectNext();
	}

	public void setFramesPerSeconds(int timeFrameInMilliseconds){
		this.timeFrameInMilliseconds = timeFrameInMilliseconds;
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

	private boolean addExplode(Cell explodeCell){
		MapCell mapCell = explodeCell.getMapCell();
		if(mapCell != MapCell.EXPLODE_1 && mapCell != MapCell.EXPLODE_2 && mapCell != MapCell.EXPLODE_3 &&
				mapCell != MapCell.EXPLODE_4 && mapCell != MapCell.EXPLODE_5)
			return false;

		int count = explodes.size();

		if(explodesCount >= count){
			Cell cell = new Cell();
			cell.setByOtherCell(explodeCell);
			explodes.add(cell);
		} else
			explodes.get(explodesCount).setByOtherCell(explodeCell);

		explodesCount++;
		return true;
	}

	public void addTree(Cell treeCell){
		int count = trees.size();

		if(treesCount >= count){
			Cell cell = new Cell();
			cell.setByOtherCell(treeCell);
			trees.add(cell);
		} else
			trees.get(treesCount).setByOtherCell(treeCell);

		treesCount++;
	}

	public void clearTrees(){
		treesCount = 0;
	}

	private void setRightMenu(GridPane ui){
		ui.setPadding(new Insets(10));

		ui.add(startPause, 0, 1);
		ui.setVgap(8);

		//VBox box = new VBox(10);
		//box.getChildren().addAll(playersLives[0], playersPoints[0], playersLives[1], playersPoints[1]);
		ui.add(playersLives[0], 0, 3);
		ui.setHgap(2);
		ui.add(playersPoints[0], 0, 5);
		ui.setHgap(8);
		ui.add(playersLives[1], 0, 7);
		ui.setHgap(2);
		ui.add(playersPoints[1], 0, 9);

		ui.setHgap(8);
		mapList.setPrefHeight( 8*MapCell.getUnitSize() );
		ui.add(mapList, 0, 11);
		ui.setHgap(2);
		ui.add(mapSelectButton, 0, 13);
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

	private void printPlayersProperties(GameDynamics dynamics){
		int lifes = dynamics.get1stPlayerLifes(), points = dynamics.get1stPlayerPoints();
		if(lifes > 0)
			playersLives[0].setText("Player 1: " + lifes + " lifes");
		else
			playersLives[0].setText("Player 1:  dead!");

		playersPoints[0].setText("          " + points + " pts");

		lifes = dynamics.get2ndPlayerLifes();
		points = dynamics.get2ndPlayerPoints();
		if(lifes > 0)
			playersLives[1].setText("Player 2: " + lifes + " lifes");
		else
			playersLives[1].setText("Player 2:  dead!");
		playersPoints[1].setText("          " + points + " pts");
	}

	public void drawMap(GameDynamics dynamics){
		if(pause)
			return;

		gContext.setFill(Color.BLACK);
		gContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		printPlayersProperties(dynamics);

		dynamics.setCellSize(sizePixels);
		dynamics.setFromCollectible(powerUps);
		powerUps.roundPos(cellDefaultSize, sizePixels);

		Cell cell;
		final double multiplier = ( (double)sizePixels)/unitSize;

		explodesCount = dynamics.getExplodes(explodes);

		Iterator<Cell> iter = dynamics.iterator();
		while(iter.hasNext() ){
			cell = iter.next();
			//cell.roundPos(cellDefaultSize, sizePixels);
			if(cell.getMapCell() == MapCell.FOREST)
				continue;

			cell.drawCell(gContext, tiles, multiplier);
		}

		cell = new Cell();
		int i = 0;
		for(; i < treesCount; i++){
			cell.setByOtherCell(trees.get(i) );
			cell.roundPos(cellDefaultSize, sizePixels);
			cell.drawCell(gContext, tiles, multiplier);
		}

		for(i = 0; i < explodesCount; i++){
			cell = explodes.get(i);
			cell.roundPos(cellDefaultSize, sizePixels);
			cell.drawCell(gContext, tiles, multiplier);
		}

		powerUps.drawCell(gContext, tiles, multiplier);
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

}
