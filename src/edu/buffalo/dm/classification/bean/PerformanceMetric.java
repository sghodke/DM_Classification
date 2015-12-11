/**
 * Created by Siddharth Ghodke on Dec 10, 2015
 */
package edu.buffalo.dm.classification.bean;

public class PerformanceMetric {
	
	private double accuracy;
	private double fmeasure;
	private double precision;
	private double recall;
	
	public PerformanceMetric(double accuracy, double fMeasure, double precision, double recall) {
		setAccuracy(accuracy);
		setFmeasure(fMeasure);
		setPrecision(precision);
		setRecall(recall);
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public double getFmeasure() {
		return fmeasure;
	}
	public void setFmeasure(double fmeasure) {
		this.fmeasure = fmeasure;
	}
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
}
