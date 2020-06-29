package com.company.view;

import com.company.logic.BattleRandom;
import com.company.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MapLoader {
	private static MapLoader instance = null;
	private int maxRows, maxCols;
	private BattleRandom rand = null;

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

	public void setRandom(BattleRandom rand){
		this.rand = rand;
	}

	public void loadMap(Cell rootCell, String fileName,
						PlayerAITank player1, PlayerAITank player2,
						EnemyPorts ports, Queue<Enemy> tanks,
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
		view.clearTrees();

		for(row = 0; row < rows && rowCellBegin != null; row++){
			try {
				line = scan.nextLine();
			} catch(NoSuchElementException e){
				System.out.println("\tloadMap: loading map rows failed in  \"" + fileName + "\"! No line found!");
				view.setColsRows(cols, row);
				throw new IOException("loadMap: error in reading map rows!");
			}

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
						view.addTree(stepCell);
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

		line = "";
		try {
			line = scan.nextLine();
		} catch(NoSuchElementException e){
			System.out.println("\tloadMap: can not read the last row with enemy tanks in  \"" + fileName + "\"! \n" + e);
		}
		scan.close();

		readEnemyTanks(tanks, line, view);
	}

	private void readEnemyTanks(Queue<Enemy> tanks, String tanksSymbolsLine, GameView view){
		tanks.clear();
		if(rand == null)
			return;
		else if(tanksSymbolsLine.length() < 1){
			addDefaultEnemyTanksSet(tanks, view);
			return;
		}

		Enemy tank;
		String[] elements = tanksSymbolsLine.split("[^\\w]+");
		boolean powerUp;
		int i, count = elements.length;

		for(i = 0; i < count; i++){
			tank = null;
			powerUp = i == 3 || i == 10 || i == 17;

			if(elements[i].equalsIgnoreCase("l") || elements[i].equalsIgnoreCase("t") || elements[i].equalsIgnoreCase("light") )
				tank = new LightTank(rand, view, powerUp);
			else if(elements[i].equalsIgnoreCase("s") || elements[i].equalsIgnoreCase("speeder") )
				tank = new Speeder(rand, view, powerUp);
			else if(elements[i].equalsIgnoreCase("r") || elements[i].equalsIgnoreCase("rapid") )
				tank = new RapidShooter(rand, view, powerUp);
			else if(elements[i].equalsIgnoreCase("m") || elements[i].equalsIgnoreCase("mammoth") ||
					elements[i].equalsIgnoreCase("h") || elements[i].equalsIgnoreCase("heavy") )
				tank = new MammothTank(rand, view, powerUp);

			if(tank != null)
				tanks.add(tank);
		}
	}

	/**
	 * Fill the list of enemy tanks according to default set from original game;
	 * 18 light tanks and 2 speeders;
	 * @param tanks list of enemy tanks to fill;
	 * @param view game view object with parameters like cell precision of time step;
	 */
	private void addDefaultEnemyTanksSet(Queue<Enemy> tanks, GameView view){
		Enemy tank;
		int i;

		for(i = 0; i < 18; i++){
			if(i == 3 || i == 10 || i == 17)
				tank = new LightTank(rand, view, true);
			else
				tank = new LightTank(rand, view);

			tanks.add(tank);
		}

		tank = new Speeder(rand, view);
		tanks.add(tank);
		tank = new Speeder(rand, view);
		tanks.add(tank);
	}


	public int getMaxRows(){
		return maxRows;
	}

	public int getMaxCols(){
		return maxCols;
	}
}
