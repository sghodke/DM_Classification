/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.adt;

public class StringData implements Data {
	
	private String data;
	
	public StringData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}
	
	public String toString() {
		return data;
	}

}
