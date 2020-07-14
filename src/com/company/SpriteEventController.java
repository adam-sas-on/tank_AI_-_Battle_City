package com.company;

import com.company.logic.TankAI;
import com.company.model.Direction;
import javafx.scene.input.KeyCode;

public class SpriteEventController {
	private TankAI AIDriver;
	private boolean readyAI;
	private boolean aiEvenMove;
	private int mapMaxColumns;
	private int actionPoints;
	private final KeyCode moveUp, moveRight, moveDown, moveLeft;
	private final KeyCode singleShot, singlePanzerShot;
	private final KeyCode continuousShooting, continuousPanzerShooting;
	private int currentAngle, currentShotPower;
	private int turningAngle;
	private boolean keepShooting, singleShoot;
	private boolean isPlayer, freezed;
	private final int upAngle = 90;

	public SpriteEventController(KeyCode up, KeyCode right, KeyCode down, KeyCode left,
								KeyCode shot, KeyCode panzerShot,
								KeyCode shooting, KeyCode panzerShooting){
		AIDriver = null;
		readyAI = false;
		actionPoints = 0;
		aiEvenMove = true;
		mapMaxColumns = 26;// default in BC;

		moveUp = up;
		moveRight = right;
		moveDown = down;
		moveLeft = left;
		singleShot = shot;
		singlePanzerShot = panzerShot;
		continuousShooting = shooting;
		continuousPanzerShooting = panzerShooting;
		currentAngle = -1;
		turningAngle = upAngle;
		currentShotPower = 0;
		isPlayer = true;
		keepShooting = false;
		freezed = false;
	}


	private void aiRequest(){
		aiEvenMove = !aiEvenMove;
		if(aiEvenMove)
			return;

		double[] order = AIDriver.getOutput();
		if(order == null)
			return;

		currentShotPower = (int) order[1];
		currentAngle = -1;
		if(order[0] > 0.0){
			currentAngle = Direction.stepVectorToDegrees(Math.cos(order[0]), Math.sin(order[0]));
		}
	}

	/**
	 * Integer representation of direction keyCodes;
	 * @param keyCode: key code for direction;
	 * @return integer of angles from x axis (like in polar plot);
	 */
	public int moveKeyValue(final KeyCode keyCode){
		if(keyCode == moveRight)
			return 0;
		if(keyCode == moveUp)
			return upAngle;
		if(keyCode == moveLeft)
			return 180;
		if(keyCode == moveDown)
			return 270;

		throw new IllegalArgumentException("Key angles does not correspond to values of angle!");
	}

	public Direction getDirection(){
		return Direction.directionByAngle(turningAngle);
	}

	public Direction getDirection(int directionAngle){
		return Direction.directionByAngle(directionAngle);
	}

	/*public boolean isAIactive(){
		return !isPlayer && readyAI;
	}*/

	public void blockUnblockController(boolean freeze){
		freezed = freeze;
	}

	public void setEvent(final KeyCode keyCode){
		if(!isPlayer || freezed)
			return;

		if(keyCode == moveRight)
			currentAngle = turningAngle = 0;
		else if(keyCode == moveUp)
			currentAngle = turningAngle = upAngle;
		else if(keyCode == moveLeft)
			currentAngle = turningAngle = 180;
		else if(keyCode == moveDown)
			currentAngle = turningAngle = 270;
		else if(keyCode == singleShot && !singleShoot){
			currentShotPower = 1;
			keepShooting = false;
			singleShoot = true;
		} else if(keyCode == singlePanzerShot && !singleShoot){
			currentShotPower = 2;
			keepShooting = false;
			singleShoot = true;
		} else if(keyCode == continuousShooting) {
			currentShotPower = 1;
			keepShooting = true;
		} else if(keyCode == continuousPanzerShooting) {
			currentShotPower = 2;
			keepShooting = true;
		}
	}

	public void stopEvent(final KeyCode keyCode){
		if(!isPlayer)
			return;

		if(keyCode == moveRight || keyCode == moveUp || keyCode == moveLeft || keyCode == moveDown)
			currentAngle = -1;
		else if(keyCode == continuousShooting || keyCode == continuousPanzerShooting){
			currentShotPower = 0;
			keepShooting = false;
		} else if(keyCode == singleShot || keyCode == singlePanzerShot)
			singleShoot = false;
	}

	public void setAI(TankAI tankAI){
		AIDriver = tankAI;
		readyAI = false;
		if(AIDriver != null){
			readyAI = AIDriver.readFile();
			if( !readyAI )
				AIDriver.setDefaultNeuralNetwork();

			readyAI = AIDriver.isAIReady();
		}
	}

	public void upDateActionPoints(int newActionPoints){
		actionPoints = newActionPoints;
	}

	public void usePlayer(){
		isPlayer = true;
	}

	public void useAI(){
		isPlayer = false;
	}

	/**
	 * Set
	 * @param maxCols
	 */
	public void setMaxColsOfMap(int maxCols){
		if(maxCols > 1)
			mapMaxColumns = maxCols;
	}

	public int move(){
		if(!isPlayer) {
			if(!readyAI)
				return -1;

			aiRequest();
			// todo: make here computer to drive the tank;
			return -1;
		}
		return currentAngle;
	}

	public int takeTheShootPower(){
		if(!isPlayer) {
			if(!readyAI)
				return 0;
			// todo: make here computer to take the shoot;
			return currentShotPower;
		}

		int shootPower = currentShotPower;
		if(!keepShooting)
			currentShotPower = 0;
		return shootPower;
	}

}
