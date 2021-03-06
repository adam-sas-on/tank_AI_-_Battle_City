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

	public void setDamages(Map<MapCell,MapCell> rightSideDestruction, Map<MapCell,MapCell> leftSideDestruction, Direction direction){
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

	public void setFullDamages(Map<MapCell,MapCell> rightSideDestruction, Map<MapCell,MapCell> leftSideDestruction, Direction direction){
		putNewMapCells(rightSideDestruction);
		putNewMapCells(leftSideDestruction);

		switch(direction){
			case UP:
				verticalRightFullDamagesSet(rightSideDestruction);
				verticalLeftFullDamagesSet(leftSideDestruction);
				break;
			case RIGHT:
				horizontalUpFullDamagesSet(leftSideDestruction);
				horizontalDownFullDamagesSet(rightSideDestruction);
				break;
			case LEFT:
				horizontalUpFullDamagesSet(rightSideDestruction);
				horizontalDownFullDamagesSet(leftSideDestruction);
				break;
			default:
				verticalRightFullDamagesSet(leftSideDestruction);
				verticalLeftFullDamagesSet(rightSideDestruction);
		}
		rightSideDestruction.put(MapCell.EAGLE, MapCell.EAGLE_DESTROYED);
		leftSideDestruction.put(MapCell.EAGLE, MapCell.EAGLE_DESTROYED);
	}

	private void upRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
		rightSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		rightSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_I_UP);
		rightSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_I_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_UP_RIGHT);
		rightSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_UP_LEFT);
		rightSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_UP_LEFT);
		verticalRightMapSet(rightSideDestruction);
	}

	private void upLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
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

	private void rightRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
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

	private void rightLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
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

	private void leftRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
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

	private void leftLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
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

	private void downRightDamagesSet(Map<MapCell,MapCell> rightSideDestruction){
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

	private void downLeftDamageSet(Map<MapCell,MapCell> leftSideDestruction){
		leftSideDestruction.put(MapCell.STEEL, MapCell.STEEL);
		leftSideDestruction.put(MapCell.BRICK, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_L_UP_LEFT, MapCell.BRICK_DOWN_LEFT);
		leftSideDestruction.put(MapCell.BRICK_L_UP_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, MapCell.BRICK_I_RIGHT);
		leftSideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, MapCell.BRICK_I_DOWN);
		leftSideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_DOWN_LEFT);
		verticalRightMapSet(leftSideDestruction);
	}

	private void verticalRightMapSet(Map<MapCell,MapCell> sideDestruction){
		sideDestruction.put(MapCell.BRICK_I_DOWN, null);
		sideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_I_RIGHT);
		sideDestruction.put(MapCell.BRICK_I_UP, null);
		sideDestruction.put(MapCell.BRICK_UP_LEFT, null);
		sideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		sideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
		sideDestruction.put(MapCell.BRICK_DOWN_LEFT, null);
	}
	/*private void verticalLeftMapSet(Map<MapCell,MapCell> sideDestruction){}

	 private void horizontalUpMapSet(Map<MapCell,MapCell> sideDestruction){}
	 private void horizontalDownMapSet(Map<MapCell,MapCell> sideDestruction){}*/

	// - - - Same us before but for powerful bullet
	private void verticalRightFullDamagesSet(Map<MapCell,MapCell> sideDestruction){
		//fullLsDamages(rightSideDestruction);
		sideDestruction.put(MapCell.BRICK_I_RIGHT, MapCell.BRICK_I_RIGHT);
		sideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
		sideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
	}

	private void verticalLeftFullDamagesSet(Map<MapCell,MapCell> sideDestruction){
		//fullLsDamages(sideDestruction);
		sideDestruction.put(MapCell.BRICK_I_LEFT, MapCell.BRICK_I_LEFT);
		sideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		sideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
	}

	private void horizontalUpFullDamagesSet(Map<MapCell,MapCell> sideDestruction){
		//fullLsDamages(sideDestruction);
		sideDestruction.put(MapCell.BRICK_I_UP, MapCell.BRICK_I_UP);
		sideDestruction.put(MapCell.BRICK_UP_LEFT, MapCell.BRICK_UP_LEFT);
		sideDestruction.put(MapCell.BRICK_UP_RIGHT, MapCell.BRICK_UP_RIGHT);
	}

	private void horizontalDownFullDamagesSet(Map<MapCell,MapCell> sideDestruction){
		//fullLsDamages(sideDestruction);
		sideDestruction.put(MapCell.BRICK_I_DOWN, MapCell.BRICK_I_DOWN);
		sideDestruction.put(MapCell.BRICK_DOWN_LEFT, MapCell.BRICK_DOWN_LEFT);
		sideDestruction.put(MapCell.BRICK_DOWN_RIGHT, MapCell.BRICK_DOWN_RIGHT);
	}

	/*private void fullLsDamages(Map<MapCell,MapCell> sideDestruction){
		sideDestruction.put(MapCell.STEEL, null);
		sideDestruction.put(MapCell.BRICK, null);
		sideDestruction.put(MapCell.BRICK_L_UP_LEFT, null);
		sideDestruction.put(MapCell.BRICK_L_UP_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_L_DOWN_RIGHT, null);
		sideDestruction.put(MapCell.BRICK_L_DOWN_LEFT, null);
	}*/
}
