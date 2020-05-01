package com.company.model;

import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class DamageClass {
	private static DamageClass instance = null;

	private DamageClass(){

	}

	public static DamageClass getInstance(){
		if(instance == null)
			instance = new DamageClass();
		return instance;
	}

	private void putNewMapCells(Map<MapCell,MapCell> sideDestruction){
		sideDestruction.put(MapCell.STEEL, null);
		sideDestruction.put(MapCell.BRICK, null);
		sideDestruction.put(MapCell.BRICK_L_UP_LEFT, null);
		sideDestruction.put(MapCell.BRICK_L_UP_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, null);
		sideDestruction.put(MapCell.BRICK_I_LEFT, null);
		sideDestruction.put(MapCell.BRICK_I_DOWN, null);
		sideDestruction.put(MapCell.BRICK_I_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_I_UP, null);
		sideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		sideDestruction.put(MapCell.BRICK_UP_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_DOWN_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}

	public void setDamages(Map<MapCell,MapCell> rightSideDestruction, Map<MapCell,MapCell> leftSideDestruction, KeyCode direction){
		putNewMapCells(rightSideDestruction);
		putNewMapCells(leftSideDestruction);

		switch(direction){
			case UP:
				upRightDamagesSet(rightSideDestruction);
				upLeftDamageSet(leftSideDestruction);
				break;
			case RIGHT:
				rightRightDamagesSet(rightSideDestruction);
				rightLeftDamageSet(leftSideDestruction);
				break;
			case LEFT:
				leftRightDamagesSet(rightSideDestruction);
				leftLeftDamageSet(leftSideDestruction);
				break;
			default:
				downRightDamagesSet(rightSideDestruction);
				downLeftDamageSet(leftSideDestruction);
		}
		rightSideDestruction.put(MapCell.EAGLE, MapCell.EAGLE_DESTROYED);
		leftSideDestruction.put(MapCell.EAGLE, MapCell.EAGLE_DESTROYED);
	}

	public void upRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
		rightSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		rightSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_I_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_DOWN, null);
		rightSideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_I_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_I_UP, null);
		rightSideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		rightSideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}

	public void upLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
		leftSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		leftSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_UP);
		leftSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_I_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_I_UP);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_UP_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_UP_LEFT);
		leftSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_I_LEFT);
		leftSideDestruction.put(MapCell.BRICK_I_DOWN, null);
		leftSideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_UP_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_I_UP, null);
		leftSideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		leftSideDestruction.put(MapCell.BRICK_UP_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
	}

	public void rightRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
		rightSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		rightSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_I_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_I_DOWN);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_DOWN_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_I_LEFT, null);
		rightSideDestruction.put(MapCell.BRICK_I_DOWN, MapCell.BRICK_I_DOWN);
		rightSideDestruction.put(MapCell.BRICK_I_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_I_UP, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		rightSideDestruction.put(MapCell.BRICK_UP_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
	}

	public void rightLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
		leftSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		leftSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_UP_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_I_UP);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_I_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_I_LEFT, null);
		leftSideDestruction.put(MapCell.BRICK_I_DOWN, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_I_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_I_UP, MapCell.BRICK_I_UP);
		leftSideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		leftSideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}

	public void leftRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
		rightSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		rightSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_LEFT);
		rightSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_DOWN_LEFT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_I_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_LEFT, null);
		rightSideDestruction.put(MapCell.BRICK_I_DOWN, MapCell.BRICK_DOWN_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_I_UP, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}

	public void leftLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
		leftSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		leftSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_I_UP);
		leftSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_UP_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_DOWN_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_I_LEFT, null);
		leftSideDestruction.put(MapCell.BRICK_I_DOWN, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_I_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_I_UP, MapCell.BRICK_UP_LEFT);
		leftSideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		leftSideDestruction.put(MapCell.BRICK_UP_RIGHT, null);
		leftSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
	}

	public void downRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
		rightSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		rightSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_DOWN);
		rightSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_DOWN_LEFT);
		rightSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_I_DOWN);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_I_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_I_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_DOWN, null);
		rightSideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_I_UP, null);
		rightSideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_UP_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, null);
		rightSideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
	}

	public void downLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
		leftSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		leftSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_DOWN_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_I_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_DOWN_LEFT);
		leftSideDestruction.put(MapCell.BRICK_I_DOWN, null);
		leftSideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_I_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_I_UP, null);
		leftSideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		leftSideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}

	/*private void verticalRightMapSet(Map<MapCell,MapCell> sideDestruction){}
	 private void verticalLeftMapSet(Map<MapCell,MapCell> sideDestruction){}

	 private void horizontalUpMapSet(Map<MapCell,MapCell> sideDestruction){}
	 private void horizontalDownMapSet(Map<MapCell,MapCell> sideDestruction){}*/

	public void upgradeDamages(HashMap<MapCell,MapCell> rightSideDestruction, HashMap<MapCell,MapCell> leftSideDestruction){

	}
}
