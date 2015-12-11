package edu.buffalo.dm.classification.model;

import edu.buffalo.dm.classification.adt.StringData;
import edu.buffalo.dm.classification.bean.*;
import edu.buffalo.dm.classification.util.ClassificationUtil;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;
import java.util.Set;

/**
 * Created by Amit on 12/7/2015.
 */
public class NaiveBayes {
    // set of ground truth class ids
    private static Set<Integer> classIds;
    private static List<Feature> features = ClassificationUtil.getFeatures();
    private static int classCountOne = 0, classCountZero = 0;
    public static int totalSamples;
    public static List<Sample> trainingSamples, testSamples;
    SummaryStatistics summaryStatistics = new SummaryStatistics();

    public NaiveBayes(List<Sample> trainingList, List<Sample> testingList) {
        trainingSamples = trainingList;
        testSamples = testingList;
        classCountOne = getClassCount(1);
        classCountZero = getClassCount(0);

    }

    public static int getClassCount(int classNumber) {
        int count = 0;
        for (Sample s : trainingSamples) {
            if (s.getGroundTruthClassId() == classNumber) {
                count++;
            }
        }
        return count;
    }

    public static double getMeanForClass(int featureNumber, int classNumber) {
        SummaryStatistics sumStat = new SummaryStatistics();
        for (Sample s : trainingSamples) {
            if (s.getGroundTruthClassId() == classNumber) {
                sumStat.addValue(Double.valueOf(s.getFeatures().get(featureNumber).toString()));
            }
        }
        return sumStat.getMean();
    }

    public static double getStdDeviation(int featureNumber, int classNumber) {
        SummaryStatistics sumStat = new SummaryStatistics();
        for (Sample s : trainingSamples) {
            if (s.getGroundTruthClassId() == classNumber) {
                sumStat.addValue(Double.valueOf(s.getFeatures().get(featureNumber).toString()));
            }
        }
        return sumStat.getStandardDeviation();
    }

    public void assignClassesToTestSamples() {
        for (Sample s : testSamples) {
            s.setClassId(getProbabilityForSample(s, 1) > getProbabilityForSample(s, 0) ? 1 : 0);
        }

    }

    private double getProbabilityForSample(Sample s, int classNumber) {
        double probabilityForClass = 1.0;
        for (int i = 0; i < s.getFeatures().size() - 1; i++) {
            if (s.getFeatures().get(i) instanceof StringData) {
                probabilityForClass *= getFeatureProbability(i, classNumber, s.getFeatures().get(i).toString());
            } else {
                probabilityForClass *= getFeatureProbability(i, classNumber, Double.valueOf(s.getFeatures().get(i).toString()));
            }
        }
        return probabilityForClass;
    }

    /**
     * Returns probability for a feature of type double
     *
     * @param featureNumber - the index of feature in the feature list
     * @param classNumber   - the value of class (in our case, 0 or 1)
     * @param featureValue  - the actual value of the feature
     * @return the probability of getting the class with given class value for the current feature
     */
    public double getFeatureProbability(int featureNumber, int classNumber, double featureValue) {
        double mean = getMeanForClass(featureNumber, classNumber);
        double stdDeviation = getStdDeviation(featureNumber, classNumber);
        NormalDistribution normalDistribution = new NormalDistribution(mean, stdDeviation);
        return normalDistribution.density(featureValue);
    }

    /**
     * Returns probability for a feature of type string
     *
     * @param featureNumber - the index of feature in the feature list
     * @param classNumber   - the value of class (in our case, 0 or 1)
     * @param featureValue  - the actual value of the feature
     * @return the probability of getting the class with given class value for the current feature
     */
    public double getFeatureProbability(int featureNumber, int classNumber, String featureValue) {
        int stringFeaturesWithClassNumber = 0;
        for (Sample s : trainingSamples) {
            if ((s.getGroundTruthClassId() == classNumber) && (s.getFeatures().get(featureNumber).toString().equals(featureValue))) {
                stringFeaturesWithClassNumber++;
            }
        }

        int countForClassNumber = (classNumber == 1 ? classCountOne : classCountZero);

        return (double) stringFeaturesWithClassNumber / countForClassNumber;
    }

    /**
     * calculates accuracy for the complete data set
     *
     * @return - the accuracy
     */
    public double returnAccuracy() {
        int correctClassification = 0;

        for (Sample s : trainingSamples) {
            if (s.getGroundTruthClassId() == s.getClassId()) {
                correctClassification++;
            }
        }

        return (double) correctClassification / trainingSamples.size();
    }
}

