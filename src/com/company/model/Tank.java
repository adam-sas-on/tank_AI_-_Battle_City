package com.company.model;

import com.company.view.Cell;
import com.company.view.GameView;

public interface Tank {
	void move(GameView view);
	void setPos(double x, double y);
	void setUpCell(Cell cell, int cellUnitSize);
	Cell getCell();
}
