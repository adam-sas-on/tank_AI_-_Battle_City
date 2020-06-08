package com.company.view;

import com.company.model.EnemyPorts;
import com.company.model.PlayerAITank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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

	private void addNeighbourCells(List<Cell> cellList, Cell rootCell){
		if(rootCell.getRightCell() != null)
			cellList.add(rootCell.getRightCell() );

		Cell cell = rootCell.getDownCell();
		if(cell != null){
			cellList.add(cell);
			cell = cell.getRightCell();
			if(cell != null)
				cellList.add(cell);
		}
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

	public void loadMap(Cell rootCell, String fileName,
						PlayerAITank player1, PlayerAITank player2,
						EnemyPorts ports, List<Integer> trees,
						GameView view) throws IOException {
		InputStream is = MapLoader.class.getResourceAsStream("/resources/" + fileName);
		Scanner scan = new Scanner(is);
		int cols = 0, rows = 0;
		if( scan.hasNextInt() )
			cols = scan.nextInt();
		if(scan.hasNextInt() )
			rows = scan.nextInt();

		if(rows == 0 || cols == 0)
			throw new IOException("loadMap: error in reading map size!");

		view.setColsRows(cols, rows);

		boolean[] freeCells = new boolean[(cols + 1)*2];
		int futureRowIndex = cols + 1, currentRowIndex = 0;

		scan.nextLine();

		Cell stepCell, rowCellBegin = rootCell;
		int col, row;
		String line;
		for(row = freeCells.length - 1; row >= 0; row--)
			freeCells[row] = true;

		if(ports == null){
			throw new NullPointerException("loadMap: ports can not be null!");
		} else
			ports.clear();

		List<Cell> nextCellToBlock = new LinkedList<>();
		trees.clear();

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
						stepCell.blockMovementsAround();
						break;
					case 'B':
						stepCell.setMapCell(MapCell.BRICK);
						stepCell.blockMovementsAround();
						break;
					case '/':
						stepCell.setMapCell(MapCell.BRICK_L_UP_LEFT);
						stepCell.blockMovementsAround();
						break;
					case '\\':
						stepCell.setMapCell(MapCell.BRICK_L_UP_RIGHT);
						stepCell.blockMovementsAround();
						break;
					case '>':
						stepCell.setMapCell(MapCell.BRICK_L_DOWN_RIGHT);
						stepCell.blockMovementsAround();
						break;
					case 'L':
						stepCell.setMapCell(MapCell.BRICK_L_DOWN_LEFT);
						stepCell.blockMovementsAround();
						break;
					case '[':
						stepCell.setMapCell(MapCell.BRICK_I_LEFT);
						stepCell.blockMovementsAround();
						break;
					case '_':
						stepCell.setMapCell(MapCell.BRICK_I_DOWN);
						stepCell.blockMovementsAround();
						break;
					case '|':
					case ']':
						stepCell.setMapCell(MapCell.BRICK_I_RIGHT);
						stepCell.blockMovementsAround();
						break;
					case '"':
						stepCell.setMapCell(MapCell.BRICK_I_UP);
						stepCell.blockMovementsAround();
						break;
					case '`':
						stepCell.setMapCell(MapCell.BRICK_UP_LEFT);
						stepCell.blockMovementsAround();
						break;
					case '\'':
						stepCell.setMapCell(MapCell.BRICK_UP_RIGHT);
						stepCell.blockMovementsAround();
						break;
					case '*':
						stepCell.setMapCell(MapCell.BRICK_DOWN_RIGHT);
						stepCell.blockMovementsAround();
						break;
					case ',':
						stepCell.setMapCell(MapCell.BRICK_DOWN_LEFT);
						stepCell.blockMovementsAround();
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
						stepCell.blockMovementsAround();
						addNeighbourCells(nextCellToBlock, stepCell);

						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case '@':
						ports.add(stepCell.getCol(), stepCell.getRow());
						break;
					case 'E':
						stepCell.setMapCell(MapCell.EAGLE);
						stepCell.blockMovementsAround();
						addNeighbourCells(nextCellToBlock, stepCell);

						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case '1':
						player1.setStartingPos(stepCell.getCol(), stepCell.getRow() );
						freeCells[currentRowIndex + col + 1] = false;
						freeCells[futureRowIndex + col] = freeCells[futureRowIndex + col + 1] = false;
						break;
					case '2':
						player2.setStartingPos(stepCell.getCol(), stepCell.getRow() );
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

		Iterator<Cell> iter = nextCellToBlock.listIterator();
		while(iter.hasNext() ){
			stepCell = iter.next();
			stepCell.blockMovementsAround();
			stepCell.setMapCell(MapCell.NULL_UNIT_BLOCKADE);
			iter.remove();
		}
	}

	public int getMaxRows(){
		return maxRows;
	}

	public int getMaxCols(){
		return maxCols;
	}
}
