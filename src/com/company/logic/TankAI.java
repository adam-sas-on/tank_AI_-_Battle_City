package com.company.logic;

import com.company.model.Bullet;
import com.company.model.Enemy;
import com.company.model.PlayerAITank;
import com.company.view.Cell;
import com.company.view.MapCell;

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
		netFitness = 0;

		this.rand = rand;
		cellPrecisionUnitSize = cellPrecision;
	}

	private void setDefaultTriple(int inputIndex){
		inputData[inputIndex] = inputData[inputIndex + 2] = -1.0;
		inputData[inputIndex + 1] = -3.0*Math.PI;// anything less then -2*PI;
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

	public void setNeuralNetwork(int inputSize){
		// todo: set fields according to arguments (there will have to be more);
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
		}

		updateOutput = true;
	}

	public void updateBulletsState(Bullet[] bullets, int activeBulletsCount){
		if(!ready)
			return;

		int[] xyPos = new int[2];
		int i, nNetIndex = bulletsFirstIndex, nNetLimit = inputData.length;

		for(i = 0; nNetIndex < nNetLimit; i++){
			setDefaultTriple(nNetIndex);
			if(i >= activeBulletsCount){
				nNetIndex += 3;
				continue;
			}

			bullets[i].getBulletPos(xyPos);
			// todo: update inputData[i] ...
		}

	}


	public void readFile(){

	}

	public void writeFile(){

	}

}
