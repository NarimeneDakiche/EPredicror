/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnapshotsPrep;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ado_k
 */
public class MainSplitting {

    public static void main(String[] args) throws FileNotFoundException, ParseException, UnsupportedEncodingException, IOException, SQLException, InterruptedException {
        // TODO code application logic here

        //snapp.getSplitSnapshots(null, 3, null);
        MyResult myResult = new MyResult();
        myResult.getResults("etc/out.dnc-temporalGraph", null, "VWXT", " ");
        System.out.println(TimeLength.timestampToDate(myResult.getMaxTS()) + "***" + TimeLength.timestampToDate(myResult.getMinTS()));

        SnapshotsPrep snapp = new SnapshotsPrep();
        Duration d = Duration.ofDays(10);
        List<Duration> listDuration = new ArrayList<Duration>();
        //listDuration.add(Duration.ofDays(500));
        listDuration.add(Duration.ofDays(900));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));
        listDuration.add(Duration.ofDays(10));

        int nbSnap = snapp.getSplitSnapshots("etc/out.dnc-temporalGraph", listDuration,
                null, "VWXT", " ", "export", false, true, "gml");
        nbSnap = snapp.getSplitSnapshots("etc/out.dnc-temporalGraph", Duration.ofDays(100),
                null, "VWXT", " ", "export", false, true, "gml");
        snapp.getSplitSnapshots("etc/out.dnc-temporalGraph", 1,
                null, "VWXT", " ", "export", false, true, "gml");
    }
}
