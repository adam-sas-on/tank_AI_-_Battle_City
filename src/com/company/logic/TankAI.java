package com.company.logic;

import com.company.model.Bullet;
import com.company.model.Enemy;
import com.company.model.PlayerAITank;
import com.company.view.Cell;
import com.company.view.MapCell;

import java.io.*;
import java.util.Arrays;

public class TankAI {
	private BattleRandom rand;
	private double[][] layers;
	private double[] inputData;
	private double[] output;
	private double[] bufferedOutput;// its size has to be the sum of 2 maximum lengths of layers;
	private int mapMaxCols, mapMaxRows;
	private int maxNeurons;
	private int netFitness;
	private int bulletsFirstIndex;
	private int[] ownerXY_pos;
	private boolean ready;
	private boolean updateOutput;
	private final int cellPrecisionUnitSize;
	private final int eagleCollectibleTankInputSize;

	public TankAI(BattleRandom rand, int outputSize, int cellPrecision){
		output = new double[outputSize];
		ready = false;
		updateOutput = true;
		ownerXY_pos = new int[2];
		maxNeurons = 0;
		netFitness = 0;

		mapMaxRows = mapMaxCols = 26;// Battle City default map size;

		this.rand = rand;
		cellPrecisionUnitSize = cellPrecision;
		eagleCollectibleTankInputSize = 8;// 8 for eagle, collectible  and 4  for controlled tank;
	}


	private void setDefaultTriple(int inputIndex){
		inputData[inputIndex + 1] = inputData[inputIndex + 2] = -1.0;
		inputData[inputIndex] = -3.0*Math.PI;// anything less then -2*PI;
	}

	private void setTripleByCell(Cell cell, int netIndex){
		double dx, dy;
		dx = (cell.getCol() - ownerXY_pos[0])/(double)cellPrecisionUnitSize;
		dy = (cell.getRow() - ownerXY_pos[1])/(double)cellPrecisionUnitSize;

		inputData[netIndex] = Math.atan2(dy, dx);
		inputData[netIndex + 1] = Math.hypot(dx, dy);
		inputData[netIndex + 2] = cell.getMapCell().getCellCode();
	}

	/**
	 * Product of 2 sub-arrays/vectors.
	 * @param v first sub-array v[..., vIndexBegin, vIndexBegin + 1, ..., vIndexBegin + productSize, ...];
	 * @param w second sub-array w[..., wIndexBegin, wIndexBegin + 1, ..., wIndexBegin + productSize, ...];
	 * @param vIndexBegin first index of vector v to start multiplication;
	 * @param wIndexBegin first index of vector w to start multiplication
	 * @param productSize length of sub-arrays to multiply;
	 * @return the dot product of sub-arrays/vectors;
	 */
	private double productVector(double[] v, double[] w, int vIndexBegin, int wIndexBegin, final int productSize){
		final int wLastIndex = wIndexBegin + productSize;
		if(vIndexBegin + productSize > v.length || wLastIndex > w.length)
			throw new IndexOutOfBoundsException("Wrong sizes of vectors in vector multiplication!");

		double product = 0.0;
		int i = vIndexBegin, j = wIndexBegin;

		for(; j < wLastIndex; j++, i++){
			product += v[i]*w[j];
		}

		return product;
	}

	private double activationFunction(double x){
		double f = -Math.PI;
		f += Math.PI*3/(1.0 + Math.exp(-x));// make domain of a function to be from -PI to 2*PI;
		return f;
	}

	public int getSetFitness(int fitnessStep){
		netFitness += fitnessStep;
		return netFitness;
	}

	public boolean isAIReady(){
		return ready;
	}

	public double[] getOutput(){
		if(!ready)
			return null;

		if(!updateOutput)
			return output;

		double product;
		final int layersCount = layers.length;
		int i, rowIndexBegin, rowsCount;

		rowsCount = layers[0].length/(inputData.length + 1);// number of neurons in first layer;
		rowIndexBegin = 0;
		for(i = 0; i < rowsCount; i++){// i * inputData.length
			product = productVector(layers[0], inputData, rowIndexBegin, 0, inputData.length);
			rowIndexBegin += inputData.length;
			product += layers[0][rowIndexBegin];// bias;
			rowIndexBegin++;

			bufferedOutput[i] = activationFunction(product);
		}

		int n, inputIndex = 0, outputIndex = maxNeurons, currentInputDataLength;

		for(n = 1; n < layersCount; n++){
			currentInputDataLength = rowsCount;
			rowsCount = layers[n].length/(currentInputDataLength + 1);
			rowIndexBegin = 0;

			for(i = 0; i < rowsCount; i++){
				product = productVector(layers[n], bufferedOutput, rowIndexBegin, inputIndex, currentInputDataLength);
				rowIndexBegin += currentInputDataLength;
				product += layers[n][rowIndexBegin];// bias;
				rowIndexBegin++;

				bufferedOutput[outputIndex + i] = activationFunction(product);
			}

			i = inputIndex;
			inputIndex = outputIndex;
			outputIndex = i;
		}

		n = Math.min(inputIndex + maxNeurons, bufferedOutput.length);
		for(i = 0; inputIndex < n && i < output.length; i++, inputIndex++)
			output[i] = bufferedOutput[inputIndex];

		updateOutput = false;
		return output;
	}

	public double[] getLayerByIndex(int index){
		if(index < 0 || index >= layers.length)
			return null;
		return layers[index];
	}

	public void setDefaultNeuralNetwork(int mapMaxCols, int mapMaxRows, int maxEnemyTanks, int maxBullets){
		ready = false;
		if(mapMaxCols == 0 && mapMaxRows == 0 && maxEnemyTanks == 0 && maxBullets == 0)
			return;

		int inputSize;
		inputSize = mapMaxCols*mapMaxRows + eagleCollectibleTankInputSize;
		inputSize += (maxEnemyTanks + 1)*3;// 3: enemy tanks angle to owner, distance to owner  and  cell-code (+ ally tank);
		bulletsFirstIndex = inputSize;
		inputSize += maxBullets*3;// 3: bullets angle to owner, distance to owner  and  direction on map;

		try {
			inputData = new double[inputSize];

			int layerSize, numberOfWeights, i;
			final double range = 1.0;
			layers = new double[4][];

			layerSize = 30;
			numberOfWeights = layerSize * (inputSize + 1);// +1: bias;
			layers[0] = new double[numberOfWeights];
			for(i = 0; i < numberOfWeights; i++)
				layers[0][i] = rand.symmetricRandRange(range);

			inputSize = layerSize;
			layerSize = maxNeurons = 100;
			numberOfWeights = layerSize*(inputSize + 1);
			layers[1] = new double[numberOfWeights];
			for(i = 0; i < numberOfWeights; i++)
				layers[1][i] = rand.symmetricRandRange(range) - 0.2;

			inputSize = layerSize;
			layerSize = 40;
			numberOfWeights = layerSize*(inputSize + 1);
			layers[2] = new double[numberOfWeights];
			for(i = 0; i < numberOfWeights; i++)
				layers[2][i] = rand.symmetricRandRange(range);

			inputSize = layerSize;
			layerSize = 2;
			output = new double[layerSize];// 2 outputs: current angle of tank and shoot power;
			numberOfWeights = layerSize*(inputSize + 1);
			layers[3] = new double[numberOfWeights];
			for(i = 0; i < numberOfWeights; i++)
				layers[3][i] = rand.symmetricRandRange(range);

			bufferedOutput = new double[maxNeurons*2];
			ready = true;
		} catch(OutOfMemoryError e){
			System.out.println("AI network can not be created for players tank! " + e);
			ready = false;
		}

		netFitness = 0;
	}

	public void setDefaultNeuralNetwork(){
		int defaultEnemyTanks = 20, defaultBullets;
		defaultBullets = defaultEnemyTanks*2 + 8;// 8 for players;
		setDefaultNeuralNetwork(50, 50, defaultEnemyTanks, defaultBullets);
	}

	public boolean resetByOtherNN(TankAI otherAI){
		if(!otherAI.ready){
			ready = false;
			return false;
		}

		netFitness = 0;
		updateOutput = otherAI.updateOutput;
		mapMaxCols = otherAI.mapMaxCols;
		mapMaxRows = otherAI.mapMaxRows;

		int i, layersCount = otherAI.layers.length, count;

		try {
			layers = new double[layersCount][];
			for (i = 0; i < layersCount; i++) {
				count = otherAI.layers[i].length;
				layers[i] = new double[count];
				System.arraycopy(otherAI.layers[i], 0, layers[i], 0, count);
			}

			count = otherAI.inputData.length;
			inputData = new double[count];
			System.arraycopy(otherAI.inputData, 0, inputData, 0, count);
			bulletsFirstIndex = otherAI.bulletsFirstIndex;


			maxNeurons = otherAI.maxNeurons;
			if (bufferedOutput == null)
				bufferedOutput = new double[maxNeurons * 2];
			else if (bufferedOutput.length != maxNeurons * 2)
				bufferedOutput = new double[maxNeurons * 2];


			if (output == null)
				output = new double[otherAI.output.length];
			else if (output.length != otherAI.output.length)
				output = new double[otherAI.output.length];

			if (!updateOutput)
				System.arraycopy(otherAI.output, 0, output, 0, output.length);

			ready = true;
		} catch(OutOfMemoryError e){
			System.out.println("Couldn't recreate AI network from another one! " + e);
			ready = false;
		}

		return ready;
	}

	public void setByOther(TankAI otherAI){
		netFitness = otherAI.getSetFitness(0);
		if(!ready)
			return;

		double[] othersLayer;
		int i, layersCount = layers.length, otherLayersCount = 0, sizeThis, otherSize;

		if(otherAI.layers != null)
			otherLayersCount = otherAI.layers.length;

		if(layersCount > otherLayersCount)
			layersCount = otherLayersCount;

		for(i = 0; i < layersCount; i++){
			othersLayer = otherAI.layers[i];
			if(othersLayer == null)
				continue;

			sizeThis = layers[i].length;
			otherSize = othersLayer.length;
			if(sizeThis > otherSize)
				sizeThis = otherSize;

			System.arraycopy(otherAI.layers[i], 0, layers[i], 0, sizeThis);
		}
	}

	public void mutate(double mutationRate){
		double randVal;
		int i, j, size, layersCount = layers.length;

		for(i = 0; i < layersCount; i++){
			size = layers[i].length;
			for(j = 0; j < size; j++){
				randVal = rand.randRange(1.0);
				if(randVal < mutationRate){
					layers[i][j] = rand.symmetricRandRange(5.0);
				}

			}
		}
	}

	/**
	 * For all layers:
	 * get random int in range [1, layers[i].length);
	 * values before random int - set from parentAI_1,
	 * after that int - set by parentAI_2;
	 *
	 * @param parentAI_1 1st neural network source;
	 * @param parentAI_2 2nd neural network source to mix;
	 */
	public void mixByOthers(TankAI parentAI_1, TankAI parentAI_2){
		if(layers == null || parentAI_1.layers == null || parentAI_2.layers == null)
			return;

		int layersCount = Math.min(layers.length, parentAI_1.layers.length);
		layersCount = Math.min(layersCount, parentAI_2.layers.length);
		if(layersCount < 1)
			return;

		int i, minSize, randIndex, sizeThis;
		for(i = 0; i < layersCount; i++){
			sizeThis = layers[i].length;
			minSize = Math.min(parentAI_1.layers[i].length, parentAI_2.layers[i].length);
			if(sizeThis > minSize)
				sizeThis = minSize;

			randIndex = rand.randRange(0, sizeThis);
			System.arraycopy(parentAI_1.layers[i], 0, layers[i], 0, randIndex);
			if(sizeThis > randIndex)
				System.arraycopy(parentAI_2.layers[i], randIndex, layers[i], randIndex, sizeThis - randIndex);
		}

		netFitness = 0;
	}

	/**
	 * Changes AI networks input data according to map situation;
	 *
	 * @param cells array of map cells sorted as ordinary image pixels;
	 * @param mapCols number of cols currently used on map;
	 * @param mapRows number of rows currently used on map;
	 * @param maxCols maximum number of columns the map cells can handle;
	 */
	public void updateMapState(Cell[] cells, int mapCols, int mapRows, int maxCols){
		if(!ready)
			return;

		MapCell mapCell;
		int i, limit = mapRows*mapCols, mapIndex, columnIndex;
		int nNetLimit, nNetIndex = 0;
		nNetLimit = mapMaxCols*mapMaxRows;// every group of mapMaxCols inputs is for defined map row;

		Arrays.fill(inputData, 0, nNetLimit - 1, 0.0);

		for(i = 0; i < limit && nNetIndex < nNetLimit; i++){
			columnIndex = i/mapCols;// rowIndex;
			nNetIndex = columnIndex*mapMaxCols;

			columnIndex = i - columnIndex*mapCols;
			nNetIndex = nNetIndex*mapMaxCols + columnIndex;
			if(columnIndex >= mapMaxCols || nNetIndex >= nNetLimit)
				continue;

			mapIndex = i/mapCols*(maxCols - mapCols) + i;// index + remaining cols;

			mapCell = cells[mapIndex].getMapCell();
			if(mapCell != null)
				inputData[nNetIndex] = mapCell.getCellCode();
		}
		updateOutput = true;
	}

	public void updateOwnerState(int ownerX_pos, int ownerY_pos, MapCell ownerMapCell, int lifes, double immortalSecs, double freezeSecs){
		if(!ready)
			return;

		int nNetIndex = mapMaxCols*mapMaxRows + 4;// eagle and collectibles;
		ownerXY_pos[0] = ownerX_pos;
		ownerXY_pos[1] = ownerY_pos;
		inputData[nNetIndex] = ownerMapCell.getCellCode();
		inputData[nNetIndex + 1] = lifes/10.0;// simple normalize;
		inputData[nNetIndex + 2] = immortalSecs;
		inputData[nNetIndex + 3] = freezeSecs;

		updateOutput = true;
	}

	public void updateEagleAndCollectibleState(Cell eagleCell, Cell collectible){
		if(!ready)
			return;

		int nNetIndex = mapMaxCols*mapMaxRows, eagleX, eagleY;
		double dx, dy;

		if(eagleCell != null){
			eagleX = eagleCell.getCol();
			eagleY = eagleCell.getRow();
			dx = (eagleX - ownerXY_pos[0])/(double)cellPrecisionUnitSize;
			dy = (eagleY - ownerXY_pos[1])/(double)cellPrecisionUnitSize;
			inputData[nNetIndex] = Math.atan2(dy, dx);
			inputData[nNetIndex + 1] = Math.hypot(dx, dy);// = sqrt(dx*dx + dy*dy);

			updateOutput = true;
		} else {
			inputData[nNetIndex] = -1.0;
			inputData[nNetIndex + 1] = -3.0*Math.PI;// anything less then -2*PI;
		}

		if(collectible == null){
			return;
		}

		nNetIndex += 2;
		inputData[nNetIndex] = -1.0;
		inputData[nNetIndex + 1] = -3.0*Math.PI;// anything less then -2*PI;

		if(collectible.getMapCell() != null){
			dx = (collectible.getCol() - ownerXY_pos[0])/(double)cellPrecisionUnitSize;
			dy = (collectible.getRow() - ownerXY_pos[1])/(double)cellPrecisionUnitSize;
			inputData[nNetIndex] = Math.atan2(dy, dx);
			inputData[nNetIndex + 1] = Math.hypot(dx, dy);// = sqrt(dx*dx + dy*dy);
		}

		updateOutput = true;
	}

	public void updateTanksState(Enemy[] tanks, int activeTanksCount, PlayerAITank allyTank){
		if(!ready)
			return;

		Cell cell = new Cell();
		// MapCell mapCell;
		int i, nNetIndex = mapMaxCols*mapMaxRows + eagleCollectibleTankInputSize;

		setDefaultTriple(nNetIndex);

		if(allyTank != null){
			allyTank.setUpCell(cell);
			setTripleByCell(cell, nNetIndex);
		}

		final int inputSize = bulletsFirstIndex;
		nNetIndex += 3;

		for(i = 0; i < activeTanksCount && nNetIndex < inputSize; i++){
			setDefaultTriple(nNetIndex);
			if(tanks[i] == null){
				nNetIndex += 3;
				continue;
			}

			tanks[i].setUpCell(cell);
			setTripleByCell(cell, nNetIndex);
			nNetIndex += 3;
		}

		while(nNetIndex < bulletsFirstIndex){
			setDefaultTriple(nNetIndex);
			nNetIndex += 3;
		}

		updateOutput = true;
	}

	public void updateBulletsState(Bullet[] bullets, int activeBulletsCount){
		if(!ready)
			return;

		int[] xyPos = new int[2];
		double dx, dy;
		int i, nNetIndex = bulletsFirstIndex, nNetLimit = inputData.length;

		for(i = 0; i < activeBulletsCount && nNetIndex < nNetLimit; i++){
			setDefaultTriple(nNetIndex);

			bullets[i].getBulletPos(xyPos);
			dx = (xyPos[0] - ownerXY_pos[0])/(double)cellPrecisionUnitSize;
			dy = (xyPos[1] - ownerXY_pos[1])/(double)cellPrecisionUnitSize;
			inputData[nNetIndex] = Math.atan2(dy, dx);
			inputData[nNetIndex + 1] = Math.hypot(dx, dy);
			inputData[nNetIndex + 2] = bullets[i].getDirectionInRadians();
			nNetIndex += 3;
		}

		for(; nNetIndex < nNetLimit; nNetIndex += 3){
			setDefaultTriple(nNetIndex);
		}

		updateOutput = true;
	}


	public boolean readFile(){
		return readFile("tank_ai.bin");
	}
	public boolean readFile(String fileName){
		ready = false;

		try {
			InputStream is = TankAI.class.getResourceAsStream("/resources/ai_resources/" + fileName);
			DataInputStream dIs = new DataInputStream(is);

			String fileHeader, correct = "tank_ai";
			StringBuilder sb = new StringBuilder(7);
			int i;
			for (i = correct.length(); i > 0; i--) {
				sb.append( dIs.readChar() );
			}
			fileHeader = sb.toString();
			if (!fileHeader.equalsIgnoreCase(correct)) {
				System.out.println("Wrong file format of  " + fileName);
				return false;
			}

			dIs.skipBytes(16 - correct.length());

			int inputSize = dIs.readInt(), numberOfWeights;

			if (inputSize < 2){// minimum 2 inputs;
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}
			inputData = new double[inputSize];

			netFitness = dIs.readInt();
			mapMaxCols = dIs.readInt();
			mapMaxRows = dIs.readInt();
			bulletsFirstIndex = dIs.readInt();

			if(mapMaxCols < 2 || mapMaxRows < 2){// map minimum 2x2;
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}

			int layersCount = dIs.readInt();
			i = mapMaxCols * mapMaxRows;

			if (layersCount < 1 || i < 4 || bulletsFirstIndex < 1){
				System.out.println("Data in file  " + fileName + "  is corrupted!");
				return false;
			}

			layers = new double[layersCount][];

			int[] neuronsCounts = new int[layersCount];
			int count = 0;
			for (i = 0; i < layersCount; i++) {
				neuronsCounts[i] = dIs.readInt();
				if(neuronsCounts[i] > count)
					count = neuronsCounts[i];

				numberOfWeights = neuronsCounts[i] * (inputSize + 1);
				if(numberOfWeights < 2){
					System.out.println("Data in file  " + fileName + "  is corrupted (#weights has to be > 2)!");
					return false;
				}

				inputSize = neuronsCounts[i];// output of current layer (n neurons) will be an input of the next one;
				layers[i] = new double[numberOfWeights];
			}
			maxNeurons = count;

			bufferedOutput = new double[count*2];

			int j;
			i = 0;
			try {
				for (; i < layersCount; i++) {
					count = layers[i].length;
					for (j = 0; j < count; j++) {
						layers[i][j] = dIs.readDouble();
					}
				}

			} catch (EOFException e) {
				System.out.println("EOF error during reading  " + fileName + ", ... creating random weights.");

				double range = 10.0;
				for (; i < layersCount; i++) {
					count = layers[i].length;
					for (j = 0; j < count; j++) {
						layers[i][j] = rand.symmetricRandRange(range);
					}
				}

			}
			ready = true;

			dIs.close();
			is.close();
		} catch(IOException | NullPointerException e){
			System.out.println("Reading AI file  " + fileName + " failed!");
			ready = false;
		}
		return ready;
	}


	public void writeFile(){
		writeFile("tank_ai.bin");
	}
	public void writeFile(String fileName){
		if(!ready){
			System.out.println("AI not saved!");
			return;
		}

		DataOutputStream dOs = null;
		File file = null;
		OutputStream os = null;

		try {
			String fPath = TankAI.class.getResource("/resources/").getFile();
			file = new File(fPath + "ai_resources/" + fileName);

			os = new FileOutputStream(file);
			dOs = new DataOutputStream(os);

			if(!file.exists() )
				file.createNewFile();

			//oos = new ObjectOutputStream(os);
			//oos.writeObject(layers[0]);
			String fileHeader = "tank_ai";
			dOs.writeChars(fileHeader);
			int inputSize = 16 - fileHeader.length();

			dOs.write(new byte[inputSize]);

			inputSize = inputData.length;
			dOs.writeInt(inputSize);
			dOs.writeInt(netFitness);
			dOs.writeInt(mapMaxCols);
			dOs.writeInt(mapMaxRows);
			dOs.writeInt(bulletsFirstIndex);

			int size = layers.length;
			dOs.writeInt(size);

			int i = 0, neuronsCount;
			for(; i < size; i++){
				neuronsCount = layers[i].length / (inputSize + 1);
				inputSize = neuronsCount;
				dOs.writeInt(neuronsCount);
			}

			neuronsCount = layers.length;
			int j;
			for(i = 0; i < neuronsCount; i++){
				size = layers[i].length;
				for(j = 0; j < size; j++){
					dOs.writeDouble(layers[i][j]);
				}
			}

			//os.flush();
			//dOs.close();
		} catch(FileNotFoundException e){
			System.out.println("Creating/writing file  " + fileName  + "  failed!");
		} catch(IOException e){
			System.out.println("Writing to file  " + fileName  + "  failed!");
		} finally {
			if(os != null){
				try {
					os.flush();
					os.close();
				}catch(IOException e){
					System.out.println("Error closing stream! " + e);
				}
			}
			if(dOs != null){
				try {
					dOs.flush();
					dOs.close();
				} catch(IOException e){
					System.out.println("Error closing data-stream! " + e);
				}
			}
		}
	}
}
