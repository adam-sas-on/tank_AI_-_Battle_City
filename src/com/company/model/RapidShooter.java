package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class RapidShooter extends Enemy {
	public RapidShooter(BattleRandom rand, GameView view){
		super(rand, view);
		int msInterval = view.getIntervalInMilliseconds();
		bulletSpeed *= 2;
		nextBulletSteps = 500/msInterval;
		setIcons(false);
	}

	public RapidShooter(BattleRandom rand, GameView view, boolean powerApp){
		super(rand, view);
		int msInterval = view.getIntervalInMilliseconds();
		bulletSpeed *= 2;
		nextBulletSteps = 500/msInterval;
		setIcons(powerApp);
	}

	@Override
	protected void setIcons(boolean containsPowerUp){
		MapCell[] cells;
		Direction direction = Direction.DOWN;

		if(containsPowerUp)
			cells = MapCell.rapidShooterTankPowerUp(direction);
		else
			cells = MapCell.rapidShooterTank(direction);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction.getDirection(), cells);

		if(containsPowerUp){
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.rapidShooterTankPowerUp(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.rapidShooterTankPowerUp(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.rapidShooterTankPowerUp(direction) );
		} else {
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.rapidShooterTank(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.rapidShooterTank(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.rapidShooterTank(direction) );
		}
	}
}
