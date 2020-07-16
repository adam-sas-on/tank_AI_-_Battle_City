package com.company.logic;

public class LearningAIClass {
	private BattleRandom rand;
	private TankAI[] tanksAI;
	private int[] allFitness;
	private TankAI processedAI;
	private boolean bestWasChanged;
	private boolean readyToLearn;
	private int indexBest, indexWorst;
	private final double globalMutationRate;
	private final int cellPrecisionUnitSize;

	public LearningAIClass(BattleRandom rand, int cellPrecision){
		globalMutationRate = 0.1;
		bestWasChanged = false;
		readyToLearn = false;
		this.rand = rand;

		processedAI = new TankAI(rand, 2, cellPrecision);

		cellPrecisionUnitSize = cellPrecision;
	}

	public TankAI getCurrentProcessed(){
		return processedAI;
	}

	public TankAI getBestOne(){
		if(tanksAI == null || indexBest < 0 || indexBest >= tanksAI.length)
			return null;

		return tanksAI[indexBest];
	}

	private void setIndexForWorst(){
		int i, worstFitness;

		worstFitness = allFitness[0];
		indexWorst = 0;
		for(i = allFitness.length - 1; i > 0; i--){
			if(allFitness[i] < worstFitness){
				worstFitness = allFitness[i];
				indexWorst = i;
			}
		}

	}

	private int limitedFitnessSum(){
		int i, fitnessSum = 0, size = allFitness.length;//, minFitness = 0, count = 0;

		for(i = 0; i < size; i++){
			if(allFitness[i] > Integer.MIN_VALUE + 1) {
				fitnessSum += allFitness[i];
				/*count++;
				if(allFitness[i] < minFitness)
					minFitness = allFitness[i];*/
			}
		}
		return fitnessSum;
	}

	private int randomSelection(int fitnessSum){
		int i, selectionIndex = -1, size = allFitness.length, stepSum = 0, rnd = 0;

		if(fitnessSum > 0)
			rnd = rand.randRange(0, fitnessSum);
		else if(fitnessSum < 0)
			rnd = rand.randRange(fitnessSum, 0);

		for(i = 0; i < size; i++){
			if(allFitness[i] <= Integer.MIN_VALUE + 1)
				continue;

			stepSum += allFitness[i];
			if(stepSum >= fitnessSum){
				selectionIndex = i;
				break;
			}
		}
		return selectionIndex;
	}

	public void weightedSelection(){
		if(!readyToLearn)
			return;

		int fitnessSum;
		fitnessSum = limitedFitnessSum();

		int firstSelected, secondSelected;
		firstSelected = randomSelection(fitnessSum);
		if(firstSelected < 0)
			firstSelected = indexBest;

		secondSelected = randomSelection(fitnessSum);
		if(secondSelected < 0)
			secondSelected = indexWorst;

		processedAI.mixByOthers(tanksAI[firstSelected], tanksAI[secondSelected]);
		processedAI.mutate(globalMutationRate);
		processedAI.resetFitness();
	}

	public void updateAI(){
		if(!readyToLearn)
			return;

		int newFitness = processedAI.getSetFitness(0);

		if(newFitness > allFitness[indexWorst]){
			tanksAI[indexWorst].setByOther(processedAI);
			allFitness[indexWorst] = newFitness;

			int i, fitness = tanksAI[indexBest].getSetFitness(0);
			if(newFitness > fitness){// new is better than best;
				indexBest = indexWorst;
				bestWasChanged = true;
			}

			setIndexForWorst();
		}
	}


	public void setDefaultLearningPopulation(){
		int count = 100, i;

		// processedAI = new TankAI(rand, 2, cellPrecisionUnitSize);
		boolean success = processedAI.readFile();

		tanksAI = new TankAI[count];
		allFitness = new int[count];
		indexBest = 0;

		try {
			for(i = 0; i < count; i++){
				tanksAI[i] = new TankAI(rand, 2, cellPrecisionUnitSize);
			}

			if(success) {
				success = tanksAI[0].resetByOtherNN(processedAI);
				if(!success)
					tanksAI[0].setDefaultNeuralNetwork();
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
