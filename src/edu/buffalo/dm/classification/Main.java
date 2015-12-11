/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.PerformanceMetric;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.model.DecisionTree;
import edu.buffalo.dm.classification.model.RandomForest;
import edu.buffalo.dm.classification.util.ClassificationUtil;
import edu.buffalo.dm.classification.util.Measure;
import edu.buffalo.dm.classification.util.Parser;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Main {

	private static List<Sample> samples;
	private static String filename;
	private static final String PATH = "src/input/"; 
	private static Scanner scanner;
	static {
		scanner = new Scanner(System.in);
	}
	public static void main(String[] args) {
		try {
			System.out.println("Enter filename (case-sensitive): ");
			filename = scanner.next();
			samples = Parser.readData(PATH + filename); 
			
			while(true) {
				System.out.println("\n1. Naive Bayes\n2. Decision Tree\n3. Random Forest\n4. Enter new file for dataset\n5. Exit");
	        	String choice = scanner.next();
	        	
	        	switch(choice) {
	        	
	        	case "1":	// Naive Bayes
	        		break;
	        		
	        	case "3":	// ANN
	        		runRF();
	        		break;
	        	
	        	case "2":	// Decision Tree
	        		runDT();
	        		break;
	        		
	        	case "4":	// New file
	    			System.out.println("Enter filename (case-sensitive): ");
	        		filename = scanner.next();
	        		samples = Parser.readData(PATH + filename);
	        		break;
	        		
	        	case "5":	// Exit
	        		System.exit(0);
	        		
	        	case "6":	// weka
	        		weka();
	        		break;
	        		
	        	default:
	        		System.out.println("Select valid choice...");
	        	}
	        	ClassificationUtil.resetData(samples);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Decision tree
	 */
	private static void runDT() {
		long startTime = System.currentTimeMillis();
		DecisionTree dt;
		List<Sample> trainSamples, testSamples;
		Node root;
		int k = 10;
		Map<Integer, List<List<Sample>>> crossValidationSplits = ClassificationUtil.getCrossValidationSplit(samples, k); 
		double avgAccuracy = 0d;
		double avgFMeasure = 0d;
		double avgPrecision = 0d;
		double avgRecall = 0d;
		PerformanceMetric metric;
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			dt = new DecisionTree(trainSamples);
			root = dt.generateTree();
			dt.classifySamples(root, testSamples);
			metric = Measure.getPerformance(testSamples);
			avgAccuracy += metric.getAccuracy();
			avgFMeasure += metric.getFmeasure();
			avgPrecision += metric.getPrecision();
			avgRecall += metric.getRecall();
			ClassificationUtil.resetData(samples);
		}
		avgAccuracy /= k;
		avgFMeasure /= k;
		avgPrecision /= k;
		avgRecall /= k;
		System.out.printf("\nAverage accuracy: %.2f", avgAccuracy);
		System.out.printf("\nAverage fMeasure: %.2f", avgFMeasure);
		System.out.printf("\nAverage Precision: %.2f", avgPrecision);
		System.out.printf("\nAverage recall: %.2f\n", avgRecall);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		System.out.println("==========================================================");
	}
	
	private static void runRF() {
		long startTime = System.currentTimeMillis();
		RandomForest rf;
		List<Sample> trainSamples, testSamples;
		int k = 5;
		Map<Integer, List<List<Sample>>> crossValidationSplits = ClassificationUtil.getCrossValidationSplit(samples, k); 
		double avgAccuracy = 0d;
		double avgFMeasure = 0d;
		double avgPrecision = 0d;
		double avgRecall = 0d;
		PerformanceMetric metric;
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			rf = new RandomForest(trainSamples, 40, 10);
			rf.generateRandomForest();
			rf.classifySamples(testSamples);
			//display(testSamples);
			metric = Measure.getPerformance(testSamples);
			avgAccuracy += metric.getAccuracy();
			avgFMeasure += metric.getFmeasure();
			avgPrecision += metric.getPrecision();
			avgRecall += metric.getRecall();
			ClassificationUtil.resetData(samples);
		}
		avgAccuracy /= k;
		avgFMeasure /= k;
		avgPrecision /= k;
		avgRecall /= k;
		System.out.printf("\nAverage accuracy: %.2f", avgAccuracy);
		System.out.printf("\nAverage fMeasure: %.2f", avgFMeasure);
		System.out.printf("\nAverage Precision: %.2f", avgPrecision);
		System.out.printf("\nAverage recall: %.2f\n", avgRecall);

		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		System.out.println("==========================================================");
	}
	
	private static void weka() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(PATH + "weka3"));
			Instances data = new Instances(br);
			data.setClassIndex(data.numAttributes() - 1);
			Instances[][] split = crossValidationSplit(data, 5);
			Instances[] trainingSplits = split[0];
			Instances[] testingSplits = split[1];
			Classifier model = new NaiveBayes();
			//String[] options = {"-I","100","-K","200"};
			//String[] options = {"-M","5","-R","-N","10"};
			//String[] options = {"-C","20","-N","2","-L",".3","-V", "3"};
			//String[] options = {"-E", "rmse", "-I", "-X", "10"};
//			String[] options = {"-D"};
//			model.setOptions(options);
			/*
			for(String s: model.getOptions()) {
				System.out.println(s); 
			}
			FastVector predictions = new FastVector();
			for(int i=0; i<trainingSplits.length; i++) {
				Evaluation eval = new Evaluation(trainingSplits[i]);
				model.buildClassifier(trainingSplits[i]);
				eval.evaluateModel(model, testingSplits[i]);
				predictions.appendElements(eval.predictions());
				System.out.println(1-eval.errorRate());
			}
			System.out.println("Accuracy: " + calculateAccuracy(predictions));
			*/
			System.out.println(data.numClasses());
			br.close();
			br = new BufferedReader(new FileReader(PATH + "weka3_test"));
			Instances testData = new Instances(br);
			testData.setClassIndex(1);
			model.buildClassifier(data);
			//testData.firstInstance().setMissing(7003);
			System.out.println(testData.firstInstance().value(7003));
			model.classifyInstance(testData.firstInstance());
			
			for(int i=0; i<testData.numInstances(); i++) {
				Instance instance = testData.instance(i);
				double pred = model.classifyInstance(instance);
				System.out.println(pred);
			}
//			model.classifyInstance(testData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
 
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
 
		return split;
	}
	
	public static double calculateAccuracy(FastVector predictions) {
		double correct = 0;
 
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
 
		return 100 * correct / predictions.size();
	}
	@SuppressWarnings("unused")
	private static void display(List<Sample> samples) {
		for(Sample sample: samples) {
			System.out.println(sample.getSampleId() + ". " + sample.getGroundTruthClassId() + "\t" + sample.getClassId());
		}
		System.out.println("---------");
	}
}
