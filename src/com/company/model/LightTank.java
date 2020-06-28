package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class LightTank extends Enemy {

	public LightTank(BattleRandom rand, GameView view){
		super(rand, view);
		setIcons(false);
	}

	public LightTank(BattleRandom rand, GameView view, boolean powerApp){
		super(rand, view, powerApp);
		setIcons(powerApp);
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
