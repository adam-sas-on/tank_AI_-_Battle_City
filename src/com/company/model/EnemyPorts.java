package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;

import java.util.Iterator;

public class EnemyPorts {
	private Cell[] portCells;
	private int[] currentIconInds;
	private int portsCount;
	private MapCell[] icons;
	private int portStepper, iterIndex;
	private int currentStepsForNewTank;
	private final double countMultiplier = 1.4;
	private final int minimumStepsForNewTank;// game-steps after which new tank can appear;

	public EnemyPorts(int stepsPerSecond){
		int sizeBegin = 10;
		portCells = new Cell[sizeBegin];
		currentIconInds = new int[sizeBegin];
		portsCount = 0;
		portStepper = 0;
		iterIndex = 0;

		icons = new MapCell[]{MapCell.CREATE_1, MapCell.CREATE_1, MapCell.CREATE_2, MapCell.CREATE_2,
				MapCell.CREATE_3, MapCell.CREATE_3, MapCell.CREATE_4, MapCell.CREATE_4,
				MapCell.CREATE_5, MapCell.CREATE_5, MapCell.CREATE_6, MapCell.CREATE_6};

		minimumStepsForNewTank = stepsPerSecond*2;// assumption that new tank appears after minimum 2s;
		currentStepsForNewTank = stepsPerSecond*10;
	}


	private void reAllocPorts(){
		int newLength = (int)(portCells.length* countMultiplier);
		Cell[] newPortCells = new Cell[newLength];
		System.arraycopy(portCells,0, newPortCells, 0, portCells.length);
		portCells = newPortCells;

		int[] newInds = new int[newLength];
		System.arraycopy(currentIconInds,0, newInds, 0, currentIconInds.length);
		currentIconInds = newInds;
	}

	public int size(){
		return portsCount;
	}

	public void add(int col, int row){
		if(portsCount == portCells.length)
			reAllocPorts();

		if(portCells[portsCount] == null){// set new Cell in portCells[portsCount] if null;
			Cell newCell = new Cell();
			newCell.setMapCell(icons[0]);
			newCell.setPos(col, row);
			portCells[portsCount] = newCell;
		} else {
			portCells[portsCount].setMapCell(icons[0]);
			portCells[portsCount].setPos(col, row);
		}
		currentIconInds[portsCount] = -1;
		portsCount++;
	}

	public void clear(){
		for(int i = 0; i < portsCount; i++){
			currentIconInds[i] = -1;
		}
		portsCount = 0;
	}

	public void levelUpPorts(){
		currentStepsForNewTank--;
		if(currentStepsForNewTank < minimumStepsForNewTank)
			currentStepsForNewTank = minimumStepsForNewTank;
	}

	public void setNextCell(Cell cell){
		cell.setMapCell(null);
		if(portsCount == 0)
			return;

		if(currentIconInds[iterIndex] >= 0){
			int ind = currentIconInds[iterIndex];
			cell.setMapCell(icons[ind]);
			cell.setPos(portCells[iterIndex].getCol(), portCells[iterIndex].getRow());

			currentIconInds[iterIndex] = (ind < icons.length - 1)?ind + 1:0;
		}
		iterIndex++;
		if(iterIndex >= portsCount)
			iterIndex = 0;
	}

	public void activatePort(){
		if(portsCount > 0){
			currentIconInds[portStepper++] = 0;
			if(portStepper == portsCount)
				portStepper = 0;
		}
	}

	public void createTank(){

	}

}
