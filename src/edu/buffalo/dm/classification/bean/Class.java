/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.bean;

import java.util.ArrayList;
import java.util.List;

public class Class {

	private int classId;
	private String classLabel;
	private List<Sample> samples;
	
	public Class(int classId) {
		setClassId(classId);
		setSamples(new ArrayList<Sample>());
	}
	
	public Class(int classId, String classLabel) {
		setClassId(classId);
		setClassLabel(classLabel);
		setSamples(new ArrayList<Sample>());
	}
	
	public Class(int classId, String classLabel, List<Sample> samples) {
		setClassId(classId);
		setClassLabel(classLabel);
		setSamples(samples);
	}
	
	public void addSample(Sample sample) {
		samples.add(sample);
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(String classLabel) {
		this.classLabel = classLabel;
	}

	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
}
