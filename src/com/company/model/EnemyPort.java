package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;

public class EnemyPort {
	private int x_pos, y_pos;
	private MapCell[] icons;
	private int currentIconInd;

	public EnemyPort(){
		x_pos = y_pos = 0;

		icons = new MapCell[]{MapCell.CREATE_1, MapCell.CREATE_2, MapCell.CREATE_3,
				MapCell.CREATE_4, MapCell.CREATE_5, MapCell.CREATE_6};
		currentIconInd = 0;
	}


	public void setUpCell(Cell cell, int newCellUnitSize, int cellPrecisionSize){
		cell.setMapCell(icons[currentIconInd]);
		cell.setPos(x_pos, y_pos);
		cell.roundPos(cellPrecisionSize, newCellUnitSize);

		currentIconInd++;
		currentIconInd = currentIconInd %icons.length;
	}

	public void setPos(int x, int y){
		x_pos = x;
		y_pos = y;
	}

}
