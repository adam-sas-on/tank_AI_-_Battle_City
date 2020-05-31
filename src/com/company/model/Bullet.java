package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class Bullet {
	private int pixelSpeed;
	private int xDirection, yDirection;

	private int x_pos, y_pos;
	private int leftColDiff, leftRowDiff;
	private int rightColDiff, rightRowDiff;
	private int flightSteps;
	private final int bulletSize;

	private PlayerAITank player;
	//private Enemy tank;
	private boolean canDestroySteel;
	private Cell cell;
	private int explodeIndex;
	private MapCell bulletMapCell;
	private MapCell[] explodes;

	private Map<MapCell,MapCell> rightSideDestruction;
	private Map<MapCell,MapCell> leftSideDestruction;

	public Bullet(PlayerAITank player, DamageClass damages){
		int[] xyPos = new int[2];
		KeyCode direction;
		int tankSize;

		player.getPos(xyPos);
		direction = player.getDirectionCode();
		tankSize = player.getTankSize();
		pixelSpeed = player.getBulletSpeed();// default: speed: 6 cells / 1000 ms;

		canDestroySteel = player.lastBulletCanDestroySteel();
		this.player = player;
		xDirection = 0;
		yDirection = 0;
		flightSteps = 0;
		bulletSize = (tankSize * MapCell.BULLET_UP.getSize() )/MapCell.TANK_1_LVL_1_STATE_1_UP.getSize();

		cell = new Cell();
		rightSideDestruction = new HashMap<>(14);
		leftSideDestruction = new HashMap<>(14);

		setMapCellAndPosition(tankSize, direction, xyPos[0], xyPos[1]);

		explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
				MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
		explodeIndex = -1;

		if(canDestroySteel)
			damages.setFullDamages(rightSideDestruction, leftSideDestruction, direction);
		else
			damages.setDamages(rightSideDestruction, leftSideDestruction, direction);
	}

	private void setMapCellAndPosition(int tankSize, KeyCode direction, int tankX, int tankY){
		x_pos = tankX;
		y_pos = tankY;

		leftColDiff = leftRowDiff = rightColDiff = rightRowDiff = 0;

		switch(direction){
			case UP:
				bulletMapCell = MapCell.BULLET_UP;
				yDirection = -1;
				x_pos += (tankSize - bulletSize)/2;

				rightColDiff = bulletSize;
				break;
			case RIGHT:
				bulletMapCell = MapCell.BULLET_RIGHT;
				xDirection = 1;
				x_pos += tankSize - bulletSize;
				y_pos += (tankSize - bulletSize)/2;

				leftColDiff = bulletSize;
				rightColDiff = rightRowDiff = bulletSize;
				break;
			case LEFT:
				bulletMapCell = MapCell.BULLET_LEFT;
				xDirection = -1;
				y_pos += (tankSize - bulletSize)/2;

				leftRowDiff = bulletSize;
				break;
			default:
				bulletMapCell = MapCell.BULLET_DOWN;
				yDirection = 1;
				x_pos += (tankSize - bulletSize)/2;
				y_pos += tankSize - bulletSize;

				rightRowDiff = bulletSize;
				leftColDiff = leftRowDiff = bulletSize;
		}
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

	public boolean canDestroySteel(){
		return canDestroySteel;
	}

	public boolean belongsToPlayer(){
		return player != null;
	}


	public void setUpCell(Cell cell){
		cell.setMapCell(bulletMapCell);
		cell.setPos(x_pos, y_pos);
	}

	public boolean isExploding(){
		return explodeIndex >= 0;
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

		cell.setPos(x_pos, y_pos);
		return true;
	}

	public void resetBulletShooting(){
		if(player != null) {
			player.resetBulletShots(flightSteps);
			player = null;
		}/*else if(tank != null)
			;*/
	}

	public void setExplode(){
		if(explodeIndex < 0) {
			explodeIndex = 0;
			explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_2,
					MapCell.EXPLODE_3, MapCell.EXPLODE_4, MapCell.EXPLODE_5};
			int posDiff = (MapCell.EXPLODE_1.getSize() - cell.getCellSize()) / 2, col = cell.getCol();
			cell.setPos(col - posDiff, cell.getRow() - posDiff);

			resetBulletShooting();
		}
	}

	public void setSmallExplode(){
		if(explodeIndex < 0) {
			explodeIndex = 0;
			explodes = new MapCell[]{MapCell.EXPLODE_1, MapCell.EXPLODE_1};
			bulletMapCell = MapCell.EXPLODE_1;
			int explodeSize = (MapCell.EXPLODE_1.getSize()*bulletSize)/(MapCell.getUnitSize() ), posDiff;
			posDiff = explodeSize - bulletSize;
			x_pos -= posDiff*(1 - xDirection);
			y_pos -= posDiff*(1 - yDirection);

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
