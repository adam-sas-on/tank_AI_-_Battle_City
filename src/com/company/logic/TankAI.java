package com.company.logic;

import com.company.model.Bullet;
import com.company.model.Enemy;
import com.company.model.PlayerAITank;
import com.company.view.Cell;
import com.company.view.MapCell;

import java.io.*;
import java.net.URL;
import java.nio.file.StandardOpenOption;
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

	public void setDefaultNeuralNetwork(int mapMaxCols, int mapMaxRows, int maxEnemyTanks, int maxBullets){
		ready = false;
		if(mapMaxCols == 0 && mapMaxRows == 0 && maxEnemyTanks == 0 && maxBullets == 0)
			return;

		int inputSize;
		inputSize = mapMaxCols*mapMaxRows + 3;// 3 - owners input and 2 inputs of eagle;
		inputSize += (maxEnemyTanks + 1)*3;// 3: enemy tanks angle to owner, distance to owner  and  cell-code (+ ally tank);
		bulletsFirstIndex = inputSize;
		inputSize += maxBullets*3;// 3: bullets angle to owner, distance to owner  and  direction on map;

		try {
			inputData = new double[inputSize];

			int layerSize, numberOfWeights, i;
			final double range = 10.0;
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
				layers[1][i] = rand.symmetricRandRange(range);

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

	public void updateMapState(Cell[] cells, int mapRows, int mapCols, int maxCols){
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
			inputData[nNetIndex] = mapCell.getCellCode();
		}
		updateOutput = true;
	}

	public void updateEagleAndOwnerState(PlayerAITank tank, Cell eagleCell, MapCell ownerMapCell){
		if(!ready)
			return;

		int nNetIndex = mapMaxCols*mapMaxRows;

		tank.getPos(ownerXY_pos);

		if(eagleCell == null){
			inputData[nNetIndex] = -1.0;
			inputData[nNetIndex + 1] = -3.0*Math.PI;// anything less then -2*PI;
			return;
		}

		int eagleX, eagleY;
		double dx, dy;

		eagleX = eagleCell.getCol();
		eagleY = eagleCell.getRow();
		dx = (eagleX - ownerXY_pos[0])/(double)cellPrecisionUnitSize;
		dy = (eagleY - ownerXY_pos[1])/(double)cellPrecisionUnitSize;
		inputData[nNetIndex] = Math.atan2(dy, dx);
		inputData[nNetIndex + 1] = Math.hypot(dx, dy);// = sqrt(dx*dx + dy*dy);

		inputData[nNetIndex + 2] = ownerMapCell.getCellCode();
		updateOutput = true;
	}

	public void updateTanksState(Enemy[] tanks, int activeTanksCount, PlayerAITank allyTank){
		if(!ready)
			return;

		Cell cell = new Cell();
		// MapCell mapCell;
		int i, nNetIndex = mapMaxCols*mapMaxRows + 3;// 3 for eagle and controlled tanks cell-code;

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

			String fileHeader = "", correct = "tank_ai";
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

			if (inputSize < 2)// minimum 2 inputs;
				throw new IOException("Data in file  \" + fileName + \"  is corrupted!");

			netFitness = dIs.readInt();
			mapMaxCols = dIs.readInt();
			mapMaxRows = dIs.readInt();
			bulletsFirstIndex = dIs.readInt();
			int layersCount = dIs.readInt();
			i = mapMaxCols * mapMaxRows;

			if (layersCount < 1 || i < 4 || bulletsFirstIndex < 1)
				throw new IOException("Data in file  " + fileName + "  is corrupted!");

			layers = new double[layersCount][];
			int[] neuronsCounts = new int[layersCount];
			for (i = 0; i < layersCount; i++) {
				neuronsCounts[i] = dIs.readInt();

				numberOfWeights = neuronsCounts[i] * (inputSize + 1);
				inputSize = neuronsCounts[i];// output of current layer (n neurons) will be an input of the next one;
				layers[i] = new double[numberOfWeights];
			}

			int j, count;
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

	}
}
