/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Prediction.PredictionUtils;
import SnapshotsPrep.SnapshotsPrep;
import communityDetection.CPM;
import evolutionIdentification.AttributesComputer;
import static evolutionIdentification.EvolutionUtils.writeEvolutionChain;
import evolutionIdentification.GED1;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.File;
import java.io.FileNotFoundException;
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
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import prefuse.data.Node;
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
    private ListView<Item> listViewAttributes;

    @FXML
    private Spinner<Integer> spinnerNBClusters;

    @FXML
    private Spinner<Integer> snipperDetection;

    @FXML
    private ComboBox<String> timeFormatCombo;

    @FXML
    private Label labelStep1;

    @FXML
    private ComboBox<String> comboStructDonnees;

    @FXML
    private ComboBox<String> comboDetection;

    @FXML
    private Label indexGraphLabel;

    @FXML
    private CheckBox checkboxSplitMultiExport;

    @FXML
    private TextField splitExportName;

    private File file;

    private boolean exists;

    private int indexGraph;

    private Viewer viewer;

    private ViewPanel view;

    private String filePath;

    @FXML
    private Button launchDetection;

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

    private LinkedList<TimeFrame> dynamicNetwork = new LinkedList<TimeFrame>();
    //private LinkedList<TimeFrame> dynamicNetwork1 = new LinkedList<TimeFrame>();
    private int nbSnapshots = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        indexGraph = 0;
        // TODO
        accordion1.setExpandedPane(titledpane1);
        launchDetection.setDisable(true);
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
        comboStructDonnees.getItems().addAll("TVW", "VWT", "TTVW", "VWXT");
        comboStructDonnees.getSelectionModel().select("TVW");

        comboDetection.getItems().addAll("CPM", "CM", "CONCLUDE", "CONGA", "COPRA", "GN", "SLPA");
        comboDetection.getSelectionModel().select("CPM");

        comboEvolution.getItems().addAll("GED", "Asur");
        comboEvolution.getSelectionModel().select("GED");

        //AquaFx.style();
        //  labelStep1 = new Label();
        //detectionMethodCombo.getItems().addAll("CPM", "Louvain", "...");
        //detectionMethodCombo1.getItems().addAll("CPM", "Louvain", "...");
        for (int i = 1; i <= 20; i++) {
            Item item = new Item("Item " + i, false);

            // observe item's on property and display message if it changes:
            item.onProperty().addListener((obs, wasOn, isNowOn) -> {
                System.out.println(item.getName() + " changed on state from " + wasOn + " to " + isNowOn);
            });

            listViewAttributes.getItems().add(item);
        }

        listViewAttributes.setCellFactory(CheckBoxListCell.forListView(new Callback<Item, ObservableValue<Boolean>>() {
            public ObservableValue<Boolean> call(Item item) {
                return item.onProperty();
            }
        }));

        prefuse.data.Graph g = new prefuse.data.Graph();
        for (int i = 0; i < 4; ++i) {
            Node n1 = g.addNode();
            Node n2 = g.addNode();
            Node n3 = g.addNode();
            g.addEdge(n1, n2);
            g.addEdge(n1, n3);
            g.addEdge(n2, n3);
        }
        g.addEdge(0, 3);
        g.addEdge(3, 6);
        g.addEdge(6, 9);
        g.addEdge(9, 0);
        //pane.setAutosizeChildren(false);
        swingNode.setContent(demoComp(g));
        /*JFrame frame = demo(g);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setVisible(true);*/

    }

    @FXML
    void browserAction(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        file = chooser.showOpenDialog(new Stage());
        filePath = file.getAbsolutePath();
        //writeLog(file.getAbsolutePath());
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
        /* FileChooser chooser = new FileChooser();
         chooser.setTitle("Choisir un fichier");
         file = chooser.showOpenDialog(new Stage());
         filePath = file.getAbsolutePath();

         g.setStrict(false);
         g.setAutoCreate(true);
         try (BufferedReader br = new BufferedReader(new FileReader(file))) {
         String sCurrentLine;

         while ((sCurrentLine = br.readLine()) != null) {
         String[] str = sCurrentLine.split(" ");
         g.addEdge(str[0] + ";" + str[1], str[0], str[1]);
         }

         } catch (IOException e) {
         e.printStackTrace();
         }*/
    }

    @FXML
    void start1ButtonAction(ActionEvent event) throws FileNotFoundException, ParseException, UnsupportedEncodingException, IOException {

        if (exists) {
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            Workspace workspace = pc.getCurrentWorkspace();
            //Get controllers and models
            ImportController importController = Lookup.getDefault().lookup(ImportController.class);
            //Import file
            Container container;
            try {
                container = importController.importFile(file);
                //File file = new File(main.getClass().getResource("/org/gephi/toolkit/demos/polblogs.gml").toURI());
                container.getLoader().setEdgeDefault(org.gephi.io.importer.api.EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
            DirectedGraph dG = graphModel.getDirectedGraph();
            for (org.gephi.graph.api.Node n : dG.getNodes()) {

            }
//            Graph g = new DefaultGraph("g");
//            FileSource fs = FileSourceFactory.sourceFor(file.getAbsolutePath());
//            fs.addSink(g);
//            try {
//                System.out.println(file.getAbsolutePath());
//                fs.readAll(file.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                fs.removeSink(g);
//            }

            /*System.out.println(g.getNodeCount());
             Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
             viewer.enableAutoLayout();
             ViewPanel view = viewer.addDefaultView(false);   // false indicates "no JFrame".
             // ...

             //frame.add(view);
             //JPanel jPanel = new JPanel();
             //jPanel.setPreferredSize(new Dimension(800, 600));
             view.setPreferredSize(new Dimension(520, 415));

             //view.setMaximumSize(new Dimension(1000, 1000));
             //jPanel.add(view);
             s.setContent(view);*/
        } else {
            try {
                SnapshotsPrep snapp = new SnapshotsPrep();
                if (file != null && spinnerNBClusters.getValue() > 0 && comboStructDonnees.getValue() != null) {
                    /* dynamicNetwork = snapp.getSplitSnapshots(file.getAbsolutePath(),
                     null, comboStructDonnees.getValue(), " ", spinnerNBClusters.getValue(), "export", false, false, "");
                     snapp = null;
                     System.gc();*/
                    labelStep1.setText("Splitting done!");
                    //this.displayGraph(dynamicNetwork.get(indexGraph).getTimGraph());
                    //startDetectionButton1.setDisable(false);

                } else {
                    int x = 1 / 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                labelStep1.setText("Inacurate entries!. Please check again.");
            }
        }

    }

    /*@FXML
     void handleNextView(ActionEvent event) {
     indexGraph = (indexGraph + 1) % dynamicNetwork1.size();
     this.displayGraph(dynamicNetwork1.get(indexGraph).getTimGraph());
     }

     @FXML
     void handlePreviousView(ActionEvent event) {
     indexGraph = (indexGraph + dynamicNetwork.size() - 1) % dynamicNetwork.size();
     this.displayGraph(dynamicNetwork1.get(indexGraph).getTimGraph());
     }

     private void displayGraph(Graph g) {
     //s.getContent().removeAll();
     indexGraphLabel.setText(Integer.toString(indexGraph));
     System.out.println(g.getNodeCount());

     viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
     viewer.enableAutoLayout();
     view = viewer.addDefaultView(false);   // false indicates "no JFrame".
     // ...
     //frame.add(view);
     //JPanel jPanel = new JPanel();
     //jPanel.setPreferredSize(new Dimension(800, 600));
     view.setPreferredSize(new Dimension(520, 415));

     //view.setMaximumSize(new Dimension(1000, 1000));
     //jPanel.add(view);
     s.setContent(view);
     System.gc();
     }*/

    /*@FXML
     void start2ButtonAction(ActionEvent event) throws FileNotFoundException, ParseException, UnsupportedEncodingException, IOException {
     if ("CPM".equals(detectionMethodCombo.getValue())) {
     System.out.println("|");
     }
     for (int i = 0; i < dynamicNetwork.size(); i++) { //for (int i = 0; i < nbSnap; i++) {
     //Init a project - and therefore a workspace
     System.out.println("TimeFrame: " + i);
     ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
     pc.newProject();
     Workspace workspace = pc.getCurrentWorkspace();
     //Get controllers and models
     ImportController importController = Lookup.getDefault().lookup(ImportController.class);
     //Import file
     Container container;
     try {

     String path = "C:\\Users\\ado_k\\Documents\\NetBeansProjects\\CleanProject\\" + "io_gexf" + i + ".gml";
     File file = new File(path);
     container = importController.importFile(file);
     //File file = new File(main.getClass().getResource("/org/gephi/toolkit/demos/polblogs.gml").toURI());
     container.getLoader().setEdgeDefault(org.gephi.io.importer.api.EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED
     } catch (Exception ex) {
     ex.printStackTrace();
     return;
     }

     //Append imported data to GraphAPI
     importController.process(container, new DefaultProcessor(), workspace);
     GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

     Graph gsGraph = new SingleGraph("");
     //gsGraph.setStrict(false);
     //gsGraph.setAutoCreate(true);

     //Create three nodes
     for (org.gephi.graph.api.Node n : graphModel.getUndirectedGraph().getNodes()) {
     gsGraph.addNode(n.getId().toString());
     }
     for (org.gephi.graph.api.Edge e : graphModel.getUndirectedGraph().getEdges()) {
     try {
     gsGraph.addEdge(e.getSource().getId().toString() + ";" + e.getTarget().toString(), e.getSource().getId().toString(), e.getTarget().getId().toString());
     } catch (Exception exp) {

     }
     }
     gsGraph.display();
     CPM cpm = new CPM();
     //Louvain_old louvain = new Louvain_old();
     //dynamicNetwork.add(louvain.execute(workspace,50));

     LinkedList<Graph> communities = cpm.execute(gsGraph, 3);//executeD(workspace, 2);
     //if (communities.size() > 0) {
     dynamicNetwork1.add(new TimeFrame(communities));
     //}
     //System.out.println(communities.size());
     //louvain.executeD(workspace, 50);
     }
     }*/
    void writeLog(String str) {
        logTextArea.setText(logTextArea.getText() + str + "\n");
    }

    @FXML
    void handleCancel(ActionEvent event) {
    }

    @FXML
    void startAll(ActionEvent event) throws IOException, ParseException, FileNotFoundException, SQLException, Exception {
        //
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
                        nbSnapshots = spinnerNBClusters.getValue();
                        snapp.getSplitSnapshots(overlapping, filePath, nbSnapshots,
                                timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                    } else {
                        List<Duration> durations = stringToDuration(durationsLabel.getText());
                        //String[] splitContent = durationsLabel.getText().split(";");
                        if (durations.size() > 1) {
                            nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations,
                                    timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                    " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                        } else {
                            if (durations.size() == 1) {
                                nbSnapshots = snapp.getSplitSnapshots(overlapping, filePath, durations.get(0),
                                        timeFormatCombo.getValue(), comboStructDonnees.getSelectionModel().getSelectedItem(),
                                        " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                            } else {
                                writeLog("Erreur d'entées (le nombre de snpashots ou les durations doivent être données)");
                                throw new IllegalArgumentException("Wrong entries (either Durations or Snapshots number should be given)");
                            }
                        }
                    }
                    //} else {

                    //}
                   /* if (timeFormatCombo.getValue().equals("")) {
                     if (overlappingLabel.getText().equals("")) {
                     nbSnap = snapp.getSplitSnapshots(0, filePath, listDuration,
                     null, comboStructDonnees.getSelectionModel().getSelectedItem(), " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                     } else {
                     snapp.getSplitSnapshots(Float.parseFloat(overlappingLabel.getText()), filePath, listDuration,
                     null, comboStructDonnees.getSelectionModel().getSelectedItem(), " ", splitExportName.getText(), false, checkboxSplitMultiExport.isSelected());
                     }
                     } else {

                     }*/
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }

                writeLog("Split done. Writing done.");

                dynamicNetwork = new LinkedList<>();
                List<Graph> graphs = new ArrayList<Graph>();

                //writeLog("Executing " + comboDetection.getSelectionModel().getSelectedItem() + "...");
                //System.out.println(dynamicNetwork.size());
                for (int i = 0; i < nbSnapshots; i++) { //for (int i = 0; i < nbSnap; i++) {
                    // reading files 
                    writeLog("Executing " + comboDetection.getSelectionModel().getSelectedItem() + " on timeframe " + i + "...");
                    graphs.add(SnapshotsPrep.readCommunity(splitExportName.getText() + i + ".txt"));
                    System.out.println("file " + splitExportName.getText() + " was read");
                    System.out.println(graphs.get(i).getNodeCount() + " nodes were read");

                    switch (comboDetection.getSelectionModel().getSelectedItem()) {
                        case "CPM": {
                            CPM cpm = new CPM();
                            LinkedList<Graph> communities = cpm.execute(graphs.get(i), snipperDetection.getValue());
                            if (communities.size() > 0) {
                                dynamicNetwork.add(new TimeFrame(communities));
                            }
                            break;
                        }
                        default: {
                            writeLog("Method not linked yet.");
                            break;
                        }
                    }
                    System.out.println("\n");
                }
                writeLog(comboDetection.getSelectionModel().getSelectedItem() + " done.");

                writeLog("Calculating attributes...");

                for (TimeFrame tf : dynamicNetwork) {
                    for (Graph com : tf.getCommunities()) {
                        AttributesComputer.calculateAttributes(tf.getTimGraph(), com);
                    }
                }
                writeLog("Calculating attributes done.");

                writeLog("Executing " + comboEvolution.getSelectionModel().getSelectedItem() + "...");

                switch (comboEvolution.getSelectionModel().getSelectedItem()) {
                    case "GED": {
                        writeLog("GED started...");
                        GED1 ged = new GED1();
                        try {
                            ged.excuteGED(dynamicNetwork, 50, 50, evolutionExportLabel.getText());
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
                        writeLog("Method not linked yet.");
                    }
                    default: {
                        writeLog("Method not linked yet.");
                        break;
                    }
                }
                writeLog(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                System.out.println(comboEvolution.getSelectionModel().getSelectedItem() + " done.");
                writeLog("Creating evolution chains...");

                String BDpath = "./GED/";
                String BDfilename = evolutionExportLabel.getText() + "_50_50" + ".db";
                String tabname = "GED_evolution";
                int nbtimeframe = dynamicNetwork.size();
                writeEvolutionChain(BDpath, BDfilename, tabname, nbtimeframe, 2/* nbre timeframes */
                );

                writeLog("Evolution chains created.");

                String filePath = "./LibPrediction/";
                String filename = "trainData";
                String extension = ".arff";

                //EvolutionUtils.writeEvolutionChain(BDpath, BDfilename, tabname,nbtimeframe/**nbre timeframes**/);
                //PredictionUtils.createClassifierJ48(filePath+filename+extension,10);
                //PredictionUtils.createArff(filePath, filename,BDpath,BDfilename,nbtimeframe, "", "");
                PredictionUtils.createArff(filePath, filename, BDpath, BDfilename, nbtimeframe, "", "", 4);
                writeLog("Arff file generated.");

                try {
                    PredictionUtils.createClassifier(filePath + filename + extension, dynamicNetwork.size());
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                writeLog("Prediction done.");
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
                                writeLog("Erreur d'entées (le nombre de snpashots ou les durations doivent être données)");
                                throw new IllegalArgumentException("Wrong entries (either Durations or Snapshots number should be given)");
                            }
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }

                writeLog("Split done. Writing done.");
                if (checkboxSplitMultiExport.isSelected()) {
                    launchDetection.setDisable(false);
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
        Runnable task = new Runnable() {
            public void run() {
                dynamicNetwork = new LinkedList<>();
                List<Graph> graphs = new ArrayList<Graph>();

                //writeLog("Executing " + comboDetection.getSelectionModel().getSelectedItem() + "...");
                //System.out.println(dynamicNetwork.size());
                for (int i = 0; i < nbSnapshots; i++) { //for (int i = 0; i < nbSnap; i++) {
                    // reading files 
                    writeLog("Executing " + comboDetection.getSelectionModel().getSelectedItem() + " on timeframe " + i + "...");
                    graphs.add(SnapshotsPrep.readCommunity(splitExportName.getText() + i + ".txt"));
                    System.out.println("file " + splitExportName.getText() + " was read");
                    System.out.println(graphs.get(i).getNodeCount() + " nodes were read");

                    switch (comboDetection.getSelectionModel().getSelectedItem()) {
                        case "CPM": {
                            CPM cpm = new CPM();
                            LinkedList<Graph> communities = cpm.execute(graphs.get(i), snipperDetection.getValue());
                            if (communities.size() > 0) {
                                dynamicNetwork.add(new TimeFrame(communities));
                            }
                            break;
                        }
                        default: {
                            writeLog("Method not linked yet.");
                            break;
                        }
                    }
                    System.out.println("\n");
                }
                writeLog(comboDetection.getSelectionModel().getSelectedItem() + " done.");

                writeLog("Calculating attributes...");

                for (TimeFrame tf : dynamicNetwork) {
                    for (Graph com : tf.getCommunities()) {
                        AttributesComputer.calculateAttributes(tf.getTimGraph(), com);
                    }
                }
                writeLog("Calculating attributes done.");
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
    void startIdentification(ActionEvent event) {

    }

    @FXML
    void startPrediction(ActionEvent event) {

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
            Long l = Long.parseLong(periodS);
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
                    writeLog("Vérifier la structure de l'entrée SVP");
                    throw new IllegalArgumentException("Vérifier la structure de l'entrée SVP");
            }
        }
        return listDuration;
    }
}
