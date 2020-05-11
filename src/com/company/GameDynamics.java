package com.company;

import com.company.model.Bullet;
import com.company.model.DamageClass;
import com.company.model.PlayerAITank;
import com.company.model.Enemy;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameDynamics implements Iterable<Cell> {
	private PlayerAITank player1;
	private PlayerAITank player2;
	private List<Enemy> tanks;
	private Bullet[] bullets;
	private int bulletsCount;
	private final double bulletsCountMultiplier = 1.4;
	private DamageClass damages;

	private Cell[] cells;
	private int rowCells;
	private int colCells;
	private final int maxCols;
	private int cellUnitSize;
	private double[] xyPos = new double[2];
	private List<Integer> treesIds;
	private Cell iterCell;


	public GameDynamics(int maxCols, int maxRows){
		rowCells = colCells = 26;// default Battle City map size;
		this.maxCols = maxCols;

		tanks = new LinkedList<>();
		treesIds = new ArrayList<>();

		bulletsCount = 0;
		bullets = new Bullet[10];// any beginning size;

		setCellsStructure(maxCols, maxRows);
		iterCell = new Cell();

		damages = DamageClass.getInstance();
	}

	private void setUpperRowCells(int maxCols){
		cells[0].linkNeighborCells(null, cells[1], cells[maxCols], null);
		cells[0].setPos(0, 0);

		int i, rowLimit = maxCols - 1;
		for(i = 1; i < rowLimit; i++){
			cells[i].linkNeighborCells(null, cells[i+1], cells[i+maxCols], cells[i-1]);
			cells[i].setPos(i, 0);
		}

		cells[i].linkNeighborCells(null, null, cells[i+maxCols], cells[i-1]);
		cells[i].setPos(i, 0);
	}

	private void setCellsStructure(int maxCols, int maxRows){
		int i, cellsCount = maxCols*maxRows;

		cells = new Cell[cellsCount];
		for (i = 0; i < cellsCount; i++){
			cells[i] = new Cell();
			cells[i].setIndexId(i);
		}

		setUpperRowCells(maxCols);

		int rowIndex, colIndex;
		final int rowLimit = maxRows - 1, colLimit = maxCols - 1;

		i = maxCols;
		for(rowIndex = 1; rowIndex < rowLimit; rowIndex++, i++){// loop through rows;
			cells[i].linkNeighborCells(cells[i - maxCols],  cells[i + 1],  cells[i + maxCols], null);
			cells[i].setPos(0, rowIndex);

			for(colIndex = 1, i++; colIndex < colLimit; colIndex++, i++){// loop through cols;
				cells[i].linkNeighborCells(cells[i - maxCols],  cells[i + 1],  cells[i + maxCols],  cells[i - 1]);
				cells[i].setPos(colIndex, rowIndex);
			}

			cells[i].linkNeighborCells(cells[i - maxCols],  null,  cells[i + maxCols],  cells[i - 1]);
			cells[i].setPos(colIndex, rowIndex);
		}

		// last row;
		cells[i].linkNeighborCells(cells[i - maxCols],  cells[i + 1],  null, null);
		cells[i].setPos(0, rowIndex);

		for(colIndex = 1, i++; colIndex < colLimit; colIndex++, i++){// connect lowest row;
			cells[i].linkNeighborCells(cells[i - maxCols],  cells[i + 1], null, cells[i - 1]);
			cells[i].setPos(colIndex, rowIndex);
		}

		cells[i].linkNeighborCells(cells[i - maxCols], null, null, cells[i - 1]);
		cells[i].setPos(colIndex, rowIndex);
	}


	private boolean isPosAccessible(double newCol, double newRow){
		int col = (int) Math.floor(newCol), row = (int) Math.floor(newRow);
		if(col < 0 || row < 0)
			return false;

		int cellIndex = row*maxCols + col;
		boolean accessible = cells[cellIndex].isAccessible();
		if(!accessible)
			return false;

		double dx = newCol - col, dy = newRow - row;
		// dy > dx: vertical movement;
		// dy < dx: horizontal movement;
		if(dy > dx){
			accessible = cells[cellIndex + 1].isAccessible();
			if(!accessible)
				return false;

			row += 2;
			if(row >= rowCells)
				return false;

			cellIndex = row*maxCols + col;
			accessible = cells[cellIndex].isAccessible();
			return accessible && cells[cellIndex + 1].isAccessible();
		} else {
			cellIndex = (row + 1)*maxCols + col;
			accessible = cells[cellIndex].isAccessible();
			if(!accessible)
				return false;

			col += 2;
			if(col >= colCells)
				return false;

			cellIndex += 2;
			int cellIndex2 = row*maxCols + col;
			return cells[cellIndex].isAccessible() && cells[cellIndex2].isAccessible();
		}
	}

	private void changeCellPositionToClosest(Cell cell, double x, double y){
		int col = (int) Math.round(x), row = (int) Math.round(y);
		cell.setPos(col, row);
	}

	public void loadMap(String mapFileName, MapLoader mapLoader){
		if(player1 == null || player2 == null)
			throw new NullPointerException("Can not load map: players are not set");

		mapLoader.loadMap(cells[0], mapFileName, player1, player2, treesIds);
	}

	public void setFirstPlayer(PlayerAITank player){
		player1 = player;
	}
	public void setSecondPlayer(PlayerAITank player){
		player2 = player;
	}

	public void addBullet(Bullet bullet){
		if(bulletsCount + 1 > bullets.length){
			int newLength = (int)(bullets.length*bulletsCountMultiplier);
			Bullet[] newBullets = new Bullet[newLength];
			System.arraycopy(bullets,0, newBullets, 0, bullets.length);
			bullets = newBullets;
		}
		bullets[bulletsCount] = bullet;
		bulletsCount++;
	}
	private void removeBullet(int index){
		if(index >= bulletsCount)
			return;

		int i = index + 1;
		while(i < bulletsCount){
			bullets[i - 1] = bullets[i];
			i++;
		}
		bulletsCount--;
	}


	public void nextStep(GameView view){
		Bullet bullet;
		player1.move(view);
		bullet = player1.fireBullet(damages);
		if(bullet != null)
			addBullet(bullet);

		player2.move(view);
		bullet = player2.fireBullet(damages);
		if(bullet != null)
			addBullet(bullet);
	}


	public void setCellSize(int cellSize){
		cellUnitSize = 16;
		if(cellSize > 0)
			cellUnitSize = cellSize;
	}

	@Override
	public Iterator<Cell> iterator(){
		Iterator<Cell> iter = new Iterator<>() {
			private boolean iterateEnvironment = true, tanksIterated = false, bulletsIterated = false;
			private boolean player1NotIterated = true, player2NotIterated = player2 != null;
			private int iterateIndex = 0;
			private Iterator<Enemy> tankIter = tanks.iterator();
			private final int treesCount = treesIds.size();

			@Override
			public boolean hasNext(){
				return iterateEnvironment || !tanksIterated || !bulletsIterated || iterateIndex < treesCount;
			}

			@Override
			public Cell next(){
				Cell cell;
				if(iterateEnvironment){
					int index = iterateIndex/colCells*(maxCols - colCells) + iterateIndex;// index + remaining cols;
					cell = cells[index];
					iterCell.setPos(cell.getCol()*cellUnitSize, cell.getRow()*cellUnitSize);
					iterCell.setMapCell(cell.getMapCell());

					iterateIndex++;
					if(iterateIndex >= rowCells*colCells){
						iterateIndex = 0;
						iterateEnvironment = false;
					}
				} else if(!tanksIterated){
					if(player1NotIterated){
						player1.setUpCell(iterCell, cellUnitSize);
						player1NotIterated = false;
					} else if(player2NotIterated){
						player1.setUpCell(iterCell, cellUnitSize);
						player2NotIterated = false;
					} else if(tankIter.hasNext() ){
						cell = tankIter.next().getCell();
					} else {
						tanksIterated = true;
					}
				} else if(!bulletsIterated){
					bullets[iterateIndex++].setUpCell(iterCell, cellUnitSize);
					if(iterateIndex >= bulletsCount){
						bulletsIterated = true;
						iterateIndex = 0;
					}
				} else {
					int treeInd = treesIds.get(iterateIndex);
					iterateIndex++;
					cell = cells[treeInd];// be sure this is properly implemented;
					iterCell.setPos(cell.getCol()*cellUnitSize, cell.getRow()*cellUnitSize);
				}
				return iterCell;
			}

			@Override
			public void remove(){

			}
		};
		return iter;
	}
}
