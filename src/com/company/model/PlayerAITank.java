package com.company.model;

public class PlayerAITank extends Tank {

	public PlayerAITank(int msInterval, int cellSize) {
		super(msInterval, cellSize);

		setPos(12, 4, cellSize);
	}

	public void setPosOnPlayer1(int cellSize){
		setPos(12, 4, cellSize);
	}

	public void setPosOnPlayer2(int cellSize){
		setPos(12, 8, cellSize);
	}

}
