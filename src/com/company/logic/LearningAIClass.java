package com.company.logic;

public class LearningAIClass {
	private BattleRandom rand;
	private TankAI[] tanksAI;
	private int[] allFitness;
	private TankAI processedAI;
	private boolean bestWasChanged;
	private int indexBest, indexWorst, currentIndex;
	private final double globalMutationRate;

	public LearningAIClass(){
		globalMutationRate = 0.1;
		bestWasChanged = false;
	}

	public TankAI getCurrentProcessed(){
		return processedAI;
	}

	private void weightedSelection(){
		// todo: sum all allFitness[], get 2 random values in range and select two AIs;
	}

	public void setDefaultLearningPopulation(){

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

	}

}
