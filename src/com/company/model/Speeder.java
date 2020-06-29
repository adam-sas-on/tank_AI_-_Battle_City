package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class Speeder extends Enemy {
	public Speeder(BattleRandom rand, GameView view){
		super(rand, view);
		int msInterval = view.getIntervalInMilliseconds(), cellUnitSize = view.getDefaultCellSize();

		cellSpeed = (156*msInterval*cellUnitSize*2)/50000;// speed: 1.3 * players speed (12 full-cells / 5000 ms);
		points = 200;
		setIcons(false);
	}

	public Speeder(BattleRandom rand, GameView view, boolean powerApp){
		super(rand, view);
		int msInterval = view.getIntervalInMilliseconds(), cellUnitSize = view.getDefaultCellSize();

		cellSpeed = (156*msInterval*cellUnitSize*2)/50000;// speed: 1.3 * players speed (12 full-cells / 5000 ms);
		points = 200;
		setIcons(powerApp);
	}

	@Override
	protected void setIcons(boolean containsPowerUp){
		MapCell[] cells;
		Direction direction = Direction.DOWN;

		if(containsPowerUp)
			cells = MapCell.speederTankPowerUp(direction);
		else
			cells = MapCell.speederTank(direction);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction.getDirection(), cells);

		if(containsPowerUp){
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.speederTankPowerUp(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.speederTankPowerUp(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.speederTankPowerUp(direction) );
		} else {
			direction = Direction.UP;
			icons.put(direction.getDirection(), MapCell.speederTank(direction) );

			direction = Direction.RIGHT;
			icons.put(direction.getDirection(), MapCell.speederTank(direction) );

			direction = Direction.LEFT;
			icons.put(direction.getDirection(), MapCell.speederTank(direction) );
		}
	}
}
