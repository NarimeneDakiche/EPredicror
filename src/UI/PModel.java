/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import weka.classifiers.Evaluation;

/**
 *
 * @author ado_k
 */
public class PModel implements Serializable {

//    private static final long serialVersionUID = -5587568328222L;

//    @Override
//    public String toString() {
//        String value = this.snapshotDuration + this.nbSnapshots
//                + this.overlapping
//                + this.detectionMethod + this.detectionParameters + this.attributesList
//                + this.evolutionMethod + this.evolutionParameters + this.attributesPrediction 
//                + this.chainLength + this.selectionMethod
//                + this.Evaluator + this.Search + this.classifier
//                + this.validationMethod + this.validationParam;
//        return value;
//    }
    String snapshotDuration ="";
    int nbSnapshots=0;
    double overlapping=0;
    //Include temporelDistributionImg

    /**
     * Detection*
     */
    String detectionMethod="";
    int detectionParameters=0;

    /**
     * Calculated attributes*
     */
    String[] attributesList = new String[1]; // <--initialized statement;

    /**
     * Evolution*
     */
    String evolutionMethod="";
    String evolutionParameters="";

    /**
     * Prediction*
     */
    String[] attributesPrediction = new String[1]; // <--initialized statement;
    int chainLength=0;
    String selectionMethod="";
    String Evaluator="";
    String Search="";
    String classifier="";
    String validationMethod="";
    String validationParam="";

    public PModel() {

    }

    public PModel(String snapshotDuration, int nbSnapshots, double overlapping, String detectionMethod, int detectionParameters, String[] attributesList, String evolutionMethod, String evolutionParameters, String[] attributesPrediction, int chainLength, String selectionMethod, String Evaluator, String Search, String classifier, String validationMethod, String validationParam) {
        this.snapshotDuration = snapshotDuration;
        this.nbSnapshots = nbSnapshots;
        this.overlapping = overlapping;
        this.detectionMethod = detectionMethod;
        this.detectionParameters = detectionParameters;
        this.attributesList = attributesList;
        this.evolutionMethod = evolutionMethod;
        this.evolutionParameters = evolutionParameters;
        this.attributesPrediction = attributesPrediction;
        this.chainLength = chainLength;
        this.selectionMethod = selectionMethod;
        this.Evaluator = Evaluator;
        this.Search = Search;
        this.classifier = classifier;
        this.validationMethod = validationMethod;
        this.validationParam = validationParam;
    }

    public String getSnapshotDuration() {
        return snapshotDuration;
    }

    public void setSnapshotDuration(String snapshotDuration) {
        this.snapshotDuration = snapshotDuration;
    }

    public double getOverlapping() {
        return overlapping;
    }

    public void setOverlapping(double overlapping) {
        this.overlapping = overlapping;
    }

    public String getDetectionMethod() {
        return detectionMethod;
    }

    public void setDetectionMethod(String detectionMethod) {
        this.detectionMethod = detectionMethod;
    }

    public int getDetectionParameters() {
        return detectionParameters;
    }

    public void setDetectionParameters(int detectionParameters) {
        this.detectionParameters = detectionParameters;
    }

    public String[] getAttributesList() {
        return attributesList;
    }

    public void setAttributesList(String[] attributesList) {
        this.attributesList = attributesList;
    }

    public String getEvolutionMethod() {
        return evolutionMethod;
    }

    public void setEvolutionMethod(String evolutionMethod) {
        this.evolutionMethod = evolutionMethod;
    }

    public String getEvolutionParameters() {
        return evolutionParameters;
    }

    public void setEvolutionParameters(String evolutionParameters) {
        this.evolutionParameters = evolutionParameters;
    }

    public String[] getMetrics() {
        return attributesPrediction;
    }

    public void setMetrics(String[] attributesPrediction) {
        this.attributesPrediction = attributesPrediction;
    }

    public int getChainLength() {
        return chainLength;
    }

    public void setChainLength(int chainLength) {
        this.chainLength = chainLength;
    }

    public String getsnapshotDuration() {
        return snapshotDuration;
    }

    public void setsnapshotDuration(String snapshotDuration) {
        this.snapshotDuration = snapshotDuration;
    }

    public int getNbSnapshots() {
        return nbSnapshots;
    }

    public void setNbSnapshots(int nbSnapshots) {
        this.nbSnapshots = nbSnapshots;
    }

    public String getdetectionMethod() {
        return detectionMethod;
    }

    public void setdetectionMethod(String detectionMethod) {
        this.detectionMethod = detectionMethod;
    }

    public int getdetectionParameters() {
        return detectionParameters;
    }

    public void setdetectionParameters(int detectionParameters) {
        this.detectionParameters = detectionParameters;
    }

    public String getevolutionMethod() {
        return evolutionMethod;
    }

    public void setevolutionMethod(String evolutionMethod) {
        this.evolutionMethod = evolutionMethod;
    }

    public String getevolutionParameters() {
        return evolutionParameters;
    }

    public void setevolutionParameters(String evolutionParameters) {
        this.evolutionParameters = evolutionParameters;
    }

    public String[] getattributesPrediction() {
        return attributesPrediction;
    }

    public void setattributesPrediction(String[] attributesPrediction) {
        this.attributesPrediction = attributesPrediction;
    }

    public String getSelectionMethod() {
        return selectionMethod;
    }

    public void setSelectionMethod(String selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public String getEvaluator() {
        return Evaluator;
    }

    public void setEvaluator(String Evaluator) {
        this.Evaluator = Evaluator;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(String Search) {
        this.Search = Search;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getValidationParam() {
        return validationParam;
    }

    public void setValidationParam(String validationParam) {
        this.validationParam = validationParam;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

}
