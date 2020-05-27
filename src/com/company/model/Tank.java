package com.company.model;

import com.company.view.Cell;

public interface Tank {
	boolean move(int[] newXY);
	void setPos(int x, int y);
	void setUpCell(Cell cell);
	Cell getCell();
}
