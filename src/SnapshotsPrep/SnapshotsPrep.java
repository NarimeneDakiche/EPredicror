package SnapshotsPrep;

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
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class SnapshotsPrep {

    public static Graph readCommunity(String file) {
        Graph g = new SingleGraph("");
        g.setStrict(false);
        g.setAutoCreate(true);
        System.out.println(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                    String[] str = sCurrentLine.split(" ");
                    g.addEdge(str[0] + ";" + str[1], str[0], str[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return g;

    }

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

    private List<Edge> readEdges(String file, String timeFormat, String dataStructure) throws ParseException, FileNotFoundException, IOException {
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
            splitContent = splitInput(input);
            v = splitContent[dataStructure.indexOf("V")];
            w = splitContent[dataStructure.indexOf("W")];

            if (timeFormat != null && !timeFormat.equals("Timestamp")) {
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

    private int getSplitSnapshots(String file, Duration duration, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws IOException, FileNotFoundException, ParseException {
        boolean bool = false;
        List<List<Edge>> parts = new ArrayList<>();
        MinMaxResults myResult = new MinMaxResults();
        myResult.getResults(file, timeFormat, dataStructure);
        //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS());
        long snapSpan = (myResult.getMaxTS() - myResult.getMinTS());
        int nbSnap = (int) (snapSpan / duration.getSeconds()) + 1;
        System.out.println("nb snaps: " + nbSnap);
        if (multipleExport) {
            BufferedWriter[] writers = new BufferedWriter[nbSnap];
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new BufferedWriter(new FileWriter(exportName + i + ".txt"));
            }

            FileInputStream stream = new FileInputStream(new File(file));
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
                        // System.out.println(duration.getSeconds()+" "+step);
                        int index = (int) ((timestamp - myResult.getMinTS()) / duration.getSeconds());
                        if (timestamp == myResult.getMaxTS()) {
                            index--;
                        }
                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        writers[index].write(v + " " + w + " " + TimeLength.timestampToDate(timestamp) + "\n");
                    }

                }
                for (int i = 0; i < writers.length; i++) {
                    writers[i].close();
                }

            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine); //splitInput(sCurrentLine);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);

                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        writer.write(v + " " + w + " " + TimeLength.timestampToDate(timestamp) + "\n");
                    }

                }
            }
        }
        System.out.println("Split done. Writing done.");
        return nbSnap;
    }

    public int getSplitSnapshots(float overlapping, String file, Duration duration, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws IOException, FileNotFoundException, ParseException {
        if (overlapping >= 1 || overlapping < 0) {
            throw new IllegalArgumentException("Illegal overlapping value (must be between 0 and 1)");
        }
        MinMaxResults myResult = new MinMaxResults();
        myResult.getResults(file, timeFormat, dataStructure);
        //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS());
        List<LongRange> list = new ArrayList<LongRange>();
        list.add(new LongRange(myResult.getMinTS(), myResult.getMinTS() + duration.getSeconds()));
//        System.out.println("Max date:" + TimeLength.timestampToDate(myResult.getMaxTS()) + " " +myResult.getMaxTS() );
//        System.out.println("Min date:" + TimeLength.timestampToDate(myResult.getMinTS())+ " " +myResult.getMinTS());
        while (list.get(list.size() - 1).getMax() < myResult.getMaxTS()) {
            long a = (long) ((list.get(list.size() - 1).getMax() - list.get(list.size() - 1).getMin()) * (1 - overlapping)) + list.get(list.size() - 1).getMin();
            //System.out.println(list.size() + " " + a + TimeLength.timestampToDate(timestamp));
            list.add(new LongRange(a, a + duration.getSeconds()));
        }
        int nbSnap = list.size();
//        System.out.println(nbSnap + " snapshots created");
        if (multipleExport) {
            BufferedWriter[] writers = new BufferedWriter[nbSnap];
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new BufferedWriter(new FileWriter(exportName + i + ".txt"));
            }

            FileInputStream stream = new FileInputStream(new File(file));
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;

                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//splitInput(sCurrentLine);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
//                    System.out.println(TimeLength.timestampToDate(timestamp));
                        // System.out.println(duration.getSeconds()+" "+step);
                        for (int j = 0; j < list.size(); j++) {
//                            if (list.get(j).contains(timestamp)){
//                                System.out.println(timestamp +" "+list.get(j).getMin()+" "+list.get(j).getMax());
//                            }
                            if (list.get(j).contains(timestamp)) {
                                writers[j].write(v + " " + w + "\n");

                            }
                        }
                        //System.out.println("");
                    }
                }
                for (int i = 0; i < writers.length; i++) {
                    writers[i].close();
                }

            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exportName + ".txt"));

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//splitInput(sCurrentLine);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);

                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        for (int j = 0; j < list.size(); j++) {
                            //System.out.println(list.get(j).contains(timestamp) + " " + timestamp +" "+list.get(j).getMin()+" "+list.get(j).getMax());
                            if (list.get(j).contains(timestamp)) {
                                writer.write(v + " " + w + " " + j + "\n");
                            }
                        }
                    }
                }
                writer.close();

            }
        }
        System.out.println("Split done. Writing done.");
        return nbSnap;
    }

    private int getSplitSnapshots(String file, List<Duration> listDuration, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws IOException, FileNotFoundException, ParseException {
        MinMaxResults myResult = new MinMaxResults();
        myResult.getResults(file, timeFormat, dataStructure);
        //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS());
        int nbSnap = listDuration.size();
        System.out.println("nb snaps: " + nbSnap);
        if (multipleExport) {
            BufferedWriter[] writers = new BufferedWriter[nbSnap];
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new BufferedWriter(new FileWriter(exportName + i + ".txt"));
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                        //System.out.println(TimeLength.timestampToDate(timestamp));
                        //int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / nbSnap);
                        // System.out.println(duration.getSeconds()+" "+step);
                        int index = 0;
                        long sum = myResult.getMinTS();
                        do {
                            sum += listDuration.get(index).getSeconds();
                            index++;
                            //System.out.println(sum + " "+timestamp);
                        } while (timestamp > sum);
                        index--;
                        /*if (timestamp == myResult.getMaxTS()) {
                         index--;
                         }*/
                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        writers[index].write(v + " " + w + "\n");
                    }
                }
                for (int i = 0; i < writers.length; i++) {
                    writers[i].close();
                }

            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

            FileInputStream stream = new FileInputStream(new File(file));
            boolean once = true;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                        int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / nbSnap);
                        int index = (int) ((timestamp - myResult.getMinTS()) / step);
                        if (timestamp == myResult.getMaxTS()) {
                            index--;
                        }
                        if (index < nbSnap) {
                            writer.write(v + " " + w + " " + index + "\n");
                        }
                    }
                }
                writer.close();

            }
        }
        System.out.println("Split done. Writing done.");
        return nbSnap;
    }

    public int getSplitSnapshots(float overlapping, String file, List<Duration> listDuration, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws IOException, FileNotFoundException, ParseException {
        if (overlapping > 1 || overlapping < 0) {
            throw new IllegalArgumentException("Illegal overlapping value (must be between 0 and 1)");
        }
        try {
            MinMaxResults myResult = new MinMaxResults();

            myResult.getResults(file, timeFormat, dataStructure);
            //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS());
            List<LongRange> list = new ArrayList<LongRange>();
            list.add(new LongRange(myResult.getMinTS(), myResult.getMinTS() + listDuration.get(0).getSeconds()));
            int k = 1;
            while (list.get(k - 1).getMax() < myResult.getMaxTS()) {
                if (listDuration.get(k - 1).getSeconds() <= listDuration.get(k).getSeconds()) {
                    long a = (long) ((list.get(k - 1).getMax() - list.get(k - 1).getMin()) * (1 - overlapping)) + list.get(k - 1).getMin();
                    // System.out.println(k + " " + a);
                    list.add(new LongRange(a, a + listDuration.get(k).getSeconds()));
                } else {
                    long a = (long) (list.get(k - 1).getMax() - (overlapping * listDuration.get(k).getSeconds()));
//((list.get(k - 1).getMax() - list.get(k - 1).getMin()) * (overlapping));
                    // System.out.println(k + " " + a);
                    list.add(new LongRange(a, a + listDuration.get(k).getSeconds()));
                }
                k++;
            }
            int nbSnap = list.size();
            System.out.println(nbSnap + " snapshots created");
            if (multipleExport) {
                BufferedWriter[] writers = new BufferedWriter[nbSnap];
                for (int i = 0; i < writers.length; i++) {
                    writers[i] = new BufferedWriter(new FileWriter(exportName + i + ".txt"));
                }

                FileInputStream stream = new FileInputStream(new File(file));
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String sCurrentLine;
                    String[] splitContent;

                    String v, w;
                    long timestamp;
                    while ((sCurrentLine = br.readLine()) != null) {
                        if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                            splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                            v = splitContent[dataStructure.indexOf("V")];
                            w = splitContent[dataStructure.indexOf("W")];
                            timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                        //System.out.println(TimeLength.timestampToDate(timestamp));
                            // System.out.println(duration.getSeconds()+" "+step);
                            for (int j = 0; j < list.size(); j++) {
                                //System.out.println(list.get(j).contains(timestamp) + " " + timestamp +" "+list.get(j).getMin()+" "+list.get(j).getMax());
                                if (list.get(j).contains(timestamp)) {
                                    writers[j].write(v + " " + w + "\n");

                                }
                            }
                            //System.out.println("");
                        }
                    }
                    for (int i = 0; i < writers.length; i++) {
                        writers[i].close();
                    }

                }
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(exportName + ".txt"));

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String sCurrentLine;
                    String[] splitContent;
                    String v, w;
                    long timestamp;
                    while ((sCurrentLine = br.readLine()) != null) {
                        if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                            splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                            v = splitContent[dataStructure.indexOf("V")];
                            w = splitContent[dataStructure.indexOf("W")];
                            timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);

                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                            //  + index);
                            for (int j = 0; j < list.size(); j++) {
                                //System.out.println(list.get(j).contains(timestamp) + " " + timestamp +" "+list.get(j).getMin()+" "+list.get(j).getMax());
                                if (list.get(j).contains(timestamp)) {
                                    writer.write(v + " " + w + " " + j + "\n");

                                }
                            }
                        }
                    }
                    writer.close();

                }
            }
            System.out.println("Split done. Writing done.");
            return nbSnap;
        } catch (Exception e) {
            return -1;
        }
    }

    public void getSplitSnapshots(float overlapping, String file, int nbSnap, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws FileNotFoundException, IOException, ParseException {
        MinMaxResults myResult = new MinMaxResults();
        myResult.getResults(file, timeFormat, dataStructure);
        long range = myResult.getMaxTS() - myResult.getMinTS();
        long duration = (long) (range / (1 + (1 - overlapping) * (nbSnap - 1))) + 1;
        this.getSplitSnapshots(overlapping, file, Duration.ofSeconds(duration), timeFormat, dataStructure, exportName, directed, multipleExport);
    }

    private void getSplitSnapshots(String file, int nbSnap, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport) throws FileNotFoundException, IOException, ParseException {
        MinMaxResults myResult = new MinMaxResults();
        myResult.getResults(file, timeFormat, dataStructure);
        //System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS());
        if (multipleExport) {
            BufferedWriter[] writers = new BufferedWriter[nbSnap];
            for (int i = 0; i < writers.length; i++) {
                writers[i] = new BufferedWriter(new FileWriter(exportName + i + ".txt"));
            }
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;

                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                        int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / nbSnap);
                        int index = (int) ((timestamp - myResult.getMinTS()) / step);
                        if (timestamp == myResult.getMaxTS()) {
                            index--;
                        }
                        // System.out.println(myResult.getMaxTS() + " " + myResult.getMinTS() + " " + step + " " + timestamp + " "
                        //  + index);
                        writers[index].write(v + " " + w + "\n");
                    }
                }
                for (int i = 0; i < writers.length; i++) {
                    writers[i].close();
                }

            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exportName));

            /*FileInputStream stream = new FileInputStream(new File(file));
             boolean once = true;*/
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String sCurrentLine;
                String[] splitContent;
                String v, w;
                long timestamp;
                while ((sCurrentLine = br.readLine()) != null) {
                    if (sCurrentLine.charAt(0) != '%' && sCurrentLine.charAt(0) != '#') {
                        splitContent = splitInput(sCurrentLine);//sCurrentLine.split(separator);
                        v = splitContent[dataStructure.indexOf("V")];
                        w = splitContent[dataStructure.indexOf("W")];
                        timestamp = Long.parseLong(splitContent[dataStructure.indexOf("T")]);
                        int step = (int) ((myResult.getMaxTS() - myResult.getMinTS()) / nbSnap);
                        int index = (int) ((timestamp - myResult.getMinTS()) / step);
                        if (timestamp == myResult.getMaxTS()) {
                            index--;
                        }
                        if (index < nbSnap) {
                            writer.write(v + " " + w + " " + index + "\n");
                        }
                    }
                }
                writer.close();

            }
        }
        System.out.println("Split done. Writing done.");
    }

    private void getSplitSnapshots(String file, int nbSnap, List<Duration> snapDurations, Duration duration, String timeFormat, String dataStructure, String exportName, boolean directed, boolean multipleExport, String exportExtension) throws ParseException, FileNotFoundException, UnsupportedEncodingException, IOException, InterruptedException {
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
            if (duration != null) {
                this.getSplitSnapshots(file, duration, timeFormat, dataStructure, exportName, directed, multipleExport);
            } else {
                this.getSplitSnapshots(file, snapDurations, timeFormat, dataStructure, exportName, directed, multipleExport);
            }
        } else {
            this.getSplitSnapshots(file, nbSnap, timeFormat, dataStructure, exportName, directed, multipleExport);
        }
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
    public static String[] splitInput(String input) {
        String[] splitContent = input.split("\\W");

        List<String> list = new LinkedList<String>(Arrays.asList(splitContent));
        List<String> toRemove = new ArrayList<String>();
        for (String str : list) {
            if (str.length() == 0) {
                toRemove.add(str);

            }
        }
        for (String str : toRemove) {
            list.remove(str);
        }

        return (String[]) list.toArray(new String[0]);
    }
}
