package com.company.model;

import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public abstract class Tank {
	private double pixelSpeed;
	private double bulletSpeed;
	private double x_pos, y_pos;
	private double x_limit, y_limit;
	private Cell cell;
	private int level;
	private KeyCode previousDirection;
	Map<KeyCode, MapCell[]> icons;
	private int currentIcon;

	public Tank(int msInterval, int cellSize){
		pixelSpeed = (12*cellSize*msInterval*2)/5000.0;// speed: 12 cells / 5000 ms;
		if(pixelSpeed < 1.0)
			pixelSpeed = 1;

		bulletSpeed = (6*cellSize*msInterval*2)/1000.0;// bullet speed: 6 cells / second;
		if(bulletSpeed <= pixelSpeed)
			bulletSpeed = pixelSpeed + 1.0;

		x_limit = 12.0*cellSize;
		y_limit = x_limit;

		level = 1;
		icons = new HashMap<>();
		currentIcon = 0;
		cell = new Cell();
	}

	public int getXpos(){
		return (int)Math.round(x_pos);
	}
	public int getYpos(){
		return (int)Math.round(y_pos);
	}

	public Cell getCell(){
		return cell;
	}

	public void setPos(int row, int col, int cellSize){
		x_pos = (double)col*cellSize;
		y_pos = (double)row*cellSize;
		cell.setPos(col*cellSize, row*cellSize);
	}

	public void addIcons(KeyCode code, MapCell[] cells){
		if(icons.isEmpty() )
			cell.setMapCell(cells[0]);
		icons.put(code, cells);
	}

	public void move(KeyCode direction){
		switch(direction){
			case UP:
				y_pos -= pixelSpeed;
				if(y_pos < 0)
					y_pos = 0.0;
				break;
			case RIGHT:
				x_pos += pixelSpeed;
				if(x_pos > x_limit)
					x_pos = x_limit;
				break;
			case DOWN:
				y_pos += pixelSpeed;
				if(y_pos > y_limit)
					y_pos = y_limit;
				break;
			case LEFT:
				x_pos -= pixelSpeed;
				if(x_pos < 0.0)
					x_pos = 0.0;
				break;
		}
		MapCell[] cells = icons.get(direction);
		if(cells != null){
			currentIcon++;
			currentIcon = currentIcon%cells.length;
			cell.setMapCell(cells[currentIcon]);
			cell.setPos((int)Math.round(x_pos), (int)Math.round(y_pos));
		}
		previousDirection = direction;
	}

}
