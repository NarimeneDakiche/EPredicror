/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Attributes.AttributesComputer;
import Prediction.EvaluationReport;
import Prediction.PredictionUtils;
import SnapshotsPrep.MyResult;
import SnapshotsPrep.SnapshotsPrep;
import static SnapshotsPrep.SnapshotsPrep.splitInput;
import communityDetection.CPM;
import communityDetection.ExternMethods.CM;
import communityDetection.ExternMethods.CONCLUDE;
import communityDetection.ExternMethods.CONGA;
import communityDetection.ExternMethods.COPRA;
import communityDetection.ExternMethods.GN;
import communityDetection.ExternMethods.SLPA;
import static evolutionIdentification.EvolutionUtils.writeEvolutionChain;
import evolutionIdentification.GED;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.openide.util.Exceptions;
import static prefuse.demos.AggregateDemo.demoComp;

/**
 *
 * @author ado_k
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea logTextArea;

    @FXML
    private Accordion accordion1;

    @FXML
    private TitledPane titledpane1;

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
    private ComboBox<String> comboSelectionAttributes;

    @FXML
    private Spinner<Integer> spinnerNBClusters;

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
    private CheckBox exportDetectionResults;

    private boolean exists;

    private int indexGraph;

    private Viewer viewer;

    private ViewPanel view;

    private File file;

    List<File> listFile;

    private String filePath = "";

    private String fileString = "";

    private String filePathDetection;

    private File fileDetection;

    private String filePathEvolution;

    private File fileEvolution;

    private String filePathPrediction;

    private File filePrediction;

    @FXML
    private Button launchDetection;

    @FXML
    private Button launchEvolution;

    @FXML
    private Button launchCalculation;

    @FXML
    private Button launchPrediction;

    @FXML
    private Label labelOverlapping;

    @FXML
    private Slider sliderOverlapping;

    /*@FXML
     private Label labepOverlapping;*/
    @FXML
    private TextField evolutionExportLabel;

    @FXML
    private TextField durationsLabel;

    @FXML
    private ComboBox<String> comboEvolution;

    @FXML
    private Pane pane;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TextField evolutionParameters;

    @FXML
    private ListView<String> listView;

    private LinkedList<TimeFrame> dynamicNetwork = new LinkedList<TimeFrame>();
    //private LinkedList<TimeFrame> dynamicNetwork1 = new LinkedList<TimeFrame>();
    private int nbSnapshots = 0;

    private TreeItem<String> root;

    //private List<String> listAttributes = new ArrayList<String>();
    private boolean directlyDetection = false;
    private boolean directlyEvolution = false;
    private boolean directlyPrediction = false;

    private List<String> selectedAttributes = new ArrayList<>();
    ObservableList<String> observableListAttibutes = FXCollections.observableList(selectedAttributes);

    private String directoryPath = "./ExportedResults/";
    private String totalPath = directoryPath;

    private String BDpath = directoryPath;
    private String BDfilename;
    private String tabname = "GED_evolution";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        sliderOverlapping.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                labelOverlapping.textProperty().setValue(
                        String.valueOf((int) sliderOverlapping.getValue()));
            }
        });

        indexGraph = 0;
        // TODO
        accordion1.setExpandedPane(titledpane1);
        launchDetection.setDisable(true);
        launchEvolution.setDisable(true);
        launchCalculation.setDisable(true);
        /*ObservableList<String> options
         = FXCollections.observableArrayList(
         "dd MMMMM yyyy",
         "dd.MM.yy",
         "MM/dd/yy",
         "yyyy.MM.dd G 'at' hh:mm:ss z",
         "EEE, MMM d, ''yy",
         "h:mm a",
         "H:mm:ss:SSS",
         "K:mm a,z",
         "yyyy.MMMMM.dd GGG hh:mm aaa",
         "Timestamp"
         );*/
        timeFormatCombo.getItems().addAll("yyyy-MM-dd HH:mm:ss",
                "Timestamp", "");
        timeFormatCombo.getSelectionModel().select("Timestamp");
        timeFormatCombo.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if (item.isEmpty()) {
                            setText("Ajouter...");
                        } else {
                            setText(item);
                        }
                    }
                }
            };
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if (cell.getItem().isEmpty() && !cell.isEmpty()) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Ajouter un élément");
                    dialog.setHeaderText("Ajouter un élément");
                    dialog.setContentText("Entrer élément...");
                    dialog.showAndWait().ifPresent(text -> {
                        int index = timeFormatCombo.getItems().size() - 1;
                        timeFormatCombo.getItems().add(index, text);
                        timeFormatCombo.getSelectionModel().select(index);
                    });
                    evt.consume();
                }
            });

            return cell;
        });

        spinnerNBClusters.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 100, 0));
        snipperDetection.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5));
        chainLength.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 4));

        //kDetectionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));
        comboStructDonnees.getItems().addAll("TVW", "VWT", "TTVW", "VWXT", "");
        comboStructDonnees.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if (item.isEmpty()) {
                            setText("Ajouter...");
                        } else {
                            setText(item);
                        }
                    }
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
                if (cell.getItem().isEmpty() && !cell.isEmpty()) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Ajouter un élément");
                    dialog.setHeaderText("Ajouter un élément");
                    dialog.setContentText("Entrer élément...");
                    dialog.showAndWait().ifPresent(text -> {
                        int index = comboStructDonnees.getItems().size() - 1;
                        comboStructDonnees.getItems().add(index, text);
                        comboStructDonnees.getSelectionModel().select(index);
                    });
                    evt.consume();
                }
            });

            return cell;
        });
        comboStructDonnees.getSelectionModel().select("TVW");

        attributesCombo.getItems().addAll("bcentrality");
        attributesCombo.getSelectionModel().select("bcentrality");

        comboDetection.getItems().addAll("CPM", "CM", "CONCLUDE", "CONGA", "COPRA", "GN", "SLPA");
        comboDetection.getSelectionModel().select("CPM");
        comboDetection.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
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
                        System.out.println("How on earth did you get into default?!");
                        break;
                }
            }
        });

        comboEvolution.getItems().addAll("GED", "Asur");
        comboEvolution.getSelectionModel().select("GED");

        comboClassifier.getItems().addAll("naiveBayes", "bayesNet", "decisionTree", "svm", "randomForest", "decisionStump", "perceptron", "logisticRegression");
        comboClassifier.getSelectionModel().select("decisionTree");

        comboSelectionAttributes.getItems().addAll("Filter", "Manual", "Wrapper");
        comboSelectionAttributes.getSelectionModel().select("Filter");

        comboSearchMethod.getItems().addAll("GreedyStepwise", "BestFirst");
        comboSearchMethod.getSelectionModel().select("GreedyStepwise");

        comboEvaluationMethod.getItems().addAll("CfsSubsetEval", "WrapperSubsetEval");
        comboEvaluationMethod.getSelectionModel().select("CfsSubsetEval");

        //AquaFx.style();
        //  labelStep1 = new Label();
        //detectionMethodCombo.getItems().addAll("CPM", "Louvain", "...");
        //detectionMethodCombo1.getItems().addAll("CPM", "Louvain", "...");

        /*JFrame frame = demo(g);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setVisible(true);*/
        //Node rootIcon = (Node) new ImageView(new Image(getClass().getResourceAsStream("root.png")));
        Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("24-128.png")));
        root = new TreeItem<>("Snapshots", rootIcon);
        treeView.setRoot(root);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                // System.out.println("Selected Text : " + selectedItem.getValue());
                int indexSnap = 0, indexComm = 0;
                try {
                    indexSnap = Integer.parseInt(selectedItem.getParent().getValue().replaceAll("[^0-9]", "")) - 1;
                    indexComm = Integer.parseInt(selectedItem.getValue().replaceAll("[^0-9]", "")) - 1;
                    //System.out.println(indexSnap+" "+indexComm);
                    Graph go = dynamicNetwork.get(indexSnap).getCommunities().get(indexComm); // do what ever you want 
                    //System.out.println(go.getNodeCount() + " " + go.getEdgeCount());
                    prefuse.data.Graph g = graphToGraph(go);
                    //pane.setAutosizeChildren(false);
                    swingNode.setContent(demoComp(g));
                } catch (NumberFormatException | NullPointerException e) {
                }
            }

            private prefuse.data.Graph graphToGraph(Graph g) {
                prefuse.data.Graph gp = new prefuse.data.Graph();
                List<org.graphstream.graph.Node> listNode = new ArrayList<org.graphstream.graph.Node>(g.getNodeSet());
                for (org.graphstream.graph.Node n : listNode) {
                    gp.addNode();
                }
                for (org.graphstream.graph.Edge e : g.getEdgeSet()) {
                    gp.addEdge(listNode.indexOf(e.getSourceNode()), listNode.indexOf(e.getTargetNode()));
                }
//                for (int i = 0; i < 4; ++i) {
//                    Node n1 = g.addNode();
//                    Node n2 = g.addNode();
//                    Node n3 = g.addNode();
//                    g.addEdge(n1, n2);
//                    g.addEdge(n1, n3);
//                    g.addEdge(n2, n3);
//                }
//                g.addEdge(0, 3);
//                g.addEdge(3, 6);
//                g.addEdge(6, 9);
//                g.addEdge(9, 0);
                return gp;
            }
        });

//        for (int i = 0; i < 10; i++) {
//            TreeItem<String> child = new TreeItem<>("Snapshot " + i);
//            root.getChildren().add(child);
//        }
//        root.setExpanded(true);
//
//        treeView.setRoot(root);// = new TreeView<String> (rootItem);  
//        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        String[] strs = {"size", "averageDegree", "averageClusteringCoefficient",
            "degreeAverageDeviation",
            "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity",
            "InOutTotalDegree", "ClosenessCentrality"};
        selectedAttributes.addAll(Arrays.asList(strs));
        selectedAttributes.remove("Centroid");
        selectedAttributes.remove("ClosenessCentrality");

        /*observableListAttibutes.addListener(new ListChangeListener() {
         @Override
         public void onChanged(ListChangeListener.Change change) {
         while (change.next()) {
         if (change.wasAdded()) {
         System.out.println("Was added! ");
         } else if (change.wasRemoved()) {
         System.out.println("Was removed! ");
         }
         //                    System.out.println("Was added? " + change.wasAdded());
         //                    System.out.println("Was removed? " + change.wasRemoved());
         //                    System.out.println("Was replaced? " + change.wasReplaced());
         //                    System.out.println("Was permutated? " + change.wasPermutated());
         }
         }
         });*/
        //******* ListView Attributes *******//
        String[] toppings = {"size", "averageDegree", "averageClusteringCoefficient", "degreeAverageDeviation", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};

        listViewAttributes.getItems().addAll(toppings);
        listViewAttributes.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                /*for (int i = 0; i < selectedAttributes.size(); i++) {
                 if (item.equals(selectedAttributes.get(i))) {
                 observable.set(true);
                 }
                 }*/
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
                    System.out.println(observableListAttibutes.size());

                });
                observable.set(observableListAttibutes.contains(item));
                observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
                        -> observable.set(observableListAttibutes.contains(item)));
                return observable;
            }
        }));
        //***********************************//

//        toppings = {"size", "averageDegree", "averageClusteringCoefficient", "degreeAverageDeviation", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};
        listView.getItems().addAll(toppings);
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                /*for (int i = 0; i < selectedAttributes.size(); i++) {
                 if (item.equals(selectedAttributes.get(i))) {
                 observable.set(true);
                 }
                 }*/
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
                    System.out.println(observableListAttibutes.size());

                });
                observable.set(observableListAttibutes.contains(item));
                observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
                        -> observable.set(observableListAttibutes.contains(item)));
                return observable;
            }
        }));
    }

    @FXML
    void browserAction(ActionEvent event
    ) {

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choisir un fichier");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            filePath = file.getAbsolutePath();
            fileString = file.getName();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            String[] splitContent;
            int i = 7;
            while ((sCurrentLine = br.readLine()) != null && i-- > 0) {

                visualizeFileTA.setText(visualizeFileTA.getText() + sCurrentLine + "\n");

            }
        } catch (Exception e) {

        }
        this.visualizeDataStructure();
        //writeLogLn(file.getAbsolutePath());
//        if (file != null) {
//            String fileName = file.getName();
//            fileLabel.setText(fileName);
//            
//            String fileExtension = fileName.substring(fileName.indexOf(".") + 1, file.getName().length());
//            System.out.println(fileExtension);
//
//            String[] VALUES = new String[]{"PDF", "GEXF", "GDF", "GML", "GraphML", "Pajek NET", "GraphViz DOT", "CSV", "UCINET DL", "Tulip TPL", "Netdraw VNA", "Spreadsheet"};
//            exists = Arrays.asList(VALUES).contains(fileExtension.toLowerCase()) || Arrays.asList(VALUES).contains(fileExtension.toUpperCase());
//            System.out.println(exists);
//
//            if (exists) {
//                timeFormatCombo.setDisable(true);
//                comboStructDonnees.setDisable(true);
//            } else {
//                timeFormatCombo.setDisable(false);
//                comboStructDonnees.setDisable(false);
//            }
//        }
    }

    @FXML
    void browserDetectionAction(ActionEvent event
    ) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choisir un ou plusieurs fichiers");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        Stage stage = (Stage) launchDetection.getScene().getWindow();
        listFile = chooser.showOpenMultipleDialog(new Stage());

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

        nbSnapshots = (listFile != null) ? listFile.size() : 0;

        //fileDetection = chooser.showOpenDialog(new Stage());
    }

    @FXML
    void browserEvolutionAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Text Files", "*.txt"));
        chooser.setTitle("Choisir un fichier");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        fileEvolution = chooser.showOpenDialog(new Stage());

        if (fileEvolution != null && fileEvolution.exists()) {
            filePathEvolution = fileEvolution.getAbsolutePath();

            launchEvolution.setDisable(false);
            directlyEvolution = true;
        }
    }

    @FXML
    void browserPredictionAction(ActionEvent event
    ) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*.*"),
                new ExtensionFilter("Arff Files", "*.arff"));
        chooser.setTitle("Choisir un fichier");
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        chooser.setInitialDirectory(new File(currentPath));

        filePrediction = chooser.showOpenDialog(new Stage());

        if (filePrediction != null && filePrediction.exists()) {
            filePathPrediction = filePrediction.getAbsolutePath();

            launchPrediction.setDisable(false);
            directlyPrediction = true;
        }
    }

    void writeLogLn(String str
    ) {
        //javafx.application.Platform.runLater(() -> logTextArea.setText(logTextArea.getText() + str + "\n"));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.setText(logTextArea.getText() + str + "\n");
                logTextArea.positionCaret(logTextArea.getText().length());
            }
        });

        /*logTextArea.setText(logTextArea.getText() + str + "\n");
         logTextArea.positionCaret(logTextArea.getText().length());*/
    }

    void writeLog(String str
    ) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTextArea.setText(logTextArea.getText() + str);
                logTextArea.positionCaret(logTextArea.getText().length());
            }
        });

    }

    @FXML
    void handleCancel(ActionEvent event
    ) {
    }

    @FXML
    void startAll(ActionEvent event) throws IOException, ParseException, FileNotFoundException, SQLException, Exception {
        //
        Runnable task = new Runnable() {
            public void run() {

            }

        };
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    @FXML
    void startSplit(ActionEvent event
    ) {
        Runnable task = new Runnable() {
            public void run() {
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

                System.out.println("spinnerNBClusters.getValue()= " + spinnerNBClusters.getValue());
                //directoryPath = directoryPath + splitExportName.getText() + "/";
                totalPath = directoryPath + fileString + "/";
                System.out.println(totalPath);
                Path path = Paths.get(totalPath);
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                File theDir = new File(totalPath);
//                if (!theDir.exists()) {
//                    System.out.println("creating directory: " + theDir.getName());
//                    boolean result = false;
//                    try {
//                        theDir.mkdir();
//                        result = true;
//                    } catch (SecurityException se) {
//                        //handle it
//                    }
//                }

                try {
                    float overlapping = (float) sliderOverlapping.getValue() / 100f;
                    System.out.println("overlapping:" + overlapping);
                    if (spinnerNBClusters.getValue() > 0) {
                        System.out.println("1");
                        nbSnapshots = spinnerNBClusters.getValue();
                        snapp.getSplitSnapshots(overlapping, filePath, nbSnapshots, timeFormatCombo.getValue(),
                                comboStructDonnees.getSelectionModel().getSelectedItem(),
                                totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                    } else {

                        List<Duration> durations = stringToDuration(durationsLabel.getText());
                        //String[] splitContent = durationsLabel.getText().split(";");
                        if (durations.size() > 1) {
                            System.out.println("2");
                            nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations,
                                    timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                    totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                        } else {
                            if (durations.size() == 1) {
                                System.out.println("3");
                                nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations.get(0),
                                        timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                        totalPath + splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                            } else {
                                writeLogLn("Erreur d'entées (le nombre de snpashots ou les durations doivent être données)");
                                throw new IllegalArgumentException("Wrong entries (either Durations or Snapshots number should be given)");
                            }
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }

                writeLogLn("Split done. Writing done.");
                if (checkboxSplitMultiExport.isSelected()) {
                    launchDetection.setDisable(false);
                    directlyDetection = false;
                }
            }
        };
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    @FXML
    void startDetection(ActionEvent event) {
        try {
            //treeView.getRoot().getChildren().clear();
            Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("24-128.png")));
            root = new TreeItem<>("Snapshots", rootIcon);
            treeView.setRoot(root);
        } catch (Exception e) {
        }

        Runnable task = new Runnable() {
            public void run() {

                dynamicNetwork = new LinkedList<>();
                List<Graph> graphs = new ArrayList<Graph>();

                //writeLogLn("Executing " + comboDetection.getSelectionModel().getSelectedItem() + "...");
                //System.out.println(dynamicNetwork.size());
                for (int i = 0; i < nbSnapshots; i++) { //for (int i = 0; i < nbSnap; i++) {
                    // reading files 
                    /*File varTmpDir = new File(splitExportName.getText() + i + ".txt");
                     System.out.println(varTmpDir.exists());*/
                    //Graph toWork = (directDetection == null) ? graphs.get(i) : directDetection;
                    String fileToExecute = "";
                    if (directlyDetection) {
                        fileToExecute = listFile.get(i).getAbsolutePath();
                    } else {
                        fileToExecute = totalPath + splitExportName.getText() + i + ".txt";
                    }
                    switch (comboDetection.getSelectionModel().getSelectedItem()) {
                        case "CPM": {
                            Graph toWork;
//                            try (BufferedReader br = new BufferedReader(new FileReader(fileToExecute))) {
//                                String sCurrentLine;
//                                while ((sCurrentLine = br.readLine()) != null) {
//                                    String[] str = sCurrentLine.split(" ");
//                                    toWork.addEdge(str[0] + ";" + str[1], str[0], str[1]);
//                                }
//                            } catch (IOException e) {
//                                //e.printStackTrace();
//                            }
                            writeLog("Exécution de CPM sur le snapshot " + i + "...");
                            graphs.add(SnapshotsPrep.readCommunity(fileToExecute));
                            System.out.println("file " + splitExportName.getText() + " was read");
                            toWork = graphs.get(i);
                            /*if (!directlyDetection) {
                             writeLog("Exécution de CPM sur le snapshot " + i + "...");
                             graphs.add(SnapshotsPrep.readCommunity(fileToExecute));
                             System.out.println("file " + splitExportName.getText() + " was read");
                             toWork = graphs.get(i);
                             } else {
                             toWork = SnapshotsPrep.readCommunity(fileToExecute);
                             writeLog("Exécution de CPM...");
                             }*/
                            System.out.println(toWork.getNodeCount() + " nodes were read");

                            CPM cpm = new CPM();
                            LinkedList<Graph> communities = cpm.execute(toWork, snipperDetection.getValue());
                            //if (communities.size() > 0) {
                            //System.out.println(dynamicNetwork.size());
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            //System.out.println(dynamicNetwork.size());
                            //}
                            break;
                        }
                        case "SLPA": {
                            if (!directlyDetection) {
                                writeLog("Exécution de SLPA sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de SLPA...");
                            }
                            LinkedList<Graph> communities = (new SLPA()).findCommunities(fileToExecute, snipperDetection.getValue());
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            break;
                        }
                        case "GN": {
                            if (!directlyDetection) {
                                writeLog("Exécution de GN sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de GN...");
                            }
                            LinkedList<Graph> communities = (new GN()).findCommunities2(fileToExecute, 50/*nbComm*/);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            break;
                        }
                        case "CONGA": {
                            if (!directlyDetection) {
                                writeLog("Exécution de CONGA sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de CONGA...");
                            }
                            //writeLogLn("Executing CONGA...");
                            LinkedList<Graph> communities = (new CONGA()).findCommunities2(fileToExecute, 5/*nbComm*/);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            //dynamicNetwork.add(new TimeFrame(communities));
                            break;
                        }
                        case "COPRA": {
                            if (!directlyDetection) {
                                writeLog("Exécution de COPRA sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de COPRA...");
                            }
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
                            writeLogLn(" terminée.");

                            //(new COPRA()).findCommunities(fileToExecute);
                            break;
                        }
                        case "CM": {
                            if (!directlyDetection) {
                                writeLog("Exécution de CM sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de CM...");
                            }
                            LinkedList<Graph> communities = (new CM().findCommunities2(fileToExecute, snipperDetection.getValue(), "KJ"));
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            break;
                        }
                        case "CONCLUDE": {
                            if (!directlyDetection) {
                                writeLog("Exécution de CONCLUDE sur le snapshot " + i + "...");
                            } else {
                                writeLog("Exécution de CONCLUDE...");
                            }
                            LinkedList<Graph> communities = (new CONCLUDE()).findCommunities(fileToExecute);
                            dynamicNetwork.add(new TimeFrame(communities));
                            writeLogLn(" terminée.");
                            //(new CONCLUDE()).findCommunities2(fileToExecute);
                            break;
                        }
                        default: {
                            writeLogLn("Méthode non encore liée.");
                            break;
                        }
                    }
                    // System.out.println("\n");
                    if (checkboxDetection.isSelected()) {
                        try {
                            writeLog("Export des résultats du Snapshot " + i + "...");
                            exportCommunity(dynamicNetwork.get(dynamicNetwork.size() - 1), totalPath + "detection_" + comboDetection.getSelectionModel().getSelectedItem() + "_" + snipperDetection.getValue() + ".txt", i);
                            writeLogLn("terminée");
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                //writeLogLn(comboDetection.getSelectionModel().getSelectedItem() + " done.");


                /*for (int i = 0; i < 10; i++) {
                 TreeItem<String> child = new TreeItem<>("Snapshot " + i);
                 treeView.getRoot().getChildren().add(child);
                 treeView.getRoot().setExpanded(true);
                 }*/
                // = new TreeView<String> (rootItem);  
                //for (TimeFrame tf : dynamicNetwork) {
                /*Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("24-128.png")));
                 root = new TreeItem<>("Snapshots", rootIcon);*/
                treeView.getRoot().getChildren().clear();
                System.out.println("Size tf: " + dynamicNetwork.size());
                for (int k = 0; k < dynamicNetwork.size(); k++) {
                    TimeFrame tf = dynamicNetwork.get(k);
                    //TreeItem<String> child = new TreeItem<>(Integer.toString(k + 1));
                    TreeItem<String> child = new TreeItem<>("Snapshot " + (k + 1));
                    treeView.getRoot().getChildren().add(child);
                    //System.out.println("comm: " + tf.getCommunities().size());
                    for (Graph com : tf.getCommunities()) {
                        //TreeItem<String>vf child2 = new TreeItem<>(Integer.toString(tf.getCommunities().indexOf(com) + 1));
                        TreeItem<String> child2 = new TreeItem<>("Communauté " + (tf.getCommunities().indexOf(com) + 1));
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
//                        writeLogLn("terminée");
//                    } catch (IOException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                }
                directlyEvolution = false;
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
                       /// writer.write(e.getSourceNode().getId() + " " + e.getTargetNode().getId() + " " + snp + " " + i + "\n");
                        //writer.write(e.getSourceNode().getId() + " " + e.getSourceNode().getAttribute(att) + " " + e.getTargetNode().getId() + " " + e.getTargetNode().getAttribute(att) + " " + k + " " + i + "\n");
                        //System.out.println(dynamicNetwork.indexOf(tf) + " " + tf.getCommunities().indexOf(g));
                    }
                }
                } catch (IOException e) {
                    System.err.println(e);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

                
                //writer.close();

            }

        };
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    @FXML
    void startEvolution(ActionEvent event
    ) {
        Runnable task = new Runnable() {
            public void run() {
                writeLogLn("Exécution de " + comboEvolution.getSelectionModel().getSelectedItem() + "...");
                if (directlyEvolution) {
                    /*Read file of structure: n1|n2|t|g */
                    dynamicNetwork = readDynamicNetwork();
                    System.out.println("dynamicNetwork.size: " + dynamicNetwork.size()
                    );
                    writeLogLn("Calcul des attributs...");
                    AttributesComputer.calculateAttributes(dynamicNetwork, observableListAttibutes);
                    writeLogLn("Calcul des attributes terminé.");
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
                            ged.excuteGED(dynamicNetwork, Integer.parseInt(para[0]), Integer.parseInt(para[1]), BDpath + BDfilename);
                        } catch (FileNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (UnsupportedEncodingException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (SQLException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    }
                    case "Asur": {
                        writeLogLn("Asur started...");
//                        try {
//                            //String str = !evolutionParameters.getText().equals("") ? evolutionParameters.getText() : evolutionParameters.getPromptText();
//                            //String para[] = str.split(";");
//                            //Asur.ex
//                        } catch (FileNotFoundException ex) {
//                            Exceptions.printStackTrace(ex);
//                        } catch (UnsupportedEncodingException ex) {
//                            Exceptions.printStackTrace(ex);
//                        } catch (SQLException ex) {
//                            Exceptions.printStackTrace(ex);
//                        }
                        break;
                    }
                    default: {
                        writeLogLn("Method not linked yet.");
                        break;
                    }
                }
                writeLogLn(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                System.out.println(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                writeLogLn("Création des chaines d'evolution...");

//                int nbtimeframe = dynamicNetwork.size();
                writeEvolutionChain(BDpath, BDfilename, tabname, dynamicNetwork.size(), 2/* nbre timeframes */
                );
                writeLogLn("Evolution chains created.");

                launchPrediction.setDisable(false);
            }

            private LinkedList<TimeFrame> readDynamicNetwork() {
                /*Read file of structure: "n1 att1 n2 att2 t g" */
                LinkedList<TimeFrame> dynamicNet = new LinkedList<TimeFrame>();
                try (BufferedReader br = new BufferedReader(new FileReader(filePathEvolution))) {
                    String sCurrentLine;
                    while ((sCurrentLine = br.readLine()) != null) {
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
//                        dynamicNet.get(snp).getCommunities().get(comm).getEdge(str[0] + ";" + str[2]).getSourceNode().setAttribute("bcentrality", Double.parseDouble(str[1]));
//                        dynamicNet.get(snp).getCommunities().get(comm).getEdge(str[0] + ";" + str[2]).getTargetNode().setAttribute("bcentrality", Double.parseDouble(str[3]));
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                return dynamicNet;
            }

        };
        // Run the task in a background thread
        Thread backgroundThread = new Thread(task);
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        // Start the thread
        backgroundThread.start();
    }

    @FXML
    void startPrediction(ActionEvent event
    ) {
        if (!directlyPrediction) {
            String filePath = "./LibPrediction/";
            String filename = "trainData";
            String extension = ".arff";

            filePathPrediction = filePath + filename + extension;

            //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,/**nbre timeframes**/);
            //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
            //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
            //PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, dynamicNetwork.size(), "", "", 4);
            PredictionUtils.createArffAttribute(filePath, filename, BDpath, BDfilename, dynamicNetwork.size(),
                    chainLength.getValue(), (ArrayList<String>) selectedAttributes, dynamicNetwork);

            // filePath, filename, BDpath, BDfilename,
            //dynamicNetwork.size(), "", "", 4, (ArrayList<String>) observableListAttibutes, dynamicNetwork);
            writeLogLn("Arff file generated.");
        }

        try {
            EvaluationReport eReport = PredictionUtils.makePredictor(comboSelectionAttributes.getSelectionModel().getSelectedItem(),
                    comboSearchMethod.getSelectionModel().getSelectedItem(), comboEvaluationMethod.getSelectionModel().getSelectedItem(),
                    comboClassifier.getSelectionModel().getSelectedItem(), null, filePathPrediction, 10);
            writeLogLn(eReport.getSummary());
            for (String str : eReport.getConfusionMatrix()) {
                writeLogLn(str);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        writeLogLn("Prediction done.");
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
                    writeLogLn("Vérifier la structure de l'entrée");
                //throw new IllegalArgumentException("Vérifier la structure de l'entrée SVP");
            }
        }
        return listDuration;
    }

    @FXML
    void selectAttributes(ActionEvent event) {
        listView = new ListView<>();
        String[] toppings = {"size", "averageDegree", "averageClusteringCoefficient", "degreeAverageDeviation", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};
        listView.getItems().addAll(toppings);
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                /*for (int i = 0; i < selectedAttributes.size(); i++) {
                 if (item.equals(selectedAttributes.get(i))) {
                 observable.set(true);
                 }
                 }*/
                observable.set(selectedAttributes.contains(item));
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
        }));

        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                /*for (int i = 0; i < selectedAttributes.size(); i++) {
                 if (item.equals(selectedAttributes.get(i))) {
                 observable.set(true);
                 }
                 }*/
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
                    System.out.println(observableListAttibutes.size());

                });
                observable.set(observableListAttibutes.contains(item));
                observableListAttibutes.addListener((ListChangeListener.Change<? extends String> c)
                        -> observable.set(observableListAttibutes.contains(item)));
                return observable;
            }
        }));

        // VBox
        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 30, 30, 30));
        vb.setSpacing(10);

        Label lbl = new Label("Séléctionner les attributs à calculer: ");
        lbl.setFont(Font.font("Amble CN", FontWeight.BOLD, 13));
        vb.getChildren().add(lbl);

        vb.getChildren().add(listView);

        // Adding VBox to the scene
        /*Scene scene = new Scene(vb);
         BorderPane root = new BorderPane(listView);*/
        Scene scene = new Scene(vb, 300, 400);
        Stage dialog = new Stage();
        dialog.setTitle("Attributs");
        dialog.initOwner((Stage) launchPrediction.getScene().getWindow());
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

            Label lbl = new Label("Structure du fichier:");
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
            dialog.setTitle("Structure de fichier");
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
         * checkboxSplitMultiExport.isSelected());*
         */
        try {
            String dataStructure = comboStructDonnees.getSelectionModel().getSelectedItem();

            MyResult myResult = new MyResult();
            myResult.getResults(filePath, timeFormatCombo.getValue(), dataStructure);

            FileInputStream stream = new FileInputStream(new File(filePath));

            List<Integer> counters = new ArrayList<Integer>();
            for (int i = 0; i < 1000; i++) {
                counters.add(0);
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
                        int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / 500);
                        // System.out.println(duration.getSeconds()+" "+step);
                        int index = (int) ((timestamp - myResult.getMinTS()) / step);
                        if (timestamp == myResult.getMaxTS()) {
                            index--;
                        }
                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        counters.set(index, counters.get(index) + 1);
                    }
                }
                /*for (int i : counters) {
                 System.out.println(i);
                 }*/

            }
            init(new Stage(), counters);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Incomplete entry");
            alert.setContentText("Please make sure to enter the file and data structure correctly");

            alert.showAndWait();
        }
    }

    private void init(Stage primaryStage, List<Integer> counters) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        root.getChildren().add(createChart(counters));
        primaryStage.show();
    }

    protected BarChart<String, Number> createChart(List<Integer> counters) {
        //final String[] years = {"2007", "2008", "2009"};
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis));
        final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        // setup chart
        bc.setTitle("Advanced Bar Chart");
        xAxis.setLabel("Year");

        yAxis.setLabel("Price");

        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
        series1.setName("Data Series 1");

        /*series1.getData().add(new XYChart.Data<String, Number>("0", 567));
         series1.getData().add(new XYChart.Data<String, Number>("1", 1292));
         series1.getData().add(new XYChart.Data<String, Number>("2", 2180));*/
        int max = 0;
        for (int i = 0; i < 500; i++) {
            /*Random r = new Random();
             int Low = 0;
             int High = 3000;
             int Result = r.nextInt(High - Low) + Low;
             max = Math.max(max, Result);*/

            series1.getData().add(new XYChart.Data<String, Number>(Integer.toString(i), counters.get(i)));
        }
        bc.getData().add(series1);
        return bc;
    }

    @FXML
    void startCalculateAttributes(ActionEvent event) {
        writeLog("Calcul des attributs...");
        AttributesComputer.calculateAttributes(dynamicNetwork, observableListAttibutes);
        writeLogLn(" terminé.");
        launchEvolution.setDisable(false);
    }

}
