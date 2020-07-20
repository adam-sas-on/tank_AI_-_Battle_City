package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;

import java.util.HashMap;
import java.util.Map;

public class Bullet {
	private int pixelSpeed;
	private int xDirection, yDirection;

	private int x_pos, y_pos;
	private int leftColDiff, leftRowDiff;
	private int rightColDiff, rightRowDiff;
	private int flightSteps, stepsLimits;
	private final int bulletSize;

	private PlayerAITank player;
	private Enemy tank;
	private boolean shotReseted;
	private boolean canDestroySteel;
	private Cell cell;
	private int explodeIndex;
	private MapCell bulletMapCell;
	private MapCell[] explodes;

	private Map<MapCell,MapCell> rightSideDestruction;
	private Map<MapCell,MapCell> leftSideDestruction;

	public Bullet(PlayerAITank player, DamageClass damages){
		int[] xyPos = new int[2];
		Direction direction;
		int tankSize;

		player.getPos(xyPos);
		direction = player.getDirectionCode();
		pixelSpeed = player.getBulletSpeed();// default: speed: 6 cells / 1000 ms;
		stepsLimits = player.getBulletSteps();
		tankSize = player.getTankSize();

		canDestroySteel = player.lastBulletCanDestroySteel();
		this.player = player;
		tank = null;

		bulletSize = (tankSize * MapCell.BULLET_UP.getSize() )/MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();

		setMapCellAndPosition(tankSize, direction, xyPos[0], xyPos[1]);

		setExplodeAttributes(direction, damages);
	}

	public Bullet(Enemy tank, DamageClass damages){
		int[] xyPos = new int[2];
		Direction direction;
		int tankSize;

		tank.getPos(xyPos);
		direction = tank.getDirectionCode();
		pixelSpeed = tank.getBulletSpeed();// default: speed: 6 cells / 1000 ms;
		stepsLimits = tank.getBulletSteps();
		tankSize = tank.getTankSize();
		canDestroySteel = false;

		this.tank = tank;
		player = null;
		bulletSize = (tankSize * MapCell.BULLET_UP.getSize() )/MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();

		setMapCellAndPosition(tankSize, direction, xyPos[0], xyPos[1]);

		setExplodeAttributes(direction, damages);
	}

	private void setMapCellAndPosition(int tankSize, Direction direction, int tankX, int tankY){
		x_pos = tankX;
		y_pos = tankY;

		rightColDiff = direction.rightCornerDx(bulletSize);
		rightRowDiff = direction.rightCornerDy(bulletSize);
		leftColDiff = direction.leftCornerDx(bulletSize);
		leftRowDiff = direction.leftCornerDy(bulletSize);

		switch(direction){
			case UP:
				bulletMapCell = MapCell.BULLET_UP;
				x_pos += (tankSize - bulletSize)/2;
				break;
			case RIGHT:
				bulletMapCell = MapCell.BULLET_RIGHT;
				x_pos += tankSize - bulletSize;
				y_pos += (tankSize - bulletSize)/2;
				break;
			case LEFT:
				bulletMapCell = MapCell.BULLET_LEFT;
				y_pos += (tankSize - bulletSize)/2;
				break;
			default:
				bulletMapCell = MapCell.BULLET_DOWN;
				x_pos += (tankSize - bulletSize)/2;
				y_pos += tankSize - bulletSize;
		}
	}

	private void setExplodeAttributes(Direction direction, DamageClass damages){
		flightSteps = 0;
		xDirection = direction.unitStepX();
		yDirection = direction.unitStepY();

		cell = new Cell();
		rightSideDestruction = new HashMap<>(14);
		leftSideDestruction = new HashMap<>(14);

		explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
				MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
		explodeIndex = -1;

		if(canDestroySteel)
			damages.setFullDamages(rightSideDestruction, leftSideDestruction, direction);
		else
			damages.setDamages(rightSideDestruction, leftSideDestruction, direction);
	}

	public void getBulletPos(int[] colRowPos){
		colRowPos[0] = x_pos;
		colRowPos[1] = y_pos;
	}

	/**
	 * 	leftPosCol
	 * UP: cell.getCol()
	 * RIGHT: cell.getCol() + cell.getCellSize()
	 * DOWN: cell.getCol() + cell.getCellSize()
	 * LEFT: cell.getCol()
	 * 	leftPosRow
	 * UP: cell.getRow()
	 * RIGHT: cell.getRow()
	 * DOWN: cell.getRow() + cell.getCellSize()
	 * LEFT: cell.getRow() + cell.getCellSize()
	 * @param colRowPos: array to assign values {col, row};
	 */
	public void getLeftCornerPos(int[] colRowPos){
		try {
			colRowPos[0] = x_pos + leftColDiff;
			colRowPos[1] = y_pos + leftRowDiff;
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	/**
	 * 	rightPosCol
	 * UP: cell.getCol() + cell.getCellSize()
	 * RIGHT: cell.getCol() + cell.getCellSize()
	 * DOWN: cell.getCol()
	 * LEFT: cell.getCol()
	 * 	rightPosRow
	 * UP: cell.getRow()
	 * RIGHT: cell.getRow() + cell.getCellSize()
	 * DOWN: cell.getRow() + cell.getCellSize()
	 * LEFT: cell.getRow()
	 * @param colRowPos: array to assign values {col, row};
	 */
	public void getRightCornerPos(int[] colRowPos){
		try {
			colRowPos[0] = x_pos + rightColDiff;
			colRowPos[1] = y_pos + rightRowDiff;
		} catch(ArrayIndexOutOfBoundsException ignore){}
	}

	public boolean belongsToPlayer(){
		return player != null;
	}

	public boolean belongsToPlayer(PlayerAITank checkPlayer){
		if(player == null || checkPlayer == null)
			return false;
		return checkPlayer.equals(player);
	}

	public void setUpCell(Cell cell){
		cell.setMapCell(bulletMapCell);
		cell.setPos(x_pos, y_pos);
	}

	public double getDirectionInRadians(){
		return Direction.stepVectorToRadians(xDirection, yDirection);
	}

	public boolean move(){
		if(explodeIndex >=0){
			bulletMapCell = explodes[explodeIndex];
			cell.setMapCell(explodes[explodeIndex]);
			explodeIndex++;
			return explodeIndex < explodes.length;
		}

		y_pos += pixelSpeed* yDirection;
		x_pos += pixelSpeed* xDirection;

		flightSteps++;
		if(flightSteps == stepsLimits)
			resetBulletShooting();

		cell.setPos(x_pos, y_pos);
		return true;
	}

	public void resetBulletShooting(){
		if(shotReseted)
			return;

		shotReseted = true;
		if(player != null) {
			player.resetBulletShots(flightSteps);
		} else if(tank != null)
			tank.resetBulletShots();
	}

	private void setDefaultPositionOfExplodes(){
		int explodeSize = (MapCell.EXPLODE_1.getSize()*bulletSize)/(MapCell.getUnitSize()), posDiff;
		posDiff = explodeSize - bulletSize;
		x_pos -= posDiff * (1 - xDirection);
		y_pos -= posDiff * (1 - yDirection);
	}

	public int setExplode(Cell cellOfExplodingObject){
		int steps = 0;
		if(explodeIndex < 0){
			explodeIndex = 0;
			explodes = MapCell.bigExplosionMapCells();
			steps = explodes.length;

			if(cellOfExplodingObject != null){
				x_pos = cellOfExplodingObject.getCol();
				y_pos = cellOfExplodingObject.getRow();
			} else {
				setDefaultPositionOfExplodes();
			}

			resetBulletShooting();
		}
		return steps;
	}

	public void setSmallExplode(){
		if(explodeIndex < 0) {
			explodeIndex = 0;
			explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_1};
			bulletMapCell = MapCell.EXPLODE_1;

			setDefaultPositionOfExplodes();

			resetBulletShooting();
		}
	}

	public boolean setRightDamageCell(Cell cell){
		boolean changed = false;
		if(explodeIndex < 0) {
			MapCell oldCellType = cell.getMapCell(), cellType;
			cellType = rightSideDestruction.get(oldCellType);
			cell.setMapCell(cellType);
			changed = oldCellType != cellType || oldCellType == MapCell.STEEL;
		}
		return changed;
	}

	public boolean setLeftDamageCell(Cell cell){
		boolean changed = false;
		if(explodeIndex < 0) {
			MapCell oldCellType = cell.getMapCell(), cellType;
			cellType = leftSideDestruction.get(oldCellType);
			cell.setMapCell(cellType);
			changed = oldCellType != cellType || oldCellType == MapCell.STEEL;
		}
		return changed;
	}
}
