package movement;

import com.company.movement.BattleRandom;
import com.company.movement.TankAI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TankAITest {
	private static final int cellPrecisionUnitSize = 10000;
	private static BattleRandom rand = new BattleRandom(cellPrecisionUnitSize);
	private static TankAI testedAI = new TankAI(rand, 2, cellPrecisionUnitSize);

	@Test
	void resetByOtherNNTest(){
		TankAI otherAI;
		double[] inputVector;
		int enemyTanksCount = 30, necessarySize;

		necessarySize = testedAI.necessaryInputSize(30, 30, enemyTanksCount, enemyTanksCount*3);
		inputVector = new double[necessarySize];
		for(int i = 0; i < necessarySize; i++)
			inputVector[i] = rand.symmetricGaussianRand(2.0);

		testedAI.setNeuralNetwork(30, 30, enemyTanksCount, enemyTanksCount*3, null, inputVector);

		otherAI = new TankAI(rand, 2, cellPrecisionUnitSize);
		otherAI.resetByOtherNN(testedAI);

		double[] originalAI_output, copiedAI_output;

		originalAI_output = testedAI.getOutput();
		copiedAI_output = otherAI.getOutput();

		assertArrayEquals(originalAI_output, copiedAI_output);
	}

	@Test
	void setByOtherTest(){

	}
}