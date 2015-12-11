/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.dm.classification.bean.PerformanceMetric;
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
	
	public static void buildConfusionMatrix(List<Sample> samples) {
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
	}
	
	/**
	 * Get classification performance parameters for provided samples
	 * @param samples
	 * @return
	 */
	public static PerformanceMetric getPerformance(List<Sample> samples) {
		buildConfusionMatrix(samples);
		double fMeasure = 0d;
		double accuracy = 0d;
		double precision = 0d;
		double recall = 0d;
		int classes = ClassificationUtil.getClassIds().size();
		int tp, fn, fp, tn;
		for(int k=0; k<classes; k++) {
			tp = fn = fp = tn = 0;
			for(int i=0; i<classes; i++) {
				if(i == k) {
					tp += confusionMatrix[k][i];
				} else {
					fn += confusionMatrix[k][i];
					fp += confusionMatrix[i][k];
				}
			}
			tn = samples.size() - (tp+fn+fp);
			double classAccuracy = (double)(tp+tn) / (tp+fn+fp+tn);
			double classPrecision = (double)(tp) / (tp+fp);
			double classRecall = (double)(tp) / (tp+fn);
			double classFMeasure = (double)(tp+tp) / (tp+tp+fp+fn);
			if(tp == 0) {
				classPrecision = classRecall = classFMeasure = 0d;
			}
			accuracy += classAccuracy;
			precision += classPrecision;
			recall += classRecall;
			fMeasure += classFMeasure;
			
		}
		accuracy /= classes;
		precision /= classes;
		recall /= classes;
		fMeasure /= classes;
		
		PerformanceMetric metric = new PerformanceMetric(accuracy, fMeasure, precision, recall);
		return metric;
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
	 * @param n - total samples at node
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
	
	/**
	 * Calculate entropy of the split
	 * @param childrenSamplesCount
	 * @param childrenClassSplits
	 * @return
	 */
	public static double getEntropySplit(Map<Integer, List<Sample>> childrenSamplesCount, Map<Integer, Map<Integer, Integer>> childrenClassSplits) {
		double entropy = 0d;
		int totalSamples = 0;
		for(int i: childrenSamplesCount.keySet()) {
			int n = childrenSamplesCount.get(i).size();
			Map<Integer, Integer> classIdsSet = childrenClassSplits.get(i);
			entropy += n * getEntropyNode(n, classIdsSet);
			totalSamples += n;
		}
		return (entropy / totalSamples);
	}
	
	/**
	 * Calculate entropy of a node
	 * @param n - total samples at node
	 * @param classIdsSet
	 * @return
	 */
	public static double getEntropyNode(int n, Map<Integer, Integer> classIdsSet) {
		double entropy = -1d;
		double sum = 0d;
		for(Integer classId: classIdsSet.keySet()) {
			int count = classIdsSet.get(classId);
			if(count == 0) {
				continue;
			}
			double classProb = (double) count/n;
			sum += classProb * Math.log(classProb) / Math.log(2);
		}
		entropy = -sum;
		return entropy;
	}
	
	/**
	 * Calculate gini index of root node
	 * @param samples
	 * @return
	 */
	public static double getEntropyRoot(List<Sample> samples) {
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
		double entropyRoot = getEntropyNode(samples.size(), classIdsSet);
		return entropyRoot;
	}
}
