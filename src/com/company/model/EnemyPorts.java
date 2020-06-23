package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;

public class EnemyPorts {
	private Cell[] portCells;
	private int[] currentIconIndices, waitingSteps;
	private boolean[] portsCollide;
	private int portsCount, activePortsCounter;
	private MapCell[] icons;
	private int currentPort, iterIndex;
	private int currentStepsForNewTank;
	//private int currentAmountOfTanks;
	private final double countMultiplier = 1.4;
	private final int minimumStepsForNewTank;// game-steps after which new tank can appear;
	private final int buildingSteps, minimumWaitingSteps;

	public EnemyPorts(int stepsPerSecond){
		int sizeBegin = 10;
		portCells = new Cell[sizeBegin];
		currentIconIndices = new int[sizeBegin];
		waitingSteps = new int[sizeBegin];
		portsCollide = new boolean[sizeBegin];
		resetPorts(sizeBegin);
		portsCount = activePortsCounter = 0;
		currentPort = 0;
		iterIndex = 0;

		icons = new MapCell[]{MapCell.CREATE_1, MapCell.CREATE_1, MapCell.CREATE_2, MapCell.CREATE_2,
				MapCell.CREATE_3, MapCell.CREATE_3, MapCell.CREATE_4, MapCell.CREATE_4,
				MapCell.CREATE_5, MapCell.CREATE_5, MapCell.CREATE_6, MapCell.CREATE_6};

		minimumStepsForNewTank = (stepsPerSecond*3)/2;// assumption that new tank appears after minimum 1.5s;
		currentStepsForNewTank = (stepsPerSecond*7)/2;// according to original game it was ~3.5 s;
		buildingSteps = (stepsPerSecond*3)/2;
		minimumWaitingSteps = Math.max(stepsPerSecond/ 25, 1);
	}


	private void reAllocPorts(){
		int newLength = (int)(portCells.length* countMultiplier);
		Cell[] newPortCells = new Cell[newLength];
		System.arraycopy(portCells,0, newPortCells, 0, portCells.length);
		portCells = newPortCells;

		int[] newInds = new int[newLength], newWaitingSteps = new int[newLength];
		System.arraycopy(currentIconIndices,0, newInds, 0, currentIconIndices.length);
		System.arraycopy(waitingSteps, 0, newWaitingSteps, 0, waitingSteps.length);
		currentIconIndices = newInds;
		waitingSteps = newWaitingSteps;
		boolean[] newCollides = new boolean[newLength];
		System.arraycopy(portsCollide, 0, newCollides, 0, portsCollide.length);
		portsCollide = newCollides;
	}

	private void resetPorts(int count){
		for(int i = 0; i < count; i++){
			currentIconIndices[i] = waitingSteps[i] = -1;
			portsCollide[i] = false;
		}
		activePortsCounter = 0;
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
		currentIconIndices[portsCount] = -1;
		portsCount++;
	}

	public void clear(){
		resetPorts(portsCount);
		portsCount = 0;
	}

	public int size(){
		return portsCount;
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

		if(currentIconIndices[iterIndex] >= 0){
			int ind = currentIconIndices[iterIndex];
			cell.setMapCell(icons[ind]);
			cell.setPos(portCells[iterIndex].getCol(), portCells[iterIndex].getRow());

			currentIconIndices[iterIndex] = (ind < icons.length - 1)?ind + 1:0;
		}
		iterIndex++;
		if(iterIndex >= portsCount)
			iterIndex = 0;
	}

	public void activatePort(){
		if(portsCount > 0 && waitingSteps[currentPort] < 0){

			portsCollide[currentPort] = false;
			waitingSteps[currentPort++] = (activePortsCounter > 0)?currentStepsForNewTank:minimumWaitingSteps;
			activePortsCounter++;
			if(currentPort == portsCount)
				currentPort = 0;
		}
	}

	public void nextStep(){
		if(portsCount < 1)
			return;

		int i = 0;
		for(; i < portsCount; i++){
			if(waitingSteps[i] < 0)
				continue;

			if(currentIconIndices[i] < 0){// currentIconIndices[i] < 0 && waitingSteps[i] > 0 -> waiting to start blinking;
				waitingSteps[i]--;
				if(waitingSteps[i] == 0) {
					currentIconIndices[i] = 0;
					waitingSteps[i] = buildingSteps;
				}
			} else {// currentIconIndices[i] >= 0 && waitingSteps[i] > 0 -> blinking and waiting to create tank (building);
				if( !portsCollide[i] )// if not collide with any tank and players bullets;
					waitingSteps[i]--;
				if(waitingSteps[i] == -1) {
					currentIconIndices[i] = -1;// add enemy tank into map;
					activePortsCounter--;
				}
			}
			portsCollide[i] = false;
		}
	}

	public boolean collide(Cell cell, final int unitSizeOfCells){
		if(cell == null || activePortsCounter > 0)
			return false;

		boolean collision = false;

		for(int i = 0; i < portsCount && !collision; i++){
			if(currentIconIndices[i] < 0)
				continue;

			collision = portCells[i].collide(cell, unitSizeOfCells);
		}
		return collision;
	}

	public boolean canMove(Cell spritePosition, Cell spriteRequestedPos, final int unitSizeOfCells){
		if(spritePosition == null || spriteRequestedPos == null)
			return true;// like: empty cell can collide;

		boolean moveAccepted = true;
		int currentArea, areaOfRequested;

		for(int i = 0; i < portsCount; i++){
			if(currentIconIndices[i] < 0)
				continue;

			areaOfRequested = portCells[i].collisionArea(spriteRequestedPos, unitSizeOfCells);
			currentArea = portCells[i].collisionArea(spritePosition, unitSizeOfCells);

			if(areaOfRequested > currentArea) {
				moveAccepted = false;
			}
			if(currentArea > 0)
				portsCollide[i] = true;
		}

		return moveAccepted;
	}

	public void blockBlinking(Cell bulletCell, final int unitSizeOfCells){
		if(bulletCell == null)
			return;// no bullets: nothing is blocking;

		int i;
		for(i = 0; i < portsCount; i++){
			if(currentIconIndices[i] < 0 || portsCollide[i])
				continue;

			portsCollide[i] = portCells[i].collide(bulletCell, unitSizeOfCells);
		}
	}

	public void createTank(){

	}

}
