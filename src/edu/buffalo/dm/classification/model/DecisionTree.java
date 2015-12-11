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
import edu.buffalo.dm.classification.util.Measure;

public class DecisionTree {
	
	private static int intervals = 3;
	private static int gini;
	private Node root;
	private static Set<Integer> classIds;
	private List<Feature> features;
	private List<Sample> samples;
	private int totalSamples;
	private int treeDepth;
	private int featurePercent;
	// testing purpose
	@SuppressWarnings("unused")
	private int numberOfNodes;
	
	public DecisionTree(List<Sample> samples, int numberOfIntervals, int isGini) {
		features = ClassificationUtil.getFeatures();
		//Collections.shuffle(features);
		classIds = ClassificationUtil.getClassIds();
		this.samples = samples;
		numberOfNodes = 1;
		treeDepth = 1;
		totalSamples = samples.size();
		featurePercent = -1;
		root = new Node(samples);
		root.setParent(null);
		root.setClassIds(classIds);
		root.setGiniIndex(Measure.getGiniRoot(samples));
		root.setEntropy(Measure.getEntropyRoot(samples));
		gini = isGini;
		intervals = numberOfIntervals;
	}
	/**
	 * Build tree and return node
	 * @return
	 */
	public Node generateTree() {
		root = buildTree(root, samples);
		//System.out.println("Total nodes: " + numberOfNodes);
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
	public static void classifySample(Node node, Sample sample) {
		if(node == null) {
			return;
		}
		while(node.getChildren() != null && node.getChildren().keySet().size() != 0) {
			Feature splitFeature = node.getSplitFeature();
			int childNumber = 0;
			String sampleFeatureData = sample.getFeatures().get(splitFeature.getFeatureId()).toString();
			if("STRING".equals(splitFeature.getType())) {
				childNumber = ClassificationUtil.getSuitableInterval(sampleFeatureData, splitFeature.getCategories());
			} else {
				childNumber = ClassificationUtil.getSuitableInterval(Double.parseDouble(sampleFeatureData), splitFeature.getMin(), splitFeature.getMax(), intervals);
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
	private Node buildTree(Node node, List<Sample> samples) {
		
		// restrict height of tree
		if(treeDepth++ > 4) {
			return node;
		}
		if(node.getClassIds().size() < 2) {
			return node;
		}
		
		// if all samples belong to same class, do not split this node
		if(node.getClassIds().size() == 1) {
			return node;
		}
		
		// if number of samples is < 2% of total samples, do not split this node
		if(samples.size() < new Double(0.02*totalSamples).intValue()) {
			return node;
		}
		
		Feature feature = getBestSplitFeature(node, samples);
		if(feature == null) {
			return node;
		}
		node.setSplitFeature(feature);
		Map<Integer, Map<Integer, Integer>> childrenClassSplits = new HashMap<>();
		Map<Integer, List<Sample>> splitLists = getSplitLists(samples, feature, childrenClassSplits);
		
		double nodeGini = node.getGiniIndex();
		boolean smallerGini = false;
		boolean doNotSplit = false;
		numberOfNodes += splitLists.keySet().size();
		for(int i: splitLists.keySet()) {
			List<Sample> childSamples = splitLists.get(i);
			// if 99% samples are contained at one child, do not partition this node
			if(childSamples.size() >= new Double(0.99*samples.size()).intValue()) {
				doNotSplit = true;
				break;
			}
				
			Node child = new Node(childSamples);
			child.setParent(node);
			double childGini = Measure.getGiniNode(childSamples.size(), childrenClassSplits.get(i));
			if(childGini < nodeGini) {
				smallerGini = true;
			}
			child.setGiniIndex(childGini);
			double childEntropy = Measure.getEntropyNode(childSamples.size(), childrenClassSplits.get(i));
			child.setEntropy(childEntropy);
			Set<Integer> childClassIds = new HashSet<>();
			for(Sample sample: childSamples) {
				childClassIds.add(sample.getGroundTruthClassId());
			}
			child.setClassIds(childClassIds);
			node.addChild(i, child);
		}

		// check if any child node has better gini (less) than current node, else do not expand this node
		if(!smallerGini || doNotSplit) {
			node.setChildren(new HashMap<>());
			feature.setSelected(false);
			numberOfNodes -= splitLists.keySet().size();
			return node;
		}
		
		//setChildrenMajority(node.getChildren(), childrenClassSplits);
		
		List<Node> children = new ArrayList<Node>(node.getChildren().values());
		if(gini == 1) {
			Collections.sort(children, new GiniComparator(samples.size()));
		} else {
			Collections.sort(children, new EntropyComparator());	
		}
		Iterator<Node> iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Node child = iterator.next();
			buildTree(child, child.getSamples());
		}
		treeDepth--;
		return node;
	}
	
	/**
	 * Finds the feature to split the current node on, depending upon lowest gini split value
	 * @param node
	 * @param samples
	 * @return
	 */
	private Feature getBestSplitFeature(Node node, List<Sample> samples) {
	
		Feature bestFeature = null;
		double gini = Double.MAX_VALUE;
		double infoGain = Double.MIN_VALUE;
		
		int featureSet = features.size();
		if(featurePercent != -1) {
			featureSet = features.size() * featurePercent / 100;
			Collections.shuffle(features);
		}
		
		Map<Integer, Map<Integer, Integer>> childrenClassSplits;
		for(int i=0; i<featureSet; i++) {
			Feature feature = features.get(i);
			if(feature.isSelected()) {
				continue;
			}
			childrenClassSplits = new HashMap<>();
			Map<Integer, List<Sample>> splitLists = getSplitLists(samples, feature, childrenClassSplits);
			if(gini == 1) {
				double giniSplit = Measure.getGiniSplit(splitLists, childrenClassSplits);
				if(giniSplit < gini) {
					gini = giniSplit;
					bestFeature = feature;
				}
			}
			else {
				double entropySplit = Measure.getEntropySplit(splitLists, childrenClassSplits);
				double gain = node.getEntropy() - entropySplit;
				if(gain > infoGain) {
					infoGain = gain;
					bestFeature = feature;
				}
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
				childNumber = ClassificationUtil.getSuitableInterval(featureData.toString(), feature.getCategories());
			} else {
				childNumber = ClassificationUtil.getSuitableInterval(Double.parseDouble(featureData.toString()), feature.getMin(), feature.getMax(), intervals);
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
	 * Comparator to sort children in descending order based on weighted gini index
	 */
	static class GiniComparator implements Comparator<Node> {
		int n;	// total samples for parent node
		GiniComparator(int n) {
			this.n = n;
		}
		@Override
		public int compare(Node n1, Node n2) {
			if((n1.getSamples().size() * n1.getGiniIndex() / n) > (n2.getSamples().size() * n2.getGiniIndex() / n)) {
				return -1;
			} else if((n1.getSamples().size() * n1.getGiniIndex() / n) < (n2.getSamples().size() * n2.getGiniIndex() / n)) {
				return 1;
			}
			return 0;
		}
	}
	
	/**
	 * Comparator to sort children in descending order based on weighted gini index
	 */
	static class EntropyComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			if(n1.getSamples().size() * n1.getGiniIndex() > n2.getSamples().size() * n2.getGiniIndex()) {
				return -1;
			} else if(n1.getSamples().size() * n1.getGiniIndex() < n2.getSamples().size() * n2.getGiniIndex()) {
				return 1;
			}
			return 0;
		}
	}
	
	/**
	 * Comparator to sort children in ascending order based on majority class samples
	 */
	static class MajorityComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			if(n1.getMaxMajority() < n2.getMaxMajority()) {
				return -1;
			} else if(n1.getMaxMajority() > n2.getMaxMajority()) {
				return 1;
			}
			return 0;
		}
	}
	
	public void setFeaturePercent(int featurePercent) {
		this.featurePercent = featurePercent;
	}
	
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
}
