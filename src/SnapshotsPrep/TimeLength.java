/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnapshotsPrep;

import java.text.ParseException;
import java.time.Duration;

/**
 *
 * @author ado_k
 */
public class TimeLength {

    public static long dateToTimestamp(String timeFormat, String dateS) throws ParseException {

        return new java.text.SimpleDateFormat(timeFormat).parse(dateS).getTime() / 1000;

    }

    public static String timestampToDate(Long epoch) {
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(epoch * 1000));
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(timestampToDate(new Long(1195613977)));
        System.out.println(dateToTimestamp("MM/dd/yyyy HH:mm:ss", "1/02/1970 00:00:02"));

        //Period p = new org.joda.time.Period(1,1,1);
        Duration d = Duration.ofDays(3);
        System.out.println(d);

    }
}
