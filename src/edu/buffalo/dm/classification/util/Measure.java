/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.dm.classification.bean.Sample;

public class Measure {

	static int[][] confusionMatrix;
	public static double entropy() {

		return -1;
	}

	/**
	 * Get accuracy of results
	 * @param samples
	 * @return
	 */
	public static double accuracy(List<Sample> samples) {
		int match;
		match = 0;
		double accuracy = -1d;
		for(Sample sample: samples) {
			if(sample.getClassId() == sample.getGroundTruthClassId()) {
				match++;
			}
		}
		accuracy = (double) match / samples.size();
		return accuracy;
	}
	
	/**
	 * Modified fMeasure (similar to accuracy) based on provided datasets
	 * (Not foolproof or general)
	 * @param samples
	 * @return
	 */
	public static double fMeasure(List<Sample> samples) {
		double fMeasure = -1d;
		int classes = ClassificationUtil.getClassIds().size();
		confusionMatrix = new int[classes][classes];
		for(int i=0; i<classes; i++) {
			for(int j=0; j<classes; j++) {
				confusionMatrix[i][j] = 0;
			}
		}
		for(Sample sample: samples) {
			int groundTruthClassId = sample.getGroundTruthClassId();
			int classId = sample.getClassId();
			confusionMatrix[groundTruthClassId][classId]++;
		}
		
		int a, b, c;
		a = b = c = 0;
		for(int i=0; i<classes; i++) {
			for(int j=0; j<classes; j++) {
				if(i == j) {
					a += confusionMatrix[i][j];
				} else {
					b += confusionMatrix[i][j];
					c += confusionMatrix[j][i];
				}
			}
		}
		fMeasure = (double)a / (a+b+c);
		return fMeasure;
		
	}
	

	/**
	 * Calculate gini index of the split
	 * @param childrenSamplesCount
	 * @param childrenClassSplits
	 * @return
	 */
	public static double getGiniSplit(Map<Integer, List<Sample>> childrenSamplesCount, Map<Integer, Map<Integer, Integer>> childrenClassSplits) {
		double gini = 0d;
		int totalSamples = 0;
		for(int i: childrenSamplesCount.keySet()) {
			int n = childrenSamplesCount.get(i).size();
			Map<Integer, Integer> classIdsSet = childrenClassSplits.get(i);
			gini += n * getGiniNode(n, classIdsSet);
			totalSamples += n;
		}
		return (gini / totalSamples);
	}
	
	/**
	 * Calculate gini index of the given node
	 * @param n
	 * @param classIdsSet
	 * @return
	 */
	public static double getGiniNode(int n, Map<Integer, Integer> classIdsSet) {
		double giniNode;
		double sum = 0d;
		for(Integer classId: classIdsSet.keySet()) {
			int count = classIdsSet.get(classId);
			sum += Math.pow(((double)count/n), 2);
		}
		giniNode = 1 - sum;
		return giniNode;
	}
	
	/**
	 * Calculate gini index of root node
	 * @param samples
	 * @return
	 */
	public static double getGiniRoot(List<Sample> samples) {
		Map<Integer, Integer> classIdsSet = new HashMap<Integer, Integer>();
		for(Sample sample: samples) {
			int classId = sample.getGroundTruthClassId();
			Integer count;
			if((count = classIdsSet.get(classId)) == null) {
				classIdsSet.put(classId, 1);
			} else {
				classIdsSet.put(classId, ++count);
			}
		}
		double giniRoot = getGiniNode(samples.size(), classIdsSet);
		return giniRoot;
	}
}
