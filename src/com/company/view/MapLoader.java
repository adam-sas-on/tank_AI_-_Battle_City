package com.company.view;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
	private static MapLoader instance = null;
	private int maxRows, maxCols;

	private MapLoader(){
		InputStream is = Cell.class.getResourceAsStream("/resources");
	}

	public static MapLoader getInstance(){
		if(instance == null)
			instance = new MapLoader();
		return instance;
	}

	private void falseArrayIndexes(boolean[] booleans, int indexBg){
		booleans[indexBg] = false;
		if(indexBg < booleans.length - 1)
			booleans[indexBg + 1] = false;
	}

	public void loadMap(Cell rootCell, String fileName){
		InputStream is = MapLoader.class.getResourceAsStream("/resources/" + fileName);
		Scanner scan = new Scanner(is);
		int cols = 0, rows = 0;
		if( scan.hasNextInt() )
			cols = scan.nextInt();
		if(scan.hasNextInt() )
			rows = scan.nextInt();


		//boolean[] freeLowerCells = new boolean[cols];
		boolean freeNextCell;

		scan.nextLine();

		Cell stepCell, rowCellBegin = rootCell;
		int col, row;
		String line;
		for(row = 0; row < rows && rowCellBegin != null; row++){
			line = scan.nextLine();
			stepCell = rowCellBegin;
			freeNextCell = true;

			for(col = 0; col < cols && stepCell != null; col++){
				stepCell.setMapCell(null);
				if(col >= line.length())
					break;

				if(!freeNextCell /*|| !freeLowerCells[col]*/){
					stepCell = stepCell.getRightCell();
					freeNextCell = true;
					continue;
				}

				freeNextCell = true;
				switch(line.charAt(col)){
					case '#':
						stepCell.setMapCell(MapCell.STEEL);
						break;
					case 'B':
						stepCell.setMapCell(MapCell.BRICK);
						break;
					case '/':
						stepCell.setMapCell(MapCell.BRICK_L_UP_LEFT);
						break;
					case '\\':
						stepCell.setMapCell(MapCell.BRICK_L_UP_RIGHT);
						break;
					case '>':
						stepCell.setMapCell(MapCell.BRICK_L_DOWN_RIGHT);
						break;
					case 'L':
						stepCell.setMapCell(MapCell.BRICK_L_DOWN_LEFT);
						break;
					case '[':
						stepCell.setMapCell(MapCell.BRICK_I_LEFT);
						break;
					case '_':
						stepCell.setMapCell(MapCell.BRICK_I_DOWN);
						break;
					case '|':
					case ']':
						stepCell.setMapCell(MapCell.BRICK_I_RIGHT);
						break;
					case '"':
						stepCell.setMapCell(MapCell.BRICK_I_UP);
						break;
					case '`':
						stepCell.setMapCell(MapCell.BRICK_UP_LEFT);
						break;
					case '\'':
						stepCell.setMapCell(MapCell.BRICK_UP_RIGHT);
						break;
					case '*':
						stepCell.setMapCell(MapCell.BRICK_DOWN_RIGHT);
						break;
					case ',':
						stepCell.setMapCell(MapCell.BRICK_DOWN_LEFT);
						break;
					case 'I':
						stepCell.setMapCell(MapCell.ICE);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
					case 'F':
						stepCell.setMapCell(MapCell.FOREST);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
					case 'W':
						stepCell.setMapCell(MapCell.WATER);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
					case 'E':
						stepCell.setMapCell(MapCell.EAGLE);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
					case '1':
						stepCell.setMapCell(MapCell.TANK_1_LVL_1_STATE_1_UP);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
					case '2':
						stepCell.setMapCell(MapCell.TANK_2_LVL_1_STATE_1_UP);
						freeNextCell = false;
						//falseArrayIndexes(freeLowerCells, col);
						break;
				}
				stepCell = stepCell.getRightCell();
			}

			rowCellBegin = rowCellBegin.getDownCell();
		}
	}

}
