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

	@Override
	public Bullet fireBullet(int msInterval, int cellSize, DamageClass damages){
		int col = (int)Math.round(x_pos), row = (int)Math.round(y_pos);
		Bullet bullet = new Bullet(msInterval, cellSize, currentDirection, col, row, damages);
		bullet.assignToPlayer();
		if(level > 1)
			bullet.setDoubleSpeed();
		bullet.setDestructivePower(level);

		return bullet;
	}
}
