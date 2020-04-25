package view;

import com.company.view.Cell;
import com.company.view.GameView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {
	private GameView view = new GameView();
	private final int rowColCellsView = view.getRowColCells();
	private final int sizePixels = view.getSizePixels();
	private Cell testCell = new Cell();

	@Test
	void testChangeCellPosition(){
		testCell.setPos(1, 1);
		view.changeCellPositionToClosest(testCell);

		assertEquals(0, testCell.getCol());
		assertEquals(0, testCell.getRow());
	}
}