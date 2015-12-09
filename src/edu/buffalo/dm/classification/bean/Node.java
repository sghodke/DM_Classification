/**
 * Created by Siddharth Ghodke on Dec 4, 2015
 */
package edu.buffalo.dm.classification.bean;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to hold nodes for decision tree
 */
public class Node {

	private List<Sample> samples;
	private Node parent;
	private Map<Integer, Node> children;
	private Set<Integer> classIds;
	private Feature splitFeature;
	double giniIndex;
	double maxMajority;
	
	public Node(List<Sample> samples) {
		setSamples(samples);
		setParent(null);
		setChildren(new HashMap<Integer, Node>());
		setClassIds(new HashSet<Integer>());
		setGiniIndex(-1);
		setMaxMajority(0);
	}
	
	public void addChild(int n, Node child) {
		children.put(n, child);
	}
	/*
	public void setChildren(int n) {
		for(int i=0; i<n; i++) {
			children.add(new Node());
		}
	}*/
	
	public List<Sample> getSamples() {
		return samples;
	}
	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public Map<Integer, Node> getChildren() {
		return children;
	}
	public void setChildren(Map<Integer, Node> children) {
		this.children = children;
	}
	public Set<Integer> getClassIds() {
		return classIds;
	}
	public void setClassIds(Set<Integer> classIds) {
		this.classIds = classIds;
	}
	public Feature getSplitFeature() {
		return splitFeature;
	}
	public void setSplitFeature(Feature splitFeature) {
		this.splitFeature = splitFeature;
	}
	public double getGiniIndex() {
		return giniIndex;
	}
	public void setGiniIndex(double giniIndex) {
		this.giniIndex = giniIndex;
	}
	public double getMaxMajority() {
		return maxMajority;
	}
	public void setMaxMajority(double maxMajority) {
		this.giniIndex = maxMajority;
	}
}

/**
 * Add child nodes based on gini index
 * (unused for now)
 */
class GiniComparator implements Comparator<Node> {
	@Override
	public int compare(Node n1, Node n2) {
		if(n1.getGiniIndex() > n2.getGiniIndex()) {
			return 1;
		} else if(n1.getGiniIndex() < n2.getGiniIndex()) {
			return -1;
		}
		return 0;
	}
}