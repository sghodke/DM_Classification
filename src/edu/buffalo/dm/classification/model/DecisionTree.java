/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.dm.classification.adt.Data;
import edu.buffalo.dm.classification.bean.Feature;
import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.util.ClassificationUtil;

public class DecisionTree {
	// set of ground truth class ids
	private static Set<Integer> classIds;
	private static List<Feature> features;
	
	public Node generateTree(List<Sample> samples) {
		features = ClassificationUtil.getFeatures();
		classIds = ClassificationUtil.getClassIds();
		Node root = new Node(samples);
		root.setParent(null);
		root.setClassIds(classIds);
		root.setGiniIndex(getGiniRoot(samples));
		root = buildTree(root, samples);
		return root;
	}

	/**
	 * Classify given samples to appropriate classes based on built tree
	 * @param samples
	 */
	public void classifySamples(Node root, List<Sample> samples) {
		for(Sample sample: samples) {
			classifySample(root, sample);
		}
	}
	
	/**
	 * Recursively find and assign suitable class for the given sample
	 * @param node
	 * @param sample
	 */
	private void classifySample(Node node, Sample sample) {
		if(node == null) {
			return;
		}
		while(node.getChildren() != null && node.getChildren().size() != 0) {
			Feature splitFeature = node.getSplitFeature();
			//Data splitCriteria = splitFeature.getSplitCriteria();
			int childNumber = 0;
			String sampleFeatureData = sample.getFeatures().get(splitFeature.getFeatureId()).toString();
			if("STRING".equals(splitFeature.getType())) {
				childNumber = getSuitableInterval(sampleFeatureData, splitFeature.getCategories());
			} else {
				childNumber = getSuitableInterval(Double.parseDouble(sampleFeatureData), splitFeature.getMin(), splitFeature.getMax(), INTERVALS_FOR_CONTINUOUS_DATA);
			}
			Node childNode = node.getChildren().get(childNumber);
			if(childNode != null) {
				node = childNode;
			} else {
				break;
			}
		}
		int classId = getMostMatchingClassId(node);
		sample.setClassId(classId);
	}
	
	/**
	 * Recursively build a decision tree using given samples
	 * @param node
	 * @param samples
	 * @return
	 */
	private static Node buildTree(Node node, List<Sample> samples) {
		if(node.getClassIds().size() < 2) {
			return node;
		}
		Feature feature = getBestSplitFeature(node, samples);
		if(feature == null) {
			return node;
		}
		node.setSplitFeature(feature);
		Map<Integer, Map<Integer, Integer>> childrenClassSplits = new HashMap<>();
		Map<Integer, List<Sample>> splitLists = getSplitLists(samples, feature, childrenClassSplits);
		for(int i: splitLists.keySet()) {
			List<Sample> childSamples = splitLists.get(i);
			Node child = new Node(childSamples);
			child.setParent(node);
			double childGini = getGiniNode(childSamples.size(), childrenClassSplits.get(i));
			child.setGiniIndex(childGini);
			Set<Integer> childClassIds = new HashSet<>();
			for(Sample sample: childSamples) {
				childClassIds.add(sample.getGroundTruthClassId());
			}
			child.setClassIds(childClassIds);
			node.addChild(i, child);
		}
		List<Node> children = new ArrayList<Node>(node.getChildren().values());
		Collections.sort(children, new GiniComparator());
		Iterator<Node> iterator = children.iterator();
		while(iterator.hasNext()) {
			Node child = iterator.next();
			buildTree(child, child.getSamples());
		}
		return node;
	}
	
	/**
	 * Finds the feature to split the current node on, depending upon lowest gini split value
	 * @param node
	 * @param samples
	 * @return
	 */
	private static Feature getBestSplitFeature(Node node, List<Sample> samples) {
	
		Feature bestFeature = null;
		double gini = Double.MAX_VALUE;
		
		Map<Integer, Map<Integer, Integer>> childrenClassSplits;
		for(int i=0; i<features.size(); i++) {
			Feature feature = features.get(i);
			if(feature.isSelected()) {
				continue;
			}
			childrenClassSplits = new HashMap<>();
			Map<Integer, List<Sample>> splitLists = getSplitLists(samples, feature, childrenClassSplits);
			double giniSplit = getGiniSplit(splitLists, childrenClassSplits);
			if(giniSplit < gini) {
				gini = giniSplit;
				bestFeature = feature;
			}
		}
		if(bestFeature != null) {
			bestFeature.setSelected(true);
		}
		return bestFeature;
	}
	
	/**
	 * Splits the given samples on the provided feature
	 * @param samples
	 * @param feature
	 * @param childrenClassSplits
	 * @return
	 */
	private static Map<Integer, List<Sample>> getSplitLists(List<Sample> samples, Feature feature, Map<Integer, Map<Integer, Integer>> childrenClassSplits) {
		Map<Integer, List<Sample>> splitList = new HashMap<Integer, List<Sample>>();
		
		for(Sample sample: samples) {
			int classId = sample.getGroundTruthClassId();
			
			Data featureData = sample.getFeatures().get(feature.getFeatureId());
			int childNumber = 0;
			if(feature.getType() == "STRING") {
				childNumber = getSuitableInterval(featureData.toString(), feature.getCategories());
			} else {
				childNumber = getSuitableInterval(Double.parseDouble(featureData.toString()), feature.getMin(), feature.getMax(), INTERVALS_FOR_CONTINUOUS_DATA);
			}
			
			List<Sample> childSamples;
			if((childSamples = splitList.get(childNumber)) == null) {
				childSamples = new ArrayList<Sample>();
			}
			childSamples.add(sample);
			splitList.put(childNumber, childSamples);
			
			Map<Integer, Integer> childClassSplits;
			if((childClassSplits = childrenClassSplits.get(childNumber)) == null) {
				childClassSplits = new HashMap<Integer, Integer>();
			}
			Integer count;
			if((count = childClassSplits.get(classId)) == null) {
				childClassSplits.put(classId, 1);
			} else {
				childClassSplits.put(classId, ++count);
			}
			childrenClassSplits.put(childNumber, childClassSplits);
		}
		
		return splitList;
	}
	
	/**
	 * Get best split value for given feature id
	 * @param samples
	 * @param featureId
	 * @param classIdsSet1
	 * @param classIdsSet2
	 * @return
	 */
	/*
	private static double getBestSplitValue(List<Sample> samples, int featureId) {
		Feature feature = features.get(featureId);
		double min = feature.getMin(), max = feature.getMax();
		double diff = max - min;
		double incr = diff / 6;
		Map<Integer, Integer> classIdsSet1, classIdsSet2;
		List<Sample> set1, set2;
		double gini = Double.MAX_VALUE;
		double optimumSplitValue = min;
		for(int i=0; i<5; i++) {
			classIdsSet1 = new HashMap<>();
			classIdsSet2 = new HashMap<>();
			double splitValue = min + incr;
			List<List<Sample>> splitLists = getSplitLists(samples, splitValue, featureId, classIdsSet1, classIdsSet2);
			set1 = new ArrayList<Sample>();
			set2 = new ArrayList<Sample>();
			if(splitLists.size() > 1) {
				set1 = splitLists.get(0);
				set2 = splitLists.get(1);
			}
			double giniSplit = getGiniSplit(set1.size(), set2.size(), classIdsSet1, classIdsSet2);
			if(giniSplit < gini) {
				optimumSplitValue = splitValue;
				gini = giniSplit;
			}
		}
		return optimumSplitValue;
	}
	*/
	
	/**
	 * Calculate gini index of the split
	 * @param childrenSamplesCount
	 * @param childrenClassSplits
	 * @return
	 */
	private static double getGiniSplit(Map<Integer, List<Sample>> childrenSamplesCount, Map<Integer, Map<Integer, Integer>> childrenClassSplits) {
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
	private static double getGiniNode(int n, Map<Integer, Integer> classIdsSet) {
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
	private static double getGiniRoot(List<Sample> samples) {
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
	 * Calculate best matching class for given node to classify sample accordingly
	 * @param node
	 * @return
	 */
	private static int getMostMatchingClassId(Node node) {
		List<Sample> samples = node.getSamples();
		List<Integer> classCount = new ArrayList<>();
		for(int i=0; i<classIds.size(); i++) {
			classCount.add(0);
		}
		for(Sample sample: samples) {
			int count = classCount.get(sample.getGroundTruthClassId());
			classCount.set(sample.getGroundTruthClassId(), ++count);
		}
		int bestMatchId = 0, maxCount = -1;
		for(int i=0; i<classCount.size(); i++) {
			if(classCount.get(i) > maxCount) {
				bestMatchId = i;
				maxCount = classCount.get(i);
			}
		}
		return bestMatchId;
	}
	
	/**
	 * Get suitable interval for given value, based upon the number of intervals
	 * @param value
	 * @param min
	 * @param max
	 * @param intervals
	 * @return
	 */
	private static int getSuitableInterval(double value, double min, double max, int intervals) {
		double incr = (max - min) / intervals;
		for(int i=0; i<intervals; i++) {
			if((value > (min+i*incr)) && (value <= (min+(i+1)*incr))) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Get suitable interval for given value, matching the appropriate category
	 * @param value
	 * @param categories
	 * @return
	 */
	private static int getSuitableInterval(String value, Set<String> categories) {
		List<String> sortedList = new ArrayList<>(categories);
		Collections.sort(sortedList);
		for(int i=0; i<sortedList.size(); i++) {
			if(sortedList.get(i).equalsIgnoreCase(value)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Comparator to sort children in descending order based on gini index
	 */
	static class GiniComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			if(n1.getGiniIndex() > n2.getGiniIndex()) {
				return -1;
			} else if(n1.getGiniIndex() < n2.getGiniIndex()) {
				return 1;
			}
			return 0;
		}
	}
	
	private static final int INTERVALS_FOR_CONTINUOUS_DATA = 5;
}