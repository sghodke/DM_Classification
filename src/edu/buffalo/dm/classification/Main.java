/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.model.DecisionTree;
import edu.buffalo.dm.classification.util.ClassificationUtil;
import edu.buffalo.dm.classification.util.Measure;
import edu.buffalo.dm.classification.util.Parser;

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
				System.out.println("1. Naive Bayes\n2. ANN\n3. Decision Tree\n4. Enter new file for dataset\n5. Exit");
	        	String choice = scanner.next();
	        	
	        	switch(choice) {
	        	
	        	case "1":	// Naive Bayes
	        		break;
	        		
	        	case "2":	// ANN
	        		break;
	        	
	        	case "3":	// Decision Tree
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
		DecisionTree dt = new DecisionTree();
		/*
		int totalSamples = samples.size();
		int splitIndex = new Double(totalSamples * 0.9d).intValue();
		Collections.shuffle(samples);
		List<Sample> trainSamples = samples.subList(0, splitIndex);
		List<Sample> testSamples = samples.subList(splitIndex, totalSamples);
		Node root = dt.generateTree(trainSamples);
		dt.classifySamples(root, testSamples);
		//display(testSamples);
		double accuracy = Measure.accuracy(testSamples);
		System.out.println("SplitIndex: " + splitIndex + "\nAccuracy: " + accuracy);
		*/
		List<Sample> trainSamples, testSamples;
		Node root;
		int k = 10;
		Map<Integer, List<List<Sample>>> crossValidationSplits = ClassificationUtil.getCrossValidationSplit(samples, k); 
		double avgAccuracy = 0d;
		for(int i: crossValidationSplits.keySet()) {
			List<List<Sample>> validationSplit = crossValidationSplits.get(i);
			trainSamples = validationSplit.get(0);
			testSamples = validationSplit.get(1);
			root = dt.generateTree(trainSamples);
			dt.classifySamples(root, testSamples);
			avgAccuracy += Measure.accuracy(testSamples);
			ClassificationUtil.resetData(samples);
		}
		avgAccuracy /= k;
		System.out.println("Average accuracy: " + avgAccuracy);

		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + " seconds");
		
	}
	
	@SuppressWarnings("unused")
	private static void display(List<Sample> samples) {
		int i = 25;
		for(Sample sample: samples) {
			System.out.println(sample.getSampleId() + "\t" + sample.getGroundTruthClassId() + "\t" + sample.getClassId());
			if(i-- < 0) {
				break;
			}
		}
	}

}
