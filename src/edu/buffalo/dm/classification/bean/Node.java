/**
 * Created by Siddharth Ghodke on Dec 4, 2015
 */
package edu.buffalo.dm.classification.bean;

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
	private double giniIndex;
	private double entropy;
	private double maxMajority;
	
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
	public double getEntropy() {
		return entropy;
	}
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}
}
