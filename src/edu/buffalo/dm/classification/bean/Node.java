/**
 * Created by Siddharth Ghodke on Dec 4, 2015
 */
package edu.buffalo.dm.classification.bean;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to hold nodes for decision tree
 */
public class Node {

	private List<Sample> samples;
	private Node parent;
	private TreeSet<Node> children;
	private Set<Integer> classIds;
	private Feature splitFeature;
	double giniIndex;
	
	public Node(List<Sample> samples) {
		setSamples(samples);
		setParent(null);
		setChildren(new TreeSet<Node>(new GiniComparator()));
		setClassIds(new HashSet<Integer>());
	}
	
	public void addChild(Node child) {
		children.add(child);
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
	public TreeSet<Node> getChildren() {
		return children;
	}
	public void setChildren(TreeSet<Node> children) {
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
}

/**
 * Add child nodes based on gini index
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