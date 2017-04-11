/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SnapshotsPrep.SnapshotsPrep11;
import communityDetection.CPM;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.openide.util.Lookup;

/**
 *
 * @author ado_k
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Accordion accordion1;

    @FXML
    private TitledPane titledpane1;

    @FXML
    private SwingNode s;

    @FXML
    private Button browserButton;

    @FXML
    private Label fileLabel;

    @FXML
    private Spinner<Integer> spinnerNBClusters;

    @FXML
    private ComboBox<String> timeFormatCombo;

    @FXML
    private Label labelStep1;

    @FXML
    private ComboBox<String> comboStructDonnees;

    @FXML
    private TitledPane titledpane2;

    @FXML
    private Button startDetectionButton1;

    @FXML
    private TitledPane titledpane4;

    @FXML
    private ComboBox<String> detectionMethodCombo;

    @FXML
    private ComboBox<String> detectionMethodCombo1;

    @FXML
    private Pane panel;

    @FXML
    private AnchorPane panelAnchor;

    @FXML
    private Label indexGraphLabel;

    @FXML
    private Spinner<Integer> kDetectionSpinner;

    private File file;

    private boolean exists;

    private int indexGraph;

    private Viewer viewer;

    private ViewPanel view;

    private LinkedList<TimeFrame> dynamicNetwork = new LinkedList<TimeFrame>();
    private LinkedList<TimeFrame> dynamicNetwork1 = new LinkedList<TimeFrame>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        indexGraph = 0;
        // TODO
        accordion1.setExpandedPane(titledpane1);

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

        spinnerNBClusters.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 7));
        kDetectionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

        comboStructDonnees.getItems().addAll("TVW", "VWT", "TTVW", "VWXT");
        comboStructDonnees.getSelectionModel().select("TVW");
        //  labelStep1 = new Label();

        detectionMethodCombo.getItems().addAll("CPM", "Louvain", "...");
        detectionMethodCombo1.getItems().addAll("CPM", "Louvain", "...");
    }

    @FXML
    void browserAction(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            String fileName = file.getName();
            fileLabel.setText(fileName);
            String fileExtension = fileName.substring(fileName.indexOf(".") + 1, file.getName().length());
            System.out.println(fileExtension);

            String[] VALUES = new String[]{"PDF", "GEXF", "GDF", "GML", "GraphML", "Pajek NET", "GraphViz DOT", "CSV", "UCINET DL", "Tulip TPL", "Netdraw VNA", "Spreadsheet"};
            exists = Arrays.asList(VALUES).contains(fileExtension.toLowerCase()) || Arrays.asList(VALUES).contains(fileExtension.toUpperCase());
            System.out.println(exists);

            if (exists) {
                timeFormatCombo.setDisable(true);
                comboStructDonnees.setDisable(true);
            } else {
                timeFormatCombo.setDisable(false);
                comboStructDonnees.setDisable(false);
            }

        }
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
                SnapshotsPrep11 snapp = new SnapshotsPrep11();
                if (file != null && spinnerNBClusters.getValue() > 0 && comboStructDonnees.getValue() != null) {
                    dynamicNetwork = snapp.getSplitSnapshots(file.getAbsolutePath(),
                            null, comboStructDonnees.getValue(), " ", spinnerNBClusters.getValue(), "export", false, false, "");
                    snapp = null;
                    System.gc();
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

    @FXML
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
    }

    @FXML
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
            for (org.gephi.graph.api.Node n: graphModel.getUndirectedGraph().getNodes()){
                gsGraph.addNode(n.getId().toString());
            }
            for (org.gephi.graph.api.Edge e: graphModel.getUndirectedGraph().getEdges()){
                try{
                    gsGraph.addEdge(e.getSource().getId().toString()+";"+e.getTarget().toString(),e.getSource().getId().toString(), e.getTarget().getId().toString());
                }
                catch(Exception exp){
                    
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
    }
}
