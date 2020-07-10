package com.company.logic;

public class TankAI {
	private BattleRandom rand;
	private double[][] layers;
	private double[] inputData;
	private double[] output;
	private double[] bufferedOutput;// its size has to be the sum of 2 maximum lengths of layers;
	private boolean ready;

	public TankAI(BattleRandom rand, int outputSize){
		output = new double[outputSize];
		ready = false;

		this.rand = rand;
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
	private double productVector(double[] v, double[] w, int vIndexBegin, int wIndexBegin, int productSize){
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
		double f = 0.0;
		// todo: set f as sigmoid: 1/( 1 + e^-x );
		return f;
	}

	public double[] getOutput(){
		if(!ready)
			return null;

		final int layersCount = layers.length;
		int i, inputOutputSize;

		for(i = 0; i < layersCount; i++){
			// todo: multiply input inputData[] by all layers with weights;
		}
		// todo: multiply input inputData[] by all layers with weights;

		return output;
	}

	public void setNeuralNetwork(int inputSize){
		// todo: set fields according to arguments (there will have to be more);
	}


	public void readFile(){

	}

	public void writeFile(){

	}

}
