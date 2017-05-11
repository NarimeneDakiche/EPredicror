package evolutionIdentification;

import evolutionIdentification.GEDUtils.NodeCompare;
import evolutionIdentification.GEDUtils.TimeFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 *
 * @author ado_k
 */
public class GED1 {

    private String url;
    private String sql;
    private Connection conn;
    private String sqlInsert;
    private PreparedStatement pstmt;
    private PreparedStatement pstmt1;
    private PreparedStatement pstmt2;
    private PreparedStatement pstmt3;

    public void insert(String event, Integer g1, int timeFrame, Integer g2, int timeFrame1, int inclusion1, int inclusion2, String threshold) throws SQLException {
        //String sqlInsert = "INSERT INTO GED_evolution(event_type,group1,timeframe1,group2,timeframe2,alpha,beta,threshold) VALUES (?,?,?,?,?,?,?,?);";

        pstmt.setString(1, event);
        if (g1 != null) {
            pstmt.setInt(2, g1);
        } else {
            pstmt.setString(2, null);
        }
        pstmt.setInt(3, timeFrame);
        if (g2 != null) {
            pstmt.setInt(4, g2);
        } else {
            pstmt.setString(4, null);
        }
        pstmt.setInt(5, timeFrame1);
        pstmt.setInt(6, inclusion1);
        pstmt.setInt(7, inclusion2);
        pstmt.setString(8, threshold);
        pstmt.addBatch();

        //System.out.println("inserted");
    }

    private void update(String event, String threshold, Integer group1, Integer timeframe1, Integer group2, Integer timeframe2, String oldEvent) throws SQLException {
        /*String update1 = "UPDATE GED_evolution "
         + "SET event_type = ? "
         + "WHERE threshold = ? AND group1 = ? AND timeframe1 = ? AND group2 = ? AND timeframe2 = ? AND event_type = ?;";
         PreparedStatement pstmt = conn.prepareStatement(update1);*/

        // set the corresponding param
        pstmt3.setString(1, event);
        pstmt3.setString(2, threshold);
        pstmt3.setInt(3, group1);
        pstmt3.setInt(4, timeframe1);
        pstmt3.setInt(5, group2);
        pstmt3.setInt(6, timeframe2);
        pstmt3.setString(7, oldEvent);
        // update 
        pstmt3.addBatch();

    }

    public void update(String event, String threshold, Integer group1, Integer timeframe1, String oldEvent, int group) throws SQLException {
        /* String update1 = (group == 1) ? "UPDATE GED_evolution "
         + "SET event_type = ? "
         + "WHERE threshold = ? AND group1 = ? AND timeframe1 = ? AND event_type = ?;"
         : ((group == 2) ? "UPDATE GED_evolution "
         + "SET event_type = ? "
         + "WHERE threshold = ? AND group2 = ? AND timeframe2 = ? AND event_type = ?;"
         : null);

         pstmt = conn.prepareStatement(update1);*/

        // set the corresponding param
        if (group == 1) {
            pstmt1.setString(1, event);
            pstmt1.setString(2, threshold);
            pstmt1.setInt(3, group1);
            pstmt1.setInt(4, timeframe1);
            pstmt1.setString(5, oldEvent);
            // update 
            pstmt1.addBatch();
        } else {
            pstmt2.setString(1, event);
            pstmt2.setString(2, threshold);
            pstmt2.setInt(3, group1);
            pstmt2.setInt(4, timeframe1);
            pstmt2.setString(5, oldEvent);
            // update 
            pstmt2.addBatch();
        }
    }

    public void createNewTable(String fileName) {
        this.sqlInsert = "INSERT INTO GED_evolution(event_type,group1,timeframe1,group2,timeframe2,alpha,beta,threshold) VALUES (?,?,?,?,?,?,?,?);";
        this.url = "jdbc:sqlite:" + fileName;

// SQL statement for creating a new table
        this.sql = "CREATE TABLE IF NOT EXISTS GED_evolution( "
                + "	id_matched INTEGER PRIMARY KEY NOT NULL, "
                + "	event_type varchar(30) NULL, "
                + "	group1 int NULL, "
                + "	timeframe1 tinyint NULL, "
                + "	group2 int NULL, "
                + "	timeframe2 tinyint NULL, "
                + "	alpha tinyint NULL, "
                + "	beta tinyint NULL,"
                + "	threshold varchar(50) NULL "
                + ");";

        System.out.println("Database " + fileName + " created");
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table

            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void excuteGED(LinkedList<TimeFrame> dynamicNetwork, int alpha, int beta, String exportName) throws FileNotFoundException, UnsupportedEncodingException, SQLException {

        /**
         * 1. Calculer Mesure d'importance et ordre en selon (e.g. degree
         * centrality, betweenness centrality, closeness centrality, page rank,
         * social position, etc.) 2. Pour chaque Groupe de i_timeFrame : Pour
         * chaque Groupe de i_timeFrame+1: Calculer intersection entre les
         * groupes Calculer sum of ranking of selected nodes in group1/2
         * Calculer total ranking in group1/2 Calculer l'inclusion
         *
         * looking for dissolving looking for forming looking for
         * splitting/shrinking and merging/growing update update growing and
         * shrinking SET continue when size is the same
         *
         */
        int group1 = 0;
        int group2 = 0;
        int g1g2 = 0;
        int i_timeFrame = 0;
        int g1_size = 0;
        int g2_size = 0;
        double sr1 = 0;
        double sr2 = 0;
        int timeframes_no = 0;

        double tr1 = 0; //total ranking in group1
        double tr2 = 0;

        int a = 0; //inclusion in group 1 
        int b = 0; //inclusion in group 2

        int fd_tres = 10;

        int a_tres_tmp = alpha;
        int a_tres = a_tres_tmp;
        int b_tres_tmp = beta;
        int b_tres = b_tres_tmp;

        String tres = new String();

        List list;
        System.out.println("GED started!");
        //PrintWriter writer = new PrintWriter("GEDTemp.txt", "UTF-8");
        //writer.println("event,G1,timeframe,G2,timeframe+1,inclusion1,inclusion2,threshold");
        /*while (a_tres < 110) {
         while (b_tres < 110) {*/

        tres = a_tres + "_" + b_tres;

        String fileName = "GED";
        File myOutputDir = new File(fileName);
        if (!myOutputDir.exists()) {
            myOutputDir.mkdir();
        }
        fileName = "GED\\" + exportName + "_" + tres + ".db";
        createNewTable(fileName);
        this.conn = DriverManager.getConnection(url);
        this.pstmt = this.conn.prepareStatement(sqlInsert);
        this.pstmt1 = this.conn.prepareStatement("UPDATE GED_evolution "
                + "SET event_type = ? "
                + "WHERE threshold = ? AND group1 = ? AND timeframe1 = ? AND event_type = ?;");
        this.pstmt2 = this.conn.prepareStatement("UPDATE GED_evolution "
                + "SET event_type = ? "
                + "WHERE threshold = ? AND group2 = ? AND timeframe2 = ? AND event_type = ?;");
        this.pstmt3 = this.conn.prepareStatement("UPDATE GED_evolution "
                + "SET event_type = ? "
                + "WHERE threshold = ? AND group1 = ? AND timeframe1 = ? AND group2 = ? AND timeframe2 = ? AND event_type = ?;");
        /*String sql = "PRAGMA synchronous=OFF";
         Statement st = conn.createStatement();
         st.execute(sql);*/
        conn.setAutoCommit(false);

        while (i_timeFrame < dynamicNetwork.size() - 1) {
            //t1_no : nb de groupes dans timeframe actuel
            int t1_no = dynamicNetwork.get(i_timeFrame).getCommunities().size();

            //t2_no : nb de groupes dans timeframe suivant
            int t2_no = dynamicNetwork.get(i_timeFrame + 1).getCommunities().size();

            // for each group in the timeframe i_timeFrame
            while (group1 < t1_no) {
                //Gr1 est dynamicNetwork.get(i_timeFrame).get(group1)

                Collection<Node> gr1 = dynamicNetwork.get(i_timeFrame).getCommunities().get(group1).getNodeSet(); //sorted by measure
                //System.out.println(gr1.size());
//                list = new ArrayList(gr1);
//                Collections.sort(list, new NodeCompare());
//                gr1 = new HashSet(list);
//                list.clear();
                int dissolve = 1;

                List<Graph> listG2 = groupsWithSharedNodes(dynamicNetwork.get(i_timeFrame).getCommunities().get(group1),
                        dynamicNetwork.get(i_timeFrame + 1).getCommunities());

                while (group2 < listG2.size()) {
                    //System.out.println("Group2: " + group2 + " " + listG2.size());
                    //Gr2 est dynamicNetwork.get(i_timeFrame+1).get(group2)
                    //Collection<Node> gr2 = dynamicNetwork.get(i_timeFrame + 1).getCommunities().get(group2).getNodeSet();
                    Collection<Node> gr2 = listG2.get(group2).getNodeSet();
//                    list = new ArrayList(gr2);
//                    Collections.sort(list, new NodeCompare());
//                    gr2 = new HashSet(list);
//                    list.clear();

                    g1_size = gr1.size();
                    g2_size = gr2.size();
                    LinkedList<Node> interG1G2 = nodeInter(gr1, gr2);
                    g1g2 = interG1G2.size();
                    sr1 = sumMesure(gr1, interG1G2);
                    sr2 = sumMesure(gr2, interG1G2);
                    tr1 = sumMesure(gr1);
                    tr2 = sumMesure(gr2);

                    //calculating inclusion
                    a = (int) ((1.0 * g1g2 / g1_size) * (1.0 * sr1 / tr1) * 100);
                    //System.out.println((1.0 * g1g2 / g1_size) * (1.0 * sr1 / tr1) *100 + " " +a);

//                    if (a > 10) {
//                        System.out.println("Inclusion: a=" + a + ", g1g2=" + g1g2 + ", g1_size=" + g1_size + ", sr1=" + sr1 + ", tr1=" + tr1);
//                    }
                    b = (int) ((1.0 * g1g2 / g2_size) * (1.0 * sr2 / tr2) * 100);

                    // System.out.println("g1g2:" + g1g2 + " g1_size:" + g1_size + " sr1:" + sr1 + " tr1:" + tr1 + " a:" + a + " b:" + b);
                    //looking for dissolving
                    if (dissolve == 1 && (a > fd_tres || b > fd_tres)) {
                        dissolve = 0;
                    }

                    int originalG2Index = Integer.parseInt(listG2.get(group2).getAttribute("originalIndex").toString());

                    if (g1_size >= g2_size) {
                        if (a >= a_tres) {
                            if (b >= b_tres) {
                                // writer.println("shrinking" + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("shrinking", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);
                            } else {
                                //writer.println("splitting/shrinking" + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("splitting/shrinking", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);

                            }
                        } else {
                            if (b >= b_tres) {
                                //writer.println("splitting/shrinking" + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("splitting/shrinking", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);

                            }
                        }
                    } else {
                        if (a >= a_tres) {
                            if (b >= b_tres) {
                                //writer.println("growing," + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("growing", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);

                            } else {
                                //writer.println("merging/growing," + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("merging/growing", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);

                            }
                        } else {
                            if (b >= b_tres) {
                                //writer.println("merging/growing," + group1 + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                                insert("merging/growing", group1, i_timeFrame, originalG2Index, (i_timeFrame + 1), (int) a, (int) b, tres);

                            }
                        }
                    }
                    group2++;
                    gr2 = null;
                    a = b = 0;
                }

                //dissolving
                if (dissolve == 1) {
                    //writer.println("dissolving," + group1 + "," + i_timeFrame + "," + null + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                    insert("dissolving", group1, i_timeFrame, (Integer) null, (i_timeFrame + 1), (int) a, (int) b, tres);
                    //   System.out.println();

                }

                group1++;
                gr1 = null;
                group2 = 0;
            }

            group1 = group2 = 0;

            while (group2 < t2_no) {

                Collection<Node> gr2 = dynamicNetwork.get(i_timeFrame + 1).getCommunities().get(group2).getNodeSet();//ordered!!!!!!!
                list = new ArrayList(gr2);
                Collections.sort(list, new NodeCompare());
                gr2 = new HashSet(list);
                list.clear();

                int form = 1;

                List<Graph> listG1 = groupsWithSharedNodes(dynamicNetwork.get(i_timeFrame + 1).getCommunities().get(group2),
                        dynamicNetwork.get(i_timeFrame).getCommunities());

                while (group1 < listG1.size()) {
                    // System.out.println("Group1: " + group1 + " " + listG1.size());
                    //Gr2 est dynamicNetwork.get(i_timeFrame+1).get(group2)
                    //Collection<Node> gr2 = dynamicNetwork.get(i_timeFrame + 1).getCommunities().get(group2).getNodeSet();
                    Collection<Node> gr1 = listG1.get(group1).getNodeSet();


                    /*while (group1 < t1_no) {
                     //Gr2 est dynamicNetwork.get(i_timeFrame+1).get(group2)
                     Collection<Node> gr1 = dynamicNetwork.get(i_timeFrame).getCommunities().get(group1).getNodeSet();//ordered!!!!!!!*/
                    list = new ArrayList(gr1);
                    Collections.sort(list, new NodeCompare());
                    gr1 = new HashSet(list);
                    list.clear();

                    g1_size = gr1.size();
                    g2_size = gr2.size();
                    LinkedList<Node> interG1G2 = nodeInter(gr1, gr2);
                    g1g2 = interG1G2.size();
                    sr1 = sumMesure(gr1, interG1G2);
                    sr2 = sumMesure(gr2, interG1G2);
                    tr1 = sumMesure(gr1);
                    tr2 = sumMesure(gr2);

                    //calculating inclusion
                    a = (int) ((1.0 * g1g2 / g1_size) * (1.0 * sr1 / tr1) * 100);
                    b = (int) ((1.0 * g1g2 / g2_size) * (1.0 * sr2 / tr2) * 100);

                    //looking for dissolving
                    if (form == 1 && (a > fd_tres || b > fd_tres)) {
                        form = 0;
                    }

                    group1++;
                    gr1 = null;
                }
                if (form == 1) {
                    //writer.println("forming," + null + "," + i_timeFrame + "," + group2 + "," + (i_timeFrame + 1) + "," + a + "," + b + "," + tres);
                    insert("forming", (Integer) null, i_timeFrame, group2, (i_timeFrame + 1), (int) a, (int) b, tres);
                }

                group2++;
                gr2 = null;
                group1 = 0;
            }

            i_timeFrame++;
            group1 = group2 = 0;

        }
        /* b_tres += 10;
         i_timeFrame = 0;
         }
         a_tres += 10;
         b_tres = 50;
         }*/
        //writer.close();
        // splitting/shrinking and merging/growing update
        // update growing and shrinking SET continue when size is the same
        int[] updateCounts = pstmt.executeBatch();
        conn.commit();
        // conn.setAutoCommit(true);

        System.out.println("Insert done!");

        System.out.println("Update started!");

        int g1, t1, g2, t2;

        a_tres = a_tres_tmp;
        b_tres = b_tres_tmp;

        int cpt = 0;
        /* while (a_tres < 101) {
         while (b_tres < 101) {*/

        tres = Integer.toString(a_tres) + "_" + Integer.toString(b_tres);
        System.out.println("Update " + tres + " started!");
        //System.out.println("Point 0 " + tres);

        String spl = "SELECT group1, timeframe1 FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type = 'splitting/shrinking';";
                //String sql = "SELECT group1, timeframe1 FROM GED_evolution WHERE threshold = '" + Integer.toString(a_tres) + "_" + Integer.toString(b_tres) + "' AND event_type = 'forming';";

        ///_________
        Statement stmt = conn.createStatement();
        ResultSet rsSpl = stmt.executeQuery(spl);
        // System.out.println("Point 1 " + spl);
        // loop through the result set
        while (rsSpl.next()) {
            //  System.out.println("Point 2");
            // System.out.println("rsSpl: " + rsSpl.getInt("group1") + " "
            //  + rsSpl.getString("timeframe1"));
            g1 = rsSpl.getInt("group1");
            t1 = rsSpl.getInt("timeframe1");

            // Condition 1
            String cdt1 = "SELECT COUNT(group1) FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type = 'splitting/shrinking' AND group1 = '" + Integer.toString(g1) + "' AND timeframe1 = '" + Integer.toString(t1) + "';";
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(cdt1);
            /*while (rs1.next()) {
             System.out.println("count(group1): " + rs1.getInt("count(group1)"));
             }*/

            // Condition 2
            String cdt2 = "SELECT COUNT(group1) FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type IN ('shrinking', 'growing') AND group1 = '" + Integer.toString(g1) + "' AND timeframe1 = '" + Integer.toString(t1) + "';";
            //System.out.println("a_tres:"+a_tres);
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(cdt2);

            rs1.next();
            rs2.next();
            int count1 = rs1.getInt("count(group1)");
            int count2 = rs2.getInt("count(group1)");

            // System.out.println("Counts: " + count1 + " " + count2);
            //update("splitting", "50_50", 1, 1, "splitting/shrinking");
            if (count1 > 1 || count2 > 0) {
                update("splitting", tres, g1, t1, "splitting/shrinking", 1);
            } else {
                update("shrinking", tres, g1, t1, "splitting/shrinking", 1);
                cpt++;
            }
        }

        String mer = "SELECT group2, timeframe2 FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type = 'merging/growing';";
        Statement stmtMer = conn.createStatement();
        ResultSet rsMer = stmt.executeQuery(mer);
        // loop through the result set
        while (rsMer.next()) {
            //System.out.println("Point 3" + mer);

            g2 = rsMer.getInt("group2");
            t2 = rsMer.getInt("timeframe2");
           // System.out.println("Mer: " + g2 + " "
            // + t2);

            // Condition 1
            String cdt1 = "SELECT COUNT(group2) FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type = 'merging/growing' AND group2 = '" + Integer.toString(g2) + "' AND timeframe2 = '" + Integer.toString(t2) + "';";

            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(cdt1);
            /*while (rs1.next()) {
             System.out.println("count(group1): " + rs1.getInt("count(group1)"));
             }*/

            // Condition 2
            String cdt2 = "SELECT COUNT(group2) FROM GED_evolution WHERE threshold = '" + tres + "' AND event_type IN ('shrinking', 'growing') AND group2 = '" + Integer.toString(g2) + "' AND timeframe2 = '" + Integer.toString(t2) + "';";
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery(cdt2);

            rs1.next();
            rs2.next();
            int count1 = rs1.getInt("count(group2)");
            int count2 = rs2.getInt("count(group2)");

            //  System.out.println("Counts: " + count1 + " " + count2);
            //update("splitting", "50_50", 1, 1, "splitting/shrinking");
            if (count1 > 1 || count2 > 0) {
                //  System.out.println(tres + "," + g2 + "," + t2 + ",");
                update("merging", tres, g2, t2, "merging/growing", 2);
            } else {
                update("growing", tres, g2, t2, "merging/growing", 2);
                cpt++;
            }
        }
        ///_________
        /*b_tres += 10;
         }
         a_tres += 10;
         b_tres = b_tres_tmp;
         }*/
        updateCounts = pstmt1.executeBatch();
        updateCounts = pstmt2.executeBatch();
        conn.commit();
        //conn.setAutoCommit(true);
        System.out.println("Update continuing started!");
        String cont = "SELECT group1, timeframe1, group2, timeframe2, event_type FROM GED_evolution WHERE event_type IN ('shrinking', 'growing');";
        Statement stmtCont = conn.createStatement();
        ResultSet rsCont = stmtCont.executeQuery(cont);
       // System.out.println("I am here!");

        // loop through the result set
        int i = 0, last = 0;
        while (rsCont.next()) {
//            if ((i / cpt) != last) {
//                System.out.print(i / cpt + "(" + i++ + "/" + cpt + ")");
//            }
            g1 = rsCont.getInt("group1");
            t1 = rsCont.getInt("timeframe1");
            g2 = rsCont.getInt("group2");
            t2 = rsCont.getInt("timeframe2");
            String eventType = rsCont.getString("event_type");
            //System.out.println("dynamicNetwork.size: " + dynamicNetwork.size());
            if (t2 < dynamicNetwork.size() && dynamicNetwork.get(t1).getCommunities().size() > 0 && dynamicNetwork.get(t2).getCommunities().size() > 0) {
                //System.out.println(t1 + " " + g1);
                int graphCont1 = dynamicNetwork.get(t1).getCommunities().get(g1).getNodeSet().size();
                //System.out.println(t2 + " " + g2 + " ");
                int graphCont2 = dynamicNetwork.get(t2).getCommunities().get(g2).getNodeSet().size();
                //System.out.println("Counts: " + graphCont1 + " " + graphCont2);
                if (graphCont1 == graphCont2) {
                    if (eventType.equals("shrinking")) {
                        // System.out.println(tres + "," + g2 + "," + t2 + ",");
                        update("continuing", tres, g1, t1, g2, t2, "shrinking");
                    } else if (eventType.equals("growing")) {
                        update("continuing", tres, g1, t1, g2, t2, "growing");
                    } else {
                        System.out.println("Whaat!?? :o");
                    }
                }
            }
        }
        updateCounts = pstmt3.executeBatch();
        conn.commit();

        conn.close();
    }

    private LinkedList<Node> nodeInter(Collection<Node> gr1, Collection<Node> gr2) {
        LinkedList<Node> inter = new LinkedList<Node>();

        for (Node n1 : gr1) {
            for (Node n2 : gr2) {
                if (n1.getId().equals(n2.getId())) {
                    inter.add(n2);
                }
            }
        }
        return inter;
    }

    private double sumMesure(Collection<Node> gr1, Collection<Node> g1g2) {
        double sum = 0;
        for (Node n1 : gr1) {
            for (Node n2 : g1g2) {
                if (n1.getId().equals(n2.getId())) {
                    sum += (Double) n1.getAttribute("bcentrality");
                }
            }
        }
        return sum;
    }

    private double sumMesure(Collection<Node> gr1) {
        double sum = 0;
        for (Node n : gr1) {
            sum += (Double) n.getAttribute("bcentrality"); // to adapt
        }
        return sum;
    }

    private List<Graph> groupsWithSharedNodes(Graph gr1, List<Graph> communities) {
        List<Graph> listGroupsWithSharedNodes = new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < communities.size(); i++) {
            Graph g = communities.get(i);
            for (Node n1 : gr1.getNodeSet()) {
                for (Node n2 : g.getNodeSet()) {
                    if (n1.getId().equals(n2.getId())) {
                        g.addAttribute("originalIndex", i);
                        listGroupsWithSharedNodes.add(g);
                        //System.out.println("Added");
                        found = true;
                        break;
                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
            //if (found) System.out.println("yay!");
            found = false;
        }

        return listGroupsWithSharedNodes;
    }

}
