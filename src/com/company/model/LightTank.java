package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.GameView;
import com.company.view.MapCell;

public class LightTank extends Enemy {

	public LightTank(BattleRandom rand, GameView view){
		super(rand, view);
	}

	public LightTank(BattleRandom rand, GameView view, boolean powerApp){
		super(rand, view);
	}

	@Override
	protected void setIcons(boolean containsPowerUp){
		MapCell[] cells;
		Direction direction = Direction.DOWN;

		cells = MapCell.lightTank(direction);
		if(icons.isEmpty() )
			currentIcons = cells;
		icons.put(direction.getDirection(), cells);

	}
}
