package com.company.logic;

import com.company.model.Direction;
import java.util.Random;

public class BattleRandom {
	private Random rand;
	private double viewDistance;
	private double preferenceToMaintainDirection;
	private final int cellPrecisionSize;

	public BattleRandom(int cellPrecisionUnitSize){
		rand = new Random();
		cellPrecisionSize = cellPrecisionUnitSize;
		viewDistance = 3.0;// tanks will see the eagle 3 cell further (visibility range);
		preferenceToMaintainDirection = 0.96;// = 0 - will surely change direction; 1.0 - will keep direction;
	}


	/**
	 * If position or direction has nat been changed or is UP -> make preference lower;
	 * @param currentDirection last direction of movement;
	 * @param positionHasChanged informs if sprite has been moving;
	 * @return modified preference (probability) of keeping direction;
	 */
	private double modifyPreference(int currentDirection, boolean positionHasChanged){
		double modifiedPreference = preferenceToMaintainDirection;

		if(!positionHasChanged){
			modifiedPreference = 0.6 * preferenceToMaintainDirection;
		} else if(Direction.directionByAngle(currentDirection) == Direction.UP)
			modifiedPreference = 0.75*preferenceToMaintainDirection;

		return modifiedPreference;
	}

	public int randomDirectionAngleOrStop(int tankToEagleDx, int tankToEagleDy, int currentDirection, boolean positionHasChanged){
		if(preferenceToMaintainDirection == 1.0)
			return currentDirection;

		double randAngle = rand.nextDouble(), modifiedPreference = modifyPreference(currentDirection, positionHasChanged);
		final double unitLengthForOtherDirections = (1.0 - modifiedPreference)/4.0;// 5 direction options, 4 without preferred one;

		if(randAngle < unitLengthForOtherDirections)
			return -1;

		double rightAngle = 90.0, distance;
		if(randAngle < (1.0 + 3.0*modifiedPreference)/4.0){
			distance = (1.0 - 1.0/modifiedPreference)/4.0;
			randAngle = rightAngle/modifiedPreference * randAngle + rightAngle*distance;
		} else {
			distance = modifiedPreference*4.0/(modifiedPreference - 1.0);
			randAngle = rightAngle*randAngle/unitLengthForOtherDirections + rightAngle*distance;
		}

		double dx = (double)tankToEagleDx/(double)cellPrecisionSize, dy;
		dy = (double)tankToEagleDy/(double)cellPrecisionSize;

		distance = Math.sqrt(dx*dx + dy*dy);

		dx = dx/distance/distance * 0.5;// dx / distance^2; 0.5 - importance of "eagle direction";
		dy = dy/distance/distance * 0.5;
		double attackParam = Math.max( (viewDistance - distance)/3.0, 0.0);
		attackParam = Math.min(attackParam, 1.0);

		//randAngle = Math.floor(randAngle/rightAngle)*rightAngle;
		randAngle  = Math.floor(randAngle/rightAngle)*rightAngle - (double)currentDirection;
		randAngle = Math.toRadians(randAngle);

		dx = attackParam*dx + (1.0 - attackParam)*Math.cos(randAngle);
		dy = attackParam*dy + (1.0 - attackParam)*Math.sin(randAngle);
		Direction direction = Direction.stepVectorToDirection(dx, dy);

		return direction.getDirection();
	}
/*		Another source:
	public Directions randMove(int playerToActorDx, int playerToActorDy, double distance, double viewDistance){
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

	public double randRange(double range){
		return range*rand.nextDouble();
	}

	public double symmetricRandRange(double range){
		return range*( 2.0*rand.nextDouble() - 1.0 );
	}

	/**
	 * Binomial trial with probability dependent on the value of steps relative to rangeLimit.
	 * @param steps
	 * @param rangeLimit
	 * @return true or false which can be interpreted as "is success" (isSuccess) or "is failure" (isFailure).
	 */
	public boolean performTask(int steps, int rangeLimit){
		if(rangeLimit <= 0)
			return false;

		double interceptProbability = 0.1, slope;
		if(steps > 0){
			slope = (0.01 - interceptProbability)/rangeLimit;
		} else {
			slope = (interceptProbability - 0.99)/rangeLimit;
		}

		interceptProbability = slope*steps + interceptProbability;
		return rand.nextDouble() < interceptProbability;
	}

	public void setViewDistance(double viewDistance){
		this.viewDistance = viewDistance;
	}

	public void setPreferenceToMaintainDirection(double maintainDirection){
		if(maintainDirection >= 0.0 && maintainDirection <= 1.0)
			preferenceToMaintainDirection = maintainDirection;
	}
}
