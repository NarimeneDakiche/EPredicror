/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EvaluationReport;

import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import org.graphstream.graph.Graph;

/**
 *
 * @author ado_k
 */
public class ResultsStats {

    private int nbSnaps=0;
    private double averageSnapSize=0;
    private int nbNodes=0;
    private int nbEdges=0;
    private String linkReference="";
    private String description="";
    private String averageDegree="";
    private String maxDegree="";
    private double averageClusteringCoeff=0;
//    private String diameter;

    private String distribution=""; /*PNG Distribution file name; Equals "" if the file wasn't generated.*/

    private int totalNbCommunities=0;
    private double averageNbCommunitiesPerSnap=0;
    private double averageCommSize=0;

    private String evolutionResults="";  /*PNG Evolution results file name; Equals "" if the file wasn't generated.*/

    private String predictionResults="";

    /*public void saveAsPng(BarChart barChart, PieChart pieChart) {
     try {
     WritableImage image = barChart.snapshot(new SnapshotParameters(), null);
     // TODO: probably use a file chooser here
     File file = new File("barchart.png");

     ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
     } catch (IOException e) {
     // TODO: handle exception here
     //e.printStackTrace();
     } catch (NullPointerException e) {

     }
     try {
     WritableImage image = barChart.snapshot(new SnapshotParameters(), null);
     image = pieChart.snapshot(new SnapshotParameters(), null);
     // TODO: probably use a file chooser here
     File file = new File("pieChart.png");
     ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
     } catch (IOException e) {
     //e.printStackTrace();
     // TODO: handle exception here

     } catch (NullPointerException e) {

     }
     }*/
    public String saveAsPng(Chart chart, String pngName) {
        try {
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            // TODO: probably use a file chooser here
            File file = new File(pngName);

            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            return pngName;
        } catch (IOException e) {
            // TODO: handle exception here
            //e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public static double calculateAverageDouble(List<Double> marks) {
        double sum = 0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    public void setResults(LinkedList<TimeFrame> dynamicNet, BarChart barChart, PieChart pieChart, String predictionResults) {

        this.nbSnaps = dynamicNet.size();
        List<Integer> marks = new ArrayList<Integer>();
        for (TimeFrame tf : dynamicNet) {
            marks.add(tf.getTimGraph().getNodeCount());
        }
        this.averageSnapSize = calculateAverage(marks);
        this.nbNodes = 0;
        this.nbEdges = 0;
        for (TimeFrame tF : dynamicNet) {
            this.nbNodes += tF.getTimGraph().getNodeCount();
            this.nbEdges += tF.getTimGraph().getEdgeCount();
        }
        this.linkReference = linkReference;
        this.description = description;
        this.averageDegree = averageDegree;
        this.maxDegree = maxDegree;
        //this.diameter = diameter;

        this.distribution = saveAsPng(barChart, "barchart.png");
        this.evolutionResults = saveAsPng(pieChart, "pieChart.png");

        List<Integer> size = new ArrayList<Integer>();
        List<Integer> nbCommPerSnap = new ArrayList<Integer>();
        List<Double> averageClusteringCoefficient = new ArrayList<Double>();
        int commSum = 0;
        for (TimeFrame tf : dynamicNet) {

            nbCommPerSnap.add(tf.getCommunities().size());
            commSum += tf.getCommunities().size();
            for (Graph com : tf.getCommunities()) {
                averageClusteringCoefficient.add(com.getAttribute("averageClusteringCoefficient"));
                int commTaille = com.getNodeCount();
                size.add(commTaille);
            }
        }

        //DecimalFormat df = new DecimalFormat("#.###");
        //System.out.print(df.format(d));
        this.averageCommSize = calculateAverage(size);
        this.averageNbCommunitiesPerSnap = calculateAverage(nbCommPerSnap);
        this.totalNbCommunities = commSum;
        this.averageClusteringCoeff = calculateAverageDouble(averageClusteringCoefficient);
        this.predictionResults = predictionResults;
    }

    public int getNbSnaps() {
        return nbSnaps;
    }

    public double getAverageSnapSize() {
        return averageSnapSize;
    }

    public int getNbNodes() {
        return nbNodes;
    }

    public int getNbEdges() {
        return nbEdges;
    }

    public String getLinkReference() {
        return linkReference;
    }

    public String getDescription() {
        return description;
    }

    public String getAverageDegree() {
        return averageDegree;
    }

    public String getMaxDegree() {
        return maxDegree;
    }

    public double getAverageClusteringCoeff() {
        return averageClusteringCoeff;
    }

    public String getDistribution() {
        return distribution;
    }

    public int getTotalNbCommunities() {
        return totalNbCommunities;
    }

    public double getAverageNbCommunitiesPerSnap() {
        return averageNbCommunitiesPerSnap;
    }

    public double getAverageCommSize() {
        return averageCommSize;
    }

    public String getEvolutionResults() {
        return evolutionResults;
    }

    public String getPredictionResults() {
        return predictionResults;
    }

    public void setLinkReference(String linkReference) {
        this.linkReference = linkReference;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAverageDegree(String averageDegree) {
        this.averageDegree = averageDegree;
    }

    public void setMaxDegree(String maxDegree) {
        this.maxDegree = maxDegree;
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
