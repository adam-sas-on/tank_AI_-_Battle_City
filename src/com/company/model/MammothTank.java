package com.company.model;

import com.company.logic.BattleRandom;
import com.company.view.Cell;
import com.company.view.GameView;
import com.company.view.MapCell;

public class MammothTank extends Enemy {
	public MammothTank(BattleRandom rand, GameView view){
		super(rand, view);
		level = 4;
		points = 400;
		setIcons(false);
	}

	public MammothTank(BattleRandom rand, GameView view, boolean powerUp){
		super(rand, view);
		level = 4;
		points = 400;
		hasPowerUp = powerUp;
		setIcons(powerUp);
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

	@Override
	public int getHit(Cell bulletCell, Cell tankBufferCell){
		if(bulletCell.getMapCell() == MapCell.BOMB){
			level = 0;
			setExplosion();
			return 0;
		}

		boolean hit = isHit(bulletCell, tankBufferCell);

		if(hit) {
			level--;

			if (level < 1)
				setExplosion();
			else {
				setIcons(false);
				currentIcons = icons.get(currentDirection);
				currentIconInd = 0;
			}
		}

		return hit?points:0;
	}
}
