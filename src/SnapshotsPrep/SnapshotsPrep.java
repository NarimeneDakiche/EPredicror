package SnapshotsPrep;

import evolutionIdentification.GEDUtils.TimeFrame;
import graphclasses1.Edge;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SnapshotsPrep {

    private static List<String> readFile(String file)
            throws FileNotFoundException, IOException {
        List<String> textLines = new ArrayList<>();
        FileInputStream stream = new FileInputStream(new File(file));
        try (Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNextLine()) {
                textLines.add(scanner.nextLine());
            }
        }
        stream.close();
        return textLines;

    }

    private List<Edge> readEdges(String file, String timeFormat, String dataStructure, String separator) throws ParseException, FileNotFoundException, IOException {
        /**
         * timeFormat should define the format of the date given in the file. If
         * the file contains timestamp, timeFormat is represented by null.
         *
         * timeFormat : format of the time present in the data file
         * dataStructure : structure of data, V ==> node 1 W ==> node 2 T ==>
         * timestamp/date X ==> useless information example: 2004-10-25 18:46:58
         * 47 59 is represented by "TTVWX" or "TTVW"
         */
        List<String> lines;
        lines = readFile(file);
        List<Edge> edges = new ArrayList<>();
        String[] splitContent;
        String v, w;
        long timestamp;
        Date d;
        for (String input : lines) {
            splitContent = input.split(separator);
            v = splitContent[dataStructure.indexOf("V")];
            w = splitContent[dataStructure.indexOf("W")];

            if (timeFormat != null) {
                String dateS = "";
                for (int i = -1; (i = dataStructure.indexOf("T", i + 1)) != -1;) {
                    dateS += splitContent[i];
                } // prints "4", "13", "22"

                d = new SimpleDateFormat(timeFormat)
                        .parse(splitContent[0] + " " + splitContent[1]);
                timestamp = d.getTime();
            } else {
                timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
            }

            edges.add(new Edge(v, w, timestamp));
        }
        Collections.sort(edges);
        return (edges);
    }

    public class MyResult {

        // etc
        /*public MyResult(int minTS, int maxTS) {
         this.minTS = minTS;
         this.maxTS = maxTS;
         }*/
        long minTS;
        long maxTS;

        public void setMinTS(long minTS) {
            this.minTS = minTS;
        }

        public void setMaxTS(long maxTS) {
            this.maxTS = maxTS;
        }

        public MyResult getResults(String file, String timeFormat, String dataStructure, String separator) throws FileNotFoundException, IOException, ParseException {

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                long timestamp;
                Date d;
                boolean first = true;
                while ((sCurrentLine = br.readLine()) != null) {
                    splitContent = sCurrentLine.split(separator);
                    if (timeFormat != null) {
                        String dateS = "";
                        for (int i = -1; (i = dataStructure.indexOf("T", i + 1)) != -1;) {
                            dateS += splitContent[i];
                        } // prints "4", "13", "22"

                        d = new SimpleDateFormat(timeFormat)
                                .parse(splitContent[0] + " " + splitContent[1]);
                        timestamp = d.getTime();
                    } else {
                        maxTS = Math.max(maxTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
                        if (first) {
                            minTS = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                            first = false;
                        } else {
                            minTS = Math.min(minTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
                        }
                    }
                }
            }
            /*String[] splitContent;
             int v, w;
             long timestamp;
             Date d;
             boolean first = true;*/
            /*try (Scanner scanner = new Scanner(stream)) {
             while (scanner.hasNextLine()) {
             splitContent = scanner.nextLine().split(separator);
             if (timeFormat != null) {
             String dateS = "";
             for (int i = -1; (i = dataStructure.indexOf("T", i + 1)) != -1;) {
             dateS += splitContent[i];
             } // prints "4", "13", "22"

             d = new SimpleDateFormat(timeFormat)
             .parse(splitContent[0] + " " + splitContent[1]);
             timestamp = d.getTime();
             } else {
             maxTS = Math.max(maxTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));
             if (first) {
             minTS = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
             first = false;
             } else {
             minTS = Math.min(minTS, Long.parseLong(splitContent[dataStructure.indexOf("T")]));

             }
             }
             }
             } finally {
             if (stream != null) {
             stream.close();
             }
             }*/

            MyResult myResult = new MyResult();
            myResult.setMaxTS(0);
            myResult.setMinTS(0);
            return myResult;
            // impl here
        }

    }

    public LinkedList<TimeFrame> getSplitSnapshots(String file, String timeFormat, String dataStructure, String separator, int nbSnap, String exportName, boolean directed, boolean multipleExport, String exportExtension) throws ParseException, FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {
        /**
         * This method reads and splits sorted List of edges to divide them into
         * k snapshots timeFormat should define the format of the date given in
         * the file. If the file already contains timestamp, timeFormat is
         * represented by null.
         *
         * timeFormat : format of the time present in the data file
         * dataStructure : structure of data, V ==> node 1 W ==> node 2 T ==>
         * timestamp/date X ==> useless information example: 2004-10-25 18:46:58
         * (yyyy-MM-dd HH:mm:ss) 47 59 is represented by "TTVWX" or "TTVW"
         *
         * Perspective: add the possibility to take the duration instead of the
         * number of clusters in parameter
         */

        if (nbSnap <= 0) {
            throw new IllegalArgumentException("k must be a strictely positive integer");
        }
        boolean bool = false;

        List<List<Edge>> parts = new ArrayList<>();
        MyResult myResult = new MyResult();
        myResult.getResults(file, timeFormat, dataStructure, separator);
        //System.out.println(myResult.maxTS + " " + myResult.minTS);
        long snapSpan = (myResult.maxTS - myResult.minTS) / nbSnap;

        if (multipleExport) {
            BufferedWriter[] writers = new BufferedWriter[nbSnap];
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new BufferedWriter(new FileWriter("Ec" + i + ".txt"));
            }

            FileInputStream stream = new FileInputStream(new File(file));
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;

                boolean once = true;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    splitContent = sCurrentLine.split(separator);
                    v = splitContent[dataStructure.indexOf("V")];
                    w = splitContent[dataStructure.indexOf("W")];
                    timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                    int step = (int) ((myResult.maxTS - myResult.minTS) / nbSnap);
                    int index = (int) ((timestamp - myResult.minTS) / step);
                    if (index == nbSnap && once) {
                        index--;
                        once = false;
                    }
                    writers[index].write(v + " " + w + "\n");
                }
            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Ec.txt"));

            FileInputStream stream = new FileInputStream(new File(file));
            boolean once = true;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    splitContent = sCurrentLine.split(separator);
                    v = splitContent[dataStructure.indexOf("V")];
                    w = splitContent[dataStructure.indexOf("W")];
                    timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                    int step = (int) ((myResult.maxTS - myResult.minTS) / nbSnap);
                    int index = (int) ((timestamp - myResult.minTS) / step);

                    if (index < nbSnap) {
                        writer.write(v + " " + w + " " + index + "\n");
                    }
                }
            }
        }
        System.out.println("Split done  parts ready\nWriting in files...");

        /*List<Edge> edges = this.readEdges(file, timeFormat, dataStructure, separator);  /* get a list of edges */
        /* final int N = edges.size();
         long snapSpan = (edges.get(N - 1).getTimestamp() - edges.get(0).getTimestamp()) / nbSnap; /* the duration of a snapshot */
        //List<Graph> gTimeFrames = new ArrayList<>();
        /*LinkedList<TimeFrame> linkedList = new LinkedList<>();
         int index = 0, j;
         for (int i = 0; i < nbSnap; i++) {
         /**
         * for all edges *
         */
        /*    for (j = index;
         (j < edges.size() && edges.get(j).getTimestamp() <= edges.get(0).getTimestamp() + (1 + i) * snapSpan);
         j++) { /* sweep all edges that reside in the snapshot i+1*/

        /*    }

         parts.add(new ArrayList<>(
         edges.subList(index, j))
         );

         index = j + 1;
         }
         System.out.println("Split done  parts ready\nWriting in files...");

         //get nodes
         /* for (int i = 0; i < parts.size(); i++) {
         ///List<String> nodes = new ArrayList<String>();
         LinkedHashSet<String> nodes = new LinkedHashSet<>();
         nodes.clear();
         List<Edge> listEdge = parts.get(i);

         Set<Edge> hs = new TreeSet(new Comparator() {
         @Override
         public int compare(Object o1, Object o2) {
         if ((((Edge) o1).getV() == ((Edge) o2).getV()) && (((Edge) o1).getW() == ((Edge) o2).getW())) {
         return 0;
         }
         return 1;
         }
         });
         hs.addAll(listEdge);
         listEdge.clear();
         listEdge.addAll(hs);
         hs.clear();

         for (Edge cEdge : listEdge) {
         //if (!nodes.contains(cEdge.getV())) {
         nodes.add(Integer.toString(cEdge.getV()));
         //}
         //if (!nodes.contains(cEdge.getW())) {
         nodes.add(Integer.toString(cEdge.getW()));
         //}
         }
         ArrayList<String> al = new ArrayList();
         al.addAll(nodes);
         Collections.sort(al);

         /**
         * * Splitting Done! **
         */
        // Start exporting
        /*    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
         pc.newProject();
         Workspace workspace = pc.getCurrentWorkspace();

         //Get a graph model - it exists because we have a workspace
         GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

         Map<String, Node> mapNodes = new HashMap<String, Node>();
         Graph graph = graphModel.getGraph();

         org.graphstream.graph.Graph gsGraph = new SingleGraph("");
         //gsGraph.setStrict(false);
         //gsGraph.setAutoCreate(true);

         //Create three nodes
         for (String node : al) {
         Node tmp = graphModel.factory().newNode(node);
         gsGraph.addNode(node);
         graph.addNode(tmp);
         mapNodes.put(node, tmp);
         }
         for (Edge cEdge : listEdge) {
         //System.out.println(cEdge.getV() + ";" + Integer.toString(cEdge.getW()) + " " + Integer.toString(cEdge.getV()) + " " + Integer.toString(cEdge.getW()));

         org.gephi.graph.api.Edge tmp = graphModel.factory().newEdge(mapNodes.get(Integer.toString(cEdge.getV())), mapNodes.get(Integer.toString(cEdge.getW())), true);
         graph.addEdge(tmp);
         if (!graph.contains(tmp)) {
         graph.addEdge(tmp);
         }
         try {
         gsGraph.addEdge(Integer.toString(cEdge.getV()) + ";" + Integer.toString(cEdge.getW()), Integer.toString(cEdge.getV()), Integer.toString(cEdge.getW()), true);
         } catch (Exception e) {
         bool = true;

         /**
         * Repeated elements found, gephi and gs have different
         * results cause gephi allows repetitions and graphstream
         * doesn't, i tried to delete repetions but there is always
         * some left, i don't know why)*
         */
        /*  }
         }
         /*System.out.println("Nodes: " + graph.getNodeCount() + " Edges: " + graph.getEdgeCount());
         System.out.println("Nodes: " + gsGraph.getNodeCount() + " Edges: " + gsGraph.getEdgeCount());*/
        /*
         linkedList.add(new TimeFrame(gsGraph));
         //gsGraph = new SingleGraph("");

         ExportController ec = Lookup.getDefault().lookup(ExportController.class);
         try {
         ec.exportFile(new File("io_gexf" + i + ".gml"));
         } catch (IOException ex) {
         ex.printStackTrace();
         }
         }
         if (bool) {
         System.err.println("Don't forget this issue! Enter code bech tefhem.");
         }*/
        TimeUnit.SECONDS.sleep(10);

        return null;
    }

    /*public GraphModel[] getSplitSnapshots(String file, int nbSnap, String period) {

     //Init a project - and therefore a workspace
     ProjectController[] pcT = new ProjectController[nbSnap];
     Workspace[] workspaceT = new Workspace[nbSnap];
     GraphModel graphModel[] = new GraphModel[nbSnap];

     for (int i = 0; i < pcT.length; i++) {

     pcT[i] = Lookup.getDefault().lookup(ProjectController.class);
     pcT[i].newProject();
     workspaceT[i] = pcT[i].getCurrentWorkspace();
     }

     //Import first file
     ImportController importController = Lookup.getDefault().lookup(ImportController.class);
     Container[] containers = new Container[nbSnap];
     Container container;

     try {
     for (int i = 0; i < containers.length; i++) {
     /*URL url = getClass().getResource("ListStopWords.txt");
     File file = new File(url.getPath());*/
    /*File file1 = new File(getClass().getResource("timeframe" + (i + 1) + ".gexf").toURI());

     container = importController.importFile(file1);
     container.getLoader().setEdgeDefault(org.gephi.io.importer.api.EdgeDirectionDefault.DIRECTED);
     importController.process(container, new DefaultProcessor(), workspace);

     containers[i] = importController.importFile(file1);
     //Process the container using the MergeProcessor
     importController.process(containers[i], new MergeProcessor(), workspaceT[i]);
     graphModel[i] = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspaceT[i]);
     }
     } catch (Exception ex) {
     ex.printStackTrace();
     return null;
     }

     /*Get the price attribute
     Graph graph = graphModel.getGraph();
     for (Node n : graph.getNodes()) {
     TimestampIntegerMap value = (TimestampIntegerMap) n.getAttribute("price");
     System.out.println("'" + n.getLabel() + "': " + value.toString());
     }*/

    /*//Get the price attribute in average - learn more about ESTIMATOR
     for (Node n : graph.getNodes()) {
     TimestampIntegerMap value = (TimestampIntegerMap) n.getAttribute("price");

     Double priceFrom2007to2009Avg = (Double) value.get(new Interval(2007, 2009), Estimator.AVERAGE);
     System.out.println("With AVERAGE estimator: '" + n.getLabel() + "': " + priceFrom2007to2009Avg);

     Integer priceFrom2007to2009Max = (Integer) value.get(new Interval(2007, 2009), Estimator.MAX);
     System.out.println("With MAX estimator: '" + n.getLabel() + "': " + priceFrom2007to2009Max);
     }

     //Create a dynamic range filter query
     FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
     DynamicRangeBuilder.DynamicRangeFilter dynamicRangeFilter = new DynamicRangeBuilder.DynamicRangeFilter(graphModel);
     Query dynamicQuery = filterController.createQuery(dynamicRangeFilter);

     //Create a attribute range filter query - on the price column
     Column priceCol = graphModel.getNodeTable().getColumn("price");
     AttributeRangeBuilder.AttributeRangeFilter.Node attributeRangeFilter = new AttributeRangeBuilder.AttributeRangeFilter.Node(priceCol);
     Query priceQuery = filterController.createQuery(attributeRangeFilter);

     //Set dynamic query as child of price query
     filterController.add(priceQuery);
     filterController.add(dynamicQuery);
     filterController.setSubQuery(priceQuery, dynamicQuery);

     //Set the filters parameters - Keep nodes between 2007-2008 which have average price >= 7
     dynamicRangeFilter.setRange(new Range(2007.0, 2008.0));
     attributeRangeFilter.setRange(new Range(7, Integer.MAX_VALUE));

     //Execute the filter query
     GraphView view = filterController.filter(priceQuery);*/
    //Graph filteredGraph = graphModel.getGraph(view);
    //Node 3 shoudln't be in this graph
    //System.out.println("Node 3 in the filtered graph: " + filteredGraph.contains(graph.getNode("3")));
        /*return graphModel;
     }*/
}
