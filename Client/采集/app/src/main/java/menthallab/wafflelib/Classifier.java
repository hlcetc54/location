package menthallab.wafflelib;

public interface Classifier
{
	void learn(Dataset dataset);

	void asyncLearn(Dataset dataset);

	boolean isCompleted();
	
	void stopLearning();
	
	double getDesiredLearningError();
	
	double getCurrentLearningError();
	
	String classify(Instance instance);
}