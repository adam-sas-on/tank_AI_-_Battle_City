package view;

import com.company.view.Cell;
import com.company.view.MapCell;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

	private Cell cellByPosition(int xPos, int yPos){
		int col = xPos/pixelsInCell, row = yPos/pixelsInCell;
		if(col < 0 || row < 0 || row >= rows || col >= cols)
			return null;

		int cellIndex = row*cols + col;
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

		resultRow = rowCell.checkModifyRow(KeyCode.DOWN, i);
		resultCol = rowCell.checkModifyCol(KeyCode.DOWN, expectedCol);

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

		resultRow = rowCell.checkModifyRow(KeyCode.UP, i);
		resultCol = rowCell.checkModifyCol(KeyCode.UP, expectedCol);

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

		resultRow = colCell.checkModifyRow(KeyCode.RIGHT, expectedRow);
		resultCol = colCell.checkModifyCol(KeyCode.RIGHT, i);

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

		resultRow = colCell.checkModifyRow(KeyCode.LEFT, expectedRow);
		resultCol = colCell.checkModifyCol(KeyCode.LEFT, i);

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
			resultRow = rowCell.checkModifyRow(KeyCode.DOWN, i);
			resultCol = rowCell.checkModifyCol(KeyCode.DOWN, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		rowCell = cellByPosition(col*pixelsInCell, i);
		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(KeyCode.DOWN, i);
			resultCol = rowCell.checkModifyCol(KeyCode.DOWN, expectedCol);

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
			resultRow = rowCell.checkModifyRow(KeyCode.UP, i);
			resultCol = rowCell.checkModifyCol(KeyCode.UP, expectedCol);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		rowCell = cellByPosition(col*pixelsInCell, i);
		if(rowCell != null){
			expectedCol = rowCell.getCol();
			resultRow = rowCell.checkModifyRow(KeyCode.UP, i);
			resultCol = rowCell.checkModifyCol(KeyCode.UP, expectedCol);

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
			resultRow = colCell.checkModifyRow(KeyCode.RIGHT, expectedRow);
			resultCol = colCell.checkModifyCol(KeyCode.RIGHT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		colCell = cellByPosition(i, row*pixelsInCell);
		if(colCell != null){
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(KeyCode.RIGHT, expectedRow);
			resultCol = colCell.checkModifyCol(KeyCode.RIGHT, i);

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
			resultRow = colCell.checkModifyRow(KeyCode.LEFT, expectedRow);
			resultCol = colCell.checkModifyCol(KeyCode.LEFT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		colCell = cellByPosition(i, row*pixelsInCell);
		if(colCell != null){
			expectedRow = colCell.getRow();
			resultRow = colCell.checkModifyRow(KeyCode.LEFT, expectedRow);
			resultCol = colCell.checkModifyCol(KeyCode.LEFT, i);

			assertEquals(expectedRow, resultRow);
			assertEquals(expectedCol, resultCol);
		} else
			failCount++;

		if(failCount > 1){
			fail("blockMovementsVerticalCellOpenRightTest: can not run the test!");
		}
	}

}
