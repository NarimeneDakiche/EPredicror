/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Prediction.PredictionUtils;
import SnapshotsPrep.SnapshotsPrep;
import communityDetection.CPM;
import communityDetection.ExternMethods.CM;
import communityDetection.ExternMethods.CONCLUDE;
import communityDetection.ExternMethods.CONGA;
import communityDetection.ExternMethods.COPRA;
import communityDetection.ExternMethods.GN;
import communityDetection.ExternMethods.SLPA;
import evolutionIdentification.AttributesComputer;
import static evolutionIdentification.EvolutionUtils.writeEvolutionChain;
import evolutionIdentification.GED1;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
import javafx.stage.FileChooser;
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

    @FXML
    private ListView<String> listViewAttributes;

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
    private CheckBox checkboxSplitMultiExport;

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

    private String filePath;

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
    private Button launchPrediction;

    @FXML
    private TextField overlappingLabel;

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

    private LinkedList<TimeFrame> dynamicNetwork = new LinkedList<TimeFrame>();
    //private LinkedList<TimeFrame> dynamicNetwork1 = new LinkedList<TimeFrame>();
    private int nbSnapshots = 0;

    private TreeItem<String> root;

    private boolean directlyDetection = false;
    private boolean directlyEvolution = false;
    private boolean directlyPrediction = false;

    private List<String> selectedAttributes = new ArrayList<>();

    private String BDpath = "./GED/";
    private String BDfilename;
    private String tabname = "GED_evolution";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        indexGraph = 0;
        // TODO
        accordion1.setExpandedPane(titledpane1);
        launchDetection.setDisable(true);
        launchEvolution.setDisable(true);
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
                "Timestamp");
        timeFormatCombo.getSelectionModel().select("Timestamp");

        spinnerNBClusters.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 100, 0));
        snipperDetection.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5));

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
                    dialog.setContentText("Enter item");
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

        //******* ListView Attributes *******//
        String[] toppings = {"averageDegree", "averageClusteringCoefficient", "averageClusteringCoefficients", "degreeAverageDeviation", "degreeDistribution", "density", "diameter", "Bc", "Centroid", "Cohesion", "Leadership", "Reciprocity", "InOutTotalDegree", "ClosenessCentrality"};
        listViewAttributes.getItems().addAll(toppings);
        listViewAttributes.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        selectedAttributes.add(item);
                    } else {
                        selectedAttributes.remove(item);
                    }
                });
                return observable;
            }
        }));
        //***********************************//

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
    }

    @FXML
    void browserAction(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        file = chooser.showOpenDialog(new Stage());

        if (file != null && file.exists()) {
            filePath = file.getAbsolutePath();

        }
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
    void browserDetectionAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        fileDetection = chooser.showOpenDialog(new Stage());

        if (fileDetection != null && fileDetection.exists()) {
            filePathDetection = fileDetection.getAbsolutePath();
            launchDetection.setDisable(false);
            directlyDetection = true;
            nbSnapshots = 1;
        }
    }

    @FXML
    void browserEvolutionAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        fileEvolution = chooser.showOpenDialog(new Stage());

        if (fileEvolution != null && fileEvolution.exists()) {
            filePathEvolution = fileEvolution.getAbsolutePath();

            launchEvolution.setDisable(false);
            directlyEvolution = true;
        }
    }

    @FXML
    void browserPredictionAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        filePrediction = chooser.showOpenDialog(new Stage());

        if (filePrediction != null && filePrediction.exists()) {
            filePathPrediction = filePrediction.getAbsolutePath();

            launchPrediction.setDisable(false);
            directlyPrediction = true;
        }
    }

    void writeLogLn(String str) {
        logTextArea.setText(logTextArea.getText() + str + "\n");
        logTextArea.positionCaret(logTextArea.getText().length());
    }

    void writeLog(String str) {
        logTextArea.setText(logTextArea.getText() + str);
        logTextArea.positionCaret(logTextArea.getText().length());
    }

    @FXML
    void handleCancel(ActionEvent event) {
    }

    @FXML
    void startAll(ActionEvent event) throws IOException, ParseException, FileNotFoundException, SQLException, Exception {
        //
        Runnable task = new Runnable() {
            public void run() {

//                SnapshotsPrep snapp = new SnapshotsPrep();
//                /* Duration d = Duration.ofDays(10);
//                 List<Duration> listDuration = new ArrayList<Duration>();
//                 //listDuration.add(Duration.ofDays(500));
//                 listDuration.add(Duration.ofDays(950));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(3));
//                 listDuration.add(Duration.ofDays(10));*/
//                //System.out.println("spinnerNBClusters.getValue()= " + spinnerNBClusters.getValue());
//
//                try {
//                    float overlapping = isFloat(overlappingLabel.getText()) ? Float.parseFloat(overlappingLabel.getText()) : 0;
//                    if (spinnerNBClusters.getValue() > 0) {
//                        nbSnapshots = spinnerNBClusters.getValue();
//                        snapp.getSplitSnapshots(overlapping, filePath, nbSnapshots,
//                                timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
//                                " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
//                    } else {
//                        List<Duration> durations = stringToDuration(durationsLabel.getText());
//                        //String[] splitContent = durationsLabel.getText().split(";");
//                        if (durations.size() > 1) {
//                            nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations,
//                                    timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
//                                    " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
//                        } else {
//                            if (durations.size() == 1) {
//                                nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations.get(0),
//                                        timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
//                                        " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
//                            } else {
//                                writeLogLn("Erreur d'entées (le nombre de snpashots ou les durations doivent être données)");
//                                throw new IllegalArgumentException("Wrong entries (either Durations or Snapshots number should be given)");
//                            }
//                        }
//                    }
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                } catch (ParseException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//
//                writeLogLn("Split done. Writing done.");
//
//                dynamicNetwork = new LinkedList<>();
//                List<Graph> graphs = new ArrayList<Graph>();
//
//                //writeLogLn("Executing " + comboDetection.getSelectionModel().getSelectedItem() + "...");
//                //System.out.println(dynamicNetwork.size());
//                for (int i = 0; i < nbSnapshots; i++) { //for (int i = 0; i < nbSnap; i++) {
//                    // reading files 
//                    writeLogLn("Executing " + comboDetection.getSelectionModel().getSelectedItem() + " on timeframe " + i + "...");
//                    graphs.add(SnapshotsPrep.readCommunity(splitExportName.getText() + i + ".txt"));
//                    System.out.println("file " + splitExportName.getText() + " was read");
//                    System.out.println(graphs.get(i).getNodeCount() + " nodes were read");
//
//                    switch (comboDetection.getSelectionModel().getSelectedItem()) {
//                        case "CPM": {
//                            CPM cpm = new CPM();
//                            LinkedList<Graph> communities = cpm.execute(graphs.get(i), snipperDetection.getValue());
//                            if (communities.size() > 0) {
//                                dynamicNetwork.add(new TimeFrame(communities));
//                            }
//                            break;
//                        }
//                        default: {
//                            writeLogLn("Method not linked yet.");
//                            break;
//                        }
//                    }
//                    System.out.println("\n");
//                }
//                writeLogLn(comboDetection.getSelectionModel().getSelectedItem() + " done.");
//
//                writeLogLn("Calculating attributes...");
//
//                for (TimeFrame tf : dynamicNetwork) {
//                    for (Graph com : tf.getCommunities()) {
//                        AttributesComputer.calculateAttributes(tf.getTimGraph(), com);
//                    }
//                }
//                writeLogLn("Calculating attributes done.");
//
//                writeLogLn("Executing " + comboEvolution.getSelectionModel().getSelectedItem() + "...");
//
//                switch (comboEvolution.getSelectionModel().getSelectedItem()) {
//                    case "GED": {
//                        writeLogLn("GED started...");
//                        GED1 ged = new GED1();
//                        try {
//                            ged.excuteGED(dynamicNetwork, 50, 50, evolutionExportLabel.getText());
//                        } catch (FileNotFoundException ex) {
//                            Exceptions.printStackTrace(ex);
//                        } catch (UnsupportedEncodingException ex) {
//                            Exceptions.printStackTrace(ex);
//                        } catch (SQLException ex) {
//                            Exceptions.printStackTrace(ex);
//                        }
//                        break;
//                    }
//                    case "Asur": {
//                        writeLogLn("Method not linked yet.");
//                    }
//                    default: {
//                        writeLogLn("Method not linked yet.");
//                        break;
//                    }
//                }
//                writeLogLn(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
//                System.out.println(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
//                writeLogLn("Creating evolution chains...");
//
//                BDpath = "./GED/";
//                BDfilename = evolutionExportLabel.getText() + "_50_50" + ".db";
//                tabname = "GED_evolution";
//                int nbtimeframe = dynamicNetwork.size();
//                writeEvolutionChain(BDpath, BDfilename, tabname, dynamicNetwork.size(), 2/* nbre timeframes */
//                );
//
//                writeLogLn("Evolution chains created.");
//
//                String filePath = "./LibPrediction/";
//                String filename = "trainData";
//                String extension = ".arff";
//
//                //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,nbtimeframe/**nbre timeframes**/);
//                //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
//                //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
//                PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, nbtimeframe, "", "", 4);
//                writeLogLn("Arff file generated.");
//
//                try {
//                    PredictionUtils.createClassifier(filePath + filename + extension, dynamicNetwork.size());
//                } catch (Exception ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//                writeLogLn("Prediction done.");
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
    void startSplit(ActionEvent event) {
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

                try {
                    float overlapping = isFloat(overlappingLabel.getText()) ? Float.parseFloat(overlappingLabel.getText()) : 0;
                    if (spinnerNBClusters.getValue() > 0) {
                        System.out.println("1");
                        nbSnapshots = spinnerNBClusters.getValue();
                        snapp.getSplitSnapshots(overlapping, filePath, nbSnapshots,
                                timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                    } else {

                        List<Duration> durations = stringToDuration(durationsLabel.getText());
                        //String[] splitContent = durationsLabel.getText().split(";");
                        if (durations.size() > 1) {
                            System.out.println("2");
                            nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations,
                                    timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                    " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                        } else {
                            if (durations.size() == 1) {
                                System.out.println("3");
                                nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations.get(0),
                                        timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                        " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
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
                        fileToExecute = filePathDetection;
                    } else {
                        fileToExecute = splitExportName.getText() + i + ".txt";
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
                            if (!directlyDetection) {
                                writeLog("Exécution de CPM sur le snapshot " + i + "...");
                                graphs.add(SnapshotsPrep.readCommunity(splitExportName.getText() + i + ".txt"));
                                System.out.println("file " + splitExportName.getText() + " was read");
                                toWork = graphs.get(i);
                            } else {
                                toWork = SnapshotsPrep.readCommunity(fileToExecute);
                                writeLog("Exécution de CPM...");
                            }
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
                writeLog("Calcul des attributs...");
                AttributesComputer.calculateAttributes(dynamicNetwork);
                writeLogLn(" terminé.");

                treeView.getRoot().setExpanded(true);
                if (dynamicNetwork.size() > 1) {
                    launchEvolution.setDisable(false);
                }
                try {
                    writeLogLn("Export des résultats...");
                    exportDynamicNetwork(dynamicNetwork, "exportDetection_" + comboDetection.getSelectionModel().getSelectedItem() + ".txt", attributesCombo.getValue());
                    writeLogLn("Export des résultats terminée");
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                directlyEvolution = false;
            }

            private void exportDynamicNetwork(LinkedList<TimeFrame> dynamicNetwork, String exportName, String att) throws IOException {
                /* Exports n1 att1 n2 att2 tf g */

                BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

                for (int k = 0; k < dynamicNetwork.size(); k++) {
                    TimeFrame tf = dynamicNetwork.get(k);
                    for (int i = 0; i < tf.getCommunities().size(); i++) {
                        Graph g = tf.getCommunities().get(i);
                        //for (Graph g : tf.getCommunities()) {
                        for (org.graphstream.graph.Edge e : g.getEdgeSet()) {
                            writer.write(e.getSourceNode().getId() + " " + e.getSourceNode().getAttribute(att) + " " + e.getTargetNode().getId() + " " + e.getTargetNode().getAttribute(att) + " " + k + " " + i + "\n");
                            //System.out.println(dynamicNetwork.indexOf(tf) + " " + tf.getCommunities().indexOf(g));
                        }
                    }
                }
                writer.close();
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
    void startEvolution(ActionEvent event) {
        Runnable task = new Runnable() {
            public void run() {
                writeLogLn("Exécution de " + comboEvolution.getSelectionModel().getSelectedItem() + "...");
                if (directlyEvolution) {
                    /*Read file of structure: n1|n2|t|g */
                    dynamicNetwork = readDynamicNetwork();
                    System.out.println("dynamicNetwork.size: " + dynamicNetwork.size()
                    );
                    writeLogLn("Calcul des attributs...");
                    AttributesComputer.calculateAttributes(dynamicNetwork);
                    writeLogLn("Calcul des attributes terminé.");
                }

                switch (comboEvolution.getSelectionModel().getSelectedItem()) {
                    case "GED": {
                        writeLogLn("GED started...");
                        GED1 ged = new GED1();
                        try {
                            String str = !evolutionParameters.getText().equals("") ? evolutionParameters.getText() : evolutionParameters.getPromptText();
                            String para[] = str.split(";");
                            ged.excuteGED(dynamicNetwork, Integer.parseInt(para[0]), Integer.parseInt(para[1]), evolutionExportLabel.getText());
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
                        writeLogLn("Method not linked yet.");
                    }
                    default: {
                        writeLogLn("Method not linked yet.");
                        break;
                    }
                }
                writeLogLn(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                System.out.println(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                writeLogLn("Création des chaines d'evolution...");

                BDpath = "./GED/";
                BDfilename = evolutionExportLabel.getText() + "_50_50" + ".db";
                tabname = "GED_evolution";
                int nbtimeframe = dynamicNetwork.size();
                writeEvolutionChain(BDpath, BDfilename, tabname, nbtimeframe, 2/* nbre timeframes */
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
                        int t = Integer.parseInt(str[4]);
                        int g = Integer.parseInt(str[5]);

                        while (dynamicNet.size() <= t) {
                            dynamicNet.add(new TimeFrame(new LinkedList<Graph>()));
                        }
                        while (dynamicNet.get(t).getCommunities().size() <= g) {
                            Graph group = new SingleGraph("");
                            group.setStrict(false);
                            group.setAutoCreate(true);
                            dynamicNet.get(t).getCommunities().add(group);
                        }

                        dynamicNet.get(t).getCommunities().get(g).addEdge(str[0] + ";" + str[2], str[0], str[2]);
                        dynamicNet.get(t).getCommunities().get(g).getEdge(str[0] + ";" + str[2]).getSourceNode().setAttribute("bcentrality", Double.parseDouble(str[1]));
                        dynamicNet.get(t).getCommunities().get(g).getEdge(str[0] + ";" + str[2]).getTargetNode().setAttribute("bcentrality", Double.parseDouble(str[3]));
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
    void startPrediction(ActionEvent event) {
        //System.out.println(selectedAttributes.size());
        if (!directlyPrediction) {
            String filePath = "./LibPrediction/";
            String filename = "trainData";
            String extension = ".arff";

            filePathPrediction = filePath + filename + extension;

            //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,nbtimeframe/**nbre timeframes**/);
            //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
            //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
            //PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, dynamicNetwork.size(), "", "", 4);
            PredictionUtils.createArffAttribute(filePath, filename, BDpath, BDfilename,
                    dynamicNetwork.size(), "", "", 4, (ArrayList<String>) selectedAttributes, dynamicNetwork);

            writeLogLn("Arff file generated.");
        }

        try {
            PredictionUtils.createClassifier(filePathPrediction, dynamicNetwork.size());
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
                    writeLogLn("Vérifier la structure de l'entrée SVP");
                    throw new IllegalArgumentException("Vérifier la structure de l'entrée SVP");
            }
        }
        return listDuration;
    }
}
