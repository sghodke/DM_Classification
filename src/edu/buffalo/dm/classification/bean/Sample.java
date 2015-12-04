/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.bean;

import java.util.List;

import edu.buffalo.dm.classification.adt.Data;

public class Sample {
	private int sampleId;
	private List<Data> features;
	private int groundTruthClassId;
	private int classId;
	
	public Sample(int sampleId, List<Data> features) {
		setSampleId(sampleId);
		setFeatures(features);
		setGroundTruthClassId(-1);
		setClassId(-1);
	}
	
	public Sample(int sampleId, List<Data> features, int groundTruthClassId) {
		setSampleId(sampleId);
		setFeatures(features);
		setGroundTruthClassId(groundTruthClassId);
		setClassId(-1);
	}
	
	public int getSampleId() {
		return sampleId;
	}
	public void setSampleId(int sampleId) {
		this.sampleId = sampleId;
	}
	public List<Data> getFeatures() {
		return features;
	}
	public void setFeatures(List<Data> features) {
		this.features = features;
	}
	public int getGroundTruthClassId() {
		return groundTruthClassId;
	}
	public void setGroundTruthClassId(int classId) {
		this.groundTruthClassId = classId;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
}
