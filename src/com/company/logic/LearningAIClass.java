package com.company.logic;

public class LearningAIClass {
	private BattleRandom rand;
	private TankAI[] tanksAI;
	private int[] allFitness;
	private TankAI processedAI;
	private boolean bestWasChanged;
	private boolean readyToLearn;
	private int indexBest, indexWorst, currentIndex;
	private final double globalMutationRate;
	private final int cellPrecisionUnitSize;

	public LearningAIClass(BattleRandom rand, int cellPrecision){
		globalMutationRate = 0.1;
		bestWasChanged = false;
		readyToLearn = false;
		this.rand = rand;

		cellPrecisionUnitSize = cellPrecision;
	}

	public TankAI getCurrentProcessed(){
		return processedAI;
	}

	public TankAI getBestOne(){
		if(indexBest < 0 || indexBest >= tanksAI.length)
			return null;

		return tanksAI[indexBest];
	}

	private void weightedSelection(){
		// todo: sum all allFitness[], get 2 random values in range and select two AIs;
	}

	public void setDefaultLearningPopulation(){
		int count = 100, i;

		processedAI = new TankAI(rand, 2, cellPrecisionUnitSize);
		boolean success = processedAI.readFile();

		tanksAI = new TankAI[count];
		allFitness = new int[count];
		indexBest = -1;

		try {
			for(i = 0; i < count; i++){
				tanksAI[i] = new TankAI(rand, 2, cellPrecisionUnitSize);
			}

			if(success) {
				tanksAI[0].resetByOtherNN(processedAI);
				indexBest = 0;
			} else {
				tanksAI[0].setDefaultNeuralNetwork();
				processedAI.setDefaultNeuralNetwork();
			}

			for(i = 1; i < count; i++){
				allFitness[i] = Integer.MIN_VALUE;
				tanksAI[i].setDefaultNeuralNetwork();
			}

			indexWorst = 1;
		} catch(OutOfMemoryError e){
			System.out.println("Can't create a population to learn,  " + e);
			readyToLearn = false;
			return;
		}

		readyToLearn = true;
	}


	public boolean readFile(){
		return readFile("tanks_ml_ai.bin");
	}
	public boolean readFile(String filename){
		return false;
	}

	public void writeFile(){
		writeFile("tanks_ml_ai.bin");
	}
	public void writeFile(String fileName){
		if(bestWasChanged)
			tanksAI[indexBest].writeFile();

	}

}
