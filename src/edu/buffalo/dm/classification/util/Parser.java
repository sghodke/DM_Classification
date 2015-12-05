/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.buffalo.dm.classification.adt.Data;
import edu.buffalo.dm.classification.adt.DoubleData;
import edu.buffalo.dm.classification.adt.StringData;
import edu.buffalo.dm.classification.bean.Sample;

public class Parser {

	/**
	 * Returns a list of samples created from the information provided in input
	 * file
	 * 
	 * @param filePath - the path of input (data) file
	 * @return - a list of samples
	 */
	public static List<Sample> readData(String filePath) {
		BufferedReader br = getReader(filePath);
		List<Sample> samples = new ArrayList<>();
		String data;
		int sampleId = 0;
		try {
			while((data = br.readLine()) != null) {
				samples.add(generateSample(data, sampleId++));
			}
			ClassificationUtil.computeFeatures(samples);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return samples;
	}

	/**
	 * Returns a buffered reader for a file
	 * 
	 * @param filePath - the path of the file
	 * @return - the buffered reader object
	 */
	private static BufferedReader getReader(String filePath) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			System.out.println("Incorrect file path!");
			System.exit(1);
		}
		return bufferedReader;
	}
	
	/**
     * Returns a sample created from the information in input file.
     * @param inputData - the input string from the file
     * @return - the created sample object.
     */
    private static Sample generateSample(String inputData, int sampleId) {
        String[] tokens = inputData.split("\t");
        List<Data> features = new ArrayList<Data>();
        for(int i=0; i<tokens.length-1; i++) {
        	try {
        		Double feature = Double.parseDouble(tokens[i]);
        		features.add(new DoubleData(feature));
        	} catch (Exception e) {
        		features.add(new StringData(tokens[i]));
        	}
        }
        int groundTruthClassId = Integer.parseInt(tokens[tokens.length-1]);
		return new Sample(sampleId, features, groundTruthClassId);
	}
}
