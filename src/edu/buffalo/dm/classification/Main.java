/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification;
/*
import java.io.BufferedReader;
import java.io.FileReader;
*/
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.PerformanceMetric;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.model.DecisionTree;
import edu.buffalo.dm.classification.model.NaiveBayesClassifier;
import edu.buffalo.dm.classification.model.RandomForest;
import edu.buffalo.dm.classification.util.ClassificationUtil;
import edu.buffalo.dm.classification.util.Measure;
import edu.buffalo.dm.classification.util.Parser;
/*
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
*/
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
					runNaiveBayes();
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
	        		//weka();
	        		break;
	        	
	        	case "7":	// classify dataset3
	        		classifyDataset3();
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
		int gini = 1, intervals = 3;
		System.out.println("Enter number of intervals for continuous data: ");
		intervals = scanner.nextInt();
		System.out.println("Select gini(1) or entropy(0): ");
		gini = scanner.nextInt();
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
		long startTime = System.currentTimeMillis();
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			dt = new DecisionTree(trainSamples, intervals, gini);
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
		System.out.printf("\nAverage accuracy: %.5f", avgAccuracy);
		System.out.printf("\nAverage fMeasure: %.5f", avgFMeasure);
		System.out.printf("\nAverage Precision: %.5f", avgPrecision);
		System.out.printf("\nAverage recall: %.5f\n", avgRecall);
		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		System.out.println("==========================================================");
	}
	
	private static void runRF() {
		int trees = 10, featureSplitPecent = 30;
		System.out.println("Enter number of trees: ");
		trees = scanner.nextInt();
		System.out.println("Enter percentage of features to select: ");
		featureSplitPecent = scanner.nextInt();
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
			rf = new RandomForest(trainSamples, featureSplitPecent, trees);
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
		System.out.printf("\nAverage accuracy: %.5f", avgAccuracy);
		System.out.printf("\nAverage fMeasure: %.5f", avgFMeasure);
		System.out.printf("\nAverage Precision: %.5f", avgPrecision);
		System.out.printf("\nAverage recall: %.5f\n", avgRecall);
		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		System.out.println("==========================================================");
	}
	
	private static void classifyDataset3() {
		List<Sample> samples3 = Parser.readData(PATH + "3");
		int k = 5;
		Map<Integer, List<List<Sample>>> crossValidationSplits = ClassificationUtil.getCrossValidationSplit(samples3, k); 
		List<Sample> trainSamples, testSamples;
		RandomForest rf, bestRf = new RandomForest(samples3, 5, 30);
		PerformanceMetric metric;
		double bestAccuracy = -1d;
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			rf = new RandomForest(trainSamples, 5, 30);
			rf.generateRandomForest();
			rf.classifySamples(testSamples);
			//display(testSamples);
			metric = Measure.getPerformance(testSamples);
			double accuracy = metric.getAccuracy();
			if(accuracy > bestAccuracy) {
				bestAccuracy = accuracy;
				bestRf = rf;
			}
			ClassificationUtil.resetData(samples3);
		}
		List<Sample> test = Parser.readData(PATH + "3test");
		bestRf.classifySamples(test);
		display(test);
	}

	/**
	 * Naive Bayes
	 */
	private static void runNaiveBayes() {
		long startTime = System.currentTimeMillis();
		NaiveBayesClassifier naiveBayesClassifier;
		List<Sample> trainSamples, testSamples;
		double avgAccuracy = 0d;
		double avgFMeasure = 0d;
		double avgPrecision = 0d;
		double avgRecall = 0d;
		PerformanceMetric metric;
		int k = 10;
		System.out.println();
		Map<Integer, List<List<Sample>>> crossValidationSplits = ClassificationUtil.getCrossValidationSplit(samples, k);
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			naiveBayesClassifier = new NaiveBayesClassifier(trainSamples, testSamples);
			naiveBayesClassifier.assignClassesToTestSamples();
			metric = Measure.getPerformance(testSamples);
			avgAccuracy += metric.getAccuracy();
			avgFMeasure += metric.getFmeasure();
			avgPrecision += metric.getPrecision();
			avgRecall += metric.getRecall();
			ClassificationUtil.resetData(samples);
			ClassificationUtil.resetData(samples);
		}
		avgAccuracy /= k;
		avgFMeasure /= k;
		avgPrecision /= k;
		avgRecall /= k;
		System.out.printf("\nAverage accuracy: %.5f", avgAccuracy);
		System.out.printf("\nAverage fMeasure: %.5f", avgFMeasure);
		System.out.printf("\nAverage Precision: %.5f", avgPrecision);
		System.out.printf("\nAverage recall: %.5f\n", avgRecall);

		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		System.out.println("==========================================================");

	}
	private static void display(List<Sample> samples) {
		for(Sample sample: samples) {
//			System.out.println(sample.getSampleId() + ". " + sample.getGroundTruthClassId() + "\t" + sample.getClassId());
			System.out.println(sample.getClassId());
		}
		System.out.println("---------");
	}
}
