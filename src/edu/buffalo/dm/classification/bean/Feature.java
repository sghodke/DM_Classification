/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.buffalo.dm.classification.adt.Data;

public class Feature {
	
	private int featureId;
	private String type;
	// the actual data for this feature for all the samples. Redundant, may remove
	private List<Data> data;
	// valid if the feature is continuous
	private double min;
	private double max;
	// valid if the feature is ordinal/nominal
	private Set<String> categories;
	private boolean selected;
	
	public Feature(int featureId, String type) {
		setFeatureId(featureId);
		setType(type);
		setData(new ArrayList<Data>());
		setMin(Double.MAX_VALUE);
		setMax(Double.MIN_VALUE);
		setCategories(new HashSet<String>());
		setSelected(false);
	}
	
	public void addCategory(String category) {
		categories.add(category);
	}
	
	public void addData(Data d) {
		data.add(d);
	}
	
	public int getFeatureId() {
		return featureId;
	}
	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Data> getData() {
		return data;
	}
	public void setData(List<Data> data) {
		this.data = data;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
