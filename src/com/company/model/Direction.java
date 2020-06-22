package com.company.model;

import javafx.scene.input.KeyCode;

public enum Direction {
	UP(90),
	RIGHT(0),
	DOWN(270),
	LEFT(180),
	IN_PLACE(-1);

	private int degAngleOrm1;
	private double radAngleOrm1;
	private KeyCode keyCode;

	Direction(int degAngleOrm1){
		if(degAngleOrm1 >= 315 || (degAngleOrm1 >= 0 && degAngleOrm1 < 45) ){
			this.degAngleOrm1 = 0;
			radAngleOrm1 = 0.0;
			keyCode = KeyCode.RIGHT;
		} else if(degAngleOrm1 >= 45 && degAngleOrm1 < 135){
			this.degAngleOrm1 = 90;
			radAngleOrm1 = Math.PI/2.0;
			keyCode = KeyCode.UP;
		} else if(degAngleOrm1 >= 135 && degAngleOrm1 < 225){
			this.degAngleOrm1 = 180;
			radAngleOrm1 = Math.PI;
			keyCode = KeyCode.LEFT;
		} else if(degAngleOrm1 >= 225 /*&& degAngleOrm1 < 315*/){
			this.degAngleOrm1 = 270;
			radAngleOrm1 = 3.0*Math.PI/2.0;
			keyCode = KeyCode.DOWN;
		} else {
			this.degAngleOrm1 = -1;
			radAngleOrm1 = -1.0;
			keyCode = null;
		}
	}

	public int getDirection(){
		return degAngleOrm1;
	}
}