package com.company.view;

import com.company.model.Direction;
import com.company.view.Cell;
import com.company.view.MapCell;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
	private static Cell[] cells;
	private static int cols, rows;
	private static final int pixelsInCell = 1000;
	static Stream<Arguments> rowsCols;

	@BeforeAll
	private static void setUpCells(){
		cols = 20;
		rows = 20;
		int i, n = cols*rows;

		cells = new Cell[n];
		for(i = 0; i < n; i++){
			cells[i] = new Cell();
			cells[i].setIndexId(i);
		}

		cells[0].setCellStructure(cells, cols, rows, pixelsInCell);
	}

	private static Stream<Arguments> buildRowsCols(int limit){
		Stream.Builder<Arguments> sBuild = Stream.builder();

		for(int i = 0; i < limit; i++)
			sBuild.add( Arguments.of(i) );

		return sBuild.build();
	}

	static Stream<Arguments> colValues(){
		rowsCols = buildRowsCols(cols - 1);
		return rowsCols;
	}
	static Stream<Arguments> rowValues(){
		rowsCols = buildRowsCols(rows - 1);
		return rowsCols;
	}

	static Stream<Arguments> waterEnvelop(){
		Stream.Builder<Arguments> sBuild = Stream.builder();
		int waterRow = rows/2, waterCol = cols/2;

		sBuild.add( Arguments.of(waterCol, waterRow, Direction.UP, -1) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.UP, 0) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.UP, 1) );

		sBuild.add( Arguments.of(waterCol, waterRow, Direction.RIGHT, -1) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.RIGHT, 0) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.RIGHT, 1) );

		sBuild.add( Arguments.of(waterCol, waterRow, Direction.DOWN, -1) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.DOWN, 0) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.DOWN, 1) );

		sBuild.add( Arguments.of(waterCol, waterRow, Direction.LEFT, -1) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.LEFT, 0) );
		sBuild.add( Arguments.of(waterCol, waterRow, Direction.LEFT, 1) );
		return sBuild.build();
	}

	private void clearCellStructure(){
		int row, col, i = 0, count = cells.length;

		for(row = 0; row < rows && i < count; row++){
			for(col = 0; col < cols && i < count; col++, i++){
				cells[i].setMapCell(null);
				cells[i].resetMovement(col, row, cols, rows);
			}
		}
	}

	private int setVerticalBlockade(int rowToOmit){
		clearCellStructure();

		int midIndex = cols/2, i = 0;
		Cell stepCell = cells[midIndex];

		while(stepCell != null){
			if(i != rowToOmit) {
				stepCell.setMapCell(MapCell.STEEL);
				stepCell.blockMovementsAround();
			}
			stepCell = stepCell.getDownCell();
			i++;
		}

		return midIndex;
	}

	private int setHorizontalBlockade(int colToOmit){
		clearCellStructure();

		int i = 0, midIndex = rows/2;
		Cell stepCell = cells[0];

		while(i < midIndex && stepCell != null){
			stepCell = stepCell.getDownCell();
			i++;
		}

		int result = (stepCell != null)?midIndex:-1;

		i = 0;
		while(stepCell != null){
			if(i != colToOmit) {
				stepCell.setMapCell(MapCell.STEEL);
				stepCell.blockMovementsAround();
			}
			stepCell = stepCell.getRightCell();
			i++;
		}

		return result;
	}

	/**
	 *
	 * @param waterCol column to place a water cell;
	 * @param waterRow row to place a water cell;
	 * @param direction direction from of moving cell after removing brick cell standing next to water;
	 * @param brickRowColDiff change column or row from water position depending on direction;
	 * @return index of cell with brick;
	 */
	private int setWaterBlockade1Brick(int waterCol, int waterRow, Direction direction, int brickRowColDiff){
		clearCellStructure();
		if(waterCol < 0 || waterRow < 0)
			return -1;

		int index, brickRow = -1, brickCol = -1;

		if(direction == Direction.UP || direction == Direction.DOWN)
			brickCol = waterCol + brickRowColDiff;
		else if(direction == Direction.RIGHT || direction == Direction.LEFT)
			brickRow = waterRow + brickRowColDiff;

		switch(direction){
			case UP:
				brickRow = waterRow + 2;
				break;
			case RIGHT:
				brickCol = waterCol - 1;
				break;
			case DOWN:
				brickRow = waterRow - 1;
				break;
			case LEFT:
				brickCol = waterCol + 2;
				break;
		}

		if(brickCol < 0 || brickRow < 0 || brickCol >= cols - 1 || brickRow >= rows -1)
			return -1;

		index = brickRow*cols + brickCol;

		int waterIndex = waterRow*cols + waterCol;
		if(waterIndex + cols + 1 >= cells.length)
			return -1;

		cells[waterIndex].setMapCell(MapCell.WATER);
		cells[waterIndex].blockMovementsAround();
		cells[waterIndex + 1].setMapCell(MapCell.NULL_UNIT_BLOCKADE);
		cells[waterIndex + 1].blockMovementsAround();
		cells[waterIndex + cols].setMapCell(MapCell.NULL_UNIT_BLOCKADE);
		cells[waterIndex + cols].blockMovementsAround();
		cells[waterIndex + cols + 1].setMapCell(MapCell.NULL_UNIT_BLOCKADE);
		cells[waterIndex + cols + 1].blockMovementsAround();

		cells[index].setMapCell(MapCell.BRICK);
		cells[index].blockMovementsAround();

		return index;
	}

	private int cellIndexForPosition(int xPos, int yPos){
		int col = xPos/pixelsInCell, row = yPos/pixelsInCell;
		if(col < 0 || row < 0 || row >= rows || col >= cols)
			return -1;

		return row*cols + col;
	}

	private Cell cellByPosition(int xPos, int yPos){
		int cellIndex = cellIndexForPosition(xPos, yPos);
		if(cellIndex < 0)
			return null;

		return cells[cellIndex];
	}
	// - - - - - - - - - - - - -

	@ParameterizedTest
	@MethodSource("colValues")
	void blockMovementsHorizontalUpperTest(int col){
		int index = setHorizontalBlockade(-1);
		String message;

		if(index < 0){
			message = "blockMovementsHorizontalUpperTest: can not run the test for col = " + col;
			System.out.println(message);
			fail(message);
			return;
		}

		int expectedRow = (index - 2)*pixelsInCell, i;
		i = expectedRow + pixelsInCell/2;// + (0, pixelsInCell)  <- anything from;
		Cell rowCell = cellByPosition(col*pixelsInCell, i);

		if(rowCell == null) {
			message = "blockMovementsHorizontalUpperTest: can not run the test for col = " + col;
			System.out.println(message);
			fail(message);
			return;
		}


		int expectedCol = rowCell.getCol(),
			resultRow, resultCol;

		resultRow = rowCell.checkModifyRow(Direction.DOWN, i);
		resultCol = rowCell.checkModifyCol(Direction.DOWN, expectedCol);

		assertEquals(expectedRow, resultRow);
		assertEquals(expectedCol, resultCol);
	}

	@ParameterizedTest
	@MethodSource("colValues")
	void blockMovementsHorizontalLowerTest(int col){
		int index = setHorizontalBlockade(-1);
		String message;

		if(index < 0){
			message = "blockMovementsHorizontalLowerTest: can not run the test for col = " + col;
			System.out.println(message);
			fail(message);
			return;
		}

		int expectedRow = (index + 1)*pixelsInCell, i;
		i = expectedRow - pixelsInCell/2;// - (0, pixelsInCell);
		Cell rowCell = cellByPosition(col*pixelsInCell, i);

		if(rowCell == null){
			message = "blockMovementsHorizontalLowerTest: can not run the test for col = " + col;
			System.out.println(message);
			fail(message);
			return;
		}


		int expectedCol = rowCell.getCol(), resultRow, resultCol;

		resultRow = rowCell.checkModifyRow(Direction.UP, i);
		resultCol = rowCell.checkModifyCol(Direction.UP, expectedCol);

		assertEquals(expectedRow, resultRow);
		assertEquals(expectedCol, resultCol);
	}

	@ParameterizedTest
	@MethodSource("rowValues")
	void blockMovementsVerticalLeftTest(int row){
		int index = setVerticalBlockade(-1);

		int expectedCol = (index - 2)*pixelsInCell, i;
		i = expectedCol + pixelsInCell/2;// + (0, pixelsInCell);
		Cell colCell = cellByPosition(i, row*pixelsInCell);

		if(colCell == null){
			String message = "blockMovementsVerticalLeftTest: can not run the test for row = " + row;
			System.out.println(message);
			fail(message);
			return;
		}

		int expectedRow = colCell.getRow(), resultRow, resultCol;

		resultRow = colCell.checkModifyRow(Direction.RIGHT, expectedRow);
		resultCol = colCell.checkModifyCol(Direction.RIGHT, i);

		assertEquals(expectedRow, resultRow);
		assertEquals(expectedCol, resultCol);
	}

	@ParameterizedTest
	@MethodSource("rowValues")
	void blockMovementsVerticalRightTest(int row){
		int index = setVerticalBlockade(-1);

		int expectedCol = (index + 1)*pixelsInCell, i;
		i = expectedCol - pixelsInCell/2;// - (0, pixelsInCell);
		Cell colCell = cellByPosition(i, row*pixelsInCell);

		if(colCell == null){
			String message = "blockMovementsVerticalRightTest: can not run the test for row = " + row;
			System.out.println(message);
			fail(message);
			return;
		}

		int expectedRow = colCell.getRow(), resultRow, resultCol;

		resultRow = colCell.checkModifyRow(Direction.LEFT, expectedRow);
		resultCol = colCell.checkModifyCol(Direction.LEFT, i);

		assertEquals(expectedRow, resultRow);
		assertEquals(expectedCol, resultCol);
	}

	@ParameterizedTest
	@MethodSource("colValues")
	void blockMovementsHorizontalCellOpenUpperTest(int col){
		int index = setHorizontalBlockade(col);

		int expectedRow = (index - 2)*pixelsInCell, i;
		i = expectedRow + pixelsInCell/2;// + (0, pixelsInCell);
		Cell rowCell = cellByPosition((col - 1)*pixelsInCell, i);

		int expectedCol, resultRow, resultCol, failCount = 0;

		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(Direction.DOWN, i);
			resultCol = rowCell.checkModifyCol(Direction.DOWN, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		rowCell = cellByPosition(col*pixelsInCell, i);
		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(Direction.DOWN, i);
			resultCol = rowCell.checkModifyCol(Direction.DOWN, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		if(failCount > 1){
			fail("blockMovementsHorizontalCellOpenUpperTest: can not run the test!");
		}
	}

	@ParameterizedTest
	@MethodSource("colValues")
	void blockMovementsHorizontalCellOpenLowerTest(int col){
		int index = setHorizontalBlockade(col);

		int expectedRow = (index + 1)*pixelsInCell, i;
		i = expectedRow - pixelsInCell/2;// - (0, pixelsInCell);
		Cell rowCell = cellByPosition((col - 1)*pixelsInCell, i);

		int expectedCol, resultRow, resultCol, failCount = 0;

		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(Direction.UP, i);
			resultCol = rowCell.checkModifyCol(Direction.UP, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		rowCell = cellByPosition(col*pixelsInCell, i);
		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(Direction.UP, i);
			resultCol = rowCell.checkModifyCol(Direction.UP, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		if(failCount > 1){
			fail("blockMovementsHorizontalCellOpenLowerTest: can not run the test!");
		}
	}

	@ParameterizedTest
	@MethodSource("rowValues")
	void blockMovementsVerticalCellOpenLeftTest(int row){
		int index = setVerticalBlockade(row);

		int expectedCol = (index - 2)*pixelsInCell, i;
		i = expectedCol + pixelsInCell/2;// + (0, pixelsInCell);
		Cell colCell = cellByPosition(i, (row - 1)*pixelsInCell);

		int expectedRow, resultRow, resultCol, failCount = 0;

		if(colCell != null) {
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(Direction.RIGHT, expectedRow);
			resultCol = colCell.checkModifyCol(Direction.RIGHT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		colCell = cellByPosition(i, row*pixelsInCell);
		if(colCell != null){
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(Direction.RIGHT, expectedRow);
			resultCol = colCell.checkModifyCol(Direction.RIGHT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		if(failCount > 1){
			fail("blockMovementsVerticalCellOpenLeftTest: can not run the test!");
		}
	}

	@ParameterizedTest
	@MethodSource("rowValues")
	void blockMovementsVerticalCellOpenRightTest(int row){
		int index = setVerticalBlockade(row);

		int expectedCol = (index + 1)*pixelsInCell, i;
		i = expectedCol - pixelsInCell/2;// - (0, pixelsInCell);
		Cell colCell = cellByPosition(i, (row - 1)*pixelsInCell);

		int expectedRow, resultRow, resultCol, failCount = 0;

		if(colCell != null) {
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(Direction.LEFT, expectedRow);
			resultCol = colCell.checkModifyCol(Direction.LEFT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		colCell = cellByPosition(i, row*pixelsInCell);
		if(colCell != null){
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(Direction.LEFT, expectedRow);
			resultCol = colCell.checkModifyCol(Direction.LEFT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		if(failCount > 1){
			fail("blockMovementsVerticalCellOpenRightTest: can not run the test!");
		}
	}

	/**
	 * Next 2 tests are for cases where whole horizontal wall/blockade has a one-cell hole;
	 * sprite shouldn't be able to move through that hole
	 * (arrays of positions before and after the move should have the same values);
	 * @param col is a column of cells to check if it block;
	 */
	@ParameterizedTest
	@MethodSource("colValues")
	void unblockingOneHorizontalCellUpperTest(int col){
		Cell cell;
		int index, expectedRow, i, cellIndex;
		int[] resultRows = new int[4], expectedRows = new int[4];// 4 neighbours for index of cell;
		resultRows[0] = resultRows[1] = resultRows[2] = resultRows[3] = -1;
		expectedRows[0] = expectedRows[1] = expectedRows[2] = expectedRows[3] = -1;

		index = setHorizontalBlockade(-1);
		expectedRow = index*pixelsInCell;
		cellIndex = cellIndexForPosition(col*pixelsInCell, expectedRow);
		if(cellIndex < 0 || cellIndex >= cells.length)
			return;

		cells[cellIndex].setMapCell(null);
		cells[cellIndex].unblockMovementsAround();
		expectedRow = (index - 2)*pixelsInCell;
		cellIndex = expectedRow + pixelsInCell/2;// + (0, pixelsInCell);

		for(i = -2; i < 2; i++){
			if(col + i < 0 || col + i >= cols)
				continue;

			cell = cellByPosition((col + i)*pixelsInCell, cellIndex);
			if(cell == null)
				continue;

			expectedRows[i + 2] = expectedRow;
			resultRows[i + 2] = cell.checkModifyRow(Direction.DOWN, cellIndex);
		}

		System.out.println("col = " + col + "; result: " + Arrays.toString(resultRows));
		assertArrayEquals(expectedRows, resultRows);
	}

	@ParameterizedTest
	@MethodSource("colValues")
	void unblockingOneHorizontalCellLowerTest(int col){
		int index, expectedRow, cellIndex;
		int[] resultRows = new int[4], expectedRows = new int[4];// 4 neighbours for index of cell;
		resultRows[0] = resultRows[1] = resultRows[2] = resultRows[3] = -1;
		expectedRows[0] = expectedRows[1] = expectedRows[2] = expectedRows[3] = -1;

		index = setHorizontalBlockade(-1);
		expectedRow = index*pixelsInCell;
		cellIndex = cellIndexForPosition(col*pixelsInCell, expectedRow);
		if(cellIndex < 0 || cellIndex >= cells.length)
			return;

		cells[cellIndex].setMapCell(null);
		cells[cellIndex].unblockMovementsAround();
		expectedRow = (index + 1)*pixelsInCell;
		cellIndex = expectedRow - pixelsInCell/2;// - (0, pixelsInCell);

		Cell cell;
		int i;
		for(i = -2; i < 2; i++){
			if(col + i < 0 || col + i >= cols)
				continue;

			cell = cellByPosition((col + i)*pixelsInCell, cellIndex);
			if(cell == null)
				continue;

			expectedRows[i + 2] = expectedRow;
			resultRows[i + 2] = cell.checkModifyRow(Direction.UP, cellIndex);
		}

		if(expectedRows[0] < 0 && expectedRows[1] < 0 && expectedRows[2] < 0 && expectedRows[3] < 0)
			fail("unblockingOneHorizontalCellLowerTest: all tests couldn't run for col = " + col);

		System.out.println("col = " + col + "; result: " + Arrays.toString(resultRows));
		assertArrayEquals(expectedRows, resultRows);
	}

	/**
	 * Next 2 tests are for cases where whole vertical wall/blockade has a one-cell hole;
	 * sprite shouldn't be able to move through that hole
	 * (arrays of positions before and after the move should have the same values);
	 * @param row is a row of cells to check if it block;
	 */
	@ParameterizedTest
	@MethodSource("rowValues")
	void unblockingOneVerticalCellLeftTest(int row){
		int index, expectedCol, cellIndex;
		int[] resultCols = new int[4], expectedCols = new int[4];// 4 neighbours for index of cell;
		resultCols[0] = resultCols[1] = resultCols[2] = resultCols[3] = -1;
		expectedCols[0] = expectedCols[1] = expectedCols[2] = expectedCols[3] = -1;

		index = setVerticalBlockade(-1);
		expectedCol = index*pixelsInCell;
		cellIndex = cellIndexForPosition(expectedCol, row*pixelsInCell);
		if(cellIndex < 0 || cellIndex >= rows*cols)
			return;

		cells[cellIndex].setMapCell(null);
		cells[cellIndex].unblockMovementsAround();
		expectedCol = (index - 2)*pixelsInCell;
		cellIndex = expectedCol + pixelsInCell/2;// + (0, pixelsInCell);

		Cell cell;
		int i;
		for(i = -2; i < 2; i++){
			if(row + i < 0 || row + i >= rows)
				continue;

			cell = cellByPosition(cellIndex, (row + i)*pixelsInCell);
			if(cell == null)
				continue;

			expectedCols[i + 2] = expectedCol;
			resultCols[i + 2] = cell.checkModifyCol(Direction.RIGHT, cellIndex);
		}

		if(expectedCols[0] < 0 && expectedCols[1] < 0 && expectedCols[2] < 0 && expectedCols[3] < 0)
			fail("unblockingOneVerticalCellLeftTest: all tests couldn't run for row = " + row);

		System.out.println("row = " + row + "; result: " + Arrays.toString(resultCols) );
		assertArrayEquals(expectedCols, resultCols);
	}

	@ParameterizedTest
	@MethodSource("rowValues")
	void unblockingOneVerticalCellRightTest(int row){
		int index, expectedCol, cellIndex;
		int[] resultCols = new int[4], expectedCols = new int[4];// 4 neighbours for index of cell;
		resultCols[0] = resultCols[1] = resultCols[2] = resultCols[3] = -1;
		expectedCols[0] = expectedCols[1] = expectedCols[2] = expectedCols[3] = -1;

		index = setVerticalBlockade(-1);
		expectedCol = index*pixelsInCell;
		cellIndex = cellIndexForPosition(expectedCol, row*pixelsInCell);
		if(cellIndex < 0 || cellIndex >= rows*cols)
			return;

		cells[cellIndex].setMapCell(null);
		cells[cellIndex].unblockMovementsAround();
		expectedCol = (index + 1)*pixelsInCell;
		cellIndex = expectedCol - pixelsInCell/2;// - (0, pixelsInCell);

		Cell cell;
		int i;
		for(i = -2; i < 2; i++){
			if(row + i < 0 || row + i >= rows)
				continue;

			cell = cellByPosition(cellIndex, (row + i)*pixelsInCell);
			if(cell == null)
				continue;

			expectedCols[i + 2] = expectedCol;
			resultCols[i + 2] = cell.checkModifyCol(Direction.LEFT, cellIndex);
		}

		if(expectedCols[0] < 0 && expectedCols[1] < 0 && expectedCols[2] < 0 && expectedCols[3] < 0)
			fail("unblockingOneVerticalCellRightTest: all tests couldn't run for row = " + row);

		System.out.println("row = " + row + "; result: " + Arrays.toString(resultCols) );
		assertArrayEquals(expectedCols, resultCols);
	}

	// todo: 2 neighbour cells test series;

	@ParameterizedTest
	@MethodSource("waterEnvelop")
	void unblock1neighbourCellToWater(int col, int row, Direction direction, int rowOrColDifferenceForBrick){
		int waterCellSize = MapCell.WATER.getSize() / MapCell.getUnitSize();

		if(waterCellSize + 2 >= rows || waterCellSize + 2 >= cols){
			fail("unblock1neighbourCellToWater: too small map to test blocking of water.");
		}

		int brickIndex = setWaterBlockade1Brick(col, row, direction, rowOrColDifferenceForBrick);
		if(brickIndex < 0)
			fail("unblock1neighbourCellToWater: bad configuration of map cells!");

		cells[brickIndex].setMapCell(null);
		cells[brickIndex].unblockMovementsAround();

		Cell cell = null;
		int expectedCol, expectedRow, resultRow = -1, resultCol = -1;

		switch(direction){
			case UP:
				cell = cells[brickIndex];
				resultCol = cell.getCol();
				resultRow = cell.checkModifyRow(direction, cell.getRow() - pixelsInCell/2);
				break;
			case LEFT:
				cell = cells[brickIndex];
				resultRow = cell.getRow();
				resultCol = cell.checkModifyCol(direction, cell.getCol() - pixelsInCell/2);
				break;
			case RIGHT:
				cell = cells[brickIndex].getLeftCell();
				resultRow = cell.getRow();
				resultCol = cell.checkModifyCol(direction, cell.getCol() + pixelsInCell/2);
				break;
			case DOWN:
				cell = cells[brickIndex].getUpCell();
				resultCol = cell.getCol();
				resultRow = cell.checkModifyRow(direction, cell.getRow() + pixelsInCell/2);
				break;
		}

		if(cell == null || resultRow == -1){
			fail("unblock1neighbourCellToWater: can not get proper cell!");
			return;
		} else {
			expectedRow = cell.getRow();
			expectedCol = cell.getCol();
		}

		assertEquals(expectedRow, resultRow);
		assertEquals(expectedCol, resultCol);
	}

}
