/**
 * Created by Siddharth Ghodke on Dec 10, 2015
 */
package edu.buffalo.dm.classification.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buffalo.dm.classification.bean.Node;
import edu.buffalo.dm.classification.bean.Sample;
import edu.buffalo.dm.classification.util.ClassificationUtil;

/**
 * 
 */
public class RandomForest {

	private int featurePercent;
	private int trees;
	private List<Sample> samples;
	private List<Node> roots;
	
	public RandomForest(List<Sample> samples, int featurePercent, int trees) {
		this.featurePercent = featurePercent;
		this.trees = trees;
		this.samples = new ArrayList<>(samples);
		this.roots = new ArrayList<>();
	}
	
	/**
	 * Generate trees for the random forest
	 */
	public void generateRandomForest() {
		DecisionTree dt;
		int splitIndex = samples.size() * 80 / 100;
		List<Sample> trainSamples;
		for(int i=0; i<trees; i++) {
			Collections.shuffle(samples);
			trainSamples = samples.subList(0, splitIndex);
			dt = new DecisionTree(trainSamples, 3, 1);
			dt.setFeaturePercent(featurePercent);
			Node root = dt.generateTree();
			roots.add(root);
			ClassificationUtil.resetData(samples);
		}
	}
	
	/**
	 * Classify given samples
	 * @param samples
	 */
	public void classifySamples(List<Sample> samples) {
		Map<Integer, Integer> classificationMap;
		for(Sample sample: samples) {
			classificationMap = new HashMap<>();
			for(Node root: roots) {
				DecisionTree.classifySample(root, sample);
				int classId = sample.getClassId();
				Integer count;
				if((count = classificationMap.get(classId)) == null) {
					classificationMap.put(classId, 1);
				} else {
					classificationMap.put(classId, ++count);
				}
			}
			int max = -1;
			int classId = 0;
			for(int key: classificationMap.keySet()) {
				if(classificationMap.get(key) > max) {
					max = classificationMap.get(key);
					classId = key;
				}
			}
			sample.setClassId(classId);
		}
	}
	
	
}
