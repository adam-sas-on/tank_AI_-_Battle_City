package view;

import com.company.view.Cell;
import com.company.view.GameView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {
	private static GameView view = new GameView();
	private static final int rowColCellsView = view.getRowColCells();
	private static final int sizePixels = view.getSizePixels();
	private Cell testCell = new Cell();
	private static Supplier<Arguments> argsSupp;

	@BeforeAll
	private static void buildSupplierOfPositions(){
		argsSupp = new PositionsSeries(rowColCellsView, sizePixels);
	}

	private static Stream<Arguments> getPositions(){
		Stream<Arguments> sArgs = Stream.generate(argsSupp);
		sArgs = sArgs.limit(sizePixels*sizePixels *rowColCellsView*rowColCellsView);
		return sArgs;
	}

	@ParameterizedTest
	@MethodSource("getPositions")
	void testChangeCellPosition(int cellCol, int cellRow, int colExpected, int rowExpected){
		testCell.setPos(cellCol, cellRow);
		view.changeCellPositionToClosest(testCell);

		assertEquals(colExpected, testCell.getCol());
		assertEquals(rowExpected, testCell.getRow());
	}
}