/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.buffalo.dm.classification.adt.Data;
import edu.buffalo.dm.classification.adt.DoubleData;
import edu.buffalo.dm.classification.adt.StringData;
import edu.buffalo.dm.classification.bean.Feature;
import edu.buffalo.dm.classification.bean.Sample;

/**
 * Generic class to serve utility methods
 */
public class ClassificationUtil {
	
	private static List<Feature> features;
	// ground truth class ids
	private static Set<Integer> classIds;
	static {
		features = new ArrayList<Feature>();
	}

	/**
	 * Compute data for all features of the given samples
	 * (e.g. calculate min and max for continuous data
	 * and categories for ordinal/nominal data)
	 * @param samples
	 */
	public static void computeFeatures(List<Sample> samples) {
		boolean isFirst = true;
		classIds = new HashSet<Integer>();
		
		for(Sample sample: samples) {
			classIds.add(sample.getGroundTruthClassId());
			for(int i=0; i<sample.getFeatures().size(); i++) {
				Data f = sample.getFeatures().get(i);
				Feature feature = null;
				if(isFirst) {
					if(f instanceof StringData) {
						feature = new Feature(i, "STRING");						
					} else if(f instanceof DoubleData) {
						feature = new Feature(i, "DOUBLE");
					}
					features.add(i, feature);
				}
				
				feature = features.get(i);
				feature.addData(f);
				if("STRING".equals(feature.getType())) {
					feature.addCategory(f.toString());
				} else {
					Double val = Double.parseDouble(f.toString());
					if(val > feature.getMax()) {
						feature.setMax(val);
					} else if(val < feature.getMin()) {
						feature.setMin(val);
					}
				}
			}
			isFirst = false;
		}
		
	}
	
	public static List<Feature> getFeatures() {
		return features;
	}
	
	public static Set<Integer> getClassIds() {
		return classIds;
	}
}
