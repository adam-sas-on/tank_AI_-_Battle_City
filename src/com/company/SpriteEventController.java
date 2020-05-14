package com.company;

import javafx.scene.input.KeyCode;

public class SpriteEventController {
	// AIDriver tankAI;
	private final KeyCode moveUp, moveRight, moveDown, moveLeft;
	private final KeyCode singleShot, singlePanzerShot;
	private final KeyCode continuousShooting, continuousPanzerShooting;
	private int currentAngle, currentShotPower;
	private int turningAngle;
	private boolean keepShooting, singleShoot;
	private boolean isPlayer;
	private final int upAngle = 90;

	public SpriteEventController(KeyCode up, KeyCode right, KeyCode down, KeyCode left,
								KeyCode shot, KeyCode panzerShot,
								KeyCode shooting, KeyCode panzerShooting){
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

	public KeyCode getKeyCode(){
		return getKeyCode(turningAngle);
	}

	public KeyCode getKeyCode(int directionAngle){
		switch(directionAngle){
			case 0:
				return KeyCode.RIGHT;
			case upAngle:
				return KeyCode.UP;
			case 180:
				return KeyCode.LEFT;
			case 270:
				return KeyCode.DOWN;
			default:
				return null;
		}
	}

	public int directionForUp(){
		return upAngle;
	}

	public void setEvent(final KeyCode keyCode){
		if(!isPlayer)
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

	public void usePlayer(){
		isPlayer = true;
	}

	public void useAI(){
		isPlayer = false;
	}

	public int move(){
		if(!isPlayer) {
			// todo: make here computer to drive the tank;
			return -1;
		}
		return currentAngle;
	}

	public int takeTheShootPower(){
		if(!isPlayer) {
			// todo: make here computer to take the shoot;
			return 0;
		}

		int shootPower = currentShotPower;
		if(!keepShooting)
			currentShotPower = 0;
		return shootPower;
	}

}
