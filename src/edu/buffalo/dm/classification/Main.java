/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification;

import java.util.List;
import java.util.Scanner;

import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.model.DecisionTree;
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
	        		break;
	        		
	        	case "5":	// Exit
	        		System.exit(0);
	        	default:
	        		System.out.println("Select valid choice...");
	        	}
	        	
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Decision tree
	 */
	@SuppressWarnings("unused")
	private static void runDT() {
		long startTime = System.currentTimeMillis();
		Node root = new DecisionTree().classify(samples);
		long endTime = System.currentTimeMillis();
		System.out.println("Execution Time: " + ((double)(endTime - startTime)/1000) + "seconds");
	}

}
