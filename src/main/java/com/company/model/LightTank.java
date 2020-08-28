package com.company.model;

import com.company.movement.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class LightTank extends Enemy {

	public LightTank(BattleRandom rand, GameView view){
		super(rand, view);
		points = 100;
		setIcons(false);
	}

	public LightTank(BattleRandom rand, GameView view, boolean powerUp){
		super(rand, view);
		points = 100;
		hasPowerUp = powerUp;
		setIcons(powerUp);
	}

	@Override
	protected void setIcons(boolean containsPowerUp){
		MapCell[] cells;
		Direction direction = Direction.DOWN;

		if(containsPowerUp)
			cells = MapCell.lightTankPowerUp(direction);
		else
			cells = MapCell.lightTank(direction);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction.getDirection(), cells);

		if(containsPowerUp){
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.lightTankPowerUp(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.lightTankPowerUp(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.lightTankPowerUp(direction) );
		} else {
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.lightTank(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.lightTank(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.lightTank(direction) );
		}
	}
}
