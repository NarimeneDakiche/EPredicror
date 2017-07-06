/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import EvaluationReport.Benchmark;
import EvaluationReport.ResultsStats;
import EvaluationReport.PModel;
import Attributes.AttributesComputer;
import Prediction.EvaluationReport;
import Prediction.PredictionUtils;
import SnapshotsPrep.MinMaxResults;
import SnapshotsPrep.SnapshotsPrep;
import static SnapshotsPrep.SnapshotsPrep.splitInput;
import SnapshotsPrep.TimeLength;
import communityDetection.CPM;
import communityDetection.ExternMethods.CM;
import communityDetection.ExternMethods.CONCLUDE;
import communityDetection.ExternMethods.CONGA;
import communityDetection.ExternMethods.COPRA;
import communityDetection.ExternMethods.GN;
import communityDetection.ExternMethods.SLPA;
import evolutionIdentification.Asur;
import static evolutionIdentification.EvolutionUtils.writeEvolutionChain;
import evolutionIdentification.GED;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JComponent;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.openide.util.Exceptions;
//import org.openide.util.Exceptions;
import static prefuse.demos.AggregateDemo.demoComp;

/**
 *
 * @author ado_k
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea logTextArea;

    @FXML
    private TextArea resultsTextArea;

    @FXML
    private Accordion accordion1;

    @FXML
    private TitledPane titledpane1;

    @FXML
    private StackPane paneVisualize;

    @FXML
    private StackPane paneVisualizeIdent;

    @FXML
    private SwingNode swingNode;

    private TextArea visualizeFileTA;

    @FXML
    private ListView<String> listViewAttributes;

    @FXML
    private ComboBox<String> comboClassifier;

    @FXML
    private Spinner<Integer> chainLength;

    @FXML
    private Spinner<Integer> kFoldsSpinner;

    @FXML
    private ComboBox<String> comboSelectionAttributes;

    @FXML
    private Spinner<Integer> spinnerNbSnaps;

    @FXML
    private Group group;

    @FXML
    private Group groupFileVisualize;

    private ProgressBar p1;

    @FXML
    private Spinner<Integer> snipperDetection;

    @FXML
    private ComboBox<String> timeFormatCombo;

    @FXML
    private ComboBox<String> comboStructDonnees;

    @FXML
    private ComboBox<String> comboDetection;

    @FXML
    private ComboBox<String> comboSearchMethod;

    @FXML
    private ComboBox<String> comboEvaluationMethod;

    @FXML
    private CheckBox checkboxSplitMultiExport;

    @FXML
    private CheckBox checkboxDetection;

    @FXML
    private TextField splitExportName;

    @FXML
    private ComboBox<String> attributesCombo;

    @FXML
    private TabPane tabPaneVisible;

    private BarChart distBarChart;

    private PieChart evolPieChart;

    private File file;

    List<File> listFile;

    private String filePath = "";

    private String fileString = "";

    private String filePathEvolution;

    private File fileEvolution;

    private String filePathPrediction;

    private File filePrediction;

    @FXML
    private Button launchSplit;

    @FXML
    private Button buttonEReport;

    @FXML
    private MenuItem menuItemEReport;

    @FXML
    private Button launchDetection;

    @FXML
    private Button launchEvolution;

    @FXML
    private Button launchCalculation;

    @FXML
    private Button launchPrediction;

    @FXML
    private TextField tfOverlapping;

    @FXML
    private Label statusLabel;

    @FXML
    private Slider sliderOverlapping;

    @FXML
    private TextField durationsLabel;

    @FXML
    private ComboBox<String> comboEvolution;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TextField evolutionParameters;

    @FXML
    private RadioButton radioLogFile;

    @FXML
    private ListView<String> listView;

    private LinkedList<TimeFrame> dynamicNetwork = new LinkedList<TimeFrame>();

    private int nbSnapshots = 0;

    private TreeItem<String> root;

    private boolean directlyDetection = false;
    private boolean directlyEvolution = false;
    private boolean directlyPrediction = false;

    private List<String> selectedAttributes = new ArrayList<>();
    ObservableList<String> observableListAttibutes = FXCollections.observableList(selectedAttributes);

    private boolean attributesCalculated = false;

    private List<String> selectedAttributes1 = new ArrayList<>();
    ObservableList<String> observableListAttibutes1 = FXCollections.observableList(selectedAttributes1);

    private String directoryPath = "./ExportedResults/";
    private String totalPath = directoryPath;

    private String BDpath = directoryPath;
    private String BDfilename;
    private String tabname = "GED_evolution";
    private Map<String, Integer> mapIdentification;

    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    private Thread threadDetection, threadCalculate, threadIdentification, threadPrediction;

    private PModel pModel = new PModel();

    private EvaluationReport eReport;

    Stage primaryStage;

    String predictionResults = "";

    private int startingStep = 1; /* represents the phase the user started from, used in report generation later */


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // create results directory if it doesn't exist
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Initate "Export results" radio
        radioLogFile.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    prepareLogFile();

                } else {
                    // ...
                }
            }
        });

        // Initiate progress bar
        double y = 15;
        final double SPACING = 15;

        y += SPACING;
        p1 = new ProgressBar();
        p1.setPrefWidth(100);
        p1.setLayoutY(y);

        group.getChildren().addAll(p1);
        p1.setProgress(0.0);

        StackPane.setAlignment(group, Pos.CENTER);

        // Initiate overlapping slider
        sliderOverlapping.valueProperty()
                .addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue arg0, Object arg1, Object arg2
                    ) {
                        tfOverlapping.textProperty().setValue(
                                String.valueOf((int) sliderOverlapping.getValue()));
                    }
                }
                );

        tfOverlapping.textProperty()
                .addListener((obsValue, oldValue, newValue) -> {
                    try {
                        if (Integer.parseInt(newValue) <= 99 && Integer.parseInt(newValue) >= 0) {
                            tfOverlapping.setText(newValue);
                            sliderOverlapping.setValue(Double.parseDouble(newValue));
                        } else {
                            tfOverlapping.setText(oldValue);
                            sliderOverlapping.setValue(Double.parseDouble(oldValue));
                        }
                    } catch (Exception e) {
                    }
                });

        // Initiate time format combo box
        timeFormatCombo.getItems()
                .addAll("yyyy-MM-dd HH:mm:ss",
                        "Timestamp", "");
        timeFormatCombo.getSelectionModel()
                .select("Timestamp");
        timeFormatCombo.setCellFactory(lv
                -> {
                    ListCell<String> cell = new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                if (item.isEmpty()) {
                                    setText("Add...");
                                } else {
                                    setText(item);
                                }
                            }
                        }
                    };
                    cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                        if (cell.getItem().isEmpty() && !cell.isEmpty()) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Add element");
                            dialog.setHeaderText("Add element");
                            dialog.setContentText("Enter element...");
                            dialog.showAndWait().ifPresent(text -> {
                                int index = timeFormatCombo.getItems().size() - 1;
                                timeFormatCombo.getItems().add(index, text);
                                timeFormatCombo.getSelectionModel().select(index);
                            });
                            evt.consume();
                        }
                    });

                    return cell;
                }
        );

        // Initiate snapshots number snipper
        spinnerNbSnaps.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 100, 0));
        snipperDetection.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5));
        chainLength.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 4));
        kFoldsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 10));

        // Initiate Data structure ComboBox 
        comboStructDonnees.getItems()
                .addAll("TVW", "VWT", "TTVW", "VWXT", "");
        comboStructDonnees.setCellFactory(lv
                -> {
                    ListCell<String> cell = new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                            } else {
                                if (item.isEmpty()) {
                                    setText("Add...");
                                } else {
                                    setText(item);
                                }
                            }
                        }
                    };

                    cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                        if (cell.getItem().isEmpty() && !cell.isEmpty()) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Add element");
                            dialog.setHeaderText("Add element");
                            dialog.setContentText("Enter element...");
                            dialog.showAndWait().ifPresent(text -> {
                                int index = comboStructDonnees.getItems().size() - 1;
                                comboStructDonnees.getItems().add(index, text);
                                comboStructDonnees.getSelectionModel().select(index);
                            });
                            evt.consume();
                        }
                    });

                    return cell;
                }
        );
        comboStructDonnees.getSelectionModel()
                .select("TVW");

        // Initiate Attributes combo box
        attributesCombo.getItems()
                .addAll("bcentrality");
        attributesCombo.getSelectionModel()
                .select("bcentrality");

        // Initiate Detection methods combo box
        comboDetection.getItems()
                .addAll("CPM", "CM", "CONCLUDE", "CONGA", "COPRA", "GN", "SLPA");
        comboDetection.getSelectionModel()
                .select("CPM");
        comboDetection.valueProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue ov, String t, String t1
                    ) {
                        switch (comboDetection.getValue()) {
                            case "CM":
                            case "CONCLUDE":
                            case "COPRA":
                                snipperDetection.setDisable(true);
                                break;
                            case "SLPA":
                            case "CPM":
                            case "GN":
                            case "CONGA":
                                snipperDetection.setDisable(false);
                                break;
                            default:
                                System.out.println("How on earth did you get here?!");//84
                                break;
                        }
                    }
                }
                );

        // Initiate Evolution methods combo box
        comboEvolution.getItems()
                .addAll("GED", "Asur");
        comboEvolution.getSelectionModel()
                .select("GED");

        // Initiate classifier ComboBox
        comboClassifier.getItems()
                .addAll("naiveBayes", "bayesNet", "decisionTree", "svm", "randomForest", "decisionStump", "perceptron", "logisticRegression", "randomTree", "iBk",
                        "oneR", "bagging");
        comboClassifier.getSelectionModel()
                .select("decisionTree");

        // Initiate attributes selection Combo Box
        comboSelectionAttributes.getItems()
                .addAll("Filter", "Manual", "Wrapper");
        comboSelectionAttributes.getSelectionModel()
                .select("Filter");

        // Initiate Search method combo box
        comboSearchMethod.getItems()
                .addAll("GreedyStepwise", "BestFirst");
        comboSearchMethod.getSelectionModel()
                .select("GreedyStepwise");

        // Initiate evolution method combo box
        comboEvaluationMethod.getItems()
                .addAll("CfsSubsetEval", "WrapperSubsetEval");
        comboEvaluationMethod.getSelectionModel()
                .select("CfsSubsetEval");

        // Initiate Timeframes list picture
        Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("images/24-128.png")));
        root = new TreeItem<>("Snapshots", rootIcon);

        // Initiate Timeframes treeview
        treeView.setRoot(root);

        treeView.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);
        treeView.getSelectionModel()
                .selectedItemProperty().addListener(new ChangeListener() {

                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue
                    ) {
                        TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                        int indexSnap = 0, indexComm = 0;
                        try {
                            indexSnap = Integer.parseInt(selectedItem.getParent().getValue().replaceAll("[^0-9]", "")) - 1;
                            indexComm = Integer.parseInt(selectedItem.getValue().replaceAll("[^0-9]", "")) - 1;
                            Graph go = dynamicNetwork.get(indexSnap).getCommunities().get(indexComm); // do what ever you want 
                            String label = "id";
                            prefuse.data.Graph g = graphToPrefuseGraph(go, label);
                            //System.out.println(g.getNodeCount() + ";" + g.getEdgeCount());
                            swingNode.setContent(demoComp(g, label));
                        } catch (NumberFormatException | NullPointerException e) {
                            try {
                                indexSnap = Integer.parseInt(selectedItem.getValue().replaceAll("[^0-9]", "")) - 1;
                                Graph go = dynamicNetwork.get(indexSnap).getTimGraph();
                                Viewer viewer = new Viewer(go, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                                viewer.enableAutoLayout();
                                View view = viewer.addDefaultView(false);   // false indicates "no JFrame".
                                swingNode.setContent((JComponent) view);
                            } catch (NumberFormatException | NullPointerException e2) {
                            }
                        }
                    }

                    private prefuse.data.Graph graphToPrefuseGraph(Graph g, String label) {

                        prefuse.data.Graph gp = new prefuse.data.Graph();
                        gp.addColumn(label, String.class);
                        List<org.graphstream.graph.Node> listNode = new ArrayList<org.graphstream.graph.Node>(g.getNodeSet());
                        for (org.graphstream.graph.Node n : listNode) {
                            prefuse.data.Node n1 = gp.addNode();
                            n1.setString(label, n.getId());
                        }
                        for (org.graphstream.graph.Edge e : g.getEdgeSet()) {
                            gp.addEdge(listNode.indexOf(e.getSourceNode()), listNode.indexOf(e.getTargetNode()));
                        }
                        return gp;
                    }
                }
                );

        // Initiate default selected attributes
        String[] strs = {"size", "averageDegree", "averageClusteringCoefficient",
            "degreeAverageDeviation",
            "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity",
            "InOutTotalDegree", "ClosenessCentrality"};
        selectedAttributes.addAll(Arrays.asList(strs));
        selectedAttributes.remove("Centroid");
        selectedAttributes.remove("ClosenessCentrality");

        // ListView Attributes 
        String[] attributes = {"size", "averageDegree", "averageClusteringCoefficient", "degreeAverageDeviation", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};

        // Initiate list view check boxs
        listView.getItems().addAll(attributes);
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        if (!observableListAttibutes.contains(item)) {
                            observableListAttibutes.add(item);
                        }
                    } else {
                        if (observableListAttibutes.contains(item)) {
                            observableListAttibutes.remove(item);
                        }
                    }
                });
                observable.set(observableListAttibutes.contains(item));
                observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
                        -> observable.set(observableListAttibutes.contains(item)));
                return observable;
            }
        }));

        listViewAttributes.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();

                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        if (!observableListAttibutes1.contains(item)) {
                            observableListAttibutes1.add(item);
                        }
                    } else {
                        if (observableListAttibutes1.contains(item)) {
                            observableListAttibutes1.remove(item);
                        }
                    }

                });
                observable.set(observableListAttibutes1.contains(item));
                observableListAttibutes1.addListener((ListChangeListener.Change<? extends String> c)
                        -> observable.set(observableListAttibutes1.contains(item)));
                return observable;
            }
        }));
    }

    @FXML
    void browserAction(ActionEvent event) { /* Dataset browser action */

        startingStep = 1;
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choose a file");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        file = chooser.showOpenDialog(primaryStage);

        if (file != null && file.exists()) {
            filePath = file.getAbsolutePath();
            fileString = file.getName();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;

            int i = 7; // prints 7 entries from the dataset to visualize Data structure
            while ((sCurrentLine = br.readLine()) != null && i-- > 0) {
                visualizeFileTA.setText(visualizeFileTA.getText() + sCurrentLine + "\n");
            }
        } catch (Exception e) {

        }
        attributesCalculated = false;
        this.visualizeDataStructure();

    }

    @FXML
    void browserDetectionAction(ActionEvent event
    ) { /* handle detection browser */

        startingStep = 2;

        /* Prepare file chooser */
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choisir un ou plusieurs fichiers");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        Stage stage = (Stage) launchDetection.getScene().getWindow();
        listFile = chooser.showOpenMultipleDialog(primaryStage);

        /* handle chosen files */
        if (listFile != null) {
            for (File file : listFile) {
                if (!file.exists()) {
                    listFile = null;
                    break;
                }
            }
            directlyDetection = true;
            launchDetection.setDisable(false);
        }

        /* set number of selected files */
        nbSnapshots = (listFile != null) ? listFile.size() : 0;
        attributesCalculated = false;
        //fileDetection = chooser.showOpenDialog(primaryStage);
    }

    @FXML
    void browserEvolutionAction(ActionEvent event) { /* handle evolution browser*/

        startingStep = 3;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choose a file");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        fileEvolution = chooser.showOpenDialog(primaryStage);

        if (fileEvolution != null && fileEvolution.exists()) {
            filePathEvolution = fileEvolution.getAbsolutePath();
            launchEvolution.setDisable(false);
            directlyEvolution = true;
        }
        attributesCalculated = false;
    }

    @FXML
    void browserPredictionAction(ActionEvent event) { /* Prediction browser handler */

        startingStep = 4;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Arff Files", "*.arff"));
        chooser.setTitle("Choose a file");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        filePrediction = chooser.showOpenDialog(primaryStage);

        if (filePrediction != null && filePrediction.exists()) {
            filePathPrediction = filePrediction.getAbsolutePath();

            launchPrediction.setDisable(false);
            directlyPrediction = true;
        }
    }

    void writeLogLn(String str) { /* Prints str in log field in UI + linebreak (\n) */

        //javafx.application.Platform.runLater(() -> logTextArea.setText(logTextArea.getText() + str + "\n"));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.setText(logTextArea.getText() + str + "\n");
                logTextArea.positionCaret(logTextArea.getText().length());
                if (radioLogFile.isSelected()) {
                    logger.info(logTextArea.getText() + str + "\n");
                }
            }
        });
    }

    void writeLog(String str) { /* Prints str in log field in UI */

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.setText(logTextArea.getText() + str);
                logTextArea.positionCaret(logTextArea.getText().length());
                if (radioLogFile.isSelected()) {
                    logger.info(logTextArea.getText() + str + "\n");
                }
            }
        });
    }

    void writeResultsLn(String str) { /* Prints str in results field in UI + linebreak */

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                resultsTextArea.setText(resultsTextArea.getText() + str + "\n");
                resultsTextArea.positionCaret(resultsTextArea.getText().length());
            }
        });

    }

    void writeResultsLog(String str) { /* Prints str in results field in UI without linebreak */

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                resultsTextArea.setText(resultsTextArea.getText() + str);
                resultsTextArea.positionCaret(resultsTextArea.getText().length());
            }
        });
    }

    @FXML
    void handleCancelDetection(ActionEvent event
    ) {
        writeLog("\nCancelling detection...");
        try {
            threadDetection.stop();
            writeLogLn("Done\n");
        } catch (Exception e) {

        }
        launchDetection.setDisable(false);
        stopProgressBar();
    }

    @FXML
    void handleCancelCalculate(ActionEvent event
    ) {
        writeLog("\nCancelling attributes calculation...");
        try {
            threadCalculate.stop();
            writeLogLn("Done\n");

        } catch (Exception e) {

        }
        launchCalculation.setDisable(false);
        stopProgressBar();
    }

    @FXML
    void handleCancelIdentification(ActionEvent event
    ) {
        writeLog("\nCancelling Identification...");
        try {
            threadIdentification.stop();
            writeLogLn("Done\n");
        } catch (Exception e) {

        }
        launchEvolution.setDisable(false);
        stopProgressBar();
    }

    @FXML
    void handleCancelPrediction(ActionEvent event
    ) {
        writeLogLn("\nCancelling prediction...");
        try {
            threadPrediction.stop();
            writeLogLn("Done\n");
        } catch (Exception e) {

        }
        launchPrediction.setDisable(false);
        stopProgressBar();
    }

    @FXML
    void startSplit(ActionEvent event) { /* Snapshots creation */

        if ((spinnerNbSnaps.getValue() == 0 && durationsLabel.getText().equals("")) || fileString.equals("")) {
            /* if some of the parameters are missing */

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Incomplete entry");
            alert.setContentText("Please make sure to enter the file and parameters");

            alert.showAndWait();
            return;
        }

        launchSplit.setDisable(true); /* Disable the starting button until the tast is finished */

        SnapshotsPrep snapp = new SnapshotsPrep();
        /* Duration d = Duration.ofDays(10);
         List<Duration> listDuration = new ArrayList<Duration>();
         //listDuration.add(Duration.ofDays(500));
         listDuration.add(Duration.ofDays(950));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(3));
         listDuration.add(Duration.ofDays(10));*/

        // prepare the export path and create it if it doesn't exist
        totalPath = directoryPath + fileString + "_" + spinnerNbSnaps.getValue() + "_" + durationsLabel.getText() + "/";
        Path path = Paths.get(totalPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            float overlapping = (float) sliderOverlapping.getValue() / 100f;
            if (spinnerNbSnaps.getValue() > 0) { // Doing segmentation by snapshots number
                System.out.println("1");
                nbSnapshots = spinnerNbSnaps.getValue();
                snapp.getSplitSnapshots(overlapping, filePath, nbSnapshots, timeFormatCombo.getValue(),
                        comboStructDonnees.getSelectionModel().getSelectedItem(),
                        totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
            } else {  // If no snapshots number is given
                List<Duration> durations = stringToDuration(durationsLabel.getText());
                if (durations.size() > 1) { // If a list of durations is given
                    System.out.println("2");
                    nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations,
                            timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                            totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                } else {
                    if (durations.size() == 1) { // If only one duration is given
                        System.out.println("3");
                        nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations.get(0),
                                timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                    } else { // Entry error
                        writeLogLn("Entry error (snapshots number or durations must be entered)");
                        throw new IllegalArgumentException("Entry error (snapshots number or durations must be entered)");
                    }
                }
                writeLogLn("Split done. Writing done. " + nbSnapshots + " snapshots created");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        if (checkboxSplitMultiExport.isSelected()) {
            launchSplit.setDisable(false);
            directlyDetection = false;
        }
    }

    @FXML
    void startDetection(ActionEvent event
    ) {
        launchDetection.setDisable(true);
        try {
            Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("24-128.png")));
            root = new TreeItem<>("Snapshots", rootIcon);
            treeView.setRoot(root);
        } catch (Exception e) {
        }

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                startProgressBar();
                dynamicNetwork = new LinkedList<>();
                List<Graph> graphs = new ArrayList<Graph>();

                for (int i = 0; i < nbSnapshots; i++) { //for (int i = 0; i < nbSnap; i++) {
                    String fileToExecute = "";
                    if (directlyDetection) {
                        fileToExecute = listFile.get(i).getAbsolutePath();
                    } else {
                        fileToExecute = totalPath + splitExportName.getText() + i + ".txt";
                    }
                    switch (comboDetection.getSelectionModel().getSelectedItem()) {
                        case "CPM": {
                            Graph toWork;

                            writeLog("Executing CPM on snapshot " + i + "...");
                            graphs.add(SnapshotsPrep.readCommunity(fileToExecute));
                            System.out.println("file " + splitExportName.getText() + " was read");
                            toWork = graphs.get(i);

                            System.out.println(toWork.getNodeCount() + " nodes were read");

                            CPM cpm = new CPM();
                            LinkedList<Graph> communities = cpm.execute(toWork, snipperDetection.getValue());

                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            break;
                        }
                        case "SLPA": {
                            //if (!directlyDetection) {
                            writeLog("Executing SLPA on snapshot " + i + "...");
                            /*} else {
                             writeLog("Executing SLPA...");
                             }*/
                            LinkedList<Graph> communities = (new SLPA()).findCommunities(fileToExecute, snipperDetection.getValue());
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            break;
                        }
                        case "GN": {
                            //if (!directlyDetection) {
                            writeLog("Executing GN on snapshot " + i + "...");
                            /*} else {
                             writeLog("Executing GN...");
                             }*/
                            LinkedList<Graph> communities = (new GN()).findCommunities2(fileToExecute, 50/*nbComm*/);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            break;
                        }
                        case "CONGA": {
                            //if (!directlyDetection) {
                            writeLog("Executing CONGA on snapshot " + i + "...");
                            /*} else {
                             writeLog("Executing CONGA...");
                             }*/
                            //writeLogLn("Executing CONGA...");
                            LinkedList<Graph> communities = (new CONGA()).findCommunities2(fileToExecute, 5/*nbComm*/);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            //dynamicNetwork.add(new TimeFrame(communities));
                            break;
                        }
                        case "COPRA": {
                            //if (!directlyDetection) {
                            writeLog("Executing COPRA on snapshot " + i + "...");
                            /* } else {
                             writeLog("Executing COPRA...");
                             }*/
                            LinkedList<Graph> communities = (new COPRA()).findCommunities2(fileToExecute, 1/**
                                     * default 1*
                                     */
                                    , 1/**
                                     * Max degree of overlapping
                                     */
                                    , true/**
                                     * Do not split discontiguous communities
                                     * into contiguous subsets.*
                                     */
                                    , false);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");

                            //(new COPRA()).findCommunities(fileToExecute);
                            break;
                        }

                        case "CM": {
                            //if (!directlyDetection) {
                            writeLog("Executing CM on snapshot " + i + "...");
                            /* } else {
                             writeLog("Executing CM...");
                             }*/
                            LinkedList<Graph> communities = (new CM().findCommunities2(fileToExecute, snipperDetection.getValue(), "KJ"));
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            break;
                        }
                        case "CONCLUDE": {
                            // if (!directlyDetection) {
                            writeLog("Executing CONCLUDE on snapshot " + i + "...");
                            /*} else {
                             writeLog("Executing CONCLUDE...");
                             }*/
                            LinkedList<Graph> communities = (new CONCLUDE()).findCommunities(fileToExecute);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" done.");
                            //(new CONCLUDE()).findCommunities2(fileToExecute);
                            break;
                        }
                        default: {
                            writeLogLn("Method not linked.");
                            break;
                        }
                    }
                    // System.out.println("\n");
                    if (checkboxDetection.isSelected()) {
                        try {
                            writeLog("Exporting results of snapshot " + i + "...");
                            exportCommunity(dynamicNetwork.get(dynamicNetwork.size() - 1), totalPath + "detection_" + comboDetection.getSelectionModel().getSelectedItem() + "_" + snipperDetection.getValue() + ".txt", i);
                            writeLogLn("done");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                treeView.getRoot().getChildren().clear();
                System.out.println("Size tf: " + dynamicNetwork.size());
                for (int k = 0; k < dynamicNetwork.size(); k++) {
                    TimeFrame tf = dynamicNetwork.get(k);
                    TreeItem<String> child = new TreeItem<>("Snapshot " + (k + 1));
                    treeView.getRoot().getChildren().add(child);

                    for (Graph com : tf.getCommunities()) {
                        //TreeItem<String>vf child2 = new TreeItem<>(Integer.toString(tf.getCommunities().indexOf(com) + 1));
                        TreeItem<String> child2 = new TreeItem<>("Community " + (tf.getCommunities().indexOf(com) + 1));
                        treeView.getRoot().getChildren().get(k).getChildren().add(child2);
                    }
                }

                treeView.getRoot().setExpanded(true);

                if (dynamicNetwork.size() > 1) {
                    launchCalculation.setDisable(false);
                }

//                if (checkboxDetection.isSelected()) {
//                    try {
//                        writeLog("Export des résultats...");
//                        exportDynamicNetwork(dynamicNetwork, totalPath + "detection_" + comboDetection.getSelectionModel().getSelectedItem() + "_" + snipperDetection.getValue() + ".txt", attributesCombo.getValue());
//                        writeLogLn("done");
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
                stopProgressBar();
                directlyEvolution = false;
                return null;
            }

            private void exportDynamicNetwork(LinkedList<TimeFrame> dynamicNetwork, String exportName) throws IOException {
                /* Exports n1 att1 n2 att2 tf g */

                BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

                for (int k = 0; k < dynamicNetwork.size(); k++) {
                    TimeFrame tf = dynamicNetwork.get(k);
                    for (int i = 0; i < tf.getCommunities().size(); i++) {
                        Graph g = tf.getCommunities().get(i);
                        //for (Graph g : tf.getCommunities()) {
                        for (org.graphstream.graph.Edge e : g.getEdgeSet()) {
                            writer.write(e.getSourceNode().getId() + " " + e.getTargetNode().getId() + " " + k + " " + i + "\n");
                            //writer.write(e.getSourceNode().getId() + " " + e.getSourceNode().getAttribute(att) + " " + e.getTargetNode().getId() + " " + e.getTargetNode().getAttribute(att) + " " + k + " " + i + "\n");
                            //System.out.println(dynamicNetwork.indexOf(tf) + " " + tf.getCommunities().indexOf(g));
                        }
                    }
                }
                writer.close();
            }

            private void exportCommunity(TimeFrame tf, String exportName, int snp) throws IOException {
                //BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

                PrintWriter out = null;
                try {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(exportName, true)));
                    //out.println("the text");
                    for (int i = 0; i < tf.getCommunities().size(); i++) {
                        Graph g = tf.getCommunities().get(i);
                        //for (Graph g : tf.getCommunities()) {
                        for (org.graphstream.graph.Edge e : g.getEdgeSet()) {
                            out.println(e.getSourceNode().getId() + " " + e.getTargetNode().getId() + " " + snp + " " + i);
                        }
                    }
                } catch (IOException e) {
                    System.err.println(e);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

            }
        };
        task.setOnSucceeded(e -> {
            launchDetection.setDisable(false);
            //tabPaneResults.getSelectionModel().select(1);
            tabPaneVisible.getSelectionModel().select(1);
            //paneVisualize.getChildren().clear();

        });

        threadDetection = new Thread(task);
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };
        threadDetection.setUncaughtExceptionHandler(h);
        threadDetection.start();

    }

    @FXML
    void startCalculateAttributes(ActionEvent event) {
        launchCalculation.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                startProgressBar();
                writeLog("Calculating attributs...");

                AttributesComputer.calculateAttributes(dynamicNetwork, observableListAttibutes);
                attributesCalculated = true;
                writeLogLn(" done.");
                listViewAttributes.setItems(observableListAttibutes);//etItems().addAll(selectedAttributes);

                launchEvolution.setDisable(false);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            launchCalculation.setDisable(false);
            stopProgressBar();
        });
        threadCalculate = new Thread(task);
        threadCalculate.start();
    }

    @FXML
    void startEvolution(ActionEvent event
    ) {
        launchEvolution.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                startProgressBar();
                writeLogLn("Executing " + comboEvolution.getSelectionModel().getSelectedItem() + "...");
                if (directlyEvolution) {
                    /*Read file of structure: n1|n2|t|g */
                    dynamicNetwork = readDynamicNetwork();
                    System.out.println("dynamicNetwork.size: " + dynamicNetwork.size()
                    );

                    if (!attributesCalculated) {
                        writeLog("Calculating attributes...");
                        AttributesComputer.calculateAttributes(dynamicNetwork, observableListAttibutes);
                        attributesCalculated = true;
                        writeLogLn(" done.");
                        listViewAttributes.setItems(observableListAttibutes);//etItems().addAll(selectedAttributes);

                        listViewAttributes.setItems(observableListAttibutes);//etItems().addAll(selectedAttributes);

                    }
                    fileString = "GED_" + fileEvolution.getName();

                }

                String str = !evolutionParameters.getText().equals("") ? evolutionParameters.getText() : evolutionParameters.getPromptText();
                String para[] = str.split(";");
                String tres;
                if (snipperDetection.isDisable()) {
                    tres = comboDetection.getSelectionModel().getSelectedItem() + "_" + Integer.parseInt(para[0]) + "_" + Integer.parseInt(para[1]);
                } else {
                    tres = comboDetection.getSelectionModel().getSelectedItem() + "_" + snipperDetection.getValue() + "_" + Integer.parseInt(para[0]) + "_" + Integer.parseInt(para[1]);
                }

                BDpath = totalPath;
                BDfilename = fileString + "_" + tres + ".db";
                tabname = "GED_evolution";

                switch (comboEvolution.getSelectionModel().getSelectedItem()) {
                    case "GED": {
                        writeLogLn("GED started...");
                        GED ged = new GED();
                        try {
                            mapIdentification = ged.excuteGED(dynamicNetwork, Integer.parseInt(para[0]), Integer.parseInt(para[1]), BDpath + BDfilename);
                            //visualizeIdentificationResults(mapIdentification);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                    case "Asur": {
                        writeLogLn("Asur started...");
                        Asur asur = new Asur();
                        int param = 40;//à lire de l'interface
                        asur.execute(dynamicNetwork, param);
                        break;
                    }
                    default: {
                        writeLogLn("Method not linked yet.");
                        break;
                    }
                }
                writeLogLn(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                System.out.println(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                writeLogLn("Creating evolution chains...");
                System.out.println("Creating evolution chains...");
//                int nbtimeframe = dynamicNetwork.size();
                writeEvolutionChain(BDpath, BDfilename, tabname, dynamicNetwork.size(), 2/* nbre timeframes */
                );
                writeLogLn("Evolution chains created.");
                System.out.println("Evolution chains created.");

                launchPrediction.setDisable(false);
                return null;
            }

            private LinkedList<TimeFrame> readDynamicNetwork() {
                /*Read file of structure: "n1 att1 n2 att2 t g" */
                LinkedList<TimeFrame> dynamicNet = new LinkedList<TimeFrame>();
                try (BufferedReader br = new BufferedReader(new FileReader(filePathEvolution))) {
                    String sCurrentLine;
                    while ((sCurrentLine = br.readLine()) != null) {
                        //System.out.println(sCurrentLine);
                        String[] str = sCurrentLine.split(" ");
                        int snp = Integer.parseInt(str[2]);
                        int comm = Integer.parseInt(str[3]);

                        while (dynamicNet.size() <= snp) {
                            dynamicNet.add(new TimeFrame(new LinkedList<Graph>()));
                        }
                        while (dynamicNet.get(snp).getCommunities().size() <= comm) {
                            Graph group = new SingleGraph("");
                            group.setStrict(false);
                            group.setAutoCreate(true);
                            dynamicNet.get(snp).getCommunities().add(group);
                        }

                        dynamicNet.get(snp).getCommunities().get(comm).addEdge(str[0] + ";" + str[1], str[0], str[1]);

                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                return dynamicNet;
            }

        };
        task.setOnSucceeded(e -> {
            visualizeIdentificationResults(mapIdentification);
            stopProgressBar();
            launchPrediction.setDisable(false);
            launchEvolution.setDisable(false);
            //tabPaneResults.getSelectionModel().select(1);
            tabPaneVisible.getSelectionModel().select(2);
            //paneVisualize.getChildren().clear();        
        });
        threadIdentification = new Thread(task);
        threadIdentification.start();
    }

    @FXML
    void startPrediction(ActionEvent event
    ) {
        launchPrediction.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                startProgressBar();
                String filePath = "./ExportedResults/Prediction/";
                String filename = "trainData_len=" + chainLength.getValue();
                String extension = ".arff";

                filePathPrediction = filePath + filename + extension;
                if (!directlyPrediction) {

                    //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,/**nbre timeframes**/);
                    //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
                    //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
                    //PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, dynamicNetwork.size(), "", "", 4);
                    System.out.println(chainLength.getValue());
                    PredictionUtils.createArffAttribute(filePath, filename, BDpath, BDfilename, dynamicNetwork.size(),
                            chainLength.getValue(), (ArrayList<String>) selectedAttributes1, dynamicNetwork);

                    // filePath, filename, BDpath, BDfilename,
                    //dynamicNetwork.size(), "", "", 4, (ArrayList<String>) observableListAttibutes, dynamicNetwork);
                    writeLogLn("Arff file generated.");
                }

                try {
                    eReport = PredictionUtils.makePredictor(comboSelectionAttributes.getSelectionModel().getSelectedItem(),
                            comboSearchMethod.getSelectionModel().getSelectedItem(), comboEvaluationMethod.getSelectionModel().getSelectedItem(),
                            comboClassifier.getSelectionModel().getSelectedItem(), null, filePathPrediction, kFoldsSpinner.getValue());
                    predictionResults = "";

                    writeResultsLn(eReport.getSummary());
                    predictionResults += eReport.getSummary() + "\n";

                    printlnResultsFile("results_" + filename + ".txt", eReport.getSummary());

                    writeResultsLn(eReport.getDetailedAccuracy());
                    printlnResultsFile("results_" + ".txt", eReport.getDetailedAccuracy());
                    predictionResults += eReport.getDetailedAccuracy() + "\n";

                    for (String str : eReport.getConfusionMatrix()) {
                        writeResultsLn(str);
                        printlnResultsFile("results.txt", str);
                        predictionResults += str + "\n";
                    }
                    writeResultsLn("############################################################################");
                    writeLogLn("Prediction done.");
                    printlnResultsFile("results.txt", "Prediction done.");

                } catch (Exception ex) {
                    System.out.println("1");
                    try {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                Alert alert = new Alert(AlertType.ERROR);
                                System.out.println("2");

                                alert.setTitle("Exception Dialog");
                                alert.setHeaderText("Look, an Exception Dialog");
                                alert.setContentText("Exception");

                                // Create expandable Exception.
                                System.out.println("1");

                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                ex.printStackTrace(pw);
                                String exceptionText = sw.toString();

                                Label label = new Label("The exception stacktrace was:");

                                TextArea textArea = new TextArea(exceptionText);
                                textArea.setEditable(false);
                                textArea.setWrapText(true);

                                textArea.setMaxWidth(Double.MAX_VALUE);
                                textArea.setMaxHeight(Double.MAX_VALUE);
                                GridPane.setVgrow(textArea, Priority.ALWAYS);
                                GridPane.setHgrow(textArea, Priority.ALWAYS);

                                GridPane expContent = new GridPane();
                                expContent.setMaxWidth(Double.MAX_VALUE);
                                expContent.add(label, 0, 0);
                                expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
                                alert.getDialogPane().setExpandableContent(expContent);
                                alert.initOwner(primaryStage);
                                alert.showAndWait();
                                //System.out.println("Hello");
                            }
                        });
                        //ex.printStackTrace();
                    } catch (Exception ex33) {
                        ex33.printStackTrace();
                    }
                }

                buttonEReport.setDisable(false);
                menuItemEReport.setDisable(false);

                stopProgressBar();
                return null;
            }

            private void printlnResultsFile(String exportName, String stringToWrite) throws IOException {
                //private void exportCommunity(TimeFrame tf, String exportName, int snp) throws IOException {
                //BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

                PrintWriter out = null;
                out = new PrintWriter(new BufferedWriter(new FileWriter(exportName, true)));
                out.println(stringToWrite);
                if (out != null) {
                    out.close();
                }

            }
        };

        task.setOnSucceeded(e -> {
            launchPrediction.setDisable(false);
            //tabPaneResults.getSelectionModel().select(1);
            tabPaneVisible.getSelectionModel().select(3);
            stopProgressBar();
        });
        new Thread(task).start();
    }

    public static boolean isFloat(String s) {
        Pattern DOUBLE_PATTERN = Pattern.compile(
                "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)"
                + "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|"
                + "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))"
                + "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
        return DOUBLE_PATTERN.matcher(s).matches();
    }

    private List<Duration> stringToDuration(String text) {
        List<Duration> listDuration = new ArrayList<Duration>();
        String[] s = text.split(";");
        for (String duration : s) {
            String periodS = duration;
            String unit = duration;
            unit = unit.replaceAll("[^A-Za-z]", "");
            periodS = periodS.replaceAll("[^\\d.]+|\\.(?!\\d)", "");
            //Long l = Long.parseLong(periodS);
            //System.out.println(l);
            //System.out.println(periodS + " " + unit);
            long period = Long.parseLong(periodS);
            switch (unit) {
                case "H":
                case "h":
                    listDuration.add(Duration.ofHours(period));
                    break;
                case "D":
                case "d":
                    listDuration.add(Duration.ofDays(period));
                    break;
                case "W":
                case "w":
                    listDuration.add(Duration.ofDays(7 * period));
                    break;
                case "m":
                case "M":
                    listDuration.add(Duration.ofDays(30 * period));
                    break;
                case "y":
                case "Y":
                    listDuration.add(Duration.ofDays(365 * period));
                    break;
                default:
                    writeLogLn("Please verify entry's structure");
                //throw new IllegalArgumentException("Vérifier la structure de l'entrée SVP");
            }
        }
        return listDuration;
    }

    @FXML
    void selectAttributes(ActionEvent event) {

        listView = new ListView<>();
        String[] attributes = {"size", "averageDegree", "averageClusteringCoefficient", "degreeAverageDeviation", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};
        listView.getItems().addAll(attributes);
        /*listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
         @Override
         public ObservableValue<Boolean> call(String item) {
         BooleanProperty observable = new SimpleBooleanProperty();
         /*for (int i = 0; i < selectedAttributes.size(); i++) {
         if (item.equals(selectedAttributes.get(i))) {
         observable.set(true);
         }
         }*/
        /* observable.set(selectedAttributes.contains(item));
         observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
         -> observable.set(selectedAttributes.contains(item)));
         observable.addListener((obs, wasSelected, isNowSelected) -> {
         if (isNowSelected) {
         observableListAttibutes.add(item);
         } else {
         observableListAttibutes.remove(item);
         }
         System.out.println(observableListAttibutes.size());
         });
         return observable;
         }
         }));*/

        /*listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
         @Override
         public ObservableValue<Boolean> call(String item) {
         BooleanProperty observable = new SimpleBooleanProperty();
         /*for (int i = 0; i < selectedAttributes.size(); i++) {
         if (item.equals(selectedAttributes.get(i))) {
         observable.set(true);
         }
         }*/
        /*observable.addListener((obs, wasSelected, isNowSelected) -> {
         if (isNowSelected) {
         if (!observableListAttibutes.contains(item)) {
         observableListAttibutes.add(item);
         }
         } else {
         if (observableListAttibutes.contains(item)) {
         observableListAttibutes.remove(item);
         }
         }
         System.out.println(observableListAttibutes.size());

         });
         observable.set(observableListAttibutes.contains(item));
         observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
         -> observable.set(observableListAttibutes.contains(item)));
         return observable;
         }
         }));*/
        // VBox
        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 30, 30, 30));
        vb.setSpacing(10);

        Label lbl = new Label("Select attributes: ");
        lbl.setFont(Font.font("Amble CN", FontWeight.BOLD, 13));
        vb.getChildren().add(lbl);

        vb.getChildren().add(listView);

        // Adding VBox to the scene
        /*Scene scene = new Scene(vb);
         BorderPane root = new BorderPane(listView);*/
        Scene scene = new Scene(vb, 300, 400);
        Stage dialog = new Stage();
        dialog.setTitle("Attributes");
        dialog.initOwner(primaryStage);
        dialog.setScene(scene);

        dialog.show();
    }

    @FXML
    private void visualizeDataStructure() {
        visualizeFileTA = new TextArea();

        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            int i = 7;
            while ((sCurrentLine = br.readLine()) != null && i-- > 0) {

                visualizeFileTA.setText(visualizeFileTA.getText() + sCurrentLine + "\n");
            }

            Label lbl = new Label("File structure:");
            lbl.setFont(Font.font("Amble CN", FontWeight.BOLD, 13));

            Button bt = new Button("OK");
            bt.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
                }
            });

            vb.getChildren().addAll(lbl, visualizeFileTA, bt);
            vb.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vb, 300, 250);
            Stage dialog = new Stage();
            dialog.setTitle("File structure");
            dialog.initOwner((Stage) launchPrediction.getScene().getWindow());
            dialog.setScene(scene);

            dialog.show();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Incomplete entry");
            alert.setContentText("Please make sure to enter the file");

            alert.showAndWait();
        }
    }

    @FXML
    void visualizeFile(ActionEvent event) throws FileNotFoundException, IOException, ParseException {

        //filePath
        /**
         * .getSplitSnapshots(overlapping, filePath, nbSnapshots,
         * timeFormatCombo.getValue(),
         * comboStructDonnees.getSelectionModel().getSelectedItem(), " ",
         * splitExportName.getText(), false,
         * checkboxSplitMultiExpogrt.isSelected());*
         */
        try {
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws Exception {

                    FileInputStream stream;
                    List<Integer> counters;
                    File f;
                    String dataStructure = comboStructDonnees.getSelectionModel().getSelectedItem();

                    MinMaxResults myResult = new MinMaxResults();
                    myResult.getResults(filePath, timeFormatCombo.getValue(), dataStructure);

                    //System.out.println("File read successfully");
                    f = new File(filePath);

                    if (f != null) {
                        stream = new FileInputStream(f);
                        counters = new ArrayList<Integer>();
                        int number = 500;
                        for (int i = 0; i < number; i++) {
                            counters.add(0);
                        }
                        if (file != null) {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String sCurrentLine;
                            String[] splitContent;
                            String v, w;
                            long timestamp;
                            while ((sCurrentLine = br.readLine()) != null) {
                                if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                                    splitContent = splitInput(sCurrentLine);
                                    v = splitContent[dataStructure.indexOf("V")];
                                    w = splitContent[dataStructure.indexOf("W")];
                                    timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                                    //System.out.println(TimeLength.timestampToDate(timestamp));
                                    int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / number);
                                    // System.out.println(duration.getSeconds()+" "+step);
                                    int index = (int) ((timestamp - myResult.getMinTS()) / step);
                                    if (index == number) {
                                        index--;
                                    }
                                    //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                                    //        + index);
                                    counters.set(index, counters.get(index) + 1);
                                }
                            }
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //tabPaneResults.getSelectionModel().select(1);
                                tabPaneVisible.getSelectionModel().select(0);
                                paneVisualize.getChildren().clear();
                                distBarChart = createChart(counters, number, myResult);
                                // System.out.println("distBarChart" + (distBarChart == null));
                                paneVisualize.getChildren().add(distBarChart);
                            }
                        });
                    }

                    /*for (int i : counters) {
                     System.out.println(i);
                     }*/
            //init(new Stage(), counters);
           /* Stage primaryStage = new Stage();
                     primaryStage.setScene(new Scene(root*/
                    //primaryStage.show();
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                launchCalculation.setDisable(false);
                stopProgressBar();
            });
            new Thread(task).start();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Incomplete entry");
            alert.setContentText("Please make sure to enter the file and data structure correctly");

            alert.showAndWait();
        }
    }

    protected BarChart<String, Number> createChart(List<Integer> counters, int number, MinMaxResults myResult) {
        //final String[] years = {"2007", "2008", "2009"};
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis));
        final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
      //  bc.setStyle(".default-color0.chart-series-line { -fx-stroke: #e9967a; }");

        // setup chart
        bc.setTitle("Activity distribution");
        bc.setCategoryGap(0);
        bc.setBarGap(0);
        xAxis.setLabel("Time");

        yAxis.setLabel("Activity");

        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        series1.setName("Activity distribution");

        /* series1.set /*series1.getData().add(new XYChart.Data<String, Number>("0", 567));
         series1.getData().add(new XYChart.Data<String, Number>("1", 1292));
         series1.getData().add(new XYChart.Data<String, Number>("2", 2180));*/
        int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / number);

        // System.out.println(duration.getSeconds()+" "+step);
        //int index = (int) ((timestamp - myResult.getMinTS()) / step);
        int max = 0;
        for (int i = 0; i < number; i++) {
            /*Random r = new Random();
             int Low = 0;
             int High = 3000;
             int Result = r.nextInt(High - Low) + Low;
             max = Math.max(max, Result);*/
            XYChart.Data<String, Number> data = new XYChart.Data<String, Number>(TimeLength.timestampToDate(myResult.getMinTS() + step * i), counters.get(i));
            /*data.nodeProperty().addListener(new ChangeListener<Node>() {
             @Override
             public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
             if (newNode != null) {
             newNode.setStyle("-fx-bar-fill: navy;");
             }
             }
             });*/
            series1.getData().add(data);
            // series1.getNode().setStyle("-fx-bar-fill: navy;");
            //System.out.println("");
            //series1.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), counters.get(i)));
        }
        bc.getData().add(series1);
        bc.setStyle("");
        //bc.getXAxis().setOpacity(0);
        // Zoom here

        return bc;
    }

    private void startProgressBar() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText("Calculating...");
                double y = 15;
                final double SPACING = 15;

                y += SPACING;
                p1 = new ProgressBar();
                p1.setPrefWidth(100);
                p1.setLayoutY(y);

                group.getChildren().addAll(p1);
            }
        });

    }

    private void stopProgressBar() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText("");
                p1.setProgress(0.0);

            }
        });
    }

    private void visualizeIdentificationResults(Map<String, Integer> map) {
        List<PieChart.Data> list = new ArrayList<PieChart.Data>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = (Map.Entry) it.next();
            list.add(new PieChart.Data((String) pair.getKey(), (Integer) pair.getValue()));
            System.out.println((String) pair.getKey() + " = " + (Integer) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(list);
        evolPieChart = new PieChart(pieChartData);
        pieChartData.forEach(data
                -> data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), ": ", data.pieValueProperty().intValue()
                        )
                )
        );

        pieChartData.stream().forEach(pieData -> {
            pieData.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Bounds b1 = pieData.getNode().getBoundsInLocal();
                double newX = (b1.getWidth()) / 2 + b1.getMinX();
                double newY = (b1.getHeight()) / 2 + b1.getMinY();
                // Make sure pie wedge location is reset
                pieData.getNode().setTranslateX(0);
                pieData.getNode().setTranslateY(0);
                TranslateTransition tt = new TranslateTransition(
                        javafx.util.Duration.millis(1500), pieData.getNode());
                tt.setByX(newX);
                tt.setByY(newY);
                tt.setAutoReverse(true);
                tt.setCycleCount(2);
                tt.play();
            });
        });
        evolPieChart.setTitle("Identified events");
        paneVisualizeIdent.getChildren().clear();
        paneVisualizeIdent.getChildren().add(evolPieChart);
    }

    @FXML
    void selectAllAttributes() {
        for (String str : listView.getItems()) {
            if (!observableListAttibutes.contains(str)) {
                observableListAttibutes.add(str);
            }
        }
    }

    @FXML
    void deselectAllAttributes() {
        for (String str : listView.getItems()) {
            if (observableListAttibutes.contains(str)) {
                observableListAttibutes.remove(str);
            }
        }
    }

    @FXML
    void selectAllAttributesPrediction() {
        observableListAttibutes1.clear();
        observableListAttibutes1.addAll(observableListAttibutes);
//        for (String str : listView.getItems()) {
//            System.out.println(str);
//            if (!observableListAttibutes1.contains(str)) {
//                observableListAttibutes1.add(str);
//            }
//        }
    }

    @FXML
    void deselectAllAttributesPrediction() {
        observableListAttibutes1.clear();
//        for (String str : listView.getItems()) {
//            if (observableListAttibutes1.contains(str)) {
//                observableListAttibutes1.remove(str);
//            }
//        }
    }

    private void prepareLogFile() {
        try {
            logger.setUseParentHandlers(false);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String logFile = dateFormat.format(date).replaceAll("[:\\\\/*\"?|<>']", "_");
            fh = new FileHandler(logFile + ".Log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @FXML
    private void handleExportPModel(ActionEvent event) throws FileNotFoundException, IOException {
        prepareModel();
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Prediction Model", "*.model");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Export model");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(pModel);
            oos.flush();
            oos.close();
        }
        writeLogLn("Prediction Model exported");
    }

    @FXML
    private void handleImportPModel(ActionEvent event) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("Prediction Model", "*.model"),
                new ExtensionFilter("All Files", "*.*"));
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));
        chooser.setTitle("Import model");
        File file = chooser.showOpenDialog(primaryStage);
        if (file != null) {
            String fileName = file.getAbsolutePath();
            FileInputStream fis = new FileInputStream(fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            PModel pModel = (PModel) ois.readObject();
            ois.close();

            durationsLabel.setText(pModel.getsnapshotDuration());
            spinnerNbSnaps.getValueFactory().setValue(pModel.getNbSnapshots());
            sliderOverlapping.setValue(pModel.getOverlapping());
            comboDetection.setValue(pModel.getDetectionMethod());
            snipperDetection.getValueFactory().setValue(pModel.getdetectionParameters());
            observableListAttibutes.clear();
            observableListAttibutes.addAll(Arrays.asList(pModel.getAttributesList()));
            System.out.println(selectedAttributes.size() + " " + pModel.getAttributesList().length);
            comboEvolution.setValue(pModel.getEvolutionMethod());
            evolutionParameters.setText(pModel.getEvolutionParameters());
            observableListAttibutes1.clear();
            observableListAttibutes1.addAll(Arrays.asList(pModel.getattributesPrediction()));
            System.out.println(selectedAttributes1.size() + " " + pModel.getattributesPrediction().length);
            chainLength.getValueFactory().setValue(pModel.getChainLength());

            comboSelectionAttributes.setValue(pModel.getSelectionMethod());
            comboEvaluationMethod.setValue(pModel.getEvaluator());
            comboSearchMethod.setValue(pModel.getSearch());
            comboClassifier.setValue(pModel.getClassifier());
        }

        writeLogLn("Prediction Model imported");
    }

    @FXML
    private void handleExportEReport(ActionEvent event) {
        try {
            Benchmark tempBench = new Benchmark();
            boolean okClicked = showPersonEditDialog(tempBench);
            if (okClicked) {
                ResultsStats rs = new ResultsStats();
                rs.setDescription(tempBench.getDescription());
                rs.setLinkReference(tempBench.getSourceLink());

                rs.setResults(dynamicNetwork, distBarChart, evolPieChart, predictionResults);
                //System.out.println("RS ready");

                FileChooser fileChooser = new FileChooser();
                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Evaluation report", "*.pdf");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setTitle("Evaluation report export");
                String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
                fileChooser.setInitialDirectory(new File(currentPath));
                //Show save file dialog
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    prepareModel();
                    //System.out.println((file == null) + " " + (pModel == null) + (rs == null) + " ");
                    try {
                        eReport.saveReportTextPDF(file.getAbsolutePath(), pModel, rs, startingStep);
                    } catch (NullPointerException e) {
                        System.err.println("eReport is null, Prediction had an exception");
                    }
                }
                writeLogLn("PDF Evaluation report is exported");
                System.out.println("rs:" + rs.toString());
                System.out.println("pModel:" + pModel.toString());

                //deleting the exported pictures
                try {

                    File fileIm1 = new File("pieChart.png");
                    File fileIm2 = new File("barchart.png");

                    if (fileIm1.delete()) {
                        System.out.println(fileIm1.getName() + " is deleted!");
                    } else {
                        System.out.println("Delete operation is failed.");
                    }

                    if (fileIm2.delete()) {
                        System.out.println(fileIm1.getName() + " is deleted!");
                    } else {
                        System.out.println("Delete operation is failed.");
                    }

                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean showPersonEditDialog(Benchmark bench) {
        try {

            // Load the fxml file and create a new stage for the popup
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog.fxml"));
            Parent root = (Parent) loader.load();

            DialogTestController controller = (DialogTestController) loader.getController();

            Scene scene = new Scene(root);

            dialogStage.setScene(scene);
            dialogStage.setTitle("Dataset info");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            controller.setBench(bench);
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
            return false;
        }
    }

    void setStageAndSetupListeners(Stage stage) {
        primaryStage = stage;
    }

    void prepareModel() {
        String evolutionParam = "50;50";
        if (evolutionParameters.getText().matches("")) {
            evolutionParam = evolutionParameters.getText();
        }
        pModel = new PModel(durationsLabel.getText(),
                spinnerNbSnaps.getValue(),
                sliderOverlapping.getValue(),
                comboDetection.getValue(),
                snipperDetection.getValue(),
                (String[]) selectedAttributes.toArray(new String[0]),
                comboEvolution.getValue(),
                evolutionParam,
                (String[]) selectedAttributes1.toArray(new String[0]),
                chainLength.getValue(),
                comboSelectionAttributes.getValue(),
                comboEvaluationMethod.getValue(),
                comboSearchMethod.getValue(),
                comboClassifier.getValue(),
                "k-fold cross-validation",
                Integer.toString(10));
    }

    @FXML
    private void handleAbout(ActionEvent event) {

    }

    @FXML
    private void handleReset(ActionEvent event) {
        try {
            try {
                threadDetection.stop();
                threadCalculate.stop();
                threadIdentification.stop();
                threadPrediction.stop();
            } catch (NullPointerException nullEx) {

            }

            primaryStage.close();
            primaryStage = new Stage();
            EPredictor sX = new EPredictor();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        sX.start(primaryStage);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        try {
            threadDetection.stop();
        } catch (Exception ex) {

        }
        try {
            threadIdentification.stop();
        } catch (Exception ex) {

        }
        try {
            threadPrediction.stop();
        } catch (Exception ex) {

        }
        Platform.exit();
    }

    public Thread getThreadDetection() {
        return threadDetection;
    }

    public Thread getThreadCalculate() {
        return threadCalculate;
    }

    public Thread getThreadIdentification() {
        return threadIdentification;
    }

    public Thread getThreadPrediction() {
        return threadPrediction;
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void loadSnapshot(ActionEvent event) {
        System.out.println("xx");
    }
}
