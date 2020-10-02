package com.company.movement;

import java.io.*;

public class LearningAIClass {
	private BattleRandom rand;
	private TankAI[] tanksAI;
	private int[] allFitness;
	private TankAI processedAI;
	private double[] bufferedInputData;
	private int[] countInputs;
	private boolean bestWasChanged;
	private boolean readyToLearn, wasUpdated;
	private int indexBest, indexWorst;
	private int mapMaxCols, mapMaxRows;
	private int maxEnemyTanks, maxBullets;
	private static int countUpdates;
	private final double globalMutationRate;
	private final int cellPrecisionUnitSize;

	public LearningAIClass(BattleRandom rand, int cellPrecision){
		globalMutationRate = 0.1;// 10%;
		bestWasChanged = false;
		readyToLearn = false;
		wasUpdated = false;
		countUpdates = 0;
		this.rand = rand;

		mapMaxCols = mapMaxRows = 50;
		maxEnemyTanks = 20;
		maxBullets = maxEnemyTanks*2 + 8;// 8 for players;

		processedAI = new TankAI(rand, cellPrecision);
		int inputSize = processedAI.necessaryInputSize(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets);
		bufferedInputData = new double[inputSize];

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

	public boolean wasMLUpdated(){
		return wasUpdated;
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
		int size = allFitness.length, stepSum = 0, rnd = 0;

		if(fitnessSum > 0)
			rnd = rand.randRange(0, fitnessSum);
		else if(fitnessSum < 0)
			rnd = rand.randRange(0, -fitnessSum);

		int i, mini, selectionIndex = -1;
		mini = allFitness[indexWorst];
		rnd -= mini;

		for(i = 0; i < size; i++){
			if(allFitness[i] <= Integer.MIN_VALUE + 1)
				continue;

			stepSum += allFitness[i] - mini;
			if(stepSum >= rnd){
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

		countUpdates++;
		System.out.println("\t" + countUpdates + ")  Updating series of AIs, fitness = " + newFitness);

		if(newFitness > allFitness[indexWorst]){
			tanksAI[indexWorst].setByOther(processedAI);
			allFitness[indexWorst] = newFitness;

			int fitness = tanksAI[indexBest].getSetFitness(0);
			if(newFitness > fitness){// new is better than best;
				indexBest = indexWorst;
				bestWasChanged = true;
			}

			setIndexForWorst();
			wasUpdated = true;
		}
	}


	public void setDefaultLearningPopulation(){
		int count = 105, i;

		// processedAI = new TankAI(rand, 2, cellPrecisionUnitSize);
		boolean success = processedAI.readFile();
		if(!success){
			System.out.println("Reading processed AI file failed -> creating new one...");
			processedAI.setDefaultNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets);
		}

		tanksAI = new TankAI[count];
		allFitness = new int[count];
		indexBest = 0;

		countInputs = new int[]{35, 50, 30};

		int inputSize = processedAI.necessaryInputSize(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets);
		bufferedInputData = new double[inputSize];

		int fitness = Integer.MIN_VALUE + 1;

		try {
			for(i = 0; i < count; i++){
				tanksAI[i] = new TankAI(rand, cellPrecisionUnitSize);
			}

			if(success) {
				success = tanksAI[0].resetByOtherNN(processedAI);
				if(!success) {
					tanksAI[0].setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);
					tanksAI[0].getSetFitness(fitness);
				}
				indexBest = 0;
			} else {
				tanksAI[0].setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);
				tanksAI[0].getSetFitness(fitness);
				processedAI.setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);
			}

			for(i = 1; i < count; i++){
				allFitness[i] = Integer.MIN_VALUE;
				tanksAI[i].setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);
				tanksAI[i].getSetFitness(fitness);
			}

			indexWorst = 1;
		} catch(OutOfMemoryError e){
			System.out.println("Can't create a population to learn,  " + e);
			readyToLearn = false;
			return;
		}

		readyToLearn = true;
		writeFile();
		processedAI.writeFile();
	}


	private boolean readNeuralNetworks(DataInputStream dIs, int countAIs, String fileName){
		if(countInputs == null)
			return false;

		double[] layer;
		final int layersCount = countInputs.length + 1;// 2 output values;
		boolean done;

		System.out.println("\tReading series of neural networks.");

		try {
			allFitness = new int[countAIs];
			tanksAI = new TankAI[countAIs];
			indexBest = indexWorst = 0;

			int i = 0, j, k, size;
			for(; i < countAIs; i++){
				allFitness[i] = dIs.readInt();
				if(countUpdates < 2 || (allFitness[i] > -5000 && countUpdates <= countAIs) )// make lowest possible fitness at the beginning of learning;
					allFitness[i] = Integer.MIN_VALUE + 1;

				if(allFitness[i] > allFitness[indexBest])
					indexBest = i;
				else if(allFitness[i] < allFitness[indexWorst])
					indexWorst = i;


				tanksAI[i] = new TankAI(rand, cellPrecisionUnitSize);
				tanksAI[i].setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);

				for(j = 0; j < layersCount; j++){
					layer = tanksAI[i].getLayerByIndex(j);
					if(layer == null){
						dIs.readDouble();
						continue;
					}

					size = layer.length;
					for(k = 0; k < size; k++){
						layer[k] = dIs.readDouble();
					}
				}
			}

			processedAI = new TankAI(rand, cellPrecisionUnitSize);
			done = processedAI.readFile();
			if(!done)
				processedAI.setNeuralNetwork(mapMaxCols, mapMaxRows, maxEnemyTanks, maxBullets, countInputs, bufferedInputData);

			System.out.println("\rAfter reading ML; best fitness = " + allFitness[indexBest]);
			System.out.println("\t\tworst fitness = " + allFitness[indexWorst]);

			done = true;
		} catch(OutOfMemoryError e){
			System.out.println("Can't read Machine Learning file,  " + e);
			done = false;
		} catch(IOException | NullPointerException e){
			System.out.println("Reading Machine learning file  " + fileName + " failed!");
			//System.err.println("Reading Machine learning file  " + fileName + " failed!");
			done = false;
		}

		return done;
	}

	public boolean readFile(){
		processedAI.readFile();
		return readFile("tanks_ml_ai.bin");
	}
	public boolean readFile(String fileName){
		readyToLearn = false;

		try {
			InputStream is = LearningAIClass.class.getResourceAsStream("/resources/ai_resources/" + fileName);
			DataInputStream dIs = new DataInputStream(is);

			String fileHeader = "", correct = "BC_ML_tanks_ai";
			StringBuilder sb = new StringBuilder(15);
			int i;
			for(i = correct.length(); i > 0; i--){
				sb.append( dIs.readChar() );
			}
			fileHeader = sb.toString();
			if(!fileHeader.equalsIgnoreCase(correct) ){
				System.out.println("Wrong file format of  " + fileName);
				return false;
			}

			dIs.skipBytes(16 - correct.length() );

			int inputSize = dIs.readInt();
			if (inputSize < 2){// minimum 2 inputs;
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}
			bufferedInputData = new double[inputSize];

			mapMaxCols = dIs.readInt();
			mapMaxRows = dIs.readInt();
			maxEnemyTanks = dIs.readInt();
			maxBullets = dIs.readInt();
			bufferedInputData[0] = dIs.readDouble();// to skip data;
			countUpdates = dIs.readInt();
			System.out.println("This network was learned  " + countUpdates + "  times.");

			i = mapMaxCols*mapMaxRows;
			inputSize = dIs.readInt();
			if(inputSize < 2 || i < 4){
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}

			inputSize--;
			countInputs = new int[inputSize];
			for(i = 0; i < inputSize; i++){
				countInputs[i] = dIs.readInt();
			}
			i = dIs.readInt();//to skip bec. = 2;

			int nnCounts = dIs.readInt();

			if(nnCounts < 3){// should be minimum 3 AIs to perform learning!
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}

			readyToLearn = readNeuralNetworks(dIs, nnCounts, fileName);
		} catch(IOException | NullPointerException e){
			System.out.println("Reading AI file  " + fileName + "  failed!");
			readyToLearn = false;
		}

		return readyToLearn;
	}

	public void writeFile(){
		writeFile("tanks_ml_ai.bin");
	}
	public void writeFile(String fileName){
		if(!readyToLearn){
			System.out.println("Machine learning class not saved!");
			return;
		}

		if(bestWasChanged)
			tanksAI[indexBest].writeFile();

		DataOutputStream dOs = null;
		File file;
		OutputStream os = null;

		try {
			String fPath = LearningAIClass.class.getResource("/resources/").getFile();
			file = new File(fPath + "ai_resources/" + fileName);

			os = new FileOutputStream(file);
			dOs = new DataOutputStream(new BufferedOutputStream(os));

			if(!file.exists() )
				file.createNewFile();

			//oos = new ObjectOutputStream(os);
			//oos.writeObject(layers[0]);
			String fileHeader = "BC_ML_tanks_ai";
			dOs.writeChars(fileHeader);
			int inputSize = 16 - fileHeader.length();

			dOs.write(new byte[inputSize]);

			inputSize = bufferedInputData.length;
			dOs.writeInt(inputSize);
			dOs.writeInt(mapMaxCols);
			dOs.writeInt(mapMaxRows);
			dOs.writeInt(maxEnemyTanks);
			dOs.writeInt(maxBullets);
			dOs.writeDouble(globalMutationRate);
			dOs.writeInt(countUpdates);// number of updates;

			inputSize = countInputs.length;
			dOs.writeInt(inputSize + 1);

			int i = 0;
			for(; i < inputSize; i++){
				dOs.writeInt(countInputs[i]);
			}
			dOs.writeInt(2);// 2 outputs of neural network;

			final int layersCount = countInputs.length + 1, nnCounts = tanksAI.length;
			double[] layer;
			int j, k, size;

			System.out.println("\tWriting series of neural networks.");

			dOs.writeInt(nnCounts);

			for(i = 0; i < nnCounts; i++){
				dOs.writeInt(allFitness[i]);

				for(j = 0; j < layersCount; j++){// write neural network weights;
					layer = tanksAI[i].getLayerByIndex(j);
					if(layer == null){
						dOs.writeDouble(0.0);
						continue;
					}

					size = layer.length;
					for(k = 0; k < size; k++){
						dOs.writeDouble(layer[k]);
					}
				}
			}

			// dOs.flush();
		} catch(FileNotFoundException e){
			System.out.println("Creating/writing Machine Learning file  " + fileName  + "  failed!");
		} catch(IOException e){
			System.out.println("Writing to Machine Learning file  " + fileName  + "  failed!");
		} finally {
			if(dOs != null){
				try {
					dOs.flush();
					dOs.close();
				} catch(IOException e){
					System.out.println("Error closing data-stream! " + e);}
			}
			if(os != null){
				try {
					os.flush();
					os.close();
				}catch(IOException e){
					System.out.println("Error closing stream (ML)! " + e);
				}
			}
		}
	}

}
