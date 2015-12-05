/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.buffalo.dm.classification.bean.Feature;
import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.util.ClassificationUtil;

public class DecisionTree {
	// set of ground truth class ids
	private static Set<Integer> classIds;
	private static List<Feature> features;
	
	public Node classify(List<Sample> samples) {
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
	 * Recursively build a decision tree for given samples
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
		double splitValue = (feature.getMax() + feature.getMin()) / 2;
		List<Map<Integer, Integer>> classIdsList = new ArrayList<>();
		classIdsList.add(new HashMap<>());
		classIdsList.add(new HashMap<>());
		List<List<Sample>> splitLists = getSplitLists(samples, splitValue, feature.getFeatureId(), classIdsList.get(0), classIdsList.get(1));
		for(int i=0; i<splitLists.size(); i++) {
			List<Sample> childSamples = splitLists.get(i);
			Node child = new Node(childSamples);
			child.setParent(node);
			double childGini = getGiniNode(childSamples.size(), classIdsList.get(i));
			child.setGiniIndex(childGini);
			Set<Integer> childClassIds = new HashSet<>();
			for(Sample sample: childSamples) {
				childClassIds.add(sample.getGroundTruthClassId());
			}
			child.setClassIds(childClassIds);
			node.addChild(child);
		}
		Set<Node> children = (TreeSet<Node>) node.getChildren().descendingSet();
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
		
		List<Sample> set1, set2;
		Map<Integer, Integer> classIdsSet1, classIdsSet2;
		for(int i=0; i<features.size(); i++) {
			Feature feature = features.get(i);
			if(feature.isSelected()) {
				continue;
			}
			classIdsSet1 = new HashMap<Integer, Integer>();
			classIdsSet2 = new HashMap<Integer, Integer>();
			double splitValue = (feature.getMax() + feature.getMin()) / 2;
			List<List<Sample>> splitLists = getSplitLists(samples, splitValue, i, classIdsSet1, classIdsSet2);
			
			set1 = new ArrayList<Sample>();
			set2 = new ArrayList<Sample>();
			if(splitLists.size() > 1) {
				set1 = splitLists.get(0);
				set2 = splitLists.get(1);
			}
			double giniSplit = getGiniSplit(set1.size(), set2.size(), classIdsSet1, classIdsSet2);
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
	 * Solits the given samples on the feature id and splitValue provided
	 * @param samples
	 * @param splitValue
	 * @param featureId
	 * @param classIdsSet1
	 * @param classIdsSet2
	 * @return List of all the splits (Currently only 2 splits based on binary)
	 */
	private static List<List<Sample>> getSplitLists(List<Sample> samples, double splitValue, int featureId, Map<Integer, Integer> classIdsSet1, Map<Integer, Integer> classIdsSet2) {
		List<Sample> set1 = new ArrayList<Sample>();
		List<Sample> set2 = new ArrayList<Sample>();
		
		for(Sample sample: samples) {
			int classId = sample.getGroundTruthClassId();
			Integer count;
			if(Double.parseDouble(sample.getFeatures().get(featureId).toString()) < splitValue) {
				set1.add(sample);
				if((count = classIdsSet1.get(classId)) == null) {
					classIdsSet1.put(classId, 1);
				} else {
					classIdsSet1.put(classId, ++count);
				}
			} else {
				set2.add(sample);
				if((count = classIdsSet2.get(classId)) == null) {
					classIdsSet2.put(classId, 1);
				} else {
					classIdsSet2.put(classId, ++count);
				}
			}
		}
		List<List<Sample>> splitLists = new ArrayList<>();
		splitLists.add(set1);
		splitLists.add(set2);
		return splitLists;
	}
	
	/**
	 * Calculate gini index of the split 
	 * @param n1
	 * @param n2
	 * @param classIdsSet1
	 * @param classIdsSet2
	 * @return
	 */
	private static double getGiniSplit(int n1, int n2, Map<Integer, Integer> classIdsSet1, Map<Integer, Integer> classIdsSet2) {
		int n = n1 + n2;
		double gini = -1d;
		double giniN1, giniN2;
		giniN1 = getGiniNode(n1, classIdsSet1);
		giniN2 = getGiniNode(n2, classIdsSet2);		
		gini = (n1 * giniN1 / n) + (n2 * giniN2 / n);
		return gini;
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
}
