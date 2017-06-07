/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolutionIdentification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author HADJER
 */
public class EvolutionUtils {

    public static void main(String[] args) throws SQLException {
        String BDpath = "./GED/";
        String BDfilename = "GED_detection_CPM_5.txt_CPM_5_50_50.db";
        String tabname = "GED_evolution";
        int nbtimeframe = 47;
        writeEvolutionChain(BDpath, BDfilename, tabname, nbtimeframe, 3/**
         * nbre timeframes*
         */
        );

    }

    /**
     * A method to generate evoltion chains tables*
     */
    public static void writeEvolutionChain(/*LinkedList<TimeFrame> dynamicNetwork,*/String BDpath, String BDfilename, String tabname, int nbtimeframe/**
             * nbre timeframes*
             */
            , int chainLength/**
     * chain's min lengh
     */
    ) throws SQLException {
        String url = "jdbc:sqlite:" + BDpath + BDfilename;

        Connection conn = DriverManager.getConnection(url);
        String sql1 = "PRAGMA synchronous=OFF";
        Statement st = conn.createStatement();
        st.execute(sql1);
        //conn.setAutoCommit(false);
        //Generating evolution chain for groups in Evolution identification method
        ArrayList<String> chains = new ArrayList<String>();

        if (nbtimeframe > 2) {
            int ti = 1;
            while (ti </*5*/ (nbtimeframe - 2)) {
                if (ti == 1) {
                    //First Table Generation : Join2 : timeframes: 0 1 2 
                    String scriptJoinTab = "CREATE TABLE Join2 (\n"
                            + " group1 text,\n"
                            + " timeframe1 text,\n"
                            + " event_type text NOT NULL,\n"
                            + " group2 integer,\n"
                            + " timeframe2 integer\n"
                            + " );";
                    //Script Join First two tables : timeframes : 0 1 /1 2
                    String scriptJoin = "INSERT INTO Join2 select * from ( \n"
                            + "\n"
                            + " select \n"
                            + "  case e2.group1\n"
                            + "    when e1.group2 THEN\n"
                            + "      case e2.event_type\n"
                            + "        when 'dissolving' THEN\n"
                            + "             case e1.event_type \n"
                            + "                  when 'forming' THEN\n"
                            + "                      'null,' || e2.group1 || ',null'               \n"
                            + "                  ELSE\n"
                            + "                      e1.group1 || ',' || e2.group1 || ',null'\n"
                            + "                  END \n"
                            + "        ELSE\n"
                            + "            case e1.event_type \n"
                            + "                  when 'forming' THEN\n"
                            + "                      'null,' || e2.group1 \n"
                            + "                  ELSE\n"
                            + "                      e1.group1 || ',' || e2.group1\n"
                            + "                  END \n"
                            + "        END\n"
                            + "    ELSE\n"
                            + "      case e2.event_type\n"
                            + "        when 'dissolving' THEN\n"
                            + "           e2.group1 || ',null' \n"
                            + "        when 'forming' THEN\n"
                            + "           'null'\n"
                            + "        ELSE\n"
                            + "           e2.group1\n"
                            + "        END\n"
                            + "  END group1,\n"
                            + "\n"
                            + "  case e2.group1\n"
                            + "    when e1.group2 THEN\n"
                            + "      case e2.event_type\n"
                            + "          when 'dissolving' THEN\n"
                            + "               e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 \n"
                            + "          ELSE\n"
                            + "             e1.timeframe1 || ',' || e2.timeframe1\n"
                            + "          END\n"
                            + "    ELSE\n"
                            + "        case e2.event_type\n"
                            + "          when 'dissolving' THEN\n"
                            + "               e2.timeframe1 || ',' || e2.timeframe2 \n"
                            + "          ELSE\n"
                            + "             e2.timeframe1 \n"
                            + "          END          \n"
                            + "    END timeframe1,\n"
                            + "\n"
                            + "  case e2.group1\n"
                            + "    when e1.group2 THEN\n"
                            + "            e1.event_type || ',' || e2.event_type\n"
                            + "    ELSE\n"
                            + "        e2.event_type\n"
                            + "  END event_type,\n"
                            + "  \n"
                            + "e2.group2 as group2, \n"
                            + "e2.timeframe2 as timeframe2 \n"
                            + "\n"
                            + "from ( (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=1) as e2 \n"
                            + "LEFT JOIN (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=0) as e1 \n"
                            + "ON e1.group2=e2.group1 )\n"
                            + "\n"
                            + "UNION\n"
                            + "\n"
                            + "select \n"
                            + "  \n"
                            + "  case e1.group2\n"
                            + "    when e2.group1 THEN\n"
                            + "      case e2.event_type\n"
                            + "        when 'dissolving' THEN\n"
                            + "             case e1.event_type  \n"
                            + "                  when 'forming' THEN\n"
                            + "                       'null,' || e2.group1 || ',null'\n"
                            + "                  ELSE\n"
                            + "                     e1.group1 || ',' || e2.group1 || ',null'                          \n"
                            + "             END\n"
                            + "        when 'forming' THEN\n"
                            + "           'null'\n"
                            + "        ELSE\n"
                            + "            case e1.event_type \n"
                            + "                  when 'forming' THEN\n"
                            + "                      'null,' || e2.group1 \n"
                            + "                  ELSE\n"
                            + "                      e1.group1 || ',' || e2.group1\n"
                            + "                  END \n"
                            + "        END\n"
                            + "    ELSE\n"
                            + "        e1.group1 || ',' || e1.group2\n"
                            + "  END group1,\n"
                            + "\n"
                            + "  case e1.group2\n"
                            + "    when e2.group1 THEN\n"
                            + "      case e2.event_type\n"
                            + "        when 'dissolving' THEN\n"
                            + "             e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 \n"
                            + "        when 'forming' THEN\n"
                            + "           e2.timeframe1\n"
                            + "        ELSE\n"
                            + "            e1.timeframe1 || ',' || e2.timeframe1 \n"
                            + "      END\n"
                            + "    ELSE\n"
                            + "    e1.timeframe1 || ',' || e1.timeframe2     \n"
                            + "  END timeframe1,\n"
                            + "\n"
                            + "  case e1.group2\n"
                            + "    when e2.group1 THEN\n"
                            + "      case e2.event_type\n"
                            + "          when 'forming' THEN\n"
                            + "             e2.event_type\n"
                            + "          ELSE\n"
                            + "              e1.event_type || ',' || e2.event_type\n"
                            + "       END\n"
                            + "    ELSE\n"
                            + "    e1.event_type  \n"
                            + "  END event_type,\n"
                            + "  \n"
                            + "e2.group2 as group2, \n"
                            + "e2.timeframe2 as timeframe2 \n"
                            + "\n"
                            + "from ( (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=0) as e1 \n"
                            + "LEFT JOIN (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=1) as e2 \n"
                            + "ON e1.group2=e2.group1 )  \n"
                            + "\n"
                            + ");";
                    //Execute Scripts
                    executeStatement(conn, scriptJoinTab);
                    executeStatement(conn, scriptJoin);
                    //Writing Completed chains (Group2==null)
                } else {
                    if (ti == nbtimeframe - 3) {
                        //Last table
                        int ti1 = ti + 1;
                        String scriptJoinTab = "CREATE TABLE Join" + ti1 + " (\n"
                                + " group1 text,\n"
                                + " timeframe1 text,\n"
                                + " event_type text NOT NULL,\n"
                                + " group2 integer,\n"
                                + " timeframe2 integer\n"
                                + " );";
                        //Script Join tables Jointi and tab ti ti+1: 
                        String scriptJoin = "INSERT INTO Join" + ti1 + " select * from ( \n"
                                + " select \n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "             case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 || ',null'               \n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1 || ',null'\n"
                                + "                  END \n"
                                + "        ELSE\n"
                                + "            case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 || ',' || e2.group2\n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1 || ',' || e2.group2\n"
                                + "                  END \n"
                                + "        END\n"
                                + "    ELSE\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "           e2.group1 || ',null' \n"
                                + "        when 'forming' THEN\n"
                                + "           'null,' || ',' || e2.group2\n"
                                + "        ELSE\n"
                                + "           e2.group1 || ',' || e2.group2\n"
                                + "        END\n"
                                + "  END group1,\n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "      e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 \n"
                                + "    ELSE\n"
                                + "        e2.timeframe1 || ',' || e2.timeframe2          \n"
                                + "    END timeframe1,\n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "            e1.event_type || ',' || e2.event_type\n"
                                + "    ELSE\n"
                                + "        e2.event_type\n"
                                + "  END event_type,\n"
                                + "e2.group2 as group2, \n"
                                + "e2.timeframe2 as timeframe2 \n"
                                + "from ( (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=" + ti + ") as e2 \n"
                                + "LEFT JOIN (select * from Join" + ti + " where group2 is not null) as e1 \n"
                                + "ON e1.group2=e2.group1 )\n"
                                + "UNION\n"
                                + "select \n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "             case e1.event_type  \n"
                                + "                  when 'forming' THEN\n"
                                + "                       'null,' || e2.group1 || ',null'\n"
                                + "                  ELSE\n"
                                + "                     e1.group1 || ',' || e2.group1 || ',null'                          \n"
                                + "             END\n"
                                + "        when 'forming' THEN\n"
                                + "           'null' || ',' || e2.group2\n"
                                + "        ELSE\n"
                                + "            case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 \n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1\n"
                                + "                  END \n"
                                + "        END\n"
                                + "    ELSE\n"
                                + "        e1.group1 || ',' || e1.group2\n"
                                + "  END group1,\n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "        /*when 'dissolving' THEN\n"
                                + "             e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 */\n"
                                + "        when 'forming' THEN\n"
                                + "           e2.timeframe1 || ',' || e2.timeframe2\n"
                                + "        ELSE\n"
                                + "            e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2\n"
                                + "      END\n"
                                + "    ELSE\n"
                                + "    e1.timeframe1 || ',' || e1.timeframe2     \n"
                                + "  END timeframe1,\n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "          when 'forming' THEN\n"
                                + "             e2.event_type\n"
                                + "          ELSE\n"
                                + "              e1.event_type || ',' || e2.event_type\n"
                                + "       END\n"
                                + "    ELSE\n"
                                + "    e1.event_type  \n"
                                + "  END event_type,\n"
                                + "e2.group2 as group2, \n"
                                + "e2.timeframe2 as timeframe2 \n"
                                + "from ( (select * from Join" + ti + " where group2 is not null) as e1 \n"
                                + "LEFT JOIN (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=" + ti + ") as e2 \n"
                                + "ON e1.group2=e2.group1 )  \n"
                                + ");";
                        /*String scriptDeleteTab ="PRAGMA foreign_keys = OFF;\n" +
                         " DROP TABLE Join"+ti+";\n" +
                         "PRAGMA foreign_keys = ON;";*/
                        //Execute Scripts
                        executeStatement(conn, scriptJoinTab);
                        executeStatement(conn, scriptJoin);
                        //executeStatement(BDpath,BDfilename, scriptDeleteTab);
                        //Writing all chains 
                    } else {
                        //Other tables
                        int ti1 = ti + 1;
                        String scriptJoinTab = "CREATE TABLE Join" + ti1 + " (\n"
                                + " group1 text,\n"
                                + " timeframe1 text,\n"
                                + " event_type text NOT NULL,\n"
                                + " group2 integer,\n"
                                + " timeframe2 integer\n"
                                + " );";
                        //Script Join tables Jointi and tab ti ti+1: 
                        String scriptJoin = "INSERT INTO Join" + ti1 + " select * from ( \n"
                                + " select \n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "             case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 || ',null'               \n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1 || ',null'\n"
                                + "                  END \n"
                                + "        ELSE\n"
                                + "            case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 \n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1\n"
                                + "                  END \n"
                                + "        END\n"
                                + "    ELSE\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "           e2.group1 || ',null' \n"
                                + "        when 'forming' THEN\n"
                                + "           'null'\n"
                                + "        ELSE\n"
                                + "           e2.group1\n"
                                + "        END\n"
                                + "  END group1,\n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "      case e2.event_type\n"
                                + "          when 'dissolving' THEN\n"
                                + "               e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 \n"
                                + "          ELSE\n"
                                + "             e1.timeframe1 || ',' || e2.timeframe1\n"
                                + "          END\n"
                                + "    ELSE\n"
                                + "        case e2.event_type\n"
                                + "          when 'dissolving' THEN\n"
                                + "               e2.timeframe1 || ',' || e2.timeframe2 \n"
                                + "          ELSE\n"
                                + "             e2.timeframe1 \n"
                                + "          END          \n"
                                + "    END timeframe1,\n"
                                + "  case e2.group1\n"
                                + "    when e1.group2 THEN\n"
                                + "            e1.event_type || ',' || e2.event_type\n"
                                + "    ELSE\n"
                                + "        e2.event_type\n"
                                + "  END event_type,\n"
                                + "  \n"
                                + "e2.group2 as group2, \n"
                                + "e2.timeframe2 as timeframe2 \n"
                                + "from ( (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=" + ti + ") as e2 \n"
                                + "LEFT JOIN (select * from Join" + ti + " where group2 is not null) as e1 \n"
                                + "ON e1.group2=e2.group1 )\n"
                                + "UNION\n"
                                + "select \n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "             case e1.event_type  \n"
                                + "                  when 'forming' THEN\n"
                                + "                       'null,' || e2.group1 || ',null'\n"
                                + "                  ELSE\n"
                                + "                     e1.group1 || ',' || e2.group1 || ',null'                          \n"
                                + "             END\n"
                                + "        when 'forming' THEN\n"
                                + "           'null'\n"
                                + "        ELSE\n"
                                + "            case e1.event_type \n"
                                + "                  when 'forming' THEN\n"
                                + "                      'null,' || e2.group1 \n"
                                + "                  ELSE\n"
                                + "                      e1.group1 || ',' || e2.group1\n"
                                + "                  END \n"
                                + "        END\n"
                                + "    ELSE\n"
                                + "        e1.group1 || ',' || e1.group2\n"
                                + "  END group1,\n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "        when 'dissolving' THEN\n"
                                + "             e1.timeframe1 || ',' || e2.timeframe1 || ',' || e2.timeframe2 \n"
                                + "        when 'forming' THEN\n"
                                + "           e2.timeframe1\n"
                                + "        ELSE\n"
                                + "            e1.timeframe1 || ',' || e2.timeframe1 \n"
                                + "      END\n"
                                + "    ELSE\n"
                                + "    e1.timeframe1 || ',' || e1.timeframe2     \n"
                                + "  END timeframe1,\n"
                                + "  case e1.group2\n"
                                + "    when e2.group1 THEN\n"
                                + "      case e2.event_type\n"
                                + "          when 'forming' THEN\n"
                                + "             e2.event_type\n"
                                + "          ELSE\n"
                                + "              e1.event_type || ',' || e2.event_type\n"
                                + "       END\n"
                                + "    ELSE\n"
                                + "    e1.event_type  \n"
                                + "  END event_type,\n"
                                + "e2.group2 as group2, \n"
                                + "e2.timeframe2 as timeframe2 \n"
                                + "from ( (select * from Join" + ti + " where group2 is not null) as e1 \n"
                                + "LEFT JOIN (select group1,timeframe1,event_type,group2,timeframe2 from " + tabname + " where timeframe1=" + ti + ") as e2 \n"
                                + "ON e1.group2=e2.group1 )  \n"
                                + ");";

                        //Execute Scripts
                        executeStatement(conn, scriptJoinTab);
                        executeStatement(conn, scriptJoin);

                    }
                }
                //next iteration
                ti++;
            }
            //Writing Completed chains:
            String scriptDelTab = "DROP TABLE IF EXISTS Chains";
            String scriptJoinTab = "CREATE TABLE Chains (\n"
                    + " group1 text,\n"
                    + " timeframe1 text,\n"
                    + " event_type text NOT NULL,\n"
                    + " group2 integer,\n"
                    + " timeframe2 integer\n"
                    + " );";
            //Execute Scripts
            executeStatement(conn, scriptDelTab);
            executeStatement(conn, scriptJoinTab);
            int min = chainLength * 7 + (chainLength - 1);
            for (int temp = chainLength; temp < nbtimeframe - 1; temp++) {
                String sql = "";
                if (temp == nbtimeframe - 2) {
                    sql = "INSERT INTO Chains select * from Join" + temp + " where LENGTH(event_type)>=" + min + ";";
                } else {
                    sql = "INSERT INTO Chains select * from Join" + temp + " where (group2 is null) and LENGTH(event_type)>=" + min + ";";
                }
                //Execute Scripts
                executeStatement(conn, sql);
            }

            //Deleting tables
            for (int temp = 2; temp < nbtimeframe - 1; temp++) {
                String scriptDeleteTab = "DROP TABLE Join" + temp + ";";
                //Execute Scripts
                executeStatement(conn, scriptDeleteTab);
            }

        }
        //System.out.println("reached.");

    }

    public static void executeStatement(String path, String fileName, String sql) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + path + fileName;

        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            /*String sql1 = "PRAGMA synchronous=OFF";
             Statement st = conn.createStatement();
             st.execute(sql1);*/
            /*conn.setAutoCommit(false);*/
            stmt.execute(sql);
            //conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void executeStatement(Connection conn, String sql) {
        // SQLite connection string

        try (
                Statement stmt = conn.createStatement()) {
            // create a new table

            stmt.execute(sql);
            //conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
