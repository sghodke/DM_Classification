/**
 * Created by Siddharth Ghodke on Dec 3, 2015
 */
package edu.buffalo.dm.classification.util;

import java.util.List;

import edu.buffalo.dm.classification.bean.Sample;

public class Measure {

	static int[][] confusionMatrix;
	public static double entropy() {

		return -1;
	}

	/**
	 * Modified fMeasure based on provided datasets
	 * (Not foolproof or general)
	 * @param samples
	 * @return
	 */
	public static double fMeasure(List<Sample> samples) {
		double fMeasure = -1d;
		int classes = ClassificationUtil.getClassIds().size();
		confusionMatrix = new int[classes][classes];
		for(int i=0; i<classes; i++) {
			for(int j=0; j<classes; j++) {
				confusionMatrix[i][j] = 0;
			}
		}
		for(Sample sample: samples) {
			int groundTruthClassId = sample.getGroundTruthClassId();
			int classId = sample.getClassId();
			confusionMatrix[groundTruthClassId][classId]++;
		}
		
		int a, b, c;
		a = b = c = 0;
		for(int i=0; i<classes; i++) {
			for(int j=0; j<classes; j++) {
				if(i == j) {
					a += confusionMatrix[i][j];
				} else {
					b += confusionMatrix[i][j];
					c += confusionMatrix[j][i];
				}
			}
		}
		fMeasure = (double)a / (b+c);
		return fMeasure;
	}
}
