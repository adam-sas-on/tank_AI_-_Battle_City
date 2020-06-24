package com.company.model;

import com.company.view.Cell;

public interface Tank {
	boolean requestedPosition(int[] newXY);
	void setUpCell(Cell cell);
}
