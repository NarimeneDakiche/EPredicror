/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionIdentification;

import evolutionIdentification.GEDUtils.TimeFrame;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 *
 * @author HADJER
 */
public class Asur {

    int timeframe = 0;
    int timeframe2 = 1;
    
    int matched;//--if 1 stop loops
    int form;
    int dissolve;
    int group10;//--currently processed group in T1
    int group11;//--currently processed group in T1
    int group20;//--currently processed group in T2
    int group21;//--currently processed group in T2
    int t1_no;//--number of groups in T1 (timestamp 1)
    int t2_no;//--number of groups in T2
    int temp_max;//--number of nodes in group10 or in group20 UNION group21
    int g10_size;//--number of nodes in group10
    int g20_size;
    int g1020_size;//--number of nodes in group10 INTERSECT group20
    int g1120_size;//--number of nodes in group11 INTERSECT group20
    int g1011_size;//--number of nodes in group10 UNION group11
    int g2021_size;//--number of nodes in group20 UNION group21

    void execute(LinkedList<TimeFrame> dynamicNetwork, int k//--percentage treshold for split
    ) {
        int nbtimeframe= dynamicNetwork.size();
        k=k*100;
        //Création des tables (Variables
        connect2(BDpath, "testAsur.db");
        createNewDatabase(BDpath, "testAsur.db");
        executeStatement(BDpath, "testAsur.db", scriptGroupsTab);
        executeStatement(BDpath, "testAsur.db", scriptAsurTab);
        //Insertion des données dans les tables 
        insertDataNodesTable(dynamicNetwork, 1000);

        //init Vars     
        matched = 0;
        form = 1;
        dissolve = 1;
        group10 = 0;
        group11 = 1;
        group20 = 0;
        group21 = 1;

        //Executer l'algorithme

        try {
            while (this.timeframe < nbtimeframe - 1) {
                t1_no = dynamicNetwork.get(timeframe).getCommunities().size();
                System.out.println("t1 = " + t1_no);
                t2_no = dynamicNetwork.get(timeframe2).getCommunities().size();
                System.out.println("t2 = " + t2_no);

                while (group10 < t1_no) {
                    g10_size = dynamicNetwork.get(timeframe).getCommunities().get(group10).getNodeCount();
                    System.out.println("g10_size = " + g10_size);
                    while (group20 < t2_no) {
                        g20_size = dynamicNetwork.get(timeframe2).getCommunities().get(group20).getNodeCount();
                        System.out.println("g20_size = " + g20_size);
                        Connection connection2 = Asur.connect(BDpath, "testAsur.db");
                        Statement s = connection2.createStatement();
                        ResultSet r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                + " INTERSECT Select node_id from Groups where "
                                + "(group_id=" + group20 + " and timeframe=" + timeframe2 + "))");
                        r.next();
                        g1020_size = r.getInt("rowcount");
                        r.close();
                        System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                + " INTERSECT Select node_id from Groups where "
                                + "(group_id=" + group20 + " and timeframe=" + timeframe2 + "))" + " g1020_size cont = "
                                + g1020_size);

                        /**
                         * Continuation event*
                         */
                        if ((g1020_size == g10_size) && (g10_size == g20_size)) {
                            matched = 1;
                            insertAsur("continue", group10, timeframe, group20, timeframe2);
                            System.out.println("Continue");
                        }
                        /**
                         * look for Dissolve event*
                         */
                        if ((dissolve == 1) && (g1020_size > 1)) {
                            dissolve = 0;
                        }
                        /**
                         * look for Merge event*
                         */
                        while (group11 < t1_no) {
                            //g11_size=dynamicNetwork.get(timeframe).getCommunities().get(group10).getNodeCount();
                            Graph g11 = dynamicNetwork.get(timeframe).getCommunities().get(group11);

                            //g1120_size=0;
                            //connection2 = Asur.connect(BDpath,"testAsur.db");
                            s = connection2.createStatement();
                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + "))");
                            r.next();
                            g1120_size = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + "))" + " g1120_size merge = "
                                    + g1120_size);

                            //g1011_size
                            //s = connection2.createStatement();
                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + "))");
                            r.next();
                            g1011_size = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + "))" + " g1011_size merge = "
                                    + g1011_size);

                            if (g20_size > g1011_size) {
                                temp_max = g20_size;
                            } else {
                                temp_max = g1011_size;
                            }
                            int A = 0, B = 0;
                            ///////
                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount  FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2
                                    + ") INTERSECT SELECT node_id FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe
                                    + ") UNION SELECT node_id FROM Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + ")) A)");
                            r.next();
                            A = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount  FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2
                                    + ") INTERSECT SELECT node_id FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe
                                    + ") UNION SELECT node_id FROM Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + ")) A)" + " A1 = " + A);

                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe2 + "))");
                            r.next();
                            
                            B = g1120_size;
                            /*B = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group11 + " and timeframe=" + timeframe + "))" + " B1 = " + B);
                            */
                            //////////
                            if ((A * 100 > (k * temp_max)) && (g1020_size > g10_size / 2) && (B > (g11.getNodeCount() / 2))) {
                                insertAsur("merge", group10, timeframe, group20, timeframe2);
                                System.out.println("Merge");
                                insertAsur("merge", group11, timeframe, group20, timeframe2);
                                System.out.println("Merge");
                            }

                            //*--look for form*/
                            if ((group10 == 0) && (form == 1)) {
                                if ((g1120_size > 1) || (g1020_size > 1)) {
                                    form = 0;
                                }
                            }
                            group11++;
                        }
                        if ((group10 == 0) && (form == 1)) {
                            //insertAsur("form",-1, timeframe, group20, timeframe2);
                            String query = "INSERT INTO Asur(event_type,group1,timeframe1,group2,timeframe2) values('"
                                    + "form" + "','" + "NULL" + "','" + timeframe + "','" + group20 + "','" + timeframe2 + "')";
                            System.out.println(query);
                            try (Connection conn = Asur.connect(BDpath, "testAsur.db");
                                    Statement stmt = conn.createStatement()) {
                                // create a new table
                                stmt.execute(query);
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        form = 1;
                        //--look for split
                        while ((group21 < t2_no) && (matched == 0)) {
                            Graph g21 = dynamicNetwork.get(timeframe2).getCommunities().get(group21);
                            //g2021_size
                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + ")"
                                    + " UNION Select node_id from Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + "))");
                            r.next();
                            g2021_size = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2 + ")"
                                    + " UNION Select node_id from Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + "))" + " g2021_size merge = "
                                    + g2021_size);

                            if (g10_size > g2021_size) {
                                temp_max = g10_size;
                            } else {
                                temp_max = g2021_size;
                            }
                            ////////////////////////
                            int A = 0, B = 0;
                            ///////
                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe
                                    + ") INTERSECT SELECT node_id FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2
                                    + ") UNION SELECT node_id FROM Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + ")) A)");
                            r.next();
                            A = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe
                                    + ") INTERSECT SELECT node_id FROM (SELECT node_id FROM Groups where "
                                    + "(group_id=" + group20 + " and timeframe=" + timeframe2
                                    + ") UNION SELECT node_id FROM Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + ")) A)" + " A1 = " + A);

                            r = s.executeQuery("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + "))");
                            r.next();
                            B = r.getInt("rowcount");
                            r.close();
                            System.out.println("SELECT COUNT(node_id) AS rowcount FROM ( SELECT node_id FROM Groups where "
                                    + "(group_id=" + group10 + " and timeframe=" + timeframe + ")"
                                    + " INTERSECT Select node_id from Groups where "
                                    + "(group_id=" + group21 + " and timeframe=" + timeframe2 + "))" + " B1 = " + B);

                            //////////
                            if ((A * 100 > (k * temp_max)) && (g1020_size > g20_size / 2) && (B > (g21.getNodeCount() / 2))) {
                                insertAsur("split", group10, timeframe, group20, timeframe2);
                                System.out.println("Split");
                                insertAsur("split", group10, timeframe, group21, timeframe2);
                                System.out.println("Split");
                            }
                            ///////////////////////
                            group21++;
                        }
                        group20++;
                        group21 = group20 + 1;
                        group11 = group10 + 1;
                        matched = 0;
                    }
                    if (dissolve == 1) {
                        String query = "INSERT INTO Asur(event_type,group1,timeframe1,group2,timeframe2) values('"
                                + "dissolve" + "','" + group10 + "','" + timeframe + "','" + "NULL" + "','" + timeframe2 + "')";
                        System.out.println(query);
                        try (Connection conn = Asur.connect(BDpath, "testAsur.db");
                                Statement stmt = conn.createStatement()) {
                            // create a new table
                            stmt.execute(query);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    group10++;
                    //TRUNCATE TABLE #Gr10
                    group20 = 0;
                    group21 = group20 + 1;
                    form = 1;
                    dissolve = 1;
                }
                timeframe++;
                timeframe2++;
                group10 = 0;
                group20 = 0;
                group21 = 0;
            }
            //connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //writeEvolutionChain();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static String BDpath = "./LibEvolution/";
    //Tab entrés
    static String scriptGroupsTab = "CREATE TABLE Groups (\n" //IF NOT EXISTS
            + "  group_id integer,\n"
            + "  node_id integer,\n"
            + "  timeframe integer,\n"
            + "  PRIMARY KEY (group_id, node_id, timeframe)\n"
            + ");";

    //Tab sorties
    static String scriptAsurTab = "CREATE TABLE Asur (\n"
            //+ " id_matched integer PRIMARY KEY,\n"
            + " event_type text NOT NULL,\n"
            + "  group1 integer,\n"
            + "  timeframe1 integer,\n"
            + "  group2 integer,\n"
            + "  timeframe2 integer\n"
            + ");";

    static String scriptUpdateAsurTab = "ALTER TABLE Asur ADD COLUMN used integer default 0;";
    static String scriptUpdate2AsurTab = "ALTER TABLE Asur ADD COLUMN Nextchain text default '';";

    static String scriptGr10Tab = "CREATE TABLE IF NOT EXISTS Gr10 (\n"
            + "  id_node integer\n"
            + ");";
    static String scriptGr11Tab = "CREATE TABLE IF NOT EXISTS Gr11 (\n"
            + "  id_node integer\n"
            + ");";

    static String scriptGr20Tab = "CREATE TABLE IF NOT EXISTS Gr20 (\n"
            + "  id_node integer\n"
            + ");";
    static String scriptGr21Tab = "CREATE TABLE IF NOT EXISTS Gr21 (\n"
            + "  id_node integer\n"
            + ");";

    public static Connection connect2(String path, String fileName) {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + path + fileName;
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return conn;
    }

    public static Connection connect(String path, String fileName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase(String path, String fileName) {

        String url = "jdbc:sqlite:" + path + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void executeStatement(String path, String fileName, String sql) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        // SQL statement for creating a new table
        /*String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
         + "	id integer PRIMARY KEY,\n"
         + "	name text NOT NULL,\n"
         + "	capacity real\n"
         + ");";*/
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertDataNodesTable(LinkedList<TimeFrame> dynamicNetwork, int batchSize) {
        try {
            Connection connection = Asur.connect(BDpath, "testAsur.db");
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            //final int batchSize = 1000;
            int count = 0;
            int timeFrame_id = 0;
            for (TimeFrame timeFrame : dynamicNetwork) {

                int group_id = 0;
                for (Graph com : timeFrame.getCommunities()) {

                    //if(com==null)System.err.println("com == null");
                    for (Node node : com.getNodeSet()) {

                        String query = "insert into Groups(group_id,node_id,timeframe) values('"
                                + group_id + "','" + node.getId() + "','" + timeFrame_id + "')";
                        System.out.println(query);

                        statement.addBatch(query);

                        if (++count % batchSize == 0) {
                            statement.executeBatch();//to avoid  OutOfMemoryError
                            connection.commit();
                        }
                    }
                    group_id++;
                }
                timeFrame_id++;
            }
            statement.executeBatch();// insert remaining records
            connection.commit();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertAsur(String event_type, int group1, int timeframe1, int group2, int timeframe2) {
        String sql = "INSERT INTO Asur(event_type,group1,timeframe1,group2,timeframe2) VALUES(?,?,?,?,?)";

        try (Connection conn = Asur.connect(BDpath, "testAsur.db");
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event_type);
            pstmt.setInt(2, group1);
            pstmt.setInt(3, timeframe1);
            pstmt.setInt(4, group2);
            pstmt.setInt(5, timeframe2);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
