package menthallab.wafflelib;

import java.util.*;

import org.neuroph.core.learning.*;
import org.neuroph.nnet.*;
import org.neuroph.nnet.learning.*;
import org.neuroph.util.*;

public class NeuralNetwork implements Classifier
{
	private final static double DESIRED_ERROR = 0.14;
	private final static double LEARNING_RATE = 0.01;
	private final static TransferFunctionType TRANSFER_FUNCTION = TransferFunctionType.SIGMOID;
	
	private MultiLayerPerceptron mlPerceptron;
	private BackPropagation backPropagation;
	private List<String> attributes;
	private List<String> labels;
	
	public NeuralNetwork()
	{
		this.mlPerceptron = null;
		this.backPropagation = null;
		this.attributes = null;
		this.labels = null;
	}
	
	public void learn(Dataset dataset)
	{
		if (this.isCompleted())
		{
			if (null == dataset)
				throw new NullPointerException("NeuralNetwork.learn: dataset is null");
			TrainingSet<SupervisedTrainingElement> trainSet = initLearning(dataset);
			this.mlPerceptron.learn(trainSet);
		}
	}

	public void asyncLearn(Dataset dataset)
	{
		if (this.isCompleted())
		{
			if (null == dataset)
				throw new NullPointerException("NeuralNetwork.asyncLearn: dataset is null");
			TrainingSet<SupervisedTrainingElement> trainSet = initLearning(dataset);
			this.mlPerceptron.learnInNewThread(trainSet);
		}
	}

	public boolean isCompleted()
	{
		return (null == this.mlPerceptron || this.backPropagation.isStopped());
	}
	
	public void stopLearning()
	{
		if (null == this.mlPerceptron)
			throw new IllegalStateException("NeuralNetwork.stopLearning: learning process was not initialized");
		this.backPropagation.stopLearning();
	}
	
	public double getDesiredLearningError()
	{
		if (null == this.mlPerceptron)
			throw new IllegalStateException("NeuralNetwork.getDesiredLearningError: network was not learned");
		double error = this.backPropagation.getMaxError();
		return (double)((int)(error * 1000000) / 1000000.0);
	}
	
	public double getCurrentLearningError()
	{

		if (null == this.mlPerceptron)
			throw new IllegalStateException("NeuralNetwork.getCurrentLearningError: learning process was not initialized");
		double error = this.backPropagation.getPreviousEpochError();
		return (double)((int)(error * 1000000) / 1000000.0);
	}
	
	public String classify(Instance instance)
	{
		if (null == this.mlPerceptron)
			throw new IllegalStateException("NeuralNetwork.classify: network was not learned");
		if (null == instance)
			throw new NullPointerException("NeuralNetwork.classify: instance is null");
		double[] input = new double[this.attributes.size()];
		for (int i = 0; i < this.attributes.size(); ++i)
		{
			String attributeName = this.attributes.get(i);
			Double value = instance.get(attributeName);
			input[i] = (null != value ? value : 0);
		}
		double[] output = new double[this.labels.size()];
		SupervisedTrainingElement testInstance = new SupervisedTrainingElement(input, output);
		this.mlPerceptron.setInput(testInstance.getInput());
		this.mlPerceptron.calculate();
		return getLabel(this.mlPerceptron.getOutput());
	}
	
	private TrainingSet<SupervisedTrainingElement> initLearning(Dataset dataset)
	{
		this.backPropagation = new BackPropagation();
		this.backPropagation.setMaxError(DESIRED_ERROR);
		this.backPropagation.setLearningRate(LEARNING_RATE);
		
		TrainingSet<SupervisedTrainingElement> trainSet = convertToNeuroph(dataset);
		final int inputUnits = trainSet.getInputSize();
		final int outputUnits = trainSet.getOutputSize();
		final int hiddenUnits = (inputUnits + outputUnits) / 2;
		
		this.mlPerceptron = new MultiLayerPerceptron(TRANSFER_FUNCTION, inputUnits, hiddenUnits, outputUnits);
		this.mlPerceptron.setLearningRule(backPropagation);
		this.mlPerceptron.randomizeWeights();
		
		return trainSet;
	}
	
	private TrainingSet<SupervisedTrainingElement> convertToNeuroph(Dataset dataset)
	{
		this.attributes = new ArrayList<String>(dataset.getAttributes());
		this.labels = new ArrayList<String>(dataset.getDifferentLabels());
		
		final int inputs = this.attributes.size();
		final int outputs = this.labels.size();		
		TrainingSet<SupervisedTrainingElement> trainSet = new TrainingSet<SupervisedTrainingElement>(inputs, outputs);
		
		for (int i = 0; i < dataset.size(); ++i)
		{
			final RawInstance rawInstance = dataset.getRawInstance(i);
			double[] input = new double[inputs];
			for (int j = 0; j < rawInstance.size(); ++j)
				input[j] = rawInstance.get(j);
			final String label = dataset.getLabel(i);
			double[] output = new double[outputs];
			output[this.labels.indexOf(label)] = 1;
			trainSet.addElement(new SupervisedTrainingElement(input, output));
		}
		return trainSet;
	}
	
	private String getLabel(double[] output)
	{
		double maxOutput = 0.0;
		int index = 0;
		for (int i = 0; i < output.length; ++i)
			if (output[i] > maxOutput)
			{
				maxOutput = output[i];
				index = i;
			}
		return this.labels.get(index);
	}
}