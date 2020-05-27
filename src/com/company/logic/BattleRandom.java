package com.company.logic;

import com.company.view.Cell;

import java.util.Random;

public class BattleRandom {
	private Random rand;

	public BattleRandom(){
		rand = new Random();
	}

	public int randomDirectionAngleOrStop(Cell tankCell, Cell eagleCell){
		int tankToEagleDx = eagleCell.getCol() - tankCell.getCol(),
			tankToEagleDy = eagleCell.getRow() - tankCell.getRow();
		double angle = rand.nextDouble()*360.0;
		/*	public Directions randMove(int playerToActorDx, int playerToActorDy, double distance, double viewDistance){
		double angle = rand.nextDouble()*360.0, dy, dx, distanceSqr = 1.0, attackParam = 0.0;
		angle = Math.toRadians(angle);

		if(directionToPlayer > 0.0) {/ / directionToPlayer
			distanceSqr = distance*distance;// -> max(dx, dy, -dx, -dy);
			dx = playerToActorDx / distanceSqr * directionToPlayer;
			dy = playerToActorDy / distanceSqr * directionToPlayer;

			attackParam = Math.max( (viewDistance - distance)/3.0, 0.0);
			attackParam = Math.min(attackParam, 1.0);
		} else
			dx = dy = 0.0;

		dx = attackParam*dx + (1.0 - attackParam)*Math.cos(angle);// ( (x1 - x)/(x1 - x0) )*dx + ( (x - x0)/(x1 - x0) )*rand();
		dy = attackParam*dy + (1.0 - attackParam)*Math.sin(angle);

		return stepVectorToDirection(dx, dy);*/
		return 0;
	}

	/**
	 * Generate random odd number in range (0, range);
	 * @param range
	 * @return random odd number
	 */
	public int randomOdd(int range){
		return 1 + 2*rand.nextInt(range/2);
	}

	public int randRange(int begin, int end){
		return begin + rand.nextInt(end - begin);
	}
}
