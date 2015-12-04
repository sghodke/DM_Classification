/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.adt;

public class DoubleData implements Data {
	
	private Double data;
	
	public DoubleData(Double data) {
		this.data = data;
	}
	
	public Double getData() {
		return data;
	}
}
