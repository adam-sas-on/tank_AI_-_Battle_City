package com.company.view;

import com.company.model.PlayerAITank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MapLoader {
	private static MapLoader instance = null;
	private int maxRows, maxCols;

	private MapLoader(){
		maxRows = maxCols = 0;
	}

	public static MapLoader getInstance(){
		if(instance == null)
			instance = new MapLoader();
		return instance;
	}

	public void getFileList(List<String> mapFiles){
		try {
			InputStream is = MapLoader.class.getResourceAsStream("/resources/");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Scanner scan;
			String resource;
			boolean check;
			int cols, rows;

			while( (resource = br.readLine()) != null){
				check = resource.regionMatches(true, 0, "map", 0, 3);
				if(!check)
					continue;

				mapFiles.add(resource);
				is = MapLoader.class.getResourceAsStream("/resources/" + resource);
				scan = new Scanner(is);
				cols = rows = 0;
				if(scan.hasNextInt() )
					cols = scan.nextInt();
				if(scan.hasNextInt() )
					rows = scan.nextInt();

				if(cols > maxCols)
					maxCols = cols;
				if(rows > maxRows)
					maxRows = rows;
			}
		} catch(IOException e){
			System.out.println("MapLoader can not load files:  " + e);
		}
		Collections.sort(mapFiles);
	}

	public void loadMap(Cell rootCell, String fileName, PlayerAITank player1, PlayerAITank player2, List<Integer> trees){
		InputStream is = MapLoader.class.getResourceAsStream("/resources/" + fileName);
		Scanner scan = new Scanner(is);
		int cols = 0, rows = 0;
		if( scan.hasNextInt() )
			cols = scan.nextInt();
		if(scan.hasNextInt() )
			rows = scan.nextInt();


		boolean[] freeCells = new boolean[(cols + 1)*2];
		int futureRowIndex = cols + 1, currentRowIndex = 0;

		scan.nextLine();

		trees.clear();
		Cell stepCell, rowCellBegin = rootCell;
		int col, row;
		String line;
		for(row = freeCells.length - 1; row >= 0; row--)
			freeCells[row] = true;

		for(row = 0; row < rows && rowCellBegin != null; row++){
			line = scan.nextLine();
			stepCell = rowCellBegin;


			for(col = 0; col < cols && stepCell != null; col++){
				stepCell.setMapCell(null);
				stepCell.resetMovement(col, row, cols, rows);

				if(col >= line.length())
					break;

				if(!freeCells[currentRowIndex + col]){
					stepCell = stepCell.getRightCell();
					freeCells[currentRowIndex + col] = true;
					continue;
				}


				freeCells[currentRowIndex + col] = true;
				freeCells[currentRowIndex + col + 1] = true;
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
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case 'F':
						stepCell.setMapCell(MapCell.FOREST);
						trees.add(stepCell.getId());
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case 'W':
						stepCell.setMapCell(MapCell.WATER);
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case 'E':
						stepCell.setMapCell(MapCell.EAGLE);
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case '1':
						player1.setPos(col, row);
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case '2':
						player2.setPos(col, row);
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
				}
				stepCell = stepCell.getRightCell();
			}

			rowCellBegin = rowCellBegin.getDownCell();
			col = futureRowIndex;// SWAP(currentRowIndex, futureRowIndex);
			futureRowIndex = currentRowIndex;
			currentRowIndex = col;
		}
	}

}
