package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class MammothTank extends Enemy {
	public MammothTank(BattleRandom rand, GameView view){
		super(rand, view);
		level = 4;
		setIcons(false);
	}

	public MammothTank(BattleRandom rand, GameView view, boolean powerApp){
		super(rand, view, powerApp);
		level = 4;
		setIcons(powerApp);
	}

	@Override
	protected void setIcons(boolean containsPowerUp){
		MapCell[] cells;
		Direction direction = Direction.DOWN;

		cells = MapCell.mammothTankDownState(level, containsPowerUp);

		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction.getDirection(), cells);

		direction = Direction.UP;
		icons.put(direction.getDirection(), MapCell.mammothTankUpState(level, containsPowerUp) );

		direction = Direction.RIGHT;
		icons.put(direction.getDirection(), MapCell.mammothTankRightState(level, containsPowerUp) );

		direction = Direction.LEFT;
		icons.put(direction.getDirection(), MapCell.mammothTankLeftState(level, containsPowerUp) );
	}
}
