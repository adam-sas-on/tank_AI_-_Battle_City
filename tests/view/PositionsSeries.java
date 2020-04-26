package view;

import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Supplier;

public class PositionsSeries implements Supplier<Arguments> {
	private int[] positions;
	private final int rowColCells;
	private final int sizePixels;

	public PositionsSeries(int rowColCellsView, int sizePixels){
		rowColCells = rowColCellsView;
		this.sizePixels = sizePixels;

		positions = new int[4];// two to set the cell and 2 to expect;
	}

	private void reset(){
		positions[0] = positions[1] = 0;
		positions[2] = positions[3] = 0;
	}

	private void roundPos(int which){
		int rest, cellNum = positions[which]/sizePixels;
		positions[which + 2] = cellNum*sizePixels;
		if(cellNum < rowColCells - 1){
			rest = positions[which] - positions[which + 2];
			if(rest >= sizePixels/2)
				positions[which + 2]+=sizePixels;
		}
	}

	private void next(){
		int pixelsSum = rowColCells*sizePixels;
		if(positions[0] == pixelsSum - 1 && positions[1] == pixelsSum - 1) {
			reset();
			return;
		} else if(positions[0] == pixelsSum - 1){
			positions[0] = 0;
			positions[1]++;
			roundPos(1);
		} else
			positions[0]++;

		roundPos(0);
	}

	@Override
	public Arguments get() {
		next();
		return Arguments.of(positions[0], positions[1], positions[2], positions[3]);
	}

	@Override
	public String toString(){
		String str = "{" + positions[0] + ", " + positions[1] + "} -> (";
		str += positions[2] + ", " + positions[3] + "}";
		return str;
	}
}
