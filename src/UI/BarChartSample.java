package UI;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 *
 * An advanced bar chart with a variety of controls.
 *
 * @see javafx.scene.chart.BarChart
 * @see javafx.scene.chart.Chart
 * @see javafx.scene.chart.NumberAxis
 * @see javafx.scene.chart.XYChart
 */
public class BarChartSample extends Application {

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        root.getChildren().add(createChart());
    }

    protected BarChart<String, Number> createChart() {
        //final String[] years = {"2007", "2008", "2009"};
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis));
        final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        // setup chart
        bc.setTitle("Advanced Bar Chart");
        xAxis.setLabel("Year");
        //xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(years)));
        yAxis.setLabel("Price");
        // add starting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        series1.setName("Data Series 1");
//        XYChart.Series<String,Number> series2 = new XYChart.Series<String,Number>();
//        series2.setName("Data Series 2");
//        XYChart.Series<String,Number> series3 = new XYChart.Series<String,Number>();
//        series3.setName("Data Series 3");
        // create sample data
        series1.getData().add(new XYChart.Data<String, Number>("0", 567));
        series1.getData().add(new XYChart.Data<String, Number>("1", 1292));
        series1.getData().add(new XYChart.Data<String, Number>("2", 2180));

        //List<Integer> list = new ArrayList<Integer>();
        int max = 0;
        for (int i = 0; i < 1000; i++) {
            Random r = new Random();
            int Low = 0;
            int High = 3000;
            int Result = r.nextInt(High - Low) + Low;
            max = Math.max(max, Result);
//            list.add(Result);
            series1.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), Result));
        }

//        series2.getData().add(new XYChart.Data<String,Number>(years[0], 956));
//        series2.getData().add(new XYChart.Data<String,Number>(years[1], 1665));
//        series2.getData().add(new XYChart.Data<String,Number>(years[2], 2450));
//        series3.getData().add(new XYChart.Data<String,Number>(years[0], 800));
//        series3.getData().add(new XYChart.Data<String,Number>(years[1], 1000));
//        series3.getData().add(new XYChart.Data<String,Number>(years[2], 2800));
        bc.getData().add(series1);
//        bc.getData().add(series2);
//        bc.getData().add(series3);
        return bc;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
