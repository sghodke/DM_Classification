/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.util.ArrayList;
import java.util.Collections;
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
	

	/**
	 * Compute data for all features of the given samples
	 * (e.g. calculate min and max for continuous data
	 * and categories for ordinal/nominal data)
	 * @param samples
	 */
	public static void computeFeatures(List<Sample> samples) {
		features = new ArrayList<>();
		classIds = new HashSet<Integer>();
		boolean isFirst = true;
		
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
	
	/**
	 * Resets classification and other info for next use
	 * @param samples
	 */
	public static void resetData(List<Sample> samples) {
		for(Sample sample: samples) {
			sample.setClassId(-1);
		}
		for(Feature feature: features) {
			feature.setSelected(false);
		}
	}
	
	/**
	 * Get suitable interval for given value, based upon the number of intervals
	 * @param value
	 * @param min
	 * @param max
	 * @param intervals
	 * @return
	 */
	public static int getSuitableInterval(double value, double min, double max, int intervals) {
		double incr = (max - min) / intervals;
		for(int i=0; i<intervals; i++) {
			if((value > (min+i*incr)) && (value <= (min+(i+1)*incr))) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Get suitable interval for given value, matching the appropriate category
	 * @param value
	 * @param categories
	 * @return
	 */
	public static int getSuitableInterval(String value, Set<String> categories) {
		List<String> sortedList = new ArrayList<>(categories);
		Collections.sort(sortedList);
		for(int i=0; i<sortedList.size(); i++) {
			if(sortedList.get(i).equalsIgnoreCase(value)) {
				return i;
			}
		}
		return 0;
	}
	
	public static List<Feature> getFeatures() {
		return features;
	}
	
	public static Set<Integer> getClassIds() {
		return classIds;
	}
}
